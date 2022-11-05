package com.chessBOTP;

public class Players {
    private static int[] playerDestroyedPieces = new int[16];
    private static String playerName;

    public Players(String name) {
        playerName = name;
    }

    public static String getPlayer1Name() {
        return playerName;
    }

    public static void addDestroyedPiece(int piece) {
        for (int i = 0; i < playerDestroyedPieces.length; i++) {
            if (playerDestroyedPieces[i] == 0) {
                playerDestroyedPieces[i] = piece;
                break;
            }
        }
    }

    public static int[] getPlayerDestroyedPieces() {
        return playerDestroyedPieces;
    }
}
