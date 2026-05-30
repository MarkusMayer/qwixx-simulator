package com.qwixx.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record Board(Map<RowColor, Row> rows, int penalties) {

    public Board {
        rows = Map.copyOf(rows);
    }

    public static Board empty() {
        Map<RowColor, Row> r = new EnumMap<>(RowColor.class);
        for (RowColor color : RowColor.values()) {
            r.put(color, Row.empty(color));
        }
        return new Board(r, 0);
    }

    public Row row(RowColor color) {
        return rows.get(color);
    }

    public boolean canMark(RowColor color, int number) {
        return rows.get(color).isMarkable(number);
    }

    public Board withMark(RowColor color, int number) {
        Map<RowColor, Row> updated = new EnumMap<>(rows);
        updated.put(color, rows.get(color).mark(number));
        return new Board(updated, penalties);
    }

    public Board withPenalty() {
        return new Board(rows, penalties + 1);
    }

    public boolean hasFourPenalties() {
        return penalties >= 4;
    }

    public Set<RowColor> lockedRows() {
        return rows.entrySet().stream()
            .filter(e -> e.getValue().locked())
            .map(Map.Entry::getKey)
            .collect(Collectors.toUnmodifiableSet());
    }

    public int score() {
        return rows.values().stream().mapToInt(Row::score).sum() - penalties * 5;
    }
}
