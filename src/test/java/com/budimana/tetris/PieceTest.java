package com.budimana.tetris;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class PieceTest {

    private static Tetronimo[] dummyTetronimos;
    static {
        dummyTetronimos = new Tetronimo[Piece.NUM_TETRONIMOS_IN_PIECE];
        for (int i = 0; i < Piece.NUM_TETRONIMOS_IN_PIECE; i++) {
            dummyTetronimos[i] = new Tetronimo(0, i);
        }

    }

    @Before
    public void setUp() {
        // make sure tests have cleaned up
        for (Tetronimo t : dummyTetronimos) {
            assert t.piece == null;
        }
    }

    /**
     * Start with an empty piece
     */
    @Test
    public void testPieceLifecycle_1() {
        // build a piece from scratch
        Piece p = new Piece();
        assertEquals(0, p.getCount());
        for (int i = 0; i < Piece.NUM_TETRONIMOS_IN_PIECE; i++) {
            assertFalse(p.isComplete());
            p.add(dummyTetronimos[i]);
            assertEquals(p, dummyTetronimos[i].piece);
            assertEquals(i + 1, p.getCount());
        }

        assertTrue(p.isComplete());
        assertEquals(Piece.NUM_TETRONIMOS_IN_PIECE, p.getCount());

        tearDownPiece(p);
    }

    /**
     * Start with a non-empty piece
     */
    @Test
    public void testPieceLifecycle_2() {
        // build a piece from scratch
        Piece p = new Piece(dummyTetronimos[0]);
        assertEquals(1, p.getCount());
        for (int i = 1; i < Piece.NUM_TETRONIMOS_IN_PIECE; i++) {
            assertFalse(p.isComplete());
            p.add(dummyTetronimos[i]);
            assertEquals(p, dummyTetronimos[i].piece);
            assertEquals(i + 1, p.getCount());
        }

        assertTrue(p.isComplete());
        assertEquals(Piece.NUM_TETRONIMOS_IN_PIECE, p.getCount());

        tearDownPiece(p);
    }

    /**
     * Start with a complete piece
     */
    @Test
    public void testPieceLifecycle_3() {
        // construct a complete piece
        Piece p = new Piece(dummyTetronimos);

        assertTrue(p.isComplete());
        assertEquals(Piece.NUM_TETRONIMOS_IN_PIECE, p.getCount());

        tearDownPiece(p);
    }

    @Test
    public void testExtraTetronimos() {
        // construct a complete piece
        Piece p = new Piece(dummyTetronimos);

        // try to add a new piece
        try {
            p.add(new Tetronimo(10, 10));
            fail("should have asserted");
        } catch (AssertionError e) {
            // expected
        }

        tearDownPiece(p);
    }

    @Test
    public void testRemoveNonAssociatedTetronimo() {
        Tetronimo t1 = new Tetronimo(0, 0);
        Tetronimo t2 = new Tetronimo(0, 0);

        Piece p1 = new Piece();
        Piece p2 = new Piece();

        // remove from empty pieces
        try {
            p1.remove(t1);
            fail("should have asserted");
        } catch (AssertionError e) {
            // expected
        }
        try {
            p2.remove(t2);
            fail("should have asserted");
        } catch (AssertionError e) {
            // expected
        }

        // add tetronimos to both pieces, but remove the wrong ones
        p1.add(t1);
        p2.add(t2);
        try {
            p1.remove(t2);
            fail("should have asserted");
        } catch (AssertionError e) {
            // expected
        }
        try {
            p2.remove(t1);
            fail("should have asserted");
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    public void testAdjacentTetronimos() {
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);

        //   x
        // x x x
        Piece p = new Piece(
            matrix[1][0],
            matrix[0][1],
            matrix[1][1],
            matrix[2][1]
        );

        Set<Tetronimo> adjTs = p.getAdjacentTetronimos(matrix);
        assertEquals(6, adjTs.size());
        assertTrue(adjTs.contains(matrix[0][0]));
        assertTrue(adjTs.contains(matrix[3][1]));
        assertTrue(adjTs.contains(matrix[3][1]));
        assertTrue(adjTs.contains(matrix[0][2]));
        assertTrue(adjTs.contains(matrix[1][2]));
        assertTrue(adjTs.contains(matrix[2][2]));
    }

    @Test
    public void testValidTranslation() {
        // x x
        //   x x
        Piece base = new Piece(
            new Tetronimo(0, 0),
            new Tetronimo(1, 0),
            new Tetronimo(1, 1),
            new Tetronimo(2, 1));

        Piece down = new Piece();
        Piece left = new Piece();
        Piece diagonal = new Piece();

        // create translations of base
        for (Tetronimo t : base.getTetronimos()) {
            down.add(new Tetronimo(t.x, t.y + 5));
            left.add(new Tetronimo(t.x - 5, t.y));
            diagonal.add(new Tetronimo(t.x + 5, t.y - 5));
        }

        // assert translations
        Piece[] pieces = new Piece[] { base, down, left, diagonal };
        for (Piece p1 : pieces) {
            for (Piece p2 : pieces) {
                assertTrue(p1.isTranslationOf(p2));
            }
        }
    }

    @Test
    public void testInvalidTranslation() {
        // x x
        //   x x
        Piece base = new Piece(
            new Tetronimo(0, 0),
            new Tetronimo(1, 0),
            new Tetronimo(1, 1),
            new Tetronimo(2, 1)
        );

        //   x
        // x x x
        Piece differentType = new Piece(
            new Tetronimo(1, 0),
            new Tetronimo(0, 1),
            new Tetronimo(1, 1),
            new Tetronimo(2, 1)
        );
        
        // assert not translation
        assertFalse(base.isTranslationOf(differentType));
        assertFalse(differentType.isTranslationOf(base));

        // rotation of base
        //   x
        // x x
        // x
        Piece rotation = new Piece(
            new Tetronimo(1, 0),
            new Tetronimo(0, 1),
            new Tetronimo(1, 1),
            new Tetronimo(0, 2)
        );

        // assert not translation
        assertFalse(base.isTranslationOf(rotation));
        assertFalse(rotation.isTranslationOf(base));
    }

    @Test
    public void testCanBeFinished() {
        // (sad) matric not big enough
        Tetronimo[][] smallMatrix = TetrisSolver.createMatrix(3, 1);
        Piece sadPiece = new Piece(
            smallMatrix[0][0],
            smallMatrix[1][0],
            smallMatrix[2][0]
        );
        assertFalse(sadPiece.canBeFinished(smallMatrix));

        // (happy) surrounded by non-associated tetronimos
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Piece p = new Piece(
            matrix[0][0],  // x x x
            matrix[1][0],
            matrix[2][0]
        );
        assertTrue(p.canBeFinished(matrix));

        // (happy) has one adjacent non-associated tetronimo
        // create a complete piece below
        Piece belowPiece = new Piece(
            matrix[0][1],  // x x x
            matrix[1][1],  // o o o o
            matrix[2][1],
            matrix[3][1]
        );
        assertTrue(belowPiece.isComplete());
        assertTrue(p.canBeFinished(matrix));

        // (sad) surrounded by associated tetronimos
        // create an incomplete piece to the right
        Piece rightPiece = new Piece(
            matrix[3][0]  // x x x #
        );                // o o o o
        assertFalse(p.canBeFinished(matrix));
    }

    /**
     * Resets the tetronimos
     * Asserts correct Piece state along the way
     */
    private static void tearDownPiece(Piece p) {
        assert p.isComplete();
        for (int i = Piece.NUM_TETRONIMOS_IN_PIECE - 1; i >= 0; i--) {
            p.remove(dummyTetronimos[i]);
            assertNull(dummyTetronimos[i].piece);
            assertFalse(p.isComplete());
            assertEquals(i, p.getCount());
        }
    }
}
