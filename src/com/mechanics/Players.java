// Players of the game

package com.mechanics;

import java.util.Stack;
public class Players {
    private final String playerName;

    private final int playerColor;

    private final Stack<Cells> playerMoves = new Stack<>();

    private boolean isCheck = false;
    private boolean isDraw = false;
    private boolean hasCastled = false;
    private boolean hasMoved = false;
    private final boolean isCheckMate = false;

    public Players(String name, int color) {
        
        playerName = name;
        playerColor = color;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void addMove(Cells cell) {
        playerMoves.push(cell);
        // Check if the player is repeating the same move 3 times
        if (playerMoves.size() >= 6) {
            if (playerMoves.get(playerMoves.size() - 1) == playerMoves.get(playerMoves.size() - 3) &&
                    playerMoves.get(playerMoves.size() - 2) == playerMoves.get(playerMoves.size() - 4) &&
                    playerMoves.get(playerMoves.size() - 5) == playerMoves.get(playerMoves.size() - 6)) {
                isDraw = true;
            }
        }
    }

    public Stack<Cells> getMove() {
        return playerMoves;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setHasCastled(boolean hasCastled) {
        this.hasCastled = hasCastled;
    }

    public boolean hasCastled() {
        return hasCastled;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isCheckMate() {
        return isCheckMate;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public int getPlayerColor() {
        return playerColor;
    }
}