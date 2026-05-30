package com.qwixx.runner;

import com.qwixx.strategy.GreedyStrategy;
import com.qwixx.strategy.RandomStrategy;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SimulationResultTest {

    @Test
    void winCountsAddUpToTotalGames() {
        SimulationResult result = new Simulator(
            new GreedyStrategy(),
            new RandomStrategy(new Random(0)),
            100, 1L
        ).run();
        assertEquals(result.totalGames(), result.winsA() + result.winsB() + result.ties());
    }

    @Test
    void avgScoresAreWithinReasonableBounds() {
        SimulationResult result = new Simulator(
            new GreedyStrategy(),
            new RandomStrategy(new Random(7)),
            200, 99L
        ).run();
        assertTrue(result.avgScoreA() >= -20 && result.avgScoreA() <= 312);
        assertTrue(result.avgScoreB() >= -20 && result.avgScoreB() <= 312);
    }

    @Test
    void printReportDoesNotThrow() {
        SimulationResult result = new Simulator(
            new GreedyStrategy(),
            new RandomStrategy(new Random(5)),
            50, 0L
        ).run();
        assertDoesNotThrow(result::printReport);
    }

    @Test
    void endReasonCountsAddUpToTotalGames() {
        SimulationResult result = new Simulator(
            new GreedyStrategy(),
            new RandomStrategy(new Random(3)),
            100, 2L
        ).run();
        long total = result.endReasonCounts().values().stream().mapToLong(Long::longValue).sum();
        assertEquals(result.totalGames(), total);
    }
}
