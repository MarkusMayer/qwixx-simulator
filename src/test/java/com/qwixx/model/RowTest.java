package com.qwixx.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RowTest {

    // --- isMarkable ---

    @Test
    void emptyRowCanMarkFirstNumber() {
        Row row = Row.empty(RowColor.RED);
        assertTrue(row.isMarkable(2));
    }

    @Test
    void emptyRowCanMarkAnyNumberInSequenceExceptLock() {
        Row row = Row.empty(RowColor.RED);
        assertTrue(row.isMarkable(7));
        assertFalse(row.isMarkable(12)); // lock number requires 5 prior marks
    }

    @Test
    void cannotMarkNumberNotInRow() {
        Row row = Row.empty(RowColor.RED);
        assertFalse(row.isMarkable(1));
        assertFalse(row.isMarkable(13));
    }

    @Test
    void cannotMarkNumberBeforeLastMark() {
        Row row = Row.empty(RowColor.RED).mark(6);
        assertFalse(row.isMarkable(2));
        assertFalse(row.isMarkable(5));
        assertFalse(row.isMarkable(6));
    }

    @Test
    void canMarkNumberAfterLastMark() {
        Row row = Row.empty(RowColor.RED).mark(6);
        assertTrue(row.isMarkable(7));
        assertFalse(row.isMarkable(12)); // only 1 mark, need 5 to lock
    }

    @Test
    void cannotMarkLockNumberWithFewerThanFiveMarks() {
        Row row = Row.empty(RowColor.RED);
        assertFalse(row.isMarkable(12));
        row = row.mark(2).mark(3).mark(4);
        assertFalse(row.isMarkable(12));
        row = row.mark(5);
        assertFalse(row.isMarkable(12)); // 4 marks, still not enough
    }

    @Test
    void canMarkLockNumberWithFiveMarks() {
        Row row = Row.empty(RowColor.RED)
            .mark(2).mark(3).mark(4).mark(5).mark(6);
        assertTrue(row.isMarkable(12));
    }

    @Test
    void lockedRowIsNotMarkable() {
        Row row = Row.empty(RowColor.RED)
            .mark(2).mark(3).mark(4).mark(5).mark(6).mark(12);
        assertTrue(row.locked());
        assertFalse(row.isMarkable(7));
        assertFalse(row.isMarkable(12));
    }

    @Test
    void descendingRowMarkingOrder() {
        Row row = Row.empty(RowColor.GREEN); // 12,11,...,2
        assertTrue(row.isMarkable(12));
        row = row.mark(12);
        assertFalse(row.isMarkable(12));
        assertTrue(row.isMarkable(11));
        assertTrue(row.isMarkable(7)); // can skip forward
    }

    @Test
    void descendingRowLockIsTwo() {
        Row row = Row.empty(RowColor.GREEN)
            .mark(12).mark(11).mark(10).mark(9).mark(8);
        assertTrue(row.isMarkable(2));
    }

    // --- mark ---

    @Test
    void markReturnsNewRow() {
        Row original = Row.empty(RowColor.RED);
        Row marked = original.mark(5);
        assertEquals(0, original.marks().size());
        assertEquals(1, marked.marks().size());
    }

    @Test
    void markingLockNumberLocksRow() {
        Row row = Row.empty(RowColor.RED)
            .mark(2).mark(3).mark(4).mark(5).mark(6).mark(12);
        assertTrue(row.locked());
    }

    @Test
    void markThrowsOnIllegalMove() {
        Row row = Row.empty(RowColor.RED).mark(6);
        assertThrows(IllegalArgumentException.class, () -> row.mark(5));
        assertThrows(IllegalArgumentException.class, () -> row.mark(12)); // not enough marks
    }

    // --- score ---

    @Test
    void emptyRowScoresZero() {
        assertEquals(0, Row.empty(RowColor.RED).score());
    }

    @Test
    void scoreFollowsTriangularFormula() {
        Row row = Row.empty(RowColor.RED);
        assertEquals(0, row.score());
        row = row.mark(2);  assertEquals(1, row.score());
        row = row.mark(3);  assertEquals(3, row.score());
        row = row.mark(4);  assertEquals(6, row.score());
        row = row.mark(5);  assertEquals(10, row.score());
        row = row.mark(6);  assertEquals(15, row.score());
    }

    @Test
    void lockMarkCountsAsExtraMark() {
        Row row = Row.empty(RowColor.RED)
            .mark(2).mark(3).mark(4).mark(5).mark(6).mark(12);
        // marks=[2,3,4,5,6,12] (6 items) + locked bonus = 7 marks, score = 7*8/2 = 28
        assertEquals(7, row.markCount());
        assertEquals(28, row.score());
    }

    @Test
    void fullRowMaxScore() {
        Row row = Row.empty(RowColor.RED)
            .mark(2).mark(3).mark(4).mark(5).mark(6)
            .mark(7).mark(8).mark(9).mark(10).mark(11).mark(12);
        // 10 marks + 1 lock = 11... wait: marking 12 after 11 marks = lock
        // Actually 11 marks in marks list + locked = markCount 12, score 78
        assertEquals(12, row.markCount());
        assertEquals(78, row.score());
    }
}
