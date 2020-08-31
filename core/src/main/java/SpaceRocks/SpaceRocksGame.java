package SpaceRocks;

import com.badlogic.gdx.Game;

public class SpaceRocksGame extends BaseGame {
    @Override
    public void create() {
        GameScreen gameScreen = new GameScreen(this);
        setScreen(gameScreen);

    }
}
