package com.budimana.tetris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Saves a set of changes from one state to another so it can be reverted
 *   if we need to backtrack
 */
public class Transition {

    // made public for ease of use
    // in practice though, very few of these will be used so
    //   pre-initializing these data structures is wasteful
    public Tetronimo tetronimo;
    public Map<Piece, List<Tetronimo>> mergedPieces = new HashMap<>();
    public List<Piece> oldUnfinishedPieces = new ArrayList<>();
    public List<Piece> oldUnverifiedPieces = new ArrayList<>();
    public List<Piece> newUnfinishedPieces = new ArrayList<>();
    public List<Piece> newUnverifiedPieces = new ArrayList<>();
    public List<Piece> newVerifiedPieces = new ArrayList<>();
}
