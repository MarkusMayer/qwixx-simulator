package com.qwixx.model;

import com.qwixx.model.action.MarkColorDice;
import com.qwixx.model.action.MarkWhiteDice;

import java.util.List;
import java.util.Set;

public interface StateView {
    int myPlayerIndex();
    Board myBoard();
    Board opponentBoard();
    DiceRoll currentRoll();
    Set<RowColor> lockedColors();
    Set<RowColor> activeColors();
    int turnNumber();
    boolean iAmActivePlayer();

    List<MarkWhiteDice> legalWhiteMarks();
    List<MarkColorDice> legalColorMarks();
}
