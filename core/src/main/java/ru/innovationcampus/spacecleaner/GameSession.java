package ru.innovationcampus.spacecleaner;

import com.badlogic.gdx.utils.TimeUtils;

public class GameSession {
    long nextTrashSpawnTime;
    long sessionStartTime;
    long sessionPauseTime;
    public GameState state;

    public void startGame() {
        sessionStartTime = TimeUtils.millis();
        nextTrashSpawnTime = sessionStartTime + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
            * getTrashPeriodCoolDown());
        state = GameState.PLAYING;
    }

    public void pauseGame() {
        sessionPauseTime = TimeUtils.millis();
        state = GameState.PAUSED;
    }

    public void resumeGame() {
        nextTrashSpawnTime = sessionStartTime - sessionPauseTime + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
            * getTrashPeriodCoolDown());
        state = GameState.PLAYING;
    }

    public boolean shouldSpawnTrash() {
        if (nextTrashSpawnTime <= TimeUtils.millis()) {
            nextTrashSpawnTime = TimeUtils.millis() + (long) (GameSettings.STARTING_TRASH_APPEARANCE_COOL_DOWN
                * getTrashPeriodCoolDown());
            return true;
        }
        return false;
    }

    private float getTrashPeriodCoolDown() {
        return (float) Math.exp(-0.001 * (TimeUtils.millis() - sessionStartTime) / 1000);
    }
}
