package com.budimana.tetris;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class TetronimoTest {
    private final int width = 4;
    private final int height = 4;
    private final Tetronimo[][] matrix = TetrisSolver.createMatrix(width, height);

    @Test
    public void testGetLeft() {
        // tetronimos on the left edge don't have a 'left' tetronimo
        for (int y = 0; y < height; y++) {
            Tetronimo left = matrix[0][y].getLeft(matrix);
            assertNull(left);
        }

        // assert other tetronimos should have the correct 'left' tetronimo
        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tetronimo left = matrix[x][y].getLeft(matrix);
                assertEquals(matrix[x-1][y], left);
            }
        }
    }

    @Test
    public void testGetRight() {
        // tetronimos on the right edge don't have a 'right' tetronimo
        for (int y = 0; y < height; y++) {
            Tetronimo right = matrix[width - 1][y].getRight(matrix);
            assertNull(right);
        }

        // assert other tetronimos should have the correct 'right' tetronimo
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height; y++) {
                Tetronimo right = matrix[x][y].getRight(matrix);
                assertEquals(matrix[x+1][y], right);
            }
        }
    }

    @Test
    public void testGetUp() {
        // tetronimos on the top edge don't have a 'up' tetronimo
        for (int x = 0; x < width; x++) {
            Tetronimo up = matrix[x][0].getUp(matrix);
            assertNull(up);
        }

        // assert other tetronimos should have the correct 'up' tetronimo
        for (int x = 0; x < width; x++) {
            for (int y = 1; y < height; y++) {
                Tetronimo up = matrix[x][y].getUp(matrix);
                assertEquals(matrix[x][y-1], up);
            }
        }
    }

    @Test
    public void testGetDown() {
        // tetronimos on the bottom edge don't have a 'down' tetronimo
        for (int x = 0; x < width; x++) {
            Tetronimo down = matrix[x][height - 1].getDown(matrix);
            assertNull(down);
        }

        // assert other tetronimos should have the correct 'down' tetronimo
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height - 1; y++) {
                Tetronimo down = matrix[x][y].getDown(matrix);
                assertEquals(matrix[x][y+1], down);
            }
        }
    }

    @Test
    public void testGetAdjacentTetronimos() {
        Tetronimo t = matrix[0][0];
        Piece p = new Piece(t);

        // easy case where none of the surrounding tetronimos are
        //   associated with any pieces
        Set<Tetronimo> adjTs = t.getAdjacentTetronimos(matrix);
        assertEquals(2, adjTs.size());
        assertTrue(adjTs.contains(matrix[0][1]));
        assertTrue(adjTs.contains(matrix[1][0]));

        // add one of the adjacent tetronimos to the piece
        // that tetronimo should now be excluded
        p.add(matrix[0][1]);
        adjTs = t.getAdjacentTetronimos(matrix);
        assertEquals(1, adjTs.size());
        assertTrue(adjTs.contains(matrix[1][0]));

        // associated the other adjacent tetronimo with a different piece
        // it should still be included in the result
        Piece otherP = new Piece(matrix[1][0]);
        adjTs = t.getAdjacentTetronimos(matrix);
        assertEquals(1, adjTs.size());
        assertTrue(adjTs.contains(matrix[1][0]));
    }

    @Test
    public void testGetNext() {
        // for tetronimos not on the right edge, getNext == getRight
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height; y++) {
                Tetronimo currTetronimo = matrix[x][y];
                Tetronimo next = currTetronimo.getNext(matrix);
                Tetronimo right = currTetronimo.getRight(matrix);
                assertEquals(right, next);
            }
        }

        // for tetronimos on the right edge, getNext returns the first
        //   tetronimo on the next row, or null if no more rows
        for (int y = 0; y < height; y++) {
            Tetronimo currTetronimo = matrix[width - 1][y];
            Tetronimo next = currTetronimo.getNext(matrix);
            Tetronimo expectedNext = y == height - 1 ? null : matrix[0][y+1];
            assertEquals(expectedNext, next);
        }
    }

    @Test
    public void testIsRestricted() {
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();

        // cannot be restricted with a null tetronimo
        Tetronimo t1 = new Tetronimo(0, 0);
        Tetronimo t2 = null;
        assertFalse(t1.isRestricted(t2, restrictions));

        // 2 normal tetronimos w/o restrictions should be fine
        t2 = new Tetronimo(1, 1);
        assertFalse(t1.isRestricted(t2, restrictions));
        assertFalse(t2.isRestricted(t1, restrictions));

        // add a restriction
        restrictions.put(t1, Arrays.asList(t2));
        assertTrue(t1.isRestricted(t2, restrictions));
    }

    @Test
    public void testCompareTo() {
        // y value is different
        assertTrue(matrix[0][0].compareTo(matrix[0][1]) < 0);
        assertTrue(matrix[0][1].compareTo(matrix[0][0]) > 0);

        // x value is different
        assertTrue(matrix[0][0].compareTo(matrix[1][0]) < 0);
        assertTrue(matrix[1][0].compareTo(matrix[0][0]) > 0);

        // same coordinates
        assertTrue(matrix[1][1].compareTo(matrix[1][1]) == 0);
        assertTrue(matrix[1][1].compareTo(new Tetronimo(1, 1)) == 0);
    }
}
