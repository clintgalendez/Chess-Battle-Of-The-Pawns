package com.chessBOTP;

import javax.swing.*;

/**
 * Creates a new cell object
 */
public class Cells extends JButton {
    public int posX;
    public int posY;
    public int CONTAINS;
    public int pieceColor;
    public Cells(int posX, int posY, int contains, int pieceColor) {
        this.posX = posX;
        this.posY = posY;
        this.CONTAINS = contains;
        this.pieceColor = pieceColor;
    }
}
