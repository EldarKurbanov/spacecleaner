package ru.innovationcampus.spacecleaner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import ru.innovationcampus.spacecleaner.GameResources;
import ru.innovationcampus.spacecleaner.GameSettings;
import ru.innovationcampus.spacecleaner.Main;
import ru.innovationcampus.spacecleaner.objects.ShipObject;

public class GameScreen extends ScreenAdapter {
    Main main;
    ShipObject shipObject;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        super.show();

        shipObject = new ShipObject(GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATH, main.world);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        main.stepWorld();
        handleInput();

        draw();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            main.touch = main.orthographicCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            shipObject.move(main.touch);
        }
    }

    private void draw() {
        main.orthographicCamera.update();
        main.batch.setProjectionMatrix(main.orthographicCamera.combined);
        ScreenUtils.clear(Color.CLEAR);

        main.batch.begin();
        shipObject.draw(main.batch);
        main.batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();

        shipObject.dispose();
    }
}
