package com.chessBOTP;

public class TurnBasedHandler {
    private Players currentPlayer;
    private Players nextPlayer;

    public TurnBasedHandler(Players player1, Players player2) {
        currentPlayer = player1;
        nextPlayer = player2;
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
