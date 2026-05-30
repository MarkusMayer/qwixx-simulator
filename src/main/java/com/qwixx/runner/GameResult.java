package com.qwixx.runner;

import com.qwixx.model.GameState;
import com.qwixx.model.RowColor;

import java.util.Set;

public record GameResult(
    int score0,
    int score1,
    int winner,           // 0, 1, or -1 for tie
    int turnCount,
    Set<RowColor> lockedColors,
    String endReason      // "2_ROWS_LOCKED" or "PENALTY_LIMIT"
) {
    public static GameResult from(GameState finalState) {
        int s0 = finalState.boards().get(0).score();
        int s1 = finalState.boards().get(1).score();
        int winner = s0 > s1 ? 0 : s1 > s0 ? 1 : -1;
        String reason = finalState.boards().stream().anyMatch(b -> b.penalties() >= 4)
            ? "PENALTY_LIMIT" : "2_ROWS_LOCKED";
        return new GameResult(s0, s1, winner, finalState.turnNumber() - 1,
            finalState.lockedColors(), reason);
    }
}
