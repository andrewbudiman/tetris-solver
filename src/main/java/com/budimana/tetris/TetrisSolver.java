package com.budimana.tetris;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Solves the problem described in the README
 * 
 * Input:
 *   dimensions of the matrix
 *   list of defined boundaries (ex boundary: '2,0 3,0')
 *
 * Output:
 *   drawing of the solved matrix and tetris pieces
 *
 * High-level view of algorithm
 *   1 - Traverse the tetronimos left-to-right, top-to-bottom
 *   2 - Each tetronimo can be part of various pieces
 *   3 - Try each piece, recursing onto the next tetronimo in each case
 *   4 - If any restriction is violated, backtrack
 *   5 - If the base tetronimo has no solution, no solution exists
 *
 * Various pieces / cases to try for each tetronimo:
 *   new piece
 *   part of the above tetronimo's piece
 *   part of the left tetronimo's piece
 *   merge the above and left piece
 *
 * State
 *   We keep track of various states of unfinished and finished pieces
 *   For example, if a completed piece is surrounded by other completed pieces
 *     that are of different piece types, we can stop checking that piece
 *     for the restriction that it cannot share an edge with a piece of the
 *     same type
 *
 * Backtracking
 *   When backtracking, we must esnure we revert the state of the matrix and
 *     all the pieces
 *   To accomplish this, any change in state is saved inside a Transition
 *     object that can be used to revert the state
 */
public class TetrisSolver {

    private final Tetronimo[][] matrix;
    private final Map<Tetronimo, List<Tetronimo>> restrictions;

    /**
     * A piece may only exist in one of these states
     *
     *  [unfinished] - 1-3 tetronimos
     *       |
     *       |
     *       V
     *  [unverified] - complete (4 tetronimos)
     *       |
     *       |
     *       V
     *  [verified] - ensured all adjacent pieces are also complete
     *               and are not of the same type
     */
    public final Set<Piece> unfinishedPieces;
    public final Set<Piece> unverifiedPieces;
    public final Set<Piece> verifiedPieces;

    public TetrisSolver(Tetronimo[][] matrix, Map<Tetronimo, List<Tetronimo>> restrictions) {
        this.matrix = matrix;
        this.restrictions = restrictions;
        this.unfinishedPieces = new HashSet<>();
        this.unverifiedPieces = new HashSet<>();
        this.verifiedPieces = new HashSet<>();
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        boolean solutionExists = findSolution(matrix[0][0]);
        long endTime = System.currentTimeMillis();

        System.out.println("solutionExists: " + solutionExists);
        System.out.println("Time taken: " + (endTime - startTime) / 1000.0);

        if (!solutionExists) {
            return;
        }
        prettyPrint(matrix);
    }

    /**
     * DFS of a valid configuration
     * Associates the tetronimo with all possible pieces, one at a time
     * Must keep track of state change so it can be reverted if the branch is
     *   found to be invalid
     */
    public boolean findSolution(Tetronimo currTetronimo) {
        // base case - reached the end
        // success depends on whether all pieces are complete
        if (currTetronimo == null) {
            return unfinishedPieces.isEmpty();
        }
        
        // get the next tetronimo
        Tetronimo nextTetronimo = currTetronimo.getNext(matrix);

        // check to see if there are any unfinished pieces adjacent to
        // the current tetronimo that we can add to
        Tetronimo left = currTetronimo.getLeft(matrix);
        Tetronimo up = currTetronimo.getUp(matrix);
        boolean leftIsValid = isValidAssociation(currTetronimo,
                                                 left,
                                                 restrictions);
        boolean upIsValid = isValidAssociation(currTetronimo,
                                               up,
                                               restrictions);

        // initialize all options this tetronimo has to be part of a piece
        List<Piece[]> options = new ArrayList<>(4);  // max 4 options

        options.add(new Piece[] { new Piece() });  // associate with a new piece
        if (leftIsValid) {  // associate with piece to the left
            options.add(new Piece[] { left.piece });
        }
        if (upIsValid) {  // associate with the piece above
            options.add(new Piece[] { up.piece });
        }
        if (leftIsValid && upIsValid) {  // merge the left and top pieces
            int mergeCount = left.piece.getCount() + up.piece.getCount();
            if (mergeCount < Piece.NUM_TETRONIMOS_IN_PIECE) {
                options.add(new Piece[] { left.piece, up.piece });
            }
        }

        // cycle through the options, check constraints, and recurse
        for (Piece[] pieces : options) {
            assert pieces != null && pieces.length > 0;

            // add to the specified piece
            Transition transition = new Transition();
            addToPiece(currTetronimo, pieces, transition);

            // see if we verified any pieces
            boolean passesConstraints = verifyPieces(transition);

            // keep going if we're still on a valid branch
            if (passesConstraints) {
                boolean pieceWorks = findSolution(nextTetronimo);

                // found a solution
                if (pieceWorks) {
                    return true;
                }
            }

            // didn't find solution, revert and try another branch
            revertTransition(transition);
        }
        
        // none of the possible piece options worked out
        return false;
    }

