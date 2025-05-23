package ru.innovationcampus.spacecleaner.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ImageView extends View {
    Texture texture;

    public ImageView(float x, float y, String imagePath) {
        super(x, y);

        texture = new Texture(imagePath);

        width = texture.getWidth();
        height = texture.getHeight();
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        batch.draw(texture, x, y, width, height);
    }

    @Override
    public void dispose() {
        super.dispose();

        texture.dispose();
    }
}
