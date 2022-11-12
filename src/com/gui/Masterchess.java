package com.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;

public class Masterchess extends JFrame {
    public static void main(String[] args) {
        Runnable r = Masterchess::new;
        SwingUtilities.invokeLater(r);
    }

    public Masterchess() {
        Chessboard gui = new Chessboard();

        setTitle("Masterchess: The Battle of the Pawns");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().add(gui.getLayeredpane());

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        int width = 1280;
        int height = 720;
        setBounds(center.x - width / 2, center.y - height / 2, width, height);

        setVisible(true);
    }
}