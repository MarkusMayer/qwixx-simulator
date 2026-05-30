package com.qwixx.runner;

import com.qwixx.strategy.ConservativeStrategy;
import com.qwixx.strategy.GreedyStrategy;
import com.qwixx.strategy.PenaltyAwareStrategy;
import com.qwixx.strategy.RandomStrategy;

import java.util.Random;

public final class Main {

    public static void main(String[] args) {
        System.out.println("=== Greedy vs Random ===");
        new Simulator(new GreedyStrategy(), new RandomStrategy(new Random(1)), 10_000, 42L)
            .run().printReport();

        System.out.println("=== Greedy vs Conservative(maxSkip=1) ===");
        new Simulator(new GreedyStrategy(), new ConservativeStrategy(1), 10_000, 42L)
            .run().printReport();

        System.out.println("=== Conservative(maxSkip=2) vs Random ===");
        new Simulator(new ConservativeStrategy(2), new RandomStrategy(new Random(2)), 10_000, 42L)
            .run().printReport();

        System.out.println("=== Greedy vs PenaltyAware ===");
        new Simulator(new GreedyStrategy(), new PenaltyAwareStrategy(), 10_000, 42L)
            .run().printReport();

        System.out.println("=== PenaltyAware vs Random ===");
        new Simulator(new PenaltyAwareStrategy(), new RandomStrategy(new Random(3)), 10_000, 42L)
            .run().printReport();
    }
}
