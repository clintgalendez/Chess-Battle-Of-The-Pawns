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

public class SettingsButton extends JLabel implements MouseListener {

    private Image background;
    private Time clock;
    Stack<Cells> undo = new Stack<Cells>();

    SettingsButton(Time clock) {
        createBackground();

        setIcon(new ImageIcon(background));

        addMouseListener(this);

        this.clock = clock;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clock.timer.stop();
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
