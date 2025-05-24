package ru.innovationcampus.spacecleaner;

import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

import ru.innovationcampus.spacecleaner.managers.MemoryManager;

public class GameSession {
    long nextTrashSpawnTime;
    long sessionStartTime;
    long sessionPauseTime;
    public GameState state;
    private int score;
    int destructedTrashNumber;

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

    public void destructionRegistration() {
        destructedTrashNumber += 1;
    }

    public void updateScore() {
        score = (int) (TimeUtils.millis() - sessionStartTime) / 100 + destructedTrashNumber * 100;
    }

    public int getScore() {
        return score;
    }

    public void endGame() {
        updateScore();
        state = GameState.ENDED;
        ArrayList<Integer> recordsTable = MemoryManager.loadRecordsTable();
        if (recordsTable == null) {
            recordsTable = new ArrayList<>();
        }
        int foundIdx = 0;
        for (; foundIdx < recordsTable.size(); foundIdx++) {
            if (recordsTable.get(foundIdx) < getScore()) break;
        }
        recordsTable.add(foundIdx, getScore());
        MemoryManager.saveTableOfRecords(recordsTable);
    }
}
