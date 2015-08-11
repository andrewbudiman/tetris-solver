package com.budimana.tetris;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a tetris piece, or at least part of one that is being formed
 */
public class Piece {

    public static final int NUM_TETRONIMOS_IN_PIECE = 4;

    private PieceType type;
    private final Set<Tetronimo> tetronimos = new TreeSet<Tetronimo>();
    private int tetronimoCount = 0;

    public Piece(Tetronimo... tetronimos) {
        if (tetronimos != null) {
            for (Tetronimo t : tetronimos) {
                add(t);
            }
        }
    }

    /**
     * Adds the tetronimo to this piece
     * Set the tetronimo's piece pointer
     */
    public void add(Tetronimo t) {
        assert !isComplete();
        assert !tetronimos.contains(t);

        tetronimos.add(t);
        tetronimoCount++;

        // cannot add tetronimo that is currently a part of another piece
        assert t.piece == null;
        t.piece = this;
    }

    /**
     * Removes the tetronimo from this piece
     * Reset the type as well as the tetronimo's piece pointer
     */
    public void remove(Tetronimo t) {
        assert tetronimos.contains(t);

        tetronimos.remove(t);
        tetronimoCount--;
        type = null;

        // don't remove a tetronimo that thinks it's a part of another piece
        assert t.piece == this;
        t.piece = null;
    }

    /**
     * Returns the type of the completed piece
     * Calculated on-demand
     */
    public PieceType getType() {
        assert isComplete();

        if (type == null) {
            type = PieceType.classifyPiece(this);
        }
        return type;
    }

    public Set<Tetronimo> getTetronimos() {
        return tetronimos;
    }

    public int getCount() {
        return tetronimoCount;
    }

    public boolean isComplete() {
        return tetronimoCount == NUM_TETRONIMOS_IN_PIECE;
    }

    /**
     * Return all tetronimos of different pieces adjacent to any
     *   of the tetronimos in the current piece
     */
    public Set<Tetronimo> getAdjacentTetronimos(Tetronimo[][] matrix) {
        Set<Tetronimo> adjTetronimos = new HashSet<>();
        for (Tetronimo t : tetronimos) {
            adjTetronimos.addAll(t.getAdjacentTetronimos(matrix));
        }
        return adjTetronimos;
    }

    /**
     * Returns true if the configurations of the pieces' tetronimos
     *   are the same, but just translated along some arbitrary vector
     */
    public boolean isTranslationOf(Piece p) {
        assert this.isComplete();
        assert p.isComplete();
        
        Iterator<Tetronimo> it1 = this.getTetronimos().iterator();
        Iterator<Tetronimo> it2 = p.getTetronimos().iterator();
        Tetronimo t1 = it1.next();
        Tetronimo t2 = it2.next();

        // check whether the deltaX and deltaY between corresponding
        //   tetronimos of the pieces are the same
        int deltaX = t1.x - t2.x;
        int deltaY = t1.y - t2.y;
        while (it1.hasNext() && it2.hasNext()) {
            t1 = it1.next();
            t2 = it2.next();
            if (t1.x - t2.x != deltaX || t1.y - t2.y != deltaY) {
                return false;
            }
        }

        assert !it1.hasNext();
        assert !it2.hasNext();
        return true;
    }

    /**
     * Checks whether any adjacent tetronimos can be associated with this piece
     */
    public boolean canBeFinished(Tetronimo[][] matrix) {
        assert !isComplete();
        for (Tetronimo t : tetronimos) {
            // only tetronimos to the right and below can be not associated yet
            Tetronimo right = t.getRight(matrix);
            Tetronimo down = t.getDown(matrix);

            if (right != null && right.piece == null) {
                return true;
            }
            if (down != null && down.piece == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this)
            .append("type", type)
            .append("tetronimoCount", tetronimoCount)
            .append("tetronimos {");

        for (Tetronimo t : tetronimos) {
            b.append(t.toString(), ",");
        }
        b.append("}");

        return b.toString();
    }
}
