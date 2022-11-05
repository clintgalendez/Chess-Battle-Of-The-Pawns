package com.chessBOTP;

import javax.swing.*;

/**
 * Creates a new cell object
 */
public class Cells extends JButton {
    public static int posX;
    public static int posY;
    public static int CONTAINS;

    public Cells(int posX, int posY, int contains) {
        this.posX = posX;
        this.posY = posY;
        CONTAINS = contains;
    }
}
