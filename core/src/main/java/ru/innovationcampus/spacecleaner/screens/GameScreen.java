package ru.innovationcampus.spacecleaner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import ru.innovationcampus.spacecleaner.managers.ContactManager;
import ru.innovationcampus.spacecleaner.GameResources;
import ru.innovationcampus.spacecleaner.GameSession;
import ru.innovationcampus.spacecleaner.GameSettings;
import ru.innovationcampus.spacecleaner.GameState;
import ru.innovationcampus.spacecleaner.Main;
import ru.innovationcampus.spacecleaner.objects.BulletObject;
import ru.innovationcampus.spacecleaner.objects.ShipObject;
import ru.innovationcampus.spacecleaner.objects.TrashObject;
import ru.innovationcampus.spacecleaner.view.ButtonView;
import ru.innovationcampus.spacecleaner.view.ImageView;
import ru.innovationcampus.spacecleaner.view.LiveView;
import ru.innovationcampus.spacecleaner.view.MovingBackgroundView;
import ru.innovationcampus.spacecleaner.view.TextView;

public class GameScreen extends ScreenAdapter {
    Main main;
    ShipObject shipObject;
    GameSession gameSession;
    ArrayList<TrashObject> trashArray;
    ArrayList<BulletObject> bulletArray;
    ContactManager contactManager;
    MovingBackgroundView backgroundView;
    ImageView topBlackoutView;
    LiveView liveView;
    TextView scoreTextView;
    ButtonView pauseButton;
    ImageView fullBlackoutView;
    ButtonView homeButton, continueButton;
    TextView pauseTextView;

    public GameScreen(Main main) {
        this.main = main;
        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        gameSession = new GameSession();
        contactManager = new ContactManager(main.world);
        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        scoreTextView = new TextView(main.commonWhiteFont, 50, 1215);
        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);
        fullBlackoutView = new ImageView(0, 0, GameResources.BLACKOUT_FULL_IMG_PATH);
        pauseTextView = new TextView(main.commonWhiteFont, 500, 1000, "Pause");
        homeButton = new ButtonView(10, 10, 160, 70, main.commonWhiteFont, GameResources.BUTTON_BACKGROUND_SHORT_IMG_PATH, "Home");
        continueButton = new ButtonView(10, 90, 160, 70, main.commonWhiteFont, GameResources.BUTTON_BACKGROUND_SHORT_IMG_PATH, "Continue");
    }

    @Override
    public void show() {
        super.show();

        restartGame();
    }

    @Override
    public void render(float delta) {

        handleInput();

        if (gameSession.state == GameState.PLAYING) {
            if (gameSession.shouldSpawnTrash()) {
                TrashObject trashObject = new TrashObject(
                    GameSettings.TRASH_WIDTH, GameSettings.TRASH_HEIGHT,
                    GameResources.TRASH_IMG_PATH,
                    main.world
                );
                trashArray.add(trashObject);
            }

            if (shipObject.needToShoot()) {
                BulletObject laserBullet = new BulletObject(
                    shipObject.getX(), shipObject.getY() + shipObject.height / 2,
                    GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                    GameResources.BULLET_IMG_PATH,
                    main.world
                );
                bulletArray.add(laserBullet);
                if (main.audioManager.isSoundOn) main.audioManager.shootSound.play();            }

            if (!shipObject.isAlive()) {
                System.out.println("Game over!");
            }

            updateTrash();
            updateBullets();
            backgroundView.move();
            scoreTextView.setText("Score: " + 100);
            liveView.setLeftLives(shipObject.getLivesLeft());

            main.stepWorld();
        }

        draw();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            main.touch = main.orthographicCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            switch (gameSession.state) {
                case PLAYING:
                    if (pauseButton.isHit(main.touch.x, main.touch.y)) {
                        gameSession.pauseGame();
                    }
                    shipObject.move(main.touch);
                    break;

                case PAUSED:
                    if (continueButton.isHit(main.touch.x, main.touch.y)) {
                        gameSession.resumeGame();
                    }
                    if (homeButton.isHit(main.touch.x, main.touch.y)) {
                        main.setScreen(main.menuScreen);
                    }
                    break;
            }
        }
    }

    private void draw() {
        main.orthographicCamera.update();
        main.batch.setProjectionMatrix(main.orthographicCamera.combined);
        ScreenUtils.clear(Color.CLEAR);

        main.batch.begin();
        backgroundView.draw(main.batch);
        shipObject.draw(main.batch);
        for (TrashObject trash : trashArray) trash.draw(main.batch);
        for (BulletObject bullet : bulletArray) bullet.draw(main.batch);
        topBlackoutView.draw(main.batch);
        scoreTextView.draw(main.batch);
        liveView.draw(main.batch);
        pauseButton.draw(main.batch);
        if (gameSession.state == GameState.PAUSED) {
            fullBlackoutView.draw(main.batch);
            pauseTextView.draw(main.batch);
            homeButton.draw(main.batch);
            continueButton.draw(main.batch);
        }
        main.batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();

        shipObject.dispose();
    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {

            boolean hasToBeDestroyed = !trashArray.get(i).isAlive() || !trashArray.get(i).isInFrame();

            if (!trashArray.get(i).isAlive()) {
                if (main.audioManager.isSoundOn) main.audioManager.explosionSound.play(0.2f);            }

            if (hasToBeDestroyed) {
                main.world.destroyBody(trashArray.get(i).body);
                trashArray.remove(i--);
            }
        }
    }

    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                main.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }

    private void restartGame() {

        for (int i = 0; i < trashArray.size(); i++) {
            main.world.destroyBody(trashArray.get(i).body);
            trashArray.remove(i--);
        }

        if (shipObject != null) {
            main.world.destroyBody(shipObject.body);
        }

        bulletArray.clear();

        shipObject = new ShipObject(
            GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATH,
            main.world
        );

        gameSession.startGame();
    }
}
