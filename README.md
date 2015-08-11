# tetris-solver
Program to solve a tetris puzzle

## Requirements
* maven
* java8

## Sample Usage
mvn package && mvn exec:java -Dexec.mainClass="com.budimana.tetris.TetrisSolver" -Dexec.args="10 26 restrictions.10x26.txt"

## Problem
Given a matrix, find a configuration of tetris pieces that covers all the squares.
Restrictions:
* every cell must be a part of a tetris piece
* some of the piece boundaries are given
* no two copies of the same piece may share an edge (reflections count as different pieces, rotations do not)

## Example

Unfinished matrix:
```
      0    1    2    3
   ┌ ── ─ ── ─ ── ─ ── ┬
0  |              |    |
   | ──                |
1  |    |    |         |
   |      ──        ── |
2  |              |    |
   | ──        ──      |
3  |    |    |    |    |
   |                   |
4  |                   |
   |      ──           |
5  |         |    |    |
   └ ── ─ ── ─ ── ─ ── ┘
```

Completed matrix:
```
      0    1    2    3
   ┌ ── ─ ── ─ ── ┬ ── ┬
0  |              |    |
   ├ ── ┐    ┌ ── ┘    |
1  |    |    |         |
   |    └ ── ┤    ┌ ── ┤
2  |         |    |    |
   ├ ── ┐    ├ ── +    |
3  |    |    |    |    |
   |    ├ ── ┘    ├    |
4  |    |         |    |
   |    └ ── ┐    |    |
5  |         |    |    |
   └ ── ─ ── ┴ ── ┴ ── ┘
```
