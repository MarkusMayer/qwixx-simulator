# QWIXX Strategy Simulator

An unofficial strategy simulator for the [QWIXX](https://www.nsv.de/brettspiele/qwixx/) dice game by NSV. Simulates thousands of games between pluggable strategies to compare their effectiveness.

> **Disclaimer:** QWIXX is a trademark of Nürnberger-Spielkarten-Verlag (NSV). This project is an unofficial fan simulator with no affiliation with NSV.

## Features

- Accurate implementation of QWIXX rules as a domain model
- Pluggable strategy interface — implement and add your own strategies
- Batch simulation runner (default: 10,000 games per matchup)
- Per-matchup statistics: win rates, score distributions, avg turns, end conditions

## Requirements

- JDK 25
- Gradle (or use the included wrapper)

## Running

```bash
./gradlew run
```

Simulates three strategy matchups and prints a results table for each.

## Running Tests

```bash
./gradlew test
```

Tests are covering the domain model, strategies, and simulation runner.

## Project Structure

```
src/main/java/com/qwixx/
├── model/          # Domain model: Row, Board, GameState, DiceRoll, ...
│   └── action/    # Sealed action types: MarkWhiteDice, MarkColorDice, Pass, ...
├── strategy/       # Strategy interface + built-in strategies
└── runner/         # GameRunner, Simulator, result types, Main
```

## Game Rules Summary

QWIXX is a 2–5 player dice game (this simulator is restricted to 2 players). Each player has a score sheet with four rows:

- **Red / Yellow:** numbers 2 → 12 (ascending)
- **Green / Blue:** numbers 12 → 2 (descending)

On each turn the active player rolls 6 dice (2 white + 4 coloured). All players may mark the white dice sum in any row. The active player may additionally mark a white + coloured die sum in the matching row. Numbers must be marked left-to-right within a row; skipped numbers are permanently lost.

To mark the last number in a row (12 or 2) and lock it, a player needs at least 5 prior marks in that row. Locking a row removes its die from the game for all players.

The game ends when 2 rows are locked or any player takes their 4th penalty. Scoring is triangular: n × (n + 1) / 2 per row, minus 5 per penalty.

## Adding a Strategy

Implement the `Strategy` interface:

```java
public class MyStrategy implements Strategy {

    @Override
    public PlayerAction decideWhitePhase(StateView view) {
        // return a MarkWhiteDice or Pass
    }

    @Override
    public PlayerAction decideColorPhase(StateView view) {
        // return a MarkColorDice or Pass (active player only)
        // returning Pass when no white mark was made causes a penalty
    }
}
```

`StateView` provides your board, the opponent's board, the current dice roll, legal moves, and game metadata. Wire your strategy into `Main.java` to include it in simulations.

## Built-in Strategies

| Strategy | Description |
|---|---|
| `RandomStrategy` | Randomly marks or passes each phase |
| `GreedyStrategy` | Always marks; prefers rows with the most existing marks |
| `ConservativeStrategy(maxSkip)` | Only marks if fewer than `maxSkip` numbers would be skipped |

## License

Licensed under the [Apache License 2.0](LICENSE).
