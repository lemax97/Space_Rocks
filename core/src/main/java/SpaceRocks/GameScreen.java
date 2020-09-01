package SpaceRocks;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
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
        rockList = new ArrayList<PhysicsActor>();
        int numRocks = 6;
        for (int n = 0; n < numRocks; n++) {
            PhysicsActor rock = new PhysicsActor();
            String filename = "rock" + (n%4) + ".png";
            Texture rockTexture = new Texture(filename);
            rockTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            rock.storeAnimation("default", rockTexture);

            rock.setPosition(800 * MathUtils.random(), 600 * MathUtils.random());
            rock.setOriginCenter();
            rock.setEllipseBoundary();
            rock.setAutoAngle(false);

            float speedUp = MathUtils.random(0.0f, 1.0f);
            rock.setVelocityAS(360 * MathUtils.random(), 75 + 50 * speedUp);
            rock.addAction(Actions.forever(Actions.rotateBy(360, 2 - speedUp)));

            mainStage.addActor(rock);
            rockList.add(rock);
            rock.setParentList(rockList);
        }

        baseExplosion = new AnimatedActor();
        Animation explosionAnimation =
                GameUtils.parseSpriteSheet("explosion.png",
                        6,
                        6,
                        0.03f,
                        Animation.PlayMode.NORMAL);
        baseExplosion.storeAnimation("default", explosionAnimation);
        baseExplosion.setWidth(96);
        baseExplosion.setHeight(96);
        baseExplosion.setOriginCenter();

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
            for (PhysicsActor rock: rockList){
                if (laser.overlaps(rock, false)){
                    removeList.add(laser);
                    removeList.add(rock);
                    AnimatedActor explosion = baseExplosion.clone();
                    explosion.moveToOrigin(rock);
                    mainStage.addActor(explosion);
                    explosion.addAction(
                            Actions.sequence(Actions.delay(1.08f),
                                    Actions.removeActor()) );
                }
            }
        }

        for (BaseActor ba: removeList){
            ba.destroy();
        }

        for (PhysicsActor rock: rockList){
            wraparound(rock);
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