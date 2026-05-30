package com.qwixx.runner;

import com.qwixx.model.RowColor;
import com.qwixx.strategy.Strategy;

import java.util.*;
import java.util.stream.Collectors;

public record SimulationResult(
    String strategyAName,
    String strategyBName,
    int totalGames,
    int winsA,
    int winsB,
    int ties,
    double avgScoreA,
    double avgScoreB,
    IntSummaryStatistics statsA,
    IntSummaryStatistics statsB,
    double avgTurns,
    Map<String, Long> endReasonCounts
) {
    public static SimulationResult aggregate(
        List<GameResult> results,
        String nameA,
        String nameB
    ) {
        int winsA = 0, winsB = 0, ties = 0;
        IntSummaryStatistics statsA = results.stream()
            .mapToInt(GameResult::score0).summaryStatistics();
        IntSummaryStatistics statsB = results.stream()
            .mapToInt(GameResult::score1).summaryStatistics();
        for (GameResult r : results) {
            if (r.winner() == 0) winsA++;
            else if (r.winner() == 1) winsB++;
            else ties++;
        }
        double avgTurns = results.stream().mapToInt(GameResult::turnCount).average().orElse(0);
        Map<String, Long> endReasons = results.stream()
            .collect(Collectors.groupingBy(GameResult::endReason, Collectors.counting()));
        return new SimulationResult(
            nameA, nameB,
            results.size(),
            winsA, winsB, ties,
            statsA.getAverage(), statsB.getAverage(),
            statsA, statsB,
            avgTurns,
            endReasons
        );
    }

    // Inner width of the box (between the │ borders).
    // Three-column layout: c1=18, c2=11, c3=23  →  18+1+11+1+23 = 54 ✓
    private static final int W  = 54;
    private static final int C1 = 18, C2 = 11, C3 = 23;

    public void printReport() {
        double winPctA = 100.0 * winsA / totalGames;
        double winPctB = 100.0 * winsB / totalGames;
        double tiePct  = 100.0 * ties  / totalGames;

        String bar1 = "╔" + "═".repeat(W)                                          + "╗";
        String bar2 = "╠" + "═".repeat(W)                                          + "╣";
        String bar3 = "╠" + "═".repeat(C1) + "╦" + "═".repeat(C2) + "╦" + "═".repeat(C3) + "╣";
        String bar4 = "╠" + "═".repeat(C1) + "╬" + "═".repeat(C2) + "╬" + "═".repeat(C3) + "╣";
        String bar5 = "╠" + "═".repeat(C1) + "╩" + "═".repeat(C2) + "╩" + "═".repeat(C3) + "╣";
        String bar6 = "╚" + "═".repeat(W)                                          + "╝";

        System.out.println(bar1);
        System.out.println(r1("  QWIXX Simulation Results"));
        System.out.println(bar2);
        System.out.println(r1(String.format("  Games played : %6d", totalGames)));
        System.out.println(bar3);
        System.out.println(r3("", " " + truncate(strategyAName, C2 - 2), " " + truncate(strategyBName, C3 - 2)));
        System.out.println(bar4);
        System.out.println(r3(" Win %",
            String.format("  %6.1f%%", winPctA),
            String.format("  %6.1f%%", winPctB)));
        System.out.println(r3(" Tie %", String.format("  %6.1f%%", tiePct), ""));
        System.out.println(r3(" Avg score",
            String.format("  %7.1f", statsA.getAverage()),
            String.format("  %7.1f", statsB.getAverage())));
        System.out.println(r3(" Min score",
            String.format("  %7d", statsA.getMin()),
            String.format("  %7d", statsB.getMin())));
        System.out.println(r3(" Max score",
            String.format("  %7d", statsA.getMax()),
            String.format("  %7d", statsB.getMax())));
        System.out.println(bar5);
        System.out.println(r1(String.format("  Avg turns : %5.1f", avgTurns)));
        System.out.println(r1(String.format("  End by 2 rows locked  : %6d",
            endReasonCounts.getOrDefault("2_ROWS_LOCKED", 0L))));
        System.out.println(r1(String.format("  End by penalty limit  : %6d",
            endReasonCounts.getOrDefault("PENALTY_LIMIT", 0L))));
        System.out.println(bar6);
    }

    /** Single-column row: pads content to exactly W chars. */
    private static String r1(String content) {
        return "║" + pad(content, W) + "║";
    }

    /** Three-column row: each cell padded to its fixed width. */
    private static String r3(String c1, String c2, String c3) {
        return "║" + pad(c1, C1) + "║" + pad(c2, C2) + "║" + pad(c3, C3) + "║";
    }

    /** Two-column row: col1 fixed width, col2+col3 merged (spans remaining width). */
    private static String r2(String c1, String c2) {
        return "║" + pad(c1, C1) + "║" + pad(c2, C2 + 1 + C3) + "║";
    }

    private static String pad(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }

    private static String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }
}
