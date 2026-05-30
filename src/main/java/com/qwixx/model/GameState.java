package com.qwixx.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record GameState(
    List<Board> boards,
    int activePlayerIndex,
    Set<RowColor> lockedColors,
    int turnNumber
) {
    public GameState {
        boards = List.copyOf(boards);
        lockedColors = Set.copyOf(lockedColors);
    }

    public static GameState initial() {
        return new GameState(
            List.of(Board.empty(), Board.empty()),
            0,
            Set.of(),
            1
        );
    }

    public Board activeBoard() {
        return boards.get(activePlayerIndex);
    }

    public Board passiveBoard() {
        return boards.get(1 - activePlayerIndex);
    }

    public Set<RowColor> activeColors() {
        if (lockedColors.isEmpty()) return EnumSet.allOf(RowColor.class);
        EnumSet<RowColor> active = EnumSet.allOf(RowColor.class);
        active.removeAll(lockedColors);
        return active;
    }

    public boolean isTerminal() {
        return lockedColors.size() >= 2
            || boards.stream().anyMatch(Board::hasFourPenalties);
    }

    public GameState withBoard(int playerIndex, Board board) {
        List<Board> updated = playerIndex == 0
            ? List.of(board, boards.get(1))
            : List.of(boards.get(0), board);
        return new GameState(updated, activePlayerIndex, lockedColors, turnNumber);
    }

    public GameState withLockedColor(RowColor color) {
        Set<RowColor> updated = EnumSet.copyOf(lockedColors.isEmpty()
            ? EnumSet.noneOf(RowColor.class)
            : EnumSet.copyOf(lockedColors));
        ((EnumSet<RowColor>) updated).add(color);
        return new GameState(boards, activePlayerIndex, updated, turnNumber);
    }

    public GameState nextTurn() {
        return new GameState(boards, 1 - activePlayerIndex, lockedColors, turnNumber + 1);
    }
}
