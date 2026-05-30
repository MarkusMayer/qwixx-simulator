package com.qwixx.strategy;

import com.qwixx.model.*;
import com.qwixx.model.action.*;
import com.qwixx.runner.GameStateView;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GreedyStrategyTest {

    private StateView viewFor(GameState state, DiceRoll roll, int playerIndex) {
        return new GameStateView(state, roll, playerIndex);
    }

    @Test
    void whitePhasePicksMarkOverPass() {
        Strategy strategy = new GreedyStrategy();
        GameState state = GameState.initial();
        // white sum = 7, all rows accept it
        DiceRoll roll = new DiceRoll(3, 4, Map.of(
            RowColor.RED, 1, RowColor.YELLOW, 1, RowColor.GREEN, 1, RowColor.BLUE, 1
        ));
        PlayerAction action = strategy.decideWhitePhase(viewFor(state, roll, 0));
        assertInstanceOf(MarkWhiteDice.class, action);
    }

    @Test
    void colorPhasePicksMarkOverPass() {
        Strategy strategy = new GreedyStrategy();
        GameState state = GameState.initial();
        DiceRoll roll = new DiceRoll(3, 4, Map.of(
            RowColor.RED, 2, RowColor.YELLOW, 3, RowColor.GREEN, 1, RowColor.BLUE, 5
        ));
        PlayerAction action = strategy.decideColorPhase(viewFor(state, roll, 0));
        assertInstanceOf(MarkColorDice.class, action);
    }

    @Test
    void whitePhasePreferRowWithMoreMarks() {
        Strategy strategy = new GreedyStrategy();
        // RED has 4 marks, YELLOW has 0 marks; both can accept white sum 8
        Board board = Board.empty()
            .withMark(RowColor.RED, 2)
            .withMark(RowColor.RED, 3)
            .withMark(RowColor.RED, 4)
            .withMark(RowColor.RED, 5);
        GameState state = GameState.initial().withBoard(0, board);
        DiceRoll roll = new DiceRoll(4, 4, Map.of(
            RowColor.RED, 1, RowColor.YELLOW, 1, RowColor.GREEN, 1, RowColor.BLUE, 1
        ));
        PlayerAction action = strategy.decideWhitePhase(viewFor(state, roll, 0));
        assertInstanceOf(MarkWhiteDice.class, action);
        assertEquals(RowColor.RED, ((MarkWhiteDice) action).color()); // RED has more marks -> higher marginal gain
    }

    @Test
    void passWhenNoLegalWhiteMarks() {
        Strategy strategy = new GreedyStrategy();
        // Lock all rows so nothing is markable
        Board board = Board.empty()
            .withMark(RowColor.RED, 2).withMark(RowColor.RED, 3)
            .withMark(RowColor.RED, 4).withMark(RowColor.RED, 5)
            .withMark(RowColor.RED, 6).withMark(RowColor.RED, 12);
        // Move red row past white sum of 7 by making 8 the next markable
        Board board2 = Board.empty().withMark(RowColor.RED, 8)
            .withMark(RowColor.YELLOW, 8)
            .withMark(RowColor.GREEN, 6)   // 6 means next green is < 6: 5,4,...
            .withMark(RowColor.BLUE, 6);
        // white sum = 7: red past it, yellow past it, green needs ≤6 but 7 not in green after 6, blue same
        // Actually simpler: just check pass when no legal marks
        GameState state = GameState.initial().withBoard(0, board2);
        DiceRoll roll = new DiceRoll(3, 4, Map.of(  // sum=7
            RowColor.RED, 1, RowColor.YELLOW, 1, RowColor.GREEN, 1, RowColor.BLUE, 1
        ));
        // green row: [12,11,...,7,6,5,...,2] — marked 6, next valid = 5 (index > 5's index)
        // 7 in green: index 5 in [12,11,10,9,8,7,6,...], last marked = 6 at index 6 → 7's index=5 ≤ 6, so NOT markable
        // Similarly blue
        // RED: marked 8 → next must be > 8, so 9,10,11,12. 7 < 8, not markable
        // YELLOW: same as red
        PlayerAction action = strategy.decideWhitePhase(viewFor(state, roll, 0));
        assertInstanceOf(Pass.class, action);
    }
}
