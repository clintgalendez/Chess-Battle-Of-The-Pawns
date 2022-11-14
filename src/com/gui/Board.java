package com.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.mechanics.Cells;

public class Board extends JPanel {
    private JButton[][] boardCells = new JButton[4][4];
    
    Board(Color color_one, Color color_two) {
        setLayout(new GridLayout(0,4));

        initCapturedBoard(boardCells, color_one, color_two);

        for (int i = 0; i < 4; i++) {
            for (int j = 0;j < 4; j++) {
                add(boardCells[j][i]);
            }
        }
    }

    private void initCapturedBoard(JButton[][] cells, Color color1, Color color2) {
        Insets buttonMargin = new Insets(0, 0, 0, 0);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    JButton cell = new JButton();
                    cell.setMargin(buttonMargin);
                    cell.setEnabled(false);
                    cell.setFocusable(false);
                    cell.setBorder(new LineBorder(color1, 1, false));
                    cell.setBackground(color2);
                    cells[j][i] = cell;
                }
            }
    }

    // Add piece to captured board
    public void addToCapturedBoard(Cells chosenCell, int y, int x) {
        boardCells[y][x].setIcon(chosenCell.piece);
        boardCells[y][x].setDisabledIcon(chosenCell.piece);
    }

    // Remove piece from captured bored
    public void removeFromCapturedBoard(int y, int x) {
        boardCells[y][x].setIcon(null);
    }

    public JButton[][] getBoardCells() {
        return boardCells;
    }
}
