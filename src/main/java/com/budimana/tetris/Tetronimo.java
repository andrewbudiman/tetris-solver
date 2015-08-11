package com.budimana.tetris;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * The basic building block for tetris pieces
 */
public class Tetronimo implements Comparable {
    public final int x, y;
    public Piece piece;

    public Tetronimo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Tetronimo getLeft(Tetronimo[][] matrix) {
        return this.x == 0 ? null : matrix[this.x - 1][this.y];
    }

    public Tetronimo getRight(Tetronimo[][] matrix) {
        return this.x == matrix.length - 1 ? null : matrix[this.x + 1][this.y];
    }

    public Tetronimo getUp(Tetronimo[][] matrix) {
        return this.y == 0 ? null : matrix[this.x][this.y - 1];
    }

    public Tetronimo getDown(Tetronimo[][] matrix) {
        return this.y == matrix[0].length - 1 ? null :  matrix[this.x][this.y + 1];
    }

    /**
     * Gets adjacent tetronimos that are part of a different piece
     */
    public Set<Tetronimo> getAdjacentTetronimos(Tetronimo[][] matrix) {
        assert this.piece != null;
        Set<Tetronimo> adjTs = getAllAdjacentTetronimos(matrix);

        // remove any that are part of the specified piece
        adjTs.removeIf(tetronimo -> tetronimo == null ||
                                    tetronimo.piece == this.piece);
        return adjTs;
    }

    /**
     * Get all adjacent tetronimos
     */
    public Set<Tetronimo> getAllAdjacentTetronimos(Tetronimo[][] matrix) {
        Set<Tetronimo> adjTs = new HashSet<Tetronimo>();
        adjTs.add(getLeft(matrix));
        adjTs.add(getRight(matrix));
        adjTs.add(getUp(matrix));
        adjTs.add(getDown(matrix));
        return adjTs;
    }

    /**
     * Gets the next tetronimo, reading from left to right, top to bottom
     */
    public Tetronimo getNext(Tetronimo[][] matrix) {
        // to the right
        if (this.x < matrix.length - 1) {
            return getRight(matrix);
        }

        // next row
        if (this.y < matrix[0].length - 1) {
            return matrix[0][this.y + 1];
        }

        return null;
    }

    /**
     * Checks to see whether combining tetronimos is allowed given restrictions
     */
    public boolean isRestricted(Tetronimo t,
                                Map<Tetronimo, List<Tetronimo>> restrictions) {
        if (t == null) {
            return false;
        }

        List<Tetronimo> restrictionList = restrictions.get(this);
        if (restrictionList == null) {
            return false;
        }

        for (Tetronimo restrictedT : restrictionList) {
            if (restrictedT != null && restrictedT.equals(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Want ordering so we can compare 2 pieces' tetronimos easily
     */
    public int compareTo(Object o) {
        assert o != null;
        assert o.getClass() == this.getClass();
        Tetronimo other = (Tetronimo) o;

        int yDelta = this.y - other.y;
        if (yDelta != 0) {
            return yDelta;
        }

        int xDelta = this.x - other.x;
        if (xDelta != 0) {
            return xDelta;
        }

        return 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("x", x)
            .append("y", y)
            .append("piece", piece)
            .toString();
    }
}
