package com.qwixx.model.action;

import com.qwixx.model.RowColor;

public record MarkColorDice(RowColor color, int number) implements PlayerAction {}
