package com.qwixx.model.action;

public sealed interface PlayerAction permits MarkWhiteDice, MarkColorDice, TakePenalty, Pass {}
