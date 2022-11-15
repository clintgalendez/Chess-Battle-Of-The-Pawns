package com.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.main.SettingsUI;

public class SettingsMenuHandler implements MouseListener {
    private SettingsUI SI;

    public SettingsMenuHandler(SettingsUI SI) {
        this.SI = SI;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == SI.getResume()) {
            SI.getResume().setUpdate(true);
            SI.getResume().repaint();
        } else if (e.getSource() == SI.getRestart()) {
            SI.getRestart().setUpdate(true);
            SI.getRestart().repaint();
        } else if(e.getSource() == SI.getHelp()) {
            SI.getHelp().setUpdate(true);
            SI.getHelp().repaint();
        } else if(e.getSource() == SI.getSounds()) {
            SI.getSounds().setUpdate(true);
            SI.getSounds().repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getSource() == SI.getResume()) {

        } else if (e.getSource() == SI.getRestart()) {

        } else if(e.getSource() == SI.getHelp()) {
            
        } else if(e.getSource() == SI.getSounds());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getSource() == SI.getResume()) {
            

        } else if (e.getSource() == SI.getRestart()) {

        } else if(e.getSource() == SI.getHelp()) {
            
        } else if(e.getSource() == SI.getSounds());
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource() == SI.getResume()) {

        } else if (e.getSource() == SI.getRestart()) {

        } else if(e.getSource() == SI.getHelp()) {
            
        } else if(e.getSource() == SI.getSounds());
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(e.getSource() == SI.getResume()) {

        } else if (e.getSource() == SI.getRestart()) {

        } else if(e.getSource() == SI.getHelp()) {
            
        } else if(e.getSource() == SI.getSounds());
        
    }
    
}
