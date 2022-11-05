package com.chessBOTP;

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
    private static final int[][] PAWN_ATTACK_START = {
        {-1, 1}, {1, 1}, {0, 2}
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
    * @param chosenCell the cell that was clicked
    * @return           an int array of the available moves
    */
    public static int[][] getAvailableMoves(Cells chosenCell) {
        int[][] moves = null;
        switch (chosenCell.CONTAINS) {
            case 1:
                moves = KNIGHT;
                break;
            case 2:
                moves = KING;
                break;
            case 3:
                moves = PAWN;
                break;
            case 4:
                moves = PAWN_ATTACK;
                break;
            case 5:
                moves = PAWN_START;
                break;
            case 6:
                moves = PAWN_ATTACK_START;
                break;
            case 7:
                moves = BISHOP;
                break;
            case 8:
                moves = ROOK;
                break;
            case 9:
                moves = QUEEN;
                break;
        }
        return moves;
    }
}
