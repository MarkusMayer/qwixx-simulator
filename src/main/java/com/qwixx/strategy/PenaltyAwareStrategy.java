package com.qwixx.strategy;

import com.qwixx.model.RowColor;
import com.qwixx.model.StateView;
import com.qwixx.model.action.MarkColorDice;
import com.qwixx.model.action.MarkWhiteDice;
import com.qwixx.model.action.Pass;
import com.qwixx.model.action.PlayerAction;

import java.util.Comparator;
import java.util.List;

/**
 * As active player: always marks something when possible — a penalty (−5) is worse than any mark.
 * As passive player: only marks if the row already has momentum (≥2 existing marks), since
 * passive passing is free and a bad white mark can waste position.
 */
public final class PenaltyAwareStrategy implements Strategy {

    private static final int PASSIVE_MOMENTUM_THRESHOLD = 2;

    @Override
    public PlayerAction decideWhitePhase(StateView view) {
        List<MarkWhiteDice> legal = view.legalWhiteMarks();
        if (legal.isEmpty()) return new Pass();
        if (view.iAmActivePlayer()) {
            return bestWhiteMark(view, legal);
        } else {
            return legal.stream()
                .filter(m -> view.myBoard().row(m.color()).markCount() >= PASSIVE_MOMENTUM_THRESHOLD)
                .max(Comparator.comparingInt(m -> view.myBoard().row(m.color()).markCount()))
                .map(PlayerAction.class::cast)
                .orElse(new Pass());
        }
    }

    @Override
    public PlayerAction decideColorPhase(StateView view) {
        List<MarkColorDice> legal = view.legalColorMarks();
        if (legal.isEmpty()) return new Pass();
        return legal.stream()
            .max(Comparator.comparingInt(m -> view.myBoard().row(m.color()).markCount()))
            .orElseThrow();
    }

    private PlayerAction bestWhiteMark(StateView view, List<MarkWhiteDice> legal) {
        return legal.stream()
            .max(Comparator.comparingInt(m -> view.myBoard().row(m.color()).markCount()))
            .orElseThrow();
    }
}
