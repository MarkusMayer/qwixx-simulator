package com.qwixx.strategy;

import com.qwixx.model.StateView;
import com.qwixx.model.action.MarkColorDice;
import com.qwixx.model.action.MarkWhiteDice;
import com.qwixx.model.action.Pass;
import com.qwixx.model.action.PlayerAction;

public interface Strategy {

    /**
     * Phase 1 decision — called for both active and passive players.
     * Must return a MarkWhiteDice or Pass.
     */
    PlayerAction decideWhitePhase(StateView view);

    /**
     * Phase 2 decision — called only for the active player.
     * Must return a MarkColorDice or Pass.
     * If both phases return Pass, the runner applies a penalty automatically.
     */
    PlayerAction decideColorPhase(StateView view);

    default String name() {
        return getClass().getSimpleName();
    }

    static void validateWhitePhaseAction(PlayerAction action) {
        if (!(action instanceof MarkWhiteDice || action instanceof Pass)) {
            throw new IllegalStateException(
                "White phase must return MarkWhiteDice or Pass, got: " + action.getClass().getSimpleName()
            );
        }
    }

    static void validateColorPhaseAction(PlayerAction action) {
        if (!(action instanceof MarkColorDice || action instanceof Pass)) {
            throw new IllegalStateException(
                "Color phase must return MarkColorDice or Pass, got: " + action.getClass().getSimpleName()
            );
        }
    }
}
