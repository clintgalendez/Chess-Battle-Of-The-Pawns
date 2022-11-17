package com.handlers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.main.GameWindow;

public class SettingsHandler implements MouseListener {
    private final GameWindow gameWindow;

    public SettingsHandler(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        gameWindow.getPlay().getClock().timer.stop();
        gameWindow.getRootPane().getGlassPane().setVisible(true);
            gameWindow.getPlay().getSettingsPanel().setVisible(true);

        gameWindow.getRootPane().getGlassPane().setVisible(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gameWindow.getPlay().getSettingsPanel().setBackground(new Color(47, 38, 29));
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        gameWindow.getPlay().getSettingsPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        gameWindow.getPlay().getSettingsPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        gameWindow.getPlay().getSettingsPanel().setBackground(new Color(214,188,153));
        
    }
}
