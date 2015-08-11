package com.budimana.tetris;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PieceTypeTest {
    
    @Test
    public void testTPiece() {
        // x
        // x x
        // x
        Piece p0 = new Piece(new Tetronimo(0, 0),
                             new Tetronimo(0, 1),
                             new Tetronimo(1, 1),
                             new Tetronimo(0, 2));
        assertEquals(PieceType.T, PieceType.classifyPiece(p0));

        // x x x
        //   x
        Piece p90 = new Piece(new Tetronimo(0, 0),
                              new Tetronimo(1, 0),
                              new Tetronimo(2, 0),
                              new Tetronimo(1, 1));
        assertEquals(PieceType.T, PieceType.classifyPiece(p90));

        //   x
        // x x
        //   x
        Piece p180 = new Piece(new Tetronimo(1, 0),
                               new Tetronimo(0, 1),
                               new Tetronimo(1, 1),
                               new Tetronimo(1, 2));
        assertEquals(PieceType.T, PieceType.classifyPiece(p180));

        //   x
        // x x x
        Piece p270 = new Piece(new Tetronimo(1, 0),
                               new Tetronimo(0, 1),
                               new Tetronimo(1, 1),
                               new Tetronimo(2, 1));
        assertEquals(PieceType.T, PieceType.classifyPiece(p270));
    }

    @Test
    public void testL1Piece() {
        // x x x
        //     x
        Piece p0 = new Piece(new Tetronimo(0, 0),
                             new Tetronimo(1, 0),
                             new Tetronimo(2, 0),
                             new Tetronimo(2, 1));
        assertEquals(PieceType.L1, PieceType.classifyPiece(p0));

        //   x
        //   x
        // x x
        Piece p90 = new Piece(new Tetronimo(1, 0),
                              new Tetronimo(1, 1),
                              new Tetronimo(0, 2),
                              new Tetronimo(1, 2));
        assertEquals(PieceType.L1, PieceType.classifyPiece(p90));

        // x
        // x x x
        Piece p180 = new Piece(new Tetronimo(0, 0),
                               new Tetronimo(0, 1),
                               new Tetronimo(1, 1),
                               new Tetronimo(2, 1));
        assertEquals(PieceType.L1, PieceType.classifyPiece(p180));

        // x x
        // x
        // x
        Piece p270 = new Piece(new Tetronimo(0, 0),
                               new Tetronimo(1, 0),
                               new Tetronimo(0, 1),
                               new Tetronimo(0, 2));
        assertEquals(PieceType.L1, PieceType.classifyPiece(p270));
    }

    @Test
    public void testL2Piece() {
        // x
        // x
        // x x
        Piece p0 = new Piece(new Tetronimo(0, 0),
                             new Tetronimo(0, 1),
                             new Tetronimo(0, 2),
                             new Tetronimo(1, 2));
        assertEquals(PieceType.L2, PieceType.classifyPiece(p0));

        // x x x
        // x
        Piece p90 = new Piece(new Tetronimo(0, 0),
                              new Tetronimo(1, 0),
                              new Tetronimo(2, 0),
                              new Tetronimo(0, 1));
        assertEquals(PieceType.L2, PieceType.classifyPiece(p90));

        // x x
        //   x
        //   x
        Piece p180 = new Piece(new Tetronimo(0, 0),
                               new Tetronimo(1, 0),
                               new Tetronimo(1, 1),
                               new Tetronimo(1, 2));
        assertEquals(PieceType.L2, PieceType.classifyPiece(p180));

        //     x
        // x x x
        Piece p270 = new Piece(new Tetronimo(2, 0),
                               new Tetronimo(0, 1),
                               new Tetronimo(1, 1),
                               new Tetronimo(2, 1));
        assertEquals(PieceType.L2, PieceType.classifyPiece(p270));
    }

    @Test
    public void testN1Piece() {
        // x
        // x x
        //   x
        Piece p0 = new Piece(new Tetronimo(0, 0),
                             new Tetronimo(0, 1),
                             new Tetronimo(1, 1),
                             new Tetronimo(1, 2));
        assertEquals(PieceType.N1, PieceType.classifyPiece(p0));

        //   x x
        // x x
        Piece p90 = new Piece(new Tetronimo(1, 0),
                              new Tetronimo(2, 0),
                              new Tetronimo(0, 1),
                              new Tetronimo(1, 1));
        assertEquals(PieceType.N1, PieceType.classifyPiece(p90));
    }

    @Test
    public void testN2Piece() {
        // x x
        //   x x
        Piece p0 = new Piece(new Tetronimo(0, 0),
                             new Tetronimo(1, 0),
                             new Tetronimo(1, 1),
                             new Tetronimo(2, 1));
        assertEquals(PieceType.N2, PieceType.classifyPiece(p0));

        //   x
        // x x
        // x
        Piece p90 = new Piece(new Tetronimo(1, 0),
                              new Tetronimo(0, 1),
                              new Tetronimo(1, 1),
                              new Tetronimo(0, 2));
        assertEquals(PieceType.N2, PieceType.classifyPiece(p90));
    }

    @Test
    public void testIPiece() {
        // x
        // x
        // x
        // x
        Piece p0 = new Piece(new Tetronimo(0, 0),
                             new Tetronimo(0, 1),
                             new Tetronimo(0, 2),
                             new Tetronimo(0, 3));
        assertEquals(PieceType.I, PieceType.classifyPiece(p0));

        // x x x x
        Piece p90 = new Piece(new Tetronimo(0, 0),
                              new Tetronimo(1, 0),
                              new Tetronimo(2, 0),
                              new Tetronimo(3, 0));
        assertEquals(PieceType.I, PieceType.classifyPiece(p90));
    }

    @Test
    public void testBOXPiece() {
        // x x
        // x x
        Piece p0 = new Piece(new Tetronimo(0, 0),
                             new Tetronimo(1, 0),
                             new Tetronimo(0, 1),
                             new Tetronimo(1, 1));
        assertEquals(PieceType.BOX, PieceType.classifyPiece(p0));
    }
}
