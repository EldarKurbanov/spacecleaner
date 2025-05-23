package ru.innovationcampus.spacecleaner.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ru.innovationcampus.spacecleaner.GameSettings;

public class MovingBackgroundView extends View {
    Texture texture;

    int texture1Y, texture2Y = GameSettings.SCREEN_HEIGHT, speed = 2;

    public MovingBackgroundView(String pathToTexture) {
        super(0, 0);

        texture = new Texture(pathToTexture);
    }

    public void move() {
        texture1Y -= speed;
        texture2Y -= speed;

        if (texture1Y <= -GameSettings.SCREEN_HEIGHT) {
            texture1Y = GameSettings.SCREEN_HEIGHT;
        }

        if (texture2Y <= -GameSettings.SCREEN_HEIGHT) {
            texture2Y = GameSettings.SCREEN_HEIGHT;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        batch.draw(texture, 0, texture1Y, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        batch.draw(texture, 0, texture2Y, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
    }

    @Override
    public void dispose() {
        super.dispose();

        texture.dispose();
    }
}
