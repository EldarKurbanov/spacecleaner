package ru.innovationcampus.spacecleaner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import ru.innovationcampus.spacecleaner.ContactManager;
import ru.innovationcampus.spacecleaner.GameResources;
import ru.innovationcampus.spacecleaner.GameSession;
import ru.innovationcampus.spacecleaner.GameSettings;
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

    public GameScreen(Main main) {
        this.main = main;
        trashArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        contactManager = new ContactManager(main.world);
        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        topBlackoutView = new ImageView(0, 1180, GameResources.BLACKOUT_TOP_IMG_PATH);
        liveView = new LiveView(305, 1215);
        scoreTextView = new TextView(main.commonWhiteFont, 50, 1215);
        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);
    }

    @Override
    public void show() {
        super.show();

        gameSession = new GameSession();
        gameSession.startGame();

        shipObject = new ShipObject(GameSettings.SCREEN_WIDTH / 2, 150,
            GameSettings.SHIP_WIDTH, GameSettings.SHIP_HEIGHT,
            GameResources.SHIP_IMG_PATH, main.world);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        scoreTextView.setText("Score: " + 100);

        liveView.setLeftLives(shipObject.getLivesLeft());

        backgroundView.move();

        main.stepWorld();

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
        }

        handleInput();
        updateTrash();
        updateBullets();

        if (!shipObject.isAlive()) {
            System.out.println("Game over!");
        }

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
        backgroundView.draw(main.batch);
        shipObject.draw(main.batch);
        for (TrashObject trash : trashArray) trash.draw(main.batch);
        for (BulletObject bullet : bulletArray) bullet.draw(main.batch);
        topBlackoutView.draw(main.batch);
        scoreTextView.draw(main.batch);
        liveView.draw(main.batch);
        pauseButton.draw(main.batch);
        main.batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();

        shipObject.dispose();
    }

    private void updateTrash() {
        for (int i = 0; i < trashArray.size(); i++) {
            if (!trashArray.get(i).isInFrame() || !trashArray.get(i).isAlive()) {
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
}
