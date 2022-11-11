package com.chessBOTP;

import java.awt.Color;

import com.gui.Chessboard;

public class TurnBasedHandler {
    private Players currentPlayer;
    private Players nextPlayer;
    private Chessboard chessboard;

    public TurnBasedHandler(Players player1, Players player2, Chessboard board) {
        firstTurn(player1, player2, board);
    }

    public void firstTurn(Players player1, Players player2, Chessboard board) {
        currentPlayer = player1;
        nextPlayer = player2;
        chessboard = board;

        chessboard.getNamePanel(currentPlayer).setBackground(Color.GREEN);
    }

    public void nextTurn() {
        Players temp = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = temp;

        chessboard.getNamePanel(currentPlayer).setBackground(Color.GREEN);
        chessboard.getNamePanel(nextPlayer).setBackground(new Color(214, 188, 153));
    }

    public Players getCurrentPlayer() {
        return currentPlayer;
    }

    public Players getNextPlayer() {
        return nextPlayer;
    }
}