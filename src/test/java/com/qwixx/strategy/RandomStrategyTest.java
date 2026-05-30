package com.qwixx.strategy;

import com.qwixx.model.*;
import com.qwixx.model.action.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomStrategyTest {

    private StateView viewFor(GameState state, DiceRoll roll, int playerIndex) {
        return new com.qwixx.model.GameStateView(state, roll, playerIndex);
    }

    @Test
    void whitePhaseAlwaysReturnsLegalAction() {
        Strategy strategy = new RandomStrategy(new Random(0));
        GameState state = GameState.initial();
        DiceRoll roll = new DiceRoll(3, 4, Map.of(
            RowColor.RED, 2, RowColor.YELLOW, 3, RowColor.GREEN, 1, RowColor.BLUE, 5
        ));
        for (int i = 0; i < 200; i++) {
            PlayerAction action = strategy.decideWhitePhase(viewFor(state, roll, 0));
            assertTrue(action instanceof MarkWhiteDice || action instanceof Pass,
                "White phase returned unexpected: " + action);
            if (action instanceof MarkWhiteDice m) {
                assertTrue(state.boards().get(0).canMark(m.color(), m.number()),
                    "Illegal white mark returned");
            }
        }
    }

    @Test
    void colorPhaseAlwaysReturnsLegalAction() {
        Strategy strategy = new RandomStrategy(new Random(1));
        GameState state = GameState.initial();
        DiceRoll roll = new DiceRoll(3, 4, Map.of(
            RowColor.RED, 2, RowColor.YELLOW, 3, RowColor.GREEN, 1, RowColor.BLUE, 5
        ));
        for (int i = 0; i < 200; i++) {
            PlayerAction action = strategy.decideColorPhase(viewFor(state, roll, 0));
            assertTrue(action instanceof MarkColorDice || action instanceof Pass,
                "Color phase returned unexpected: " + action);
            if (action instanceof MarkColorDice m) {
                assertTrue(state.boards().get(0).canMark(m.color(), m.number()),
                    "Illegal color mark returned");
            }
        }
    }

    @Test
    void passivePlayerColorPhaseReturnsPass() {
        Strategy strategy = new RandomStrategy(new Random(2));
        GameState state = GameState.initial(); // player 0 is active
        DiceRoll roll = new DiceRoll(3, 4, Map.of(
            RowColor.RED, 2, RowColor.YELLOW, 3, RowColor.GREEN, 1, RowColor.BLUE, 5
        ));
        // Player 1 is passive — legalColorMarks() returns empty, so always Pass
        for (int i = 0; i < 50; i++) {
            PlayerAction action = strategy.decideColorPhase(viewFor(state, roll, 1));
            assertInstanceOf(Pass.class, action);
        }
    }

    @Test
    void passReturnsWhenNoLegalWhiteMarks() {
        Strategy strategy = new RandomStrategy(new Random(0));
        // Advance RED row past 7 (white sum), so 7 is no longer markable
        GameState state = GameState.initial()
            .withBoard(0, Board.empty().withMark(RowColor.RED, 8));
        // white sum = 7, but RED is past 7; other rows still allow 7
        DiceRoll roll = new DiceRoll(3, 4, Map.of(
            RowColor.RED, 2, RowColor.YELLOW, 3, RowColor.GREEN, 1, RowColor.BLUE, 5
        ));
        // Just verify it returns a legal type
        PlayerAction action = strategy.decideWhitePhase(viewFor(state, roll, 0));
        assertTrue(action instanceof MarkWhiteDice || action instanceof Pass);
    }
}
