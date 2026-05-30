package com.qwixx.strategy;

import com.qwixx.model.StateView;
import com.qwixx.model.action.MarkColorDice;
import com.qwixx.model.action.MarkWhiteDice;
import com.qwixx.model.action.Pass;
import com.qwixx.model.action.PlayerAction;

import java.util.List;
import java.util.Random;

public final class RandomStrategy implements Strategy {

    private final Random rng;

    public RandomStrategy(Random rng) {
        this.rng = rng;
    }

    @Override
    public PlayerAction decideWhitePhase(StateView view) {
        List<MarkWhiteDice> legal = view.legalWhiteMarks();
        if (legal.isEmpty()) return new Pass();
        return rng.nextBoolean() ? legal.get(rng.nextInt(legal.size())) : new Pass();
    }

    @Override
    public PlayerAction decideColorPhase(StateView view) {
        List<MarkColorDice> legal = view.legalColorMarks();
        if (legal.isEmpty()) return new Pass();
        return rng.nextBoolean() ? legal.get(rng.nextInt(legal.size())) : new Pass();
    }
}
