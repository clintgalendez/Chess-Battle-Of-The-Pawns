package com.main;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

public class GameWindow extends JFrame {
    public static void main(String[] args) {
        new GameWindow(1280, 720, "Masterchess: Battle of the Pawns");
    }

    GameWindow(int width, int height, String title) {
        GameUI GI = new GameUI(this);
        setTitle(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().add(GI);

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
    
        setBounds(center.x - width / 2, center.y - height / 2, width, height);

        setGlasspaness();
        setVisible(true);
    }

    public void setGlasspaness() {
        getRootPane().setGlassPane(new JComponent() {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        });
    }
}