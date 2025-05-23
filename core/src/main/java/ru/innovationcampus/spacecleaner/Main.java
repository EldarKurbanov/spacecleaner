package ru.innovationcampus.spacecleaner;

import static ru.innovationcampus.spacecleaner.GameSettings.POSITION_ITERATIONS;
import static ru.innovationcampus.spacecleaner.GameSettings.STEP_TIME;
import static ru.innovationcampus.spacecleaner.GameSettings.VELOCITY_ITERATIONS;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import ru.innovationcampus.spacecleaner.screens.GameScreen;
import ru.innovationcampus.spacecleaner.screens.MenuScreen;
import ru.innovationcampus.spacecleaner.view.FontBuilder;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch batch;
    public OrthographicCamera orthographicCamera;
    public World world;
    float accumulator = 0;
    public Vector3 touch;
    public BitmapFont commonWhiteFont, largeWhiteFont, commonBlackFont;
    public GameScreen gameScreen;
    public MenuScreen menuScreen;

    @Override
    public void create() {
        commonWhiteFont = FontBuilder.generate(24, Color.WHITE, GameResources.FONT_PATH);
        largeWhiteFont = FontBuilder.generate(48, Color.WHITE, GameResources.FONT_PATH);
        commonBlackFont = FontBuilder.generate(24, Color.BLACK, GameResources.FONT_PATH);

        Box2D.init();
        world = new World(new Vector2(0, 0), true);

        batch = new SpriteBatch();
        orthographicCamera = new OrthographicCamera();
        orthographicCamera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += delta;

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }
    }
}
