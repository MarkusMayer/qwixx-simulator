package com.qwixx.model;

import org.junit.jupiter.api.Test;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class DiceRollTest {

    @Test
    void whiteSumAddsBothDice() {
        DiceRoll roll = new DiceRoll(3, 4, java.util.Map.of());
        assertEquals(7, roll.whiteSum());
    }

    @Test
    void colorSumsReturnsBothCombinations() {
        DiceRoll roll = new DiceRoll(2, 5, java.util.Map.of(RowColor.RED, 3));
        Set<Integer> sums = roll.colorSums(RowColor.RED);
        assertTrue(sums.contains(5));  // 2+3
        assertTrue(sums.contains(8));  // 5+3
    }

    @Test
    void colorSumsReturnsSingleValueWhenBothWhiteDiceEqual() {
        DiceRoll roll = new DiceRoll(3, 3, java.util.Map.of(RowColor.RED, 4));
        Set<Integer> sums = roll.colorSums(RowColor.RED);
        assertEquals(1, sums.size());
        assertTrue(sums.contains(7));
    }

    @Test
    void colorSumsEmptyForLockedColor() {
        DiceRoll roll = new DiceRoll(3, 4, java.util.Map.of());
        assertTrue(roll.colorSums(RowColor.RED).isEmpty());
    }

    @Test
    void hasColorTrueForActiveColor() {
        DiceRoll roll = new DiceRoll(1, 2, java.util.Map.of(RowColor.BLUE, 5));
        assertTrue(roll.hasColor(RowColor.BLUE));
        assertFalse(roll.hasColor(RowColor.RED));
    }

    @Test
    void diceRollProducesValuesInRange() {
        Random rng = new Random(42);
        for (int i = 0; i < 1000; i++) {
            DiceRoll roll = Dice.roll(EnumSet.allOf(RowColor.class), rng);
            assertTrue(roll.white1() >= 1 && roll.white1() <= 6);
            assertTrue(roll.white2() >= 1 && roll.white2() <= 6);
            for (RowColor color : RowColor.values()) {
                int cv = roll.coloredValues().get(color);
                assertTrue(cv >= 1 && cv <= 6);
            }
        }
    }

    @Test
    void diceRollOmitsLockedColors() {
        Random rng = new Random(0);
        Set<RowColor> active = EnumSet.of(RowColor.RED, RowColor.YELLOW);
        DiceRoll roll = Dice.roll(active, rng);
        assertTrue(roll.hasColor(RowColor.RED));
        assertTrue(roll.hasColor(RowColor.YELLOW));
        assertFalse(roll.hasColor(RowColor.GREEN));
        assertFalse(roll.hasColor(RowColor.BLUE));
    }
}
