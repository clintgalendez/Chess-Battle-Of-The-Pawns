package com.main;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.loaders.GraphicsLoader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

public class GameWindow extends JFrame {
    Play newGame;
    Game masterchess;
    
    public static void main(String[] args) {
        new GameWindow(1280, 720, "Masterchess: Battle of the Pawns");
    }

    GameWindow(int width, int height, String title) {
        newGame = new Play(this);
        masterchess = new Game(this);

        newGame.fadeOut();

        setTitle(title);
        setIconImage(GraphicsLoader.loadImage("resources/WindowsLogo.png", 148, 148));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(null);
        getContentPane().add(masterchess);

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        setBounds(center.x - width / 2, center.y - height / 2, width, height);

        setGlasspane();
        setVisible(true);
    }

    public void setGlasspane() {
        getRootPane().setGlassPane(new JComponent() {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        });
    }

    public void restartGame() {
        getContentPane().add(masterchess);
        masterchess.fadeIn();

        newGame = new Play(this);
        newGame.fadeOut();
    }

    public Game getGame() {
        return masterchess;
    }

    public Play getPlay() {
        return newGame;
    }
}