package it.works.flappybee;

import com.badlogic.gdx.Game;

public class FlappyBeeGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