    /**
     * Returns true if the current tetronimo can try to be added to the
     *   same piece as the candidate tetronimo
     */
    public static boolean isValidAssociation(Tetronimo currTetronimo,
                                             Tetronimo candidate,
                                             Map<Tetronimo, List<Tetronimo>> restrictions) {
        // out of bounds of the matrix
        if (candidate == null) {
            return false;
        }

        assert candidate.piece != null;
        Piece piece = candidate.piece;

        // candidate's piece is full already
        if (piece.isComplete()) {
            return false;
        }

        // check if any tetronimos in the candidate piece are
        //   restricted against the current tetronimo
        for (Tetronimo t : piece.getTetronimos()) {
            if (currTetronimo.isRestricted(t, restrictions)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds the tetronimo to a piece
     * Merges pieces if more than one is specified
     * Saves state changes so it can potentially be reverted later
     */
    public void addToPiece(Tetronimo t,
                           Piece[] pieces,
                           Transition transition) {
        assert pieces != null && pieces.length > 0;

        // add to the first piece
        Piece piece = pieces[0];
        piece.add(t);
        transition.tetronimo = t;

        // optionally merge other pieces
        for (int i = 1; i < pieces.length; i++) {
            Piece mergePiece = pieces[i];
            assert unfinishedPieces.contains(mergePiece);

            // remove the piece
            unfinishedPieces.remove(mergePiece);
            transition.oldUnfinishedPieces.add(mergePiece);

            // add the tetronimos to the base piece
            // need to remove them via Piece.remove
            // but also avoid concurrent modification with new list
            List<Tetronimo> savedMergedPieces = new ArrayList<>(mergePiece.getCount());
            for (Tetronimo newT : new ArrayList<>(mergePiece.getTetronimos())) {
                mergePiece.remove(newT);
                savedMergedPieces.add(newT);
                piece.add(newT);
            }

            // save merge transition
            transition.mergedPieces.put(mergePiece, savedMergedPieces);
        }

        // update the state structures
        if (piece.isComplete()) {
            // can't go from a brand new piece to complete
            assert unfinishedPieces.contains(piece);

            // remove from 'unfinished' state
            unfinishedPieces.remove(piece);
            transition.oldUnfinishedPieces.add(piece);

            // add to 'finished but unverified' state
            unverifiedPieces.add(piece);
            transition.newUnverifiedPieces.add(piece);
        } else if (!unfinishedPieces.contains(piece)) {
            unfinishedPieces.add(piece);
            transition.newUnfinishedPieces.add(piece);
        }  // else piece started as 'unfinished' and stayed there
    }

    /**
     * Verifies that the current piece configuration doesn't violate
     *   any of the constraints
     * Updates the state of the pieces and saves the transition in
     *   case we need to revert
     */
    public boolean verifyPieces(Transition transition) {

        // check if unfinished pieces cannot be finished
        for (Piece p : unfinishedPieces) {
            if (!p.canBeFinished(matrix)) {
                return false;
            }
        }

        /**
         * 3 ending states for unverified pieces
         *  there exists some adjacent piece of same type -> fail verification
         *  adjacent pieces are complete and different type -> verified
         *  adjacent pieces are different type or unfinished -> stay unverified
         */
        List<Piece> newVerifiedPieces = new ArrayList<>();
        for (Piece p : unverifiedPieces) {
            boolean verified = true;
            for (Tetronimo adjT : p.getAdjacentTetronimos(matrix)) {
                Piece adjPiece = adjT.piece;

                if (adjPiece == null || !adjPiece.isComplete()) {
                    verified = false;  // remain unverified
                } else if (adjPiece.getType() == p.getType()) {
                    return false;  // fail verification
                }
            }
            
            // do later so we don't modify unverifiedPices while iterating
            if (verified) {
                newVerifiedPieces.add(p);
            }
        }

        for (Piece p : newVerifiedPieces) {
            // remove from unverified state
            unverifiedPieces.remove(p);
            transition.oldUnverifiedPieces.add(p);

            // add to verified state
            verifiedPieces.add(p);
            transition.newVerifiedPieces.add(p);
        }

        return true;
    }

    /**
     * Reverts the tetronimos and pieces based on the transition
     */
    public void revertTransition(Transition t) {
        assert t.tetronimo != null;
        assert t.tetronimo.piece != null;
        Piece piece = t.tetronimo.piece;
        
        // reset the tetronimo that was associated
        piece.remove(t.tetronimo);

        // revert any merged pieces
        for (Entry<Piece, List<Tetronimo>> entry : t.mergedPieces.entrySet()) {
            Piece mergePiece = entry.getKey();
            List<Tetronimo> tetronimos = entry.getValue();

            for (Tetronimo mergedT : tetronimos) {
                piece.remove(mergedT);
                mergePiece.add(mergedT);
            }
        }

        // remove pieces from their new states
        for (Piece p : t.newUnfinishedPieces) {
            assert unfinishedPieces.contains(p);
            unfinishedPieces.remove(p);
        }
        for (Piece p : t.newUnverifiedPieces) {
            assert unverifiedPieces.contains(p);
            unverifiedPieces.remove(p);
        }
        for (Piece p : t.newVerifiedPieces) {
            assert verifiedPieces.contains(p);
            verifiedPieces.remove(p);
        }

        // add pieces to their previous states
        for (Piece p : t.oldUnfinishedPieces) {
            assert !unfinishedPieces.contains(p);
            unfinishedPieces.add(p);
        }
        for (Piece p : t.oldUnverifiedPieces) {
            assert !unverifiedPieces.contains(p);
            unverifiedPieces.add(p);
        }

    }

    /**
     * Creates an empty canvas matrix with tetronimos initialized
     */
    public static Tetronimo[][] createMatrix(int width, int height) {
        Tetronimo[][] matrix = new Tetronimo[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                matrix[x][y] = new Tetronimo(x,y);
            }
        }
        return matrix;
    }

    public static void main(String[] args) throws Exception {
        assert args != null && args.length == 3;
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        String restrictionsFilename = args[2];

        // create matrix
        System.out.println("Creating matrix. width: " + width +
                           ", height: " + height);
        Tetronimo[][] matrix = createMatrix(width, height);

        // parse restrictions
        System.out.println("Parsing restrictions...");
        Map<Tetronimo, List<Tetronimo>> restrictions = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(restrictionsFilename));
        String line;
        int numRestrictions = 0;
        while ((line = br.readLine()) != null) {
            String[] tetronimos = line.split(" ");

            // parse each tetronimo's coordinates
            String[] coordinates1 = tetronimos[0].split(",");
            String[] coordinates2 = tetronimos[1].split(",");
            int x1 = Integer.parseInt(coordinates1[0]);
            int y1 = Integer.parseInt(coordinates1[1]);
            int x2 = Integer.parseInt(coordinates2[0]);
            int y2 = Integer.parseInt(coordinates2[1]);

            // get the corresponding tetronimos from the matrix
            Tetronimo t1 = matrix[x1][y1];
            Tetronimo t2 = matrix[x2][y2];

            assert t1.getAllAdjacentTetronimos(matrix).contains(t2);

            addToRestrictionList(t1, t2, restrictions);
            addToRestrictionList(t2, t1, restrictions);

            numRestrictions++;
        }
        System.out.println("Restrictions parsed. " +
                           numRestrictions + " entries");

        // create the solver and run
        System.out.println("Solving the puzzle now...");
        TetrisSolver solver = new TetrisSolver(matrix, restrictions);
        solver.run();
    }

    private static void addToRestrictionList(Tetronimo t1,
                                             Tetronimo t2,
                                             Map<Tetronimo, List<Tetronimo>> restrictions) {
        List<Tetronimo> t1RestrictionList = restrictions.get(t1);
        if (t1RestrictionList == null) {
            t1RestrictionList = new ArrayList<>();
            restrictions.put(t1, t1RestrictionList);
        }
        t1RestrictionList.add(t2);
    }

    /**
     * Not much effort in the code below
     * Just used to format the solution
     * Not required for correctness
     */

    public void prettyPrint(Tetronimo[][] matrix) {

        int pieceCounter = 0;
        Map<Piece, Integer> pieceMap = new HashMap<>();
        for (int j = 0; j <= matrix[0].length; j++) {

            // print dividers if appropriate
            for (int i = -1; i < matrix.length; i++) {
                Piece p = i < 0 || j == matrix[0].length ? null : matrix[i][j].piece;
                Piece upPiece = j == 0 || i < 0 ? null : matrix[i][j-1].piece;

                if (p == upPiece) {
                    System.out.print("  ");
                } else {
                    System.out.print("\u2500\u2500");
                }

                Piece rightPiece = i == matrix.length - 1 || j == matrix[0].length
                                       ? null
                                       : matrix[i+1][j].piece;
                Piece diagonalPiece = i == matrix.length - 1 || j == 0
                                          ? null
                                          : matrix[i+1][j-1].piece;
                String corner = getCornerMarker(upPiece,
                                                diagonalPiece,
                                                p,
                                                rightPiece);
                System.out.print(" " + corner + " ");
            }
            System.out.println();

            // print tetronimos
            if (j == matrix[0].length) {
                continue;
            }
            for (int i = -1; i < matrix.length; i++) {
                Piece p = i < 0 ? null : matrix[i][j].piece;
                Piece leftPiece = i <= 0 ? null : matrix[i-1][j].piece;

                // print divider if appropriate
                if (p != null) {
                    if (p == leftPiece) {
                        System.out.print("   ");
                    } else {
                        System.out.print(" \u2503 ");
                    }
                }

                // get piece number or create it
                if (p != null) {
                    Integer pieceNumber = pieceMap.get(p);
                    if (pieceNumber == null) {
                        pieceNumber = pieceCounter++;
                        pieceMap.put(p, pieceNumber);
                    }
                    System.out.format("%2d", pieceNumber);
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println(" \u2503 ");
        }
    }

    private static String getCornerMarker(Piece topLeft,
                                          Piece topRight,
                                          Piece bottomLeft,
                                          Piece bottomRight) {
        byte cornerKey = (byte) 0;
        Map<Piece, Integer> pieceTracker = new HashMap<>(4);
        int uniquePieceCounter = 0;

        List<Piece> pieces = Arrays.asList(topLeft,
                                           topRight,
                                           bottomLeft,
                                           bottomRight);
        int i = 0;
        for (Piece p : pieces) {
            Integer pNum = pieceTracker.get(p);
            if (pNum == null) {
                pNum = uniquePieceCounter++;
                pieceTracker.put(p, pNum);
            }

            // can only have 4 unique pices
            assert pNum >= 0 && pNum < 4;

            cornerKey |= (pNum << (i * 2));

            i++;
        }

        String markerValue = cornerMarkers.get(cornerKey);
        assert markerValue != null;
        return markerValue;
    }

    private static final Map<Byte, String> cornerMarkers = new HashMap<>();
    static {
        // all same
        cornerMarkers.put((byte) 0, " ");

        // one different
        cornerMarkers.put((byte) (1 << 2), "\u2514");
        cornerMarkers.put((byte) (1 << 4), "\u2510");
        cornerMarkers.put((byte) (1 << 6), "\u250C");
        cornerMarkers.put((byte) ((1 << 2) |
                                  (1 << 4) |
                                  (1 << 6)), "\u2518");

        // two pieces, two tetronimos each
        cornerMarkers.put((byte) ((1 << 4) |
                                  (1 << 6)), "\u2500");
        cornerMarkers.put((byte) ((1 << 2) |
                                  (1 << 6)), "\u2503");

        // three pieces
        cornerMarkers.put((byte) ((1 << 4) |
                                  (2 << 6)), "\u252C");
        cornerMarkers.put((byte) ((1 << 2) |
                                  (2 << 6)), "\u251C");
        cornerMarkers.put((byte) ((1 << 2) |
                                  (2 << 4) |
                                  (1 << 6)), "\u2524");
        cornerMarkers.put((byte) ((1 << 2) |
                                  (2 << 4) |
                                  (2 << 6)), "\u2534");

        // four pieces
        cornerMarkers.put((byte) ((1 << 2) |
                                  (2 << 4) |
                                  (3 << 6)), "\u254B");
    }
}
