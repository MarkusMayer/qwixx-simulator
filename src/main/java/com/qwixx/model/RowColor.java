package com.qwixx.model;

import java.util.List;

public enum RowColor {
    RED, YELLOW, GREEN, BLUE;

    public Direction direction() {
        return (this == RED || this == YELLOW) ? Direction.ASCENDING : Direction.DESCENDING;
    }

    public List<Integer> numbers() {
        return switch (direction()) {
            case ASCENDING  -> List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
            case DESCENDING -> List.of(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2);
        };
    }

    public int lockNumber() {
        return direction() == Direction.ASCENDING ? 12 : 2;
    }
}
