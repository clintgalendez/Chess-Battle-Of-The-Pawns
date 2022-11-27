package com.handlers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.main.GameWindow;

public class HomeHandler implements MouseListener {
    private final GameWindow gameWindow;

    public HomeHandler(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        gameWindow.getPlay().getBgMusic().stop();
        gameWindow.getPlay().getBgMusic().playOnLoop();
        gameWindow.getPlay().fadeOut();

        gameWindow.getContentPane().remove(gameWindow.getPlay());
        gameWindow.restartGame();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gameWindow.getPlay().getHomePanel().setBackground(new Color(47, 38, 29));
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        gameWindow.getPlay().getHomePanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        gameWindow.getPlay().getHomePanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        gameWindow.getPlay().getHomePanel().setBackground(new Color(214,188,153));
        
    }
}
