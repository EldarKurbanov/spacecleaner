package ru.innovationcampus.spacecleaner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import ru.innovationcampus.spacecleaner.GameResources;
import ru.innovationcampus.spacecleaner.Main;
import ru.innovationcampus.spacecleaner.managers.MemoryManager;
import ru.innovationcampus.spacecleaner.view.ButtonView;
import ru.innovationcampus.spacecleaner.view.ImageView;
import ru.innovationcampus.spacecleaner.view.MovingBackgroundView;
import ru.innovationcampus.spacecleaner.view.TextView;

public class SettingsScreen extends ScreenAdapter {
    Main main;
    MovingBackgroundView background;
    ImageView blackout;
    ButtonView button;
    TextView screenName, music, sound, resetRecords;

    public SettingsScreen(Main main) {
        this.main = main;

        background = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);

        blackout = new ImageView(85, 365, GameResources.BLACKOUT_TOP_IMG_PATH);

        button = new ButtonView(280, 447, 160, 70, main.commonWhiteFont, GameResources.BUTTON_BACKGROUND_SHORT_IMG_PATH, "return");

        screenName = new TextView(main.largeWhiteFont, 256, 956, "Settings");
        music = new TextView(main.commonWhiteFont, 173, 717, "music: ON");
        sound = new TextView(main.commonWhiteFont, 173, 658, "sound: ON");
        resetRecords = new TextView(main.commonWhiteFont, 173, 599, "clear records");
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        handleInput();

        main.orthographicCamera.update();
        main.batch.setProjectionMatrix(main.orthographicCamera.combined);

        ScreenUtils.clear(Color.CLEAR);

        main.batch.begin();

        background.draw(main.batch);
        blackout.draw(main.batch);
        button.draw(main.batch);
        screenName.draw(main.batch);
        music.draw(main.batch);
        sound.draw(main.batch);
        resetRecords.draw(main.batch);

        main.batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();

        background.dispose();
        blackout.dispose();
        button.dispose();
        screenName.dispose();
        music.dispose();
        sound.dispose();
        resetRecords.dispose();
    }

    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            main.touch = main.orthographicCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (button.isHit(main.touch.x, main.touch.y)) {
                main.setScreen(main.menuScreen);
            }
            if (resetRecords.isHit(main.touch.x, main.touch.y)) {
                resetRecords.setText("clear records (cleared)");
            }
            if (music.isHit(main.touch.x, main.touch.y)) {
                MemoryManager.saveMusicSettings(!MemoryManager.loadIsMusicOn());
                music.setText("music: " + translateStateToText(MemoryManager.loadIsMusicOn()));
                main.audioManager.updateMusicFlag();
            }
            if (sound.isHit(main.touch.x, main.touch.y)) {
                MemoryManager.saveSoundSettings(!MemoryManager.loadIsSoundOn());
                sound.setText("sound: " + translateStateToText(MemoryManager.loadIsMusicOn()));
            }
            if (resetRecords.isHit(main.touch.x, main.touch.y)) {
                MemoryManager.saveTableOfRecords(new ArrayList<>());
            }
        }
    }
}
