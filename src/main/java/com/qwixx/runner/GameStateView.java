package com.qwixx.runner;

import com.qwixx.model.*;
import com.qwixx.model.action.MarkColorDice;
import com.qwixx.model.action.MarkWhiteDice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class GameStateView implements StateView {

    private final GameState state;
    private final DiceRoll roll;
    private final int playerIndex;

    public GameStateView(GameState state, DiceRoll roll, int playerIndex) {
        this.state = state;
        this.roll = roll;
        this.playerIndex = playerIndex;
    }

    @Override public int myPlayerIndex() { return playerIndex; }
    @Override public Board myBoard() { return state.boards().get(playerIndex); }
    @Override public Board opponentBoard() { return state.boards().get(1 - playerIndex); }
    @Override public DiceRoll currentRoll() { return roll; }
    @Override public Set<RowColor> lockedColors() { return state.lockedColors(); }
    @Override public Set<RowColor> activeColors() { return state.activeColors(); }
    @Override public int turnNumber() { return state.turnNumber(); }
    @Override public boolean iAmActivePlayer() { return playerIndex == state.activePlayerIndex(); }

    @Override
    public List<MarkWhiteDice> legalWhiteMarks() {
        Board board = myBoard();
        int sum = roll.whiteSum();
        List<MarkWhiteDice> result = new ArrayList<>();
        for (RowColor color : RowColor.values()) {
            if (!state.lockedColors().contains(color) && board.canMark(color, sum)) {
                result.add(new MarkWhiteDice(color, sum));
            }
        }
        return result;
    }

    @Override
    public List<MarkColorDice> legalColorMarks() {
        if (!iAmActivePlayer()) return List.of();
        Board board = myBoard();
        List<MarkColorDice> result = new ArrayList<>();
        for (RowColor color : state.activeColors()) {
            for (int sum : roll.colorSums(color)) {
                if (board.canMark(color, sum)) {
                    result.add(new MarkColorDice(color, sum));
                }
            }
        }
        return result;
    }
}
