package com.qwixx.runner;

import com.qwixx.model.*;
import com.qwixx.model.action.*;
import com.qwixx.strategy.Strategy;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

public final class GameRunner {

    private final Strategy strategy0;
    private final Strategy strategy1;
    private final Random rng;

    public GameRunner(Strategy strategy0, Strategy strategy1, Random rng) {
        this.strategy0 = strategy0;
        this.strategy1 = strategy1;
        this.rng = rng;
    }

    public GameState runGame() {
        GameState state = GameState.initial();
        while (!state.isTerminal()) {
            state = runTurn(state);
        }
        return state;
    }

    private GameState runTurn(GameState state) {
        DiceRoll roll = Dice.roll(state.activeColors(), rng);
        int active = state.activePlayerIndex();
        int passive = 1 - active;

        // --- Phase 1: white dice — both players decide ---
        boolean activeMarked = false;
        for (int i = 0; i < 2; i++) {
            StateView view = new GameStateView(state, roll, i);
            PlayerAction action = strategyFor(i).decideWhitePhase(view);
            Strategy.validateWhitePhaseAction(action);
            if (action instanceof MarkWhiteDice m) {
                if (!state.boards().get(i).canMark(m.color(), m.number())) {
                    throw new IllegalStateException("Strategy returned illegal white mark: " + m);
                }
                Board updated = state.boards().get(i).withMark(m.color(), m.number());
                state = state.withBoard(i, updated);
                state = propagateLock(state, m.color(), i);
                if (i == active) activeMarked = true;
            }
            if (state.isTerminal()) return state.nextTurn();
        }

        // --- Phase 2: color dice — active player only ---
        StateView colorView = new GameStateView(state, roll, active);
        PlayerAction colorAction = strategyFor(active).decideColorPhase(colorView);
        Strategy.validateColorPhaseAction(colorAction);

        if (colorAction instanceof MarkColorDice m) {
            if (!state.boards().get(active).canMark(m.color(), m.number())) {
                throw new IllegalStateException("Strategy returned illegal color mark: " + m);
            }
            Board updated = state.boards().get(active).withMark(m.color(), m.number());
            state = state.withBoard(active, updated);
            state = propagateLock(state, m.color(), active);
        } else if (!activeMarked) {
            // Active player passed both phases — take penalty
            Board penalized = state.boards().get(active).withPenalty();
            state = state.withBoard(active, penalized);
        }

        return state.isTerminal() ? state : state.nextTurn();
    }

    /**
     * If the player just locked their own row, record it in GameState.lockedColors so the
     * colored die is removed and GameStateView filters out that row for all players.
     * The other player does NOT receive a lock-bonus mark — only the locking player earns it.
     */
    private GameState propagateLock(GameState state, RowColor color, int playerWhoMarked) {
        if (!state.boards().get(playerWhoMarked).row(color).locked()) return state;
        if (state.lockedColors().contains(color)) return state; // already recorded
        return state.withLockedColor(color);
    }

    private Strategy strategyFor(int playerIndex) {
        return playerIndex == 0 ? strategy0 : strategy1;
    }
}
