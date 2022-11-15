package com.gui_components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseListener;

import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import com.handlers.BoardCellsHandler;
import com.main.GameUI;
import com.mechanics.Cells;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Chessboard extends JPanel {

    public Chessboard(Cells[][] cells, Color color_one, Color color_two, GameUI GI, Clock clock) {
        BoardCellsHandler bch = new BoardCellsHandler(GI, clock);
        GI.setBCH(bch);
        setLayout(new GridBagLayout());
        setBackground(new Color(49,42,33));
        setBorder(new LineBorder(color_one, 2));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel topleft = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(topleft, gbc);
        
        createLetters(this, 0, true, color_one);

        JLabel topright = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.gridx = 12;
        gbc.gridy = 12;
        add(topright, gbc);

        createNumbers(this, 0, false, color_one);

        JLabel bottomleft = new JLabel("");
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.gridx = 0;
        gbc.gridy = 11;
        add(bottomleft, gbc);

        // Initialize the cells of the chessboard
        initChessBoardCells(cells, 8, true, color_one, color_two, null, bch);

        JPanel board = new JPanel(new GridLayout(0,8));
        board.setBorder(new LineBorder(color_one, 2));
        board.setOpaque(false);
        board.setPreferredSize(new Dimension(550,550));
 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {   
                board.add(cells[j][i]);
            }
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 10;
        gbc.gridheight = 10;
        add(board, gbc);

        createNumbers(this, 12, true, color_one);

        createLetters(this, 12, false, color_one);

        JLabel bottomright = new JLabel("");
        gbc.fill = GridBagConstraints.REMAINDER;
        add(bottomright, gbc);
    }

    // Initialize chessboard cells
    private void initChessBoardCells(Cells[][] cells, int dimension, boolean isEnabled, Color color1, Color color2, Color color3, BoardCellsHandler bch) {
        Insets buttonMargin = new Insets(0, 0, 0, 0);

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                Cells cell = new Cells(j, i, 0, 0,null);
                cell.setMargin(buttonMargin);
                cell.setEnabled(isEnabled);
                cell.setFocusable(false);
                cell.setBorder(new LineBorder(color3, 1, false));
                cell.addActionListener(bch);

                cell.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        cell.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, Color.BLACK));
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        cell.setBorder(new LineBorder(color3, 1, false));
                    }
                    
                });

                if (((j % 2 == 1) && (i % 2 == 1))
                        || ((j % 2 == 0) && (i % 2 == 0))) {
                    cell.setBackground(color1);
                } else {
                    cell.setBackground(color2);
                }
                cells[j][i] = cell;
            }
        }
    }

    // Create letters of the chessboard
    private void createLetters(JPanel panel, int row, boolean isReverse, Color color) {
        GridBagConstraints gbc = new GridBagConstraints();
        String letters = "ABCDEFGH";
        StringBuilder reverse = new StringBuilder();
        if(isReverse) {
            for(int i = 0; i<letters.length(); i++) {
                char ch = letters.charAt(i);
                reverse.insert(0, ch);
            }
        } else {
            reverse = new StringBuilder(letters);
        }
        
        for (int i = 0; i < 8; i++) {
            JLabel letter = new JLabel(reverse.substring(i, i + 1),
                            SwingConstants.CENTER);
            letter.setForeground(color);
            letter.setFont(new Font("Times New Roman",Font.PLAIN,12) );
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = i+2;
            gbc.gridy = row;
            gbc.weightx = 1;
            gbc.weighty = 1;
            panel.add(letter,gbc);
        }
    }

    // Create numbers of the chessboard
    private void createNumbers(JPanel panel, int column, boolean isReverse, Color color) {
        GridBagConstraints gbc = new GridBagConstraints();
        int num;
        for (int i = 0; i < 8; i++) {
            if(isReverse) {
                num = i + 1;
            } else {
                num = 8 - i;
            }
            JLabel number = new JLabel("" + num,
                            SwingConstants.CENTER);
            number.setForeground(color);
            number.setFont(new Font("Times New Roman",Font.PLAIN,12) );
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = column;
            gbc.gridy = i+2;
            gbc.weightx = 1;
            gbc. weighty = 1;
            panel.add(number,gbc);
        }
    }
}