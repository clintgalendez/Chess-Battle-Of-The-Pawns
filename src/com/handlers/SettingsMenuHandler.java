package com.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.main.GameWindow;

public class SettingsMenuHandler implements MouseListener {
    final GameWindow gameWindow;

    public SettingsMenuHandler(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getResume()) {
            gameWindow.getPlay().getSettingsDialog().getResume().setUpdate(true);
            gameWindow.getPlay().getSettingsDialog().getResume().repaint();
        } else if (e.getSource() == gameWindow.getPlay().getSettingsDialog().getRestart()) {
            gameWindow.getPlay().getSettingsDialog().getRestart().setUpdate(true);
            gameWindow.getPlay().getSettingsDialog().getRestart().repaint();
        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getHelp()) {
            gameWindow.getPlay().getSettingsDialog().getHelp().setUpdate(true);
            gameWindow.getPlay().getSettingsDialog().getHelp().repaint();
        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getSounds()) {
            gameWindow.getPlay().getSettingsDialog().getSounds().setUpdate(true);
            gameWindow.getPlay().getSettingsDialog().getSounds().repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getResume()) {

        } else if (e.getSource() == gameWindow.getPlay().getSettingsDialog().getRestart()) {

        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getHelp()) {
            
        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getSounds());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getResume()) {
            

        } else if (e.getSource() == gameWindow.getPlay().getSettingsDialog().getRestart()) {

        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getHelp()) {
            
        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getSounds());
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getResume()) {

        } else if (e.getSource() == gameWindow.getPlay().getSettingsDialog().getRestart()) {

        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getHelp()) {
            
        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getSounds());
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getResume()) {

        } else if (e.getSource() == gameWindow.getPlay().getSettingsDialog().getRestart()) {

        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getHelp()) {
            
        } else if(e.getSource() == gameWindow.getPlay().getSettingsDialog().getSounds());
        
    }
    
}
