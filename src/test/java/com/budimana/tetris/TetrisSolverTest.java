package com.budimana.tetris;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class TetrisSolverTest {

    @Test
    public void testValidAssociation_NullCandidate() {
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        Tetronimo t = matrix[0][0];

        Tetronimo candidate = null;

        // null candidate
        assertFalse(TetrisSolver.isValidAssociation(t, candidate, restrictions));
    }

    @Test
    public void testValidAssociation_FullCandidate() {
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        Tetronimo t = matrix[0][1];
        
        // candidate piece is full
        Piece p = new Piece(
            matrix[0][0],
            matrix[1][0],
            matrix[2][0],
            matrix[3][0]
        );
        Tetronimo candidate = matrix[0][0];
        assertFalse(TetrisSolver.isValidAssociation(t, candidate, restrictions));
    }

    @Test
    public void testValidAssociation_RestrictedCandidate() {
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        Tetronimo t = matrix[1][1];

        // candidate is restricted
        Piece p = new Piece(
            matrix[0][0],
            matrix[1][0],
            matrix[0][1]
        );
        Tetronimo candidate = matrix[1][0];
        restrictions.put(t, Arrays.asList(candidate));
        assertFalse(TetrisSolver.isValidAssociation(t, candidate, restrictions));

        // another tetronimo in the candidate's piece is restricted
        restrictions.clear();
        restrictions.put(t, Arrays.asList(matrix[0][1]));
        assertFalse(TetrisSolver.isValidAssociation(t, candidate, restrictions));
    }

    @Test
    public void testAddToPiece_New() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // create a blank piece
        Piece piece = new Piece();
        Piece[] pieces = new Piece[] { piece };
        Transition transition = new Transition();

        // add the tetronimo to the piece
        Tetronimo currTetronimo = matrix[1][1];
        ts.addToPiece(currTetronimo, pieces, transition);

        // verify tetronimo and piece
        assertEquals(piece, currTetronimo.piece);
        assertEquals(1, piece.getCount());
        assertTrue(piece.getTetronimos().contains(currTetronimo));
        assertFalse(piece.isComplete());

        // verify state
        assertEquals(1, ts.unfinishedPieces.size());
        assertTrue(ts.unfinishedPieces.contains(piece));
        assertTrue(ts.unverifiedPieces.isEmpty());
        assertTrue(ts.verifiedPieces.isEmpty());
        
        // verify transition
        assertEquals(currTetronimo, transition.tetronimo);
        assertEquals(1, transition.newUnfinishedPieces.size());
        assertTrue(transition.newUnfinishedPieces.contains(piece));

        assertTrue(transition.mergedPieces.isEmpty());
        assertTrue(transition.oldUnfinishedPieces.isEmpty());
        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    /**
     * Add to an existing piece that will still be incomplete after
     *   adding a new tetronimo
     */
    @Test
    public void testAddToPiece_ExistingIncomplete() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // create a piece with one tetronimo
        Piece piece = new Piece(matrix[1][0]);
        ts.unfinishedPieces.add(piece);
        Piece[] pieces = new Piece[] { piece };
        Transition transition = new Transition();

        // add the tetronimo to the piece
        Tetronimo currTetronimo = matrix[1][1];
        ts.addToPiece(currTetronimo, pieces, transition);

        // verify tetronimo and piece
        assertEquals(piece, currTetronimo.piece);
        assertEquals(2, piece.getCount());
        assertTrue(piece.getTetronimos().contains(currTetronimo));
        assertFalse(piece.isComplete());

        // verify state
        assertEquals(1, ts.unfinishedPieces.size());
        assertTrue(ts.unfinishedPieces.contains(piece));
        assertTrue(ts.unverifiedPieces.isEmpty());
        assertTrue(ts.verifiedPieces.isEmpty());
        
        // verify transition
        assertEquals(currTetronimo, transition.tetronimo);
        assertTrue(transition.mergedPieces.isEmpty());
        assertTrue(transition.oldUnfinishedPieces.isEmpty());
        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    /**
     * Add to an existing piece that will be complete after
     *   adding a new tetronimo
     */
    @Test
    public void testAddToPiece_ExistingComplete() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // create a piece with three tetronimo
        Piece piece = new Piece(
            matrix[0][0],  // x x
            matrix[1][0],  // x
            matrix[0][1]
        );
        ts.unfinishedPieces.add(piece);
        Piece[] pieces = new Piece[] { piece };
        Transition transition = new Transition();

        // add the tetronimo to the piece
        Tetronimo currTetronimo = matrix[1][1];
        ts.addToPiece(currTetronimo, pieces, transition);

        // verify tetronimo and piece
        assertEquals(piece, currTetronimo.piece);
        assertEquals(4, piece.getCount());
        assertTrue(piece.getTetronimos().contains(currTetronimo));
        assertTrue(piece.isComplete());

        // verify state
        assertTrue(ts.unfinishedPieces.isEmpty());

        assertEquals(1, ts.unverifiedPieces.size());
        assertTrue(ts.unverifiedPieces.contains(piece));

        assertTrue(ts.verifiedPieces.isEmpty());
        
        // verify transition
        assertEquals(currTetronimo, transition.tetronimo);

        assertEquals(1, transition.oldUnfinishedPieces.size());
        assertTrue(transition.oldUnfinishedPieces.contains(piece));

        assertEquals(1, transition.newUnverifiedPieces.size());
        assertTrue(transition.newUnverifiedPieces.contains(piece));

        assertTrue(transition.mergedPieces.isEmpty());
        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    /**
     * Merge two existing pieces that will still be incomplete after
     *   adding a new tetronimo
     */
    @Test
    public void testAddToPiece_MergeIncomplete() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // create two pieces with one tetronimo each
        Piece piece1 = new Piece(matrix[1][0]);
        Piece piece2 = new Piece(matrix[0][1]);
        ts.unfinishedPieces.add(piece1);
        ts.unfinishedPieces.add(piece2);
        Piece[] pieces = new Piece[] { piece1, piece2 };
        Transition transition = new Transition();

        // add the tetronimo to the pieces
        Tetronimo currTetronimo = matrix[1][1];
        ts.addToPiece(currTetronimo, pieces, transition);

        // verify tetronimo and piece
        assertEquals(piece1, currTetronimo.piece);
        // piece1
        assertEquals(3, piece1.getCount());
        assertTrue(piece1.getTetronimos().contains(currTetronimo));
        assertFalse(piece1.isComplete());
        // piece2
        assertEquals(0, piece2.getCount());
        assertFalse(piece2.getTetronimos().contains(currTetronimo));
        assertFalse(piece2.isComplete());

        // verify state
        assertEquals(1, ts.unfinishedPieces.size());
        assertTrue(ts.unfinishedPieces.contains(piece1));
        assertTrue(ts.unverifiedPieces.isEmpty());
        assertTrue(ts.verifiedPieces.isEmpty());
        
        // verify transition
        assertEquals(currTetronimo, transition.tetronimo);

        assertEquals(1, transition.mergedPieces.size());
        assertNotNull(transition.mergedPieces.get(piece2));

        assertEquals(1, transition.oldUnfinishedPieces.size());
        assertTrue(transition.oldUnfinishedPieces.contains(piece2));

        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    /**
     * Merge two existing pieces that will be complete after
     *   adding a new tetronimo
     */
    @Test
    public void testAddToPiece_MergeComplete() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // create two pieces with two and one tetronimos respectively
        Piece piece1 = new Piece(matrix[0][0], matrix[1][0]);
        Piece piece2 = new Piece(matrix[0][1]);
        ts.unfinishedPieces.add(piece1);
        ts.unfinishedPieces.add(piece2);
        Piece[] pieces = new Piece[] { piece1, piece2 };
        Transition transition = new Transition();

        // add the tetronimo to the pieces
        Tetronimo currTetronimo = matrix[1][1];
        ts.addToPiece(currTetronimo, pieces, transition);

        // verify tetronimo and piece
        assertEquals(piece1, currTetronimo.piece);
        // piece1
        assertEquals(4, piece1.getCount());
        assertTrue(piece1.getTetronimos().contains(currTetronimo));
        assertTrue(piece1.isComplete());
        // piece2
        assertEquals(0, piece2.getCount());
        assertFalse(piece2.getTetronimos().contains(currTetronimo));
        assertFalse(piece2.isComplete());

        // verify state
        assertTrue(ts.unfinishedPieces.isEmpty());
        assertEquals(1, ts.unverifiedPieces.size());
        assertTrue(ts.verifiedPieces.isEmpty());
        
        // verify transition
        assertEquals(currTetronimo, transition.tetronimo);

        assertEquals(1, transition.mergedPieces.size());
        assertNotNull(transition.mergedPieces.get(piece2));

        assertEquals(2, transition.oldUnfinishedPieces.size());
        assertTrue(transition.oldUnfinishedPieces.contains(piece1));
        assertTrue(transition.oldUnfinishedPieces.contains(piece2));

        assertEquals(1, transition.newUnverifiedPieces.size());
        assertTrue(transition.newUnverifiedPieces.contains(piece1));

        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    @Test
    public void testVerifyPieces_IsolatedUnfinished() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);
        Transition transition = new Transition();

        // create an isolated unfinished piece
        Piece isolated = new Piece(matrix[0][0]);
        Piece surrounding = new Piece(
            matrix[1][0],
            matrix[0][1],
            matrix[1][1]
        );
        ts.unfinishedPieces.add(isolated);
        ts.unfinishedPieces.add(surrounding);

        // verify should fail
        assertFalse(ts.verifyPieces(transition));

        // verify state
        assertEquals(2, ts.unfinishedPieces.size());
        assertTrue(ts.unfinishedPieces.contains(isolated));
        assertTrue(ts.unfinishedPieces.contains(surrounding));
        assertTrue(ts.unverifiedPieces.isEmpty());
        assertTrue(ts.verifiedPieces.isEmpty());

        // verify transition
        assertTrue(transition.oldUnfinishedPieces.isEmpty());
        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    @Test
    public void testVerifyPieces_NonIsolatedUnfinished() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);
        Transition transition = new Transition();

        // create non-isolated unfinished pieces
        Piece piece1 = new Piece(matrix[0][0]);
        Piece piece2 = new Piece(
            matrix[0][1],
            matrix[1][1]
        );
        ts.unfinishedPieces.add(piece1);
        ts.unfinishedPieces.add(piece2);

        // verify should succeed
        assertTrue(ts.verifyPieces(transition));

        // verify state
        assertEquals(2, ts.unfinishedPieces.size());
        assertTrue(ts.unfinishedPieces.contains(piece1));
        assertTrue(ts.unfinishedPieces.contains(piece2));
        assertTrue(ts.unverifiedPieces.isEmpty());
        assertTrue(ts.verifiedPieces.isEmpty());

        // verify transition
        assertTrue(transition.oldUnfinishedPieces.isEmpty());
        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    @Test
    public void testVerifyPieces_Unverified() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);
        Transition transition = new Transition();

        // create multiple unverified pieces, one of which can be verified
        Piece piece1 = new Piece(
            matrix[0][0],  // x x
            matrix[1][0],  // x x
            matrix[0][1],
            matrix[1][1]
        );
        Piece piece2 = new Piece(
            matrix[2][0],  // x x x
            matrix[3][0],  // x
            matrix[4][0],
            matrix[2][1]
        );
        Piece piece3 = new Piece(
            matrix[0][2],  // x x
            matrix[1][2],  // x
            matrix[0][3],  // x
            matrix[0][4]
        );
        ts.unverifiedPieces.add(piece1);
        ts.unverifiedPieces.add(piece2);
        ts.unverifiedPieces.add(piece3);

        // verify should succeed
        assertTrue(ts.verifyPieces(transition));

        // verify state
        assertTrue(ts.unfinishedPieces.isEmpty());

        assertEquals(2, ts.unverifiedPieces.size());
        assertTrue(ts.unverifiedPieces.contains(piece2));
        assertTrue(ts.unverifiedPieces.contains(piece3));

        assertEquals(1, ts.verifiedPieces.size());
        assertTrue(ts.verifiedPieces.contains(piece1));

        // verify transition
        assertTrue(transition.oldUnfinishedPieces.isEmpty());

        assertEquals(1, transition.oldUnverifiedPieces.size());
        assertTrue(transition.oldUnverifiedPieces.contains(piece1));

        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());

        assertEquals(1, transition.newVerifiedPieces.size());
        assertTrue(transition.newVerifiedPieces.contains(piece1));
    }

    @Test
    public void testVerifyPieces_Verified() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);
        Transition transition = new Transition();

        // create one verified and two unverified pieces
        Piece piece1 = new Piece(
            matrix[0][0],  // x x
            matrix[1][0],  // x x
            matrix[0][1],
            matrix[1][1]
        );
        Piece piece2 = new Piece(
            matrix[2][0],  // x x x
            matrix[3][0],  // x
            matrix[4][0],
            matrix[2][1]
        );
        Piece piece3 = new Piece(
            matrix[0][2],  // x x
            matrix[1][2],  // x
            matrix[0][3],  // x
            matrix[0][4]
        );
        ts.verifiedPieces.add(piece1);
        ts.unverifiedPieces.add(piece2);
        ts.unverifiedPieces.add(piece3);

        // verify should succeed
        assertTrue(ts.verifyPieces(transition));

        // verify state
        assertTrue(ts.unfinishedPieces.isEmpty());

        assertEquals(2, ts.unverifiedPieces.size());
        assertTrue(ts.unverifiedPieces.contains(piece2));
        assertTrue(ts.unverifiedPieces.contains(piece3));

        assertEquals(1, ts.verifiedPieces.size());
        assertTrue(ts.verifiedPieces.contains(piece1));

        // verify transition
        assertTrue(transition.oldUnfinishedPieces.isEmpty());
        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    @Test
    public void testVerifyPieces_AdjacentSameType() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(9, 9);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);
        Transition transition = new Transition();

        // create two same-type pieces adjacent to each other
        Piece piece1 = new Piece(
            matrix[0][0],  // x x
            matrix[1][0],  // x x
            matrix[0][1],
            matrix[1][1]
        );
        Piece piece2 = new Piece(
            matrix[2][0],  // x x
            matrix[3][0],  // x x
            matrix[2][1],
            matrix[3][1]
        );
        ts.unverifiedPieces.add(piece1);
        ts.unverifiedPieces.add(piece2);

        // verify should fail
        assertFalse(ts.verifyPieces(transition));

        // verify state
        assertTrue(ts.unfinishedPieces.isEmpty());

        assertEquals(2, ts.unverifiedPieces.size());
        assertTrue(ts.unverifiedPieces.contains(piece1));
        assertTrue(ts.unverifiedPieces.contains(piece2));

        assertTrue(ts.verifiedPieces.isEmpty());

        // verify transition
        assertTrue(transition.oldUnfinishedPieces.isEmpty());
        assertTrue(transition.oldUnverifiedPieces.isEmpty());
        assertTrue(transition.newUnfinishedPieces.isEmpty());
        assertTrue(transition.newUnverifiedPieces.isEmpty());
        assertTrue(transition.newVerifiedPieces.isEmpty());
    }

    @Test
    public void testRevertTransition() {
        // tetronimo
        {
            // initialize a dummy tetris solver and blank transition
            TetrisSolver ts = new TetrisSolver(null, null);
            Transition transition = new Transition();

            // add a tetronimo to a new piece
            Tetronimo currTetronimo = new Tetronimo(0, 0);
            Piece piece = new Piece(currTetronimo);
            transition.tetronimo = currTetronimo;
            
            // revert
            ts.revertTransition(transition);

            // verify tetronimo state
            assertNull(currTetronimo.piece);
            assertFalse(piece.getTetronimos().contains(currTetronimo));
        }

        // merged pieces
        {
            // initialize a dummy tetris solver and blank transition
            TetrisSolver ts = new TetrisSolver(null, null);
            Transition transition = new Transition();

            // merge a tetronimo with two unfinished pieces
            Tetronimo t1 = new Tetronimo(1, 0);
            Tetronimo t2 = new Tetronimo(0, 1);
            Piece piece1 = new Piece(t1);
            Piece piece2 = new Piece(t2);

            Tetronimo currTetronimo = new Tetronimo(1, 1);
            transition.tetronimo = currTetronimo;
            transition.mergedPieces.put(piece2,
                                        new ArrayList<>(piece2.getTetronimos()));

            piece2.remove(t2);
            piece1.add(t2);
            piece1.add(currTetronimo);
            
            // revert
            ts.revertTransition(transition);

            // verify tetronimo state
            assertNull(currTetronimo.piece);
            assertFalse(piece1.getTetronimos().contains(currTetronimo));
            assertFalse(piece2.getTetronimos().contains(currTetronimo));

            // verify piece1 state
            assertEquals(piece1, t1.piece);
            assertEquals(1, piece1.getCount());
            assertTrue(piece1.getTetronimos().contains(t1));

            // verify piece1 state
            assertEquals(piece2, t2.piece);
            assertEquals(1, piece2.getCount());
            assertTrue(piece2.getTetronimos().contains(t2));
        }

        // changes in state
        {
            // initialize a dummy tetris solver and blank transition
            TetrisSolver ts = new TetrisSolver(null, null);
            Transition transition = new Transition();

            // need a tetronimo to revert as well (invariant)
            Tetronimo currentTetronimo = new Tetronimo(0, 0);
            Piece p = new Piece(currentTetronimo);
            transition.tetronimo = currentTetronimo;

            // create pieces for each state transition
            Piece newUnfinished = new Piece();
            Piece newUnverified = new Piece();
            Piece newVerified = new Piece();

            // setup the states and the transition
            ts.unfinishedPieces.add(newUnfinished);
            transition.newUnfinishedPieces.add(newUnfinished);

            ts.unverifiedPieces.add(newUnverified);
            transition.oldUnfinishedPieces.add(newUnverified);
            transition.newUnverifiedPieces.add(newUnverified);

            ts.verifiedPieces.add(newVerified);
            transition.oldUnverifiedPieces.add(newVerified);
            transition.newVerifiedPieces.add(newVerified);
            
            // revert
            ts.revertTransition(transition);

            // verify state
            assertEquals(1, ts.unfinishedPieces.size());
            assertTrue(ts.unfinishedPieces.contains(newUnverified));

            assertEquals(1, ts.unverifiedPieces.size());
            assertTrue(ts.unverifiedPieces.contains(newVerified));

            assertTrue(ts.verifiedPieces.isEmpty());
        }
    }

    @Test
    public void testFindSolution_OnePieceBOX() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(2, 2);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // solve
        assertTrue(ts.findSolution(matrix[0][0]));

        // verify correct pieces
        assertEquals(PieceType.BOX, matrix[0][0].piece.getType());
    }

    @Test
    public void testFindSolution_OnePieceI() {
        // horizontal
        {
            // initialize the tetris solver
            Tetronimo[][] matrix = TetrisSolver.createMatrix(4, 1);
            Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
            TetrisSolver ts = new TetrisSolver(matrix, restrictions);

            // solve
            assertTrue(ts.findSolution(matrix[0][0]));

            // verify correct pieces
            assertEquals(PieceType.I, matrix[0][0].piece.getType());
        }

        // vertical
        {
            // initialize the tetris solver
            Tetronimo[][] matrix = TetrisSolver.createMatrix(1, 4);
            Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
            TetrisSolver ts = new TetrisSolver(matrix, restrictions);

            // solve
            assertTrue(ts.findSolution(matrix[0][0]));

            // verify correct pieces
            assertEquals(PieceType.I, matrix[0][0].piece.getType());
        }
    }

    @Test
    public void testFindSolution_UnsolvableDimensions() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(3, 2);
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // solve
        assertFalse(ts.findSolution(matrix[0][0]));
    }

    @Test
    public void testFindSolution_UnsolvableRestrictions() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(4, 2);

        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        restrictions.put(matrix[0][0], Arrays.asList(matrix[0][1]));  // x   x | x   x
        restrictions.put(matrix[1][0], Arrays.asList(matrix[2][0]));  // -
                                                                      // x   x   x   x

        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // solve
        assertFalse(ts.findSolution(matrix[0][0]));
    }

    @Test
    public void testFindSolution_MergeRequired() {
        // initialize the tetris solver
        Tetronimo[][] matrix = TetrisSolver.createMatrix(4, 3);

        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        restrictions.put(matrix[0][0], Arrays.asList(matrix[1][0]));  // x | x   x | x
        restrictions.put(matrix[2][0], Arrays.asList(matrix[3][0]));  //
        restrictions.put(matrix[0][1], Arrays.asList(matrix[1][1]));  // x | x   x | x
        restrictions.put(matrix[2][1], Arrays.asList(matrix[3][1]));  //
        restrictions.put(matrix[1][2], Arrays.asList(matrix[2][2]));  // x   x | x   x

        TetrisSolver ts = new TetrisSolver(matrix, restrictions);

        // solve
        assertTrue(ts.findSolution(matrix[0][0]));

        // verify correct pieces
        assertEquals(PieceType.L2, matrix[0][0].piece.getType());
        assertEquals(PieceType.BOX, matrix[1][0].piece.getType());
        assertEquals(PieceType.L1, matrix[3][0].piece.getType());
    }
}
