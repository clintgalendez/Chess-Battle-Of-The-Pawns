package com.gui;

import java.awt.event.MouseListener;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Point;

import java.awt.event.MouseEvent;

public class SettingsButton extends JLabel implements MouseListener {

    JPanel panel;
    Time clock;
    

    SettingsButton(Time clock, JPanel panel) {
        setIcon(new ImageIcon(createBackground("images/Settings.png", 60, 60)));
        addMouseListener(this);
        this.panel = panel;
        this.clock = clock;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clock.timer.stop();
        JDialog settingsDialog = new JDialog();
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        int width = 700;
        int height = 500;
        settingsDialog.setBounds(center.x - width / 2, center.y - height / 2, width, height);
        settingsDialog.setVisible(true);
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
