package com.budimana.tetris;

import java.util.HashSet;
import java.util.Set;


/**
 * Enum for each type of tetris piece
 * Also gives a way to identify which type a piece is
 */
public enum PieceType {
    T(4,  // expected number of rotations
      new Piece(
        new Tetronimo(0, 0),  // x
        new Tetronimo(0, 1),  // x x
        new Tetronimo(1, 1),  // x
        new Tetronimo(0, 2)
    )),
    L1(4,   // expected number of rotations
       new Piece(
         new Tetronimo(0, 0),  // x x x
         new Tetronimo(1, 0),  //     x
         new Tetronimo(2, 0),
         new Tetronimo(2, 1)
    )),
    L2(4,  // expected number of rotations
       new Piece(
         new Tetronimo(0, 0),  // x
         new Tetronimo(0, 1),  // x
         new Tetronimo(0, 2),  // x x
         new Tetronimo(1, 2)
    )),
    N1(2,  // expected number of rotations
       new Piece(
         new Tetronimo(0, 0),  // x
         new Tetronimo(0, 1),  // x x
         new Tetronimo(1, 1),  //   x
         new Tetronimo(1, 2)
    )),
    N2(2,  // expected number of rotations
       new Piece(
         new Tetronimo(0, 0),  // x x
         new Tetronimo(1, 0),  //   x x
         new Tetronimo(1, 1),
         new Tetronimo(2, 1)
    )),
    I(2,  // expected number of rotations
      new Piece(
        new Tetronimo(0, 0),  // x
        new Tetronimo(0, 1),  // x
        new Tetronimo(0, 2),  // x
        new Tetronimo(0, 3)   // x
    )),
    BOX(1,  // expected number of rotations
        new Piece(
          new Tetronimo(0, 0),  // x x
          new Tetronimo(1, 0),  // x x
          new Tetronimo(0, 1),
          new Tetronimo(1, 1)
    )),
    ;

    private final Set<Piece> samplePieces;

    private PieceType(int expectedRotations, Piece samplePiece) {
        this.samplePieces = getAllRotations(samplePiece);

        // validate the sample pieces look fine
        assert this.samplePieces.size() == expectedRotations;
        for (Piece p1 : samplePieces) {
            for (Piece p2 : samplePieces) {
                assert p1 == p2 || !p1.isTranslationOf(p2);
            }
        }
    }

    /**
     * Returns all rotations for the given piece
     * Assumes 90 degree rotations
     */
    private static Set<Piece> getAllRotations(Piece p) {
        Set<Piece> rotations = new HashSet<>(4);
        rotations.add(p);

        // initialize all the rotated pieces
        Piece[] newPieces = new Piece[] {
            new Piece(),
            new Piece(),
            new Piece()
        };

        boolean firstTetronimo = true;
        for (Tetronimo t : p.getTetronimos()) {
            // rotation around (0,0) makes things a bit easier
            if (firstTetronimo) {
                assert t.x == 0 && t.y == 0;
                firstTetronimo = false;
            }

            // add the corresponding rotated tetronimos
            newPieces[0].add(new Tetronimo(-1 * t.y, t.x));  // 90
            newPieces[1].add(new Tetronimo(-1 * t.x, -1 * t.y));  // 180
            newPieces[2].add(new Tetronimo(t.y, -1 * t.x));  // 270
        }

        // only add non-duplicates
        for (Piece newPiece : newPieces) {
            assert newPiece.isComplete();

            // check to make sure it's not a translation of an existing rotation
            boolean validRotation = true;
            for (Piece oldPiece : rotations) {
                if (newPiece.isTranslationOf(oldPiece)) {
                    validRotation = false;
                    break;
                }
            }

            if (validRotation) {
                rotations.add(newPiece);
            }
        }
        return rotations;
    }

    /**
     * Returns the type of the piece
     * Designed for simplicity
     */
    public static PieceType classifyPiece(Piece p) {
        assert p.isComplete();

        // checks if the piece is a translation of any of the rotations
        //   of a given piece type
        for (PieceType type : PieceType.values()) {
            for (Piece samplePiece : type.samplePieces) {
                if (p.isTranslationOf(samplePiece)) {
                    return type;
                }
            }
        }

        // dead code
        assert false;
        return null;
    }
}
