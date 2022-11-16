package com.mechanics;

import javax.swing.*;

/**
 * Creates a new cell object
 */
public class Cells extends JButton {
    public int posX;
    public int posY;
    public int CONTAINS;
    public int pieceColor;
    public Icon piece;
    public Cells(int posX, int posY, int contains, int pieceColor, Icon piece) {
        this.posX = posX;
        this.posY = posY;
        this.CONTAINS = contains;
        this.pieceColor = pieceColor;
        this.piece = piece;
    }

    public Cells(int contains, int pieceColor, Icon piece) {
        this.CONTAINS = contains;
        this.pieceColor = pieceColor;
        this.piece = piece;
    }
}