package com.qwixx.runner;

import com.qwixx.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Simulator {

    private final Strategy strategyA;
    private final Strategy strategyB;
    private final int nGames;
    private final long seed;

    public Simulator(Strategy strategyA, Strategy strategyB, int nGames, long seed) {
        this.strategyA = strategyA;
        this.strategyB = strategyB;
        this.nGames = nGames;
        this.seed = seed;
    }

    public SimulationResult run() {
        Random rng = new Random(seed);
        List<GameResult> results = new ArrayList<>(nGames);
        for (int i = 0; i < nGames; i++) {
            // Alternate who goes first to eliminate first-mover bias
            Strategy first  = (i % 2 == 0) ? strategyA : strategyB;
            Strategy second = (i % 2 == 0) ? strategyB : strategyA;
            GameRunner runner = new GameRunner(first, second, rng);
            var finalState = runner.runGame();
            GameResult raw = GameResult.from(finalState);
            // Normalize so score0 always maps to strategyA regardless of who went first
            GameResult normalized = (i % 2 == 0) ? raw
                : new GameResult(raw.score1(), raw.score0(),
                    raw.winner() == -1 ? -1 : 1 - raw.winner(),
                    raw.turnCount(), raw.lockedColors(), raw.endReason());
            results.add(normalized);
        }
        return SimulationResult.aggregate(results, strategyA.name(), strategyB.name());
    }
}
