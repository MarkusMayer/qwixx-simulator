package com.qwixx.model;

import java.util.ArrayList;
import java.util.List;

public record Row(RowColor color, List<Integer> marks, boolean locked) {

    public Row {
        marks = List.copyOf(marks);
    }

    public static Row empty(RowColor color) {
        return new Row(color, List.of(), false);
    }

    public List<Integer> numbers() {
        return color.numbers();
    }

    public int markCount() {
        return marks.size() + (locked ? 1 : 0);
    }

    public int score() {
        int n = markCount();
        return n * (n + 1) / 2;
    }

    public boolean isMarkable(int number) {
        if (locked) return false;
        List<Integer> nums = numbers();
        int idx = nums.indexOf(number);
        if (idx < 0) return false;
        int lastIdx = lastMarkIndex();
        if (idx <= lastIdx) return false;
        if (number == color.lockNumber() && marks.size() < 5) return false;
        return true;
    }

    public Row mark(int number) {
        if (!isMarkable(number)) {
            throw new IllegalArgumentException(
                "Cannot mark " + number + " in " + color + " row (marks=" + marks + ", locked=" + locked + ")"
            );
        }
        List<Integer> newMarks = new ArrayList<>(marks);
        newMarks.add(number);
        boolean nowLocked = (number == color.lockNumber());
        return new Row(color, newMarks, nowLocked);
    }

    private int lastMarkIndex() {
        if (marks.isEmpty()) return -1;
        List<Integer> nums = numbers();
        int last = -1;
        for (int m : marks) {
            int i = nums.indexOf(m);
            if (i > last) last = i;
        }
        return last;
    }
}
