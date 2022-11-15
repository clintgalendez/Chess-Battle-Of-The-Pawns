package com.main;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

public class GameWindow extends JFrame {
    public static void main(String[] args) {
        new GameWindow(1280, 720, "Masterchess: Battle of the Pawns");
    }

    GameWindow(int width, int height, String title) {
        GameUI GI = new GameUI();
        setTitle(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().add(GI.getLayeredPane());

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
    
        setBounds(center.x - width / 2, center.y - height / 2, width, height);

        setVisible(true);
    }
}