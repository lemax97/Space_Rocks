package SpaceRocks;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen extends BaseScreen {

    private BaseActor background;
    private PhysicsActor spaceship;
    private BaseActor rocketfire;

    //create "base" objects to clone later
    private PhysicsActor baseLaser;
    private AnimatedActor baseExplosion;

    private ArrayList<PhysicsActor> laserList;
    private ArrayList<PhysicsActor> rockList;
    private ArrayList<BaseActor> removeList;

    //game world dimensions
    final int mapWidth = 800;
    final int mapHeight = 600;

    public GameScreen(BaseGame g) {
        super(g);
    }

    @Override
    public void create() {
        background = new BaseActor();
        background.setTexture(new Texture("space.png"));
        background.setPosition(0,0);
        mainStage.addActor(background);

        spaceship = new PhysicsActor();
        Texture shipTexture = new Texture("spaceship.png");
        shipTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        spaceship.storeAnimation("default", shipTexture);

        spaceship.setPosition(400,300);
        spaceship.setOriginCenter();
        spaceship.setMaxSpeed(200);
        spaceship.setDeceleration(20);
        spaceship.setEllipseBoundary();

        rocketfire = new BaseActor();
        rocketfire.setPosition(-28,24);
        Texture fireTexture = new Texture("fire.png");
        fireTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        rocketfire.setTexture(fireTexture);
        spaceship.addActor(rocketfire);

        baseLaser = new PhysicsActor();
        Texture laserTexture = new Texture("laser.png");
        laserTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        baseLaser.storeAnimation("default", laserTexture);

        baseLaser.setMaxSpeed(400);
        baseLaser.setDeceleration(0);
        baseLaser.setEllipseBoundary();
        baseLaser.setOriginCenter();
        baseLaser.setAutoAngle(true);

        laserList = new ArrayList<PhysicsActor>();
        removeList = new ArrayList<BaseActor>();

        mainStage.addActor(spaceship);
    }

    public void wraparound(BaseActor baseActor){
        if (baseActor.getX() + baseActor.getWidth() < 0)
            baseActor.setX(mapWidth);
        if (baseActor.getX() > mapWidth)
            baseActor.setX(-baseActor.getWidth());
        if (baseActor.getY() + baseActor.getHeight() < 0)
            baseActor.setY(mapHeight);
        if (baseActor.getY() > mapHeight)
            baseActor.setY(-baseActor.getHeight());
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE){
            PhysicsActor laser = baseLaser.clone();
            laser.moveToOrigin(spaceship);
            laser.setVelocityAS(spaceship.getRotation(), 400);
            laserList.add(laser);
            laser.setParentList(laserList);

            mainStage.addActor(laser);

            laser.addAction(
                    Actions.sequence(Actions.delay(2), Actions.fadeOut(0.5f), Actions.visible(false)));
        }

        return false;
    }

    @Override
    public void update(float dt) {
        spaceship.setAccelerationXY(0,0);
        removeList.clear();
        for (PhysicsActor laser : laserList){
            wraparound(laser);
            if (!laser.isVisible())
                removeList.add(laser);
        }

        for (BaseActor ba: removeList){
            ba.destroy();
        }
        wraparound(spaceship);
        rocketfire.setVisible(Gdx.input.isKeyPressed(Input.Keys.UP));
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            spaceship.rotateBy(180 * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            spaceship.rotateBy(-180 * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            spaceship.addAccelerationAS(spaceship.getRotation(), 100);
    }
}