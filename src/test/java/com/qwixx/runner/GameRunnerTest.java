package com.qwixx.runner;

import com.qwixx.model.GameState;
import com.qwixx.model.RowColor;
import com.qwixx.strategy.GreedyStrategy;
import com.qwixx.strategy.RandomStrategy;
import com.qwixx.strategy.Strategy;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameRunnerTest {

    @Test
    void gameAlwaysTerminates() {
        Strategy s0 = new RandomStrategy(new Random(0));
        Strategy s1 = new RandomStrategy(new Random(1));
        GameRunner runner = new GameRunner(s0, s1, new Random(42));
        GameState result = runner.runGame();
        assertTrue(result.isTerminal());
    }

    @Test
    void terminalStateHasTwoLockedRowsOrFourPenalties() {
        for (int seed = 0; seed < 50; seed++) {
            GameRunner runner = new GameRunner(
                new RandomStrategy(new Random(seed)),
                new RandomStrategy(new Random(seed + 1000)),
                new Random(seed + 2000)
            );
            GameState state = runner.runGame();
            boolean twoLocked = state.lockedColors().size() >= 2;
            boolean anyEliminated = state.boards().stream().anyMatch(b -> b.penalties() >= 4);
            assertTrue(twoLocked || anyEliminated,
                "Game ended in invalid state: locked=" + state.lockedColors().size()
                + " penalties=" + state.boards().stream().mapToInt(b -> b.penalties()).sum());
        }
    }

    @Test
    void greedyVsRandomTerminates() {
        GameRunner runner = new GameRunner(
            new GreedyStrategy(),
            new RandomStrategy(new Random(99)),
            new Random(77)
        );
        GameState result = runner.runGame();
        assertTrue(result.isTerminal());
    }

    @Test
    void penaltyAppliedWhenActivePlayerPassesBothPhases() {
        // Use a strategy that always passes — active player must get a penalty
        Strategy alwaysPass = new Strategy() {
            @Override
            public com.qwixx.model.action.PlayerAction decideWhitePhase(com.qwixx.model.StateView view) {
                return new com.qwixx.model.action.Pass();
            }
            @Override
            public com.qwixx.model.action.PlayerAction decideColorPhase(com.qwixx.model.StateView view) {
                return new com.qwixx.model.action.Pass();
            }
        };
        GameRunner runner = new GameRunner(alwaysPass, alwaysPass, new Random(1));
        GameState state = runner.runGame();
        // Game must end with a player at 4 penalties (since both always pass)
        assertTrue(state.boards().stream().anyMatch(b -> b.penalties() >= 4));
    }

    @Test
    void lockedColorMeansAtLeastOnePlayerLockedThatRow() {
        // A color in lockedColors means some player locked it (their board shows locked=true).
        // The OTHER player's row may or may not be locked — they don't get a free lock bonus.
        for (int i = 0; i < 20; i++) {
            GameRunner r = new GameRunner(
                new GreedyStrategy(),
                new RandomStrategy(new Random(i)),
                new Random(i * 37)
            );
            GameState final_ = r.runGame();
            for (RowColor color : final_.lockedColors()) {
                boolean atLeastOneLocked =
                    final_.boards().get(0).row(color).locked() ||
                    final_.boards().get(1).row(color).locked();
                assertTrue(atLeastOneLocked,
                    "Neither player's board shows locked for " + color);
            }
        }
    }
}
