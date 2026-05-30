package com.qwixx.model;

import org.junit.jupiter.api.Test;
import java.util.EnumSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void initialStateIsNotTerminal() {
        assertFalse(GameState.initial().isTerminal());
    }

    @Test
    void initialActivePlayerIsZero() {
        assertEquals(0, GameState.initial().activePlayerIndex());
    }

    @Test
    void initialTurnNumberIsOne() {
        assertEquals(1, GameState.initial().turnNumber());
    }

    @Test
    void initialHasAllColorsActive() {
        assertEquals(EnumSet.allOf(RowColor.class), GameState.initial().activeColors());
    }

    @Test
    void terminalWhenTwoRowsLocked() {
        GameState state = GameState.initial()
            .withLockedColor(RowColor.RED)
            .withLockedColor(RowColor.GREEN);
        assertTrue(state.isTerminal());
    }

    @Test
    void notTerminalWithOneLockedRow() {
        GameState state = GameState.initial().withLockedColor(RowColor.RED);
        assertFalse(state.isTerminal());
    }

    @Test
    void terminalWhenPlayerHasFourPenalties() {
        Board penalized = Board.empty()
            .withPenalty().withPenalty().withPenalty().withPenalty();
        GameState state = GameState.initial().withBoard(0, penalized);
        assertTrue(state.isTerminal());
    }

    @Test
    void notTerminalWithThreePenalties() {
        Board penalized = Board.empty()
            .withPenalty().withPenalty().withPenalty();
        GameState state = GameState.initial().withBoard(0, penalized);
        assertFalse(state.isTerminal());
    }

    @Test
    void activeColorsExcludesLockedColors() {
        GameState state = GameState.initial().withLockedColor(RowColor.RED);
        Set<RowColor> active = state.activeColors();
        assertFalse(active.contains(RowColor.RED));
        assertTrue(active.contains(RowColor.YELLOW));
        assertTrue(active.contains(RowColor.GREEN));
        assertTrue(active.contains(RowColor.BLUE));
    }

    @Test
    void nextTurnRotatesActivePlayer() {
        GameState state = GameState.initial();
        assertEquals(0, state.activePlayerIndex());
        assertEquals(1, state.nextTurn().activePlayerIndex());
        assertEquals(0, state.nextTurn().nextTurn().activePlayerIndex());
    }

    @Test
    void nextTurnIncrementsTurnNumber() {
        GameState state = GameState.initial();
        assertEquals(2, state.nextTurn().turnNumber());
    }

    @Test
    void withBoardReplacesCorrectPlayer() {
        Board modified = Board.empty().withPenalty();
        GameState state = GameState.initial().withBoard(1, modified);
        assertEquals(0, state.boards().get(0).penalties());
        assertEquals(1, state.boards().get(1).penalties());
    }

    @Test
    void activeBoardAndPassiveBoardReflectActivePlayer() {
        Board p0 = Board.empty().withPenalty();
        Board p1 = Board.empty().withPenalty().withPenalty();
        GameState state = new GameState(java.util.List.of(p0, p1), 0, Set.of(), 1);
        assertEquals(1, state.activeBoard().penalties());
        assertEquals(2, state.passiveBoard().penalties());
    }
}
