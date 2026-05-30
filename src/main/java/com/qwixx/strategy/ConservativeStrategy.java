package com.qwixx.strategy;

import com.qwixx.model.RowColor;
import com.qwixx.model.Row;
import com.qwixx.model.StateView;
import com.qwixx.model.action.MarkColorDice;
import com.qwixx.model.action.MarkWhiteDice;
import com.qwixx.model.action.Pass;
import com.qwixx.model.action.PlayerAction;

import java.util.List;

/**
 * Only marks a number if it skips fewer than {@code maxSkip} positions in the row.
 * Avoids locking in early gaps that block higher-value numbers later.
 */
public final class ConservativeStrategy implements Strategy {

    private final int maxSkip;

    public ConservativeStrategy(int maxSkip) {
        this.maxSkip = maxSkip;
    }

    @Override
    public PlayerAction decideWhitePhase(StateView view) {
        List<MarkWhiteDice> legal = view.legalWhiteMarks();
        return legal.stream()
            .filter(m -> skipsAllowed(view, m.color(), m.number()))
            .findFirst()
            .map(PlayerAction.class::cast)
            .orElse(new Pass());
    }

    @Override
    public PlayerAction decideColorPhase(StateView view) {
        List<MarkColorDice> legal = view.legalColorMarks();
        return legal.stream()
            .filter(m -> skipsAllowed(view, m.color(), m.number()))
            .findFirst()
            .map(PlayerAction.class::cast)
            .orElse(new Pass());
    }

    private boolean skipsAllowed(StateView view, RowColor color, int number) {
        Row row = view.myBoard().row(color);
        List<Integer> numbers = row.numbers();
        int targetIdx = numbers.indexOf(number);
        int lastIdx = row.marks().isEmpty() ? -1
            : numbers.indexOf(row.marks().getLast());
        int skipped = targetIdx - lastIdx - 1;
        return skipped <= maxSkip;
    }

    @Override
    public String name() {
        return "ConservativeStrategy(maxSkip=" + maxSkip + ")";
    }
}
