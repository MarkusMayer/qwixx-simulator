package com.qwixx.strategy;

import com.qwixx.model.StateView;
import com.qwixx.model.action.MarkColorDice;
import com.qwixx.model.action.MarkWhiteDice;
import com.qwixx.model.action.Pass;
import com.qwixx.model.action.PlayerAction;

import java.util.Comparator;
import java.util.List;

/**
 * Always marks when possible, preferring marks with the highest marginal score gain
 * (i.e., furthest along in the row, since each additional mark is worth more).
 */
public final class GreedyStrategy implements Strategy {

    @Override
    public PlayerAction decideWhitePhase(StateView view) {
        List<MarkWhiteDice> legal = view.legalWhiteMarks();
        if (legal.isEmpty()) return new Pass();
        return legal.stream()
            .max(Comparator.comparingInt(m -> marginalGain(view, m.color())))
            .orElseThrow();
    }

    @Override
    public PlayerAction decideColorPhase(StateView view) {
        List<MarkColorDice> legal = view.legalColorMarks();
        if (legal.isEmpty()) return new Pass();
        return legal.stream()
            .max(Comparator.comparingInt(m -> marginalGain(view, m.color())))
            .orElseThrow();
    }

    private int marginalGain(StateView view, com.qwixx.model.RowColor color) {
        int currentMarks = view.myBoard().row(color).markCount();
        return currentMarks + 1; // n-th mark is worth n points
    }
}
