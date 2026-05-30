package com.qwixx.model;

import java.util.Map;
import java.util.Set;

public record DiceRoll(int white1, int white2, Map<RowColor, Integer> coloredValues) {

    public DiceRoll {
        coloredValues = Map.copyOf(coloredValues);
    }

    public int whiteSum() {
        return white1 + white2;
    }

    public boolean hasColor(RowColor color) {
        return coloredValues.containsKey(color);
    }

    public Set<Integer> colorSums(RowColor color) {
        Integer cv = coloredValues.get(color);
        if (cv == null) return Set.of();
        int s1 = white1 + cv;
        int s2 = white2 + cv;
        return s1 == s2 ? Set.of(s1) : Set.of(s1, s2);
    }
}
