package com.mechanics;

public class MoveSets {
    private static final int[][] KNIGHT = {
        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
        {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };
    private static final int[][] KING = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1}, {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };
    private static final int[][] PAWN = {
        {0, 1}
    };
    private static final int[][] PAWN_ATTACK = {
        {-1, 1}, {1, 1}
    };
    private static final int[][] PAWN_START = {
        {0, 1}, {0, 2}
    };
    private static final int[][] BISHOP = {
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };
    private static final int[][] ROOK = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };
    private static final int[][] QUEEN = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1}, {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    /**
    * Returns the available move set of the selected piece
    * @param piece      the piece to get the move set of
    * @return           an int array of the available moves
    */
    public static int[][] getAvailableMoves(int piece) {
        return switch (piece) {
            case 1 -> KNIGHT;
            case 2 -> KING;
            case 3 -> PAWN;
            case 4 -> PAWN_ATTACK;
            case 5 -> PAWN_START;
            case 7 -> BISHOP;
            case 8 -> ROOK;
            case 9 -> QUEEN;
            default -> null;
        };
    }
}