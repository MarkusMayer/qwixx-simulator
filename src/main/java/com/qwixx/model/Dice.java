package com.qwixx.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class Dice {

    private Dice() {}

    public static DiceRoll roll(Set<RowColor> activeColors, Random rng) {
        int white1 = rollDie(rng);
        int white2 = rollDie(rng);
        Map<RowColor, Integer> colored = new EnumMap<>(RowColor.class);
        for (RowColor color : activeColors) {
            colored.put(color, rollDie(rng));
        }
        return new DiceRoll(white1, white2, colored);
    }

    private static int rollDie(Random rng) {
        return rng.nextInt(6) + 1;
    }
}
