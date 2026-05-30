package com.qwixx.model.action;

import com.qwixx.model.RowColor;

public record MarkWhiteDice(RowColor color, int number) implements PlayerAction {}
