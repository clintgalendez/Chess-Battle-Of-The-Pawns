// Turn-based handler of the game

package com.chessBOTP;

import java.awt.Color;

import com.gui.Chessboard;

public class TurnBasedHandler {
    private Players currentPlayer;
    private Players nextPlayer;
    private Chessboard chessboard;

    public TurnBasedHandler(Players player1, Players player2, Chessboard board) {
        currentPlayer = player1;
        nextPlayer = player2;
        chessboard = board;

        chessboard.getNamePanel(currentPlayer).setBackground(Color.GREEN);
    }

    public void nextTurn() {
        Players temp = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = temp;
    }

    public Players getCurrentPlayer() {
        return currentPlayer;
    }

    public Players getNextPlayer() {
        return nextPlayer;
    }
}