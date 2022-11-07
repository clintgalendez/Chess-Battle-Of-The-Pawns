package com.gui;

import com.chessBOTP.Cells;

import java.awt.event.MouseListener;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.awt.event.MouseEvent;


import java.util.Stack;

public class HomeButton extends JLabel implements MouseListener {

    Stack<Cells> undo = new Stack<Cells>();
    JPanel panel;

    HomeButton(JPanel panel) {
        setIcon(new ImageIcon(createBackground("images/Home.png", 60, 60)));
        addMouseListener(this);
        this.panel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        panel.setBackground(new Color(47, 38, 29));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        panel.setBackground(new Color(71, 64, 55));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        panel.setBackground(new Color(71, 64, 55));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        panel.setBackground(new Color(214,188,153));
    }

    private Image createBackground(String filename, int width, int height) {
        Image background = null;
        try {
            InputStream in = Chessboard.class.getResourceAsStream(filename);
            BufferedImage bi = ImageIO.read(in);
            ImageIcon icon = new ImageIcon(bi);
            background = icon.getImage().getScaledInstance(width, height,Image.SCALE_SMOOTH);;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return background;
    }
}
