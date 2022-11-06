package com.gui;

import com.chessBOTP.Cells;

import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;

import java.io.InputStream;

import javax.imageio.ImageIO;

import java.util.Stack;

public class UndoButton extends JLabel implements MouseListener {

    private Image background;
    Stack<Cells> undo = new Stack<Cells>();

    UndoButton() {
        createBackground();

        setIcon(new ImageIcon(background));

        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setBorder(BorderFactory.createLoweredSoftBevelBorder());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setBorder(BorderFactory.createRaisedSoftBevelBorder());
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBorder(BorderFactory.createRaisedSoftBevelBorder());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBorder(null);
        
    }

    private final void createBackground() {
        try {
            InputStream in = Chessboard.class.getResourceAsStream("sample.png");
            BufferedImage bi = ImageIO.read(in);
            background = bi;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
