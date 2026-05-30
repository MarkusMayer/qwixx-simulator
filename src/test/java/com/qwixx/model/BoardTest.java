package com.qwixx.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void emptyBoardHasZeroScore() {
        assertEquals(0, Board.empty().score());
    }

    @Test
    void emptyBoardHasNoPenalties() {
        assertEquals(0, Board.empty().penalties());
    }

    @Test
    void emptyBoardHasNoLockedRows() {
        assertTrue(Board.empty().lockedRows().isEmpty());
    }

    @Test
    void withMarkDelegatesToRow() {
        Board board = Board.empty().withMark(RowColor.RED, 5);
        assertTrue(board.row(RowColor.RED).marks().contains(5));
    }

    @Test
    void withMarkDoesNotMutateOriginal() {
        Board original = Board.empty();
        Board updated = original.withMark(RowColor.RED, 5);
        assertTrue(original.row(RowColor.RED).marks().isEmpty());
        assertFalse(updated.row(RowColor.RED).marks().isEmpty());
    }

    @Test
    void withMarkOnOtherRowsUnchanged() {
        Board board = Board.empty().withMark(RowColor.RED, 5);
        assertTrue(board.row(RowColor.YELLOW).marks().isEmpty());
        assertTrue(board.row(RowColor.GREEN).marks().isEmpty());
        assertTrue(board.row(RowColor.BLUE).marks().isEmpty());
    }

    @Test
    void withPenaltyIncreasesPenaltyCount() {
        Board board = Board.empty().withPenalty().withPenalty();
        assertEquals(2, board.penalties());
    }

    @Test
    void hasFourPenaltiesReturnsTrueAtFour() {
        Board board = Board.empty()
            .withPenalty().withPenalty().withPenalty().withPenalty();
        assertTrue(board.hasFourPenalties());
    }

    @Test
    void hasFourPenaltiesReturnsFalseAtThree() {
        Board board = Board.empty()
            .withPenalty().withPenalty().withPenalty();
        assertFalse(board.hasFourPenalties());
    }

    @Test
    void scoreAggregatesAllRows() {
        Board board = Board.empty()
            .withMark(RowColor.RED, 5)
            .withMark(RowColor.YELLOW, 7);
        // 1 mark per row = 1 point each = 2 total
        assertEquals(2, board.score());
    }

    @Test
    void penaltiesReduceScore() {
        Board board = Board.empty()
            .withMark(RowColor.RED, 5)      // 1 point
            .withPenalty().withPenalty();   // -10
        assertEquals(-9, board.score());
    }

    @Test
    void lockedRowsReflectsLockedState() {
        Board board = Board.empty()
            .withMark(RowColor.RED, 2)
            .withMark(RowColor.RED, 3)
            .withMark(RowColor.RED, 4)
            .withMark(RowColor.RED, 5)
            .withMark(RowColor.RED, 6)
            .withMark(RowColor.RED, 12);
        assertTrue(board.lockedRows().contains(RowColor.RED));
        assertFalse(board.lockedRows().contains(RowColor.YELLOW));
    }

    @Test
    void canMarkDelegatesToRow() {
        Board board = Board.empty();
        assertTrue(board.canMark(RowColor.RED, 5));
        assertFalse(board.canMark(RowColor.RED, 12)); // lock needs 5 marks
    }
}
