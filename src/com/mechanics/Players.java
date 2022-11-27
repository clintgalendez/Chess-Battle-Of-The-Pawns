// Players of the game

package com.mechanics;

import java.util.Stack;
public class Players {
    private final String playerName;

    private final int playerColor;

    private final Stack<Cells> playerMoves = new Stack<>();

    private boolean isCheck = false;
    private boolean hasCastled = false;
    private boolean hasStored = false;
    private boolean hasMovedLeft = false;
    private boolean hasMovedRight = false;
    private boolean hasMovedKing = false;

    public Players(String name, int color) {
        
        playerName = name;
        playerColor = color;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void addMove(Cells cell) {
        playerMoves.push(cell);
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

    public boolean hasStored() {
        return hasStored;
    }

    public void setHasStored(boolean hasStored) {
        this.hasStored = hasStored;
    }

    

    public boolean isHasMovedLeft() {
        return hasMovedLeft;
    }

    public void setHasMovedLeft(boolean hasMovedLeft) {
        this.hasMovedLeft = hasMovedLeft;
    }

    public boolean isHasMovedRight() {
        return hasMovedRight;
    }

    public void setHasMovedRight(boolean hasMovedRight) {
        this.hasMovedRight = hasMovedRight;
    }

    public boolean isHasMovedKing() {
        return hasMovedKing;
    }

    public void setHasMovedKing(boolean hasMovedKing) {
        this.hasMovedKing = hasMovedKing;
    }

    public int getPlayerColor() {
        return playerColor;
    }
}