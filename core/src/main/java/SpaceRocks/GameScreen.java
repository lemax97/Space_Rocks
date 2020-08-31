package SpaceRocks;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;

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
    private ArrayList<PhysicsActor> removeList;

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
    public void update(float dt) {
        spaceship.setAccelerationXY(0,0);
        wraparound(spaceship);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            spaceship.rotateBy(180 * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            spaceship.rotateBy(-180 * dt);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            spaceship.addAccelerationAS(spaceship.getRotation(), 100);
    }
}