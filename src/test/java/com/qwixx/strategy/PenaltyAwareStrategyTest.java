package com.qwixx.strategy;

import com.qwixx.model.*;
import com.qwixx.model.action.*;
import com.qwixx.runner.GameRunner;
import com.qwixx.model.GameStateView;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PenaltyAwareStrategyTest {

    private final PenaltyAwareStrategy strategy = new PenaltyAwareStrategy();

    // white1=3, white2=4 → whiteSum=7; colored values are per-die (not sums)
    private static DiceRoll roll7() {
        return new DiceRoll(3, 4, Map.of(
            RowColor.RED, 2,
            RowColor.YELLOW, 3,
            RowColor.GREEN, 1,
            RowColor.BLUE, 2
        ));
    }

    // white1=2, white2=3 → whiteSum=5
    private static DiceRoll roll5() {
        return new DiceRoll(2, 3, Map.of(
            RowColor.RED, 2,    // colorSums RED: 2+2=4, 3+2=5
            RowColor.YELLOW, 3, // colorSums YELLOW: 2+3=5, 3+3=6
            RowColor.GREEN, 2,
            RowColor.BLUE, 1
        ));
    }

    // --- Active player: always marks when legal marks exist ---

    @Test
    void activePlayerMarksWhenLegalMarkExists() {
        GameState state = GameState.initial(); // player 0 is active, all rows empty
        StateView view = new GameStateView(state, roll7(), 0);

        assertTrue(view.iAmActivePlayer());
        // whiteSum=7 is legal on any empty row (≥1 mark required only for lock)
        PlayerAction action = strategy.decideWhitePhase(view);
        assertInstanceOf(MarkWhiteDice.class, action);
    }

    @Test
    void activePlayerPassesWhenNoLegalMarksExist() {
        GameState state = GameState.initial()
            .withLockedColor(RowColor.RED)
            .withLockedColor(RowColor.YELLOW)
            .withLockedColor(RowColor.GREEN)
            .withLockedColor(RowColor.BLUE);
        StateView view = new GameStateView(state, roll7(), 0);

        PlayerAction action = strategy.decideWhitePhase(view);
        assertInstanceOf(Pass.class, action);
    }

    // --- Passive player: only marks rows with momentum ---

    @Test
    void passivePlayerPassesOnRowWithNoMomentum() {
        GameState state = GameState.initial(); // all rows empty, player 1 is passive
        StateView view = new GameStateView(state, roll7(), 1);

        assertFalse(view.iAmActivePlayer());
        PlayerAction action = strategy.decideWhitePhase(view);
        assertInstanceOf(Pass.class, action);
    }

    @Test
    void passivePlayerMarksRowWithSufficientMomentum() {
        // Give passive player (index 1) 3 marks in RED → markCount=3 ≥ threshold of 2
        GameState base = GameState.initial();
        Board boardWithMomentum = base.boards().get(1)
            .withMark(RowColor.RED, 2)
            .withMark(RowColor.RED, 3)
            .withMark(RowColor.RED, 4);
        GameState state = base.withBoard(1, boardWithMomentum);

        // whiteSum=5 is legal in RED (next after 4)
        StateView view = new GameStateView(state, roll5(), 1);

        assertFalse(view.iAmActivePlayer());
        PlayerAction action = strategy.decideWhitePhase(view);
        assertInstanceOf(MarkWhiteDice.class, action);
        assertEquals(RowColor.RED, ((MarkWhiteDice) action).color());
    }

    // --- Color phase: active player picks row with most marks ---

    @Test
    void colorPhasePicksMostAdvancedRow() {
        // Give active player (index 0) 2 marks in RED, 0 in YELLOW
        GameState base = GameState.initial();
        Board board = base.boards().get(0)
            .withMark(RowColor.RED, 2)
            .withMark(RowColor.RED, 3);
        GameState state = base.withBoard(0, board);

        // roll5: colorSums RED = {4,5}, colorSums YELLOW = {5,6}
        // RED next after 3 is 4 → legal; YELLOW next is 2 → 5 legal
        StateView view = new GameStateView(state, roll5(), 0);

        assertTrue(view.iAmActivePlayer());
        PlayerAction action = strategy.decideColorPhase(view);
        assertInstanceOf(MarkColorDice.class, action);
        assertEquals(RowColor.RED, ((MarkColorDice) action).color());
    }

    // --- Integration: games always terminate without exceptions ---

    @Test
    void penaltyAwareVsGreedyTerminates() {
        GameRunner runner = new GameRunner(
            new PenaltyAwareStrategy(),
            new GreedyStrategy(),
            new Random(55)
        );
        assertTrue(runner.runGame().isTerminal());
    }

    @Test
    void penaltyAwareVsRandomTerminatesAcrossSeeds() {
        for (int seed = 0; seed < 30; seed++) {
            GameRunner runner = new GameRunner(
                new PenaltyAwareStrategy(),
                new RandomStrategy(new Random(seed)),
                new Random(seed * 13)
            );
            assertTrue(runner.runGame().isTerminal(), "Game did not terminate for seed " + seed);
        }
    }
}
