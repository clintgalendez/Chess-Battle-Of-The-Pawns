// Turn-based handler of the game

package com.handlers;

import java.awt.Color;

import com.main.Play;
import com.mechanics.Players;

public class TurnBasedHandler {
    private Players currentPlayer;
    private Players nextPlayer;

    public TurnBasedHandler(Players player1, Players player2, Play newGame) {
        currentPlayer = player1;
        nextPlayer = player2;

        newGame.getNamePanel(currentPlayer).setBackground(Color.GREEN);
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