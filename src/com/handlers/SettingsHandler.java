package com.handlers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import com.gui_components.Clock;
import com.main.GameUI;
import com.main.SettingsUI;

public class SettingsHandler implements MouseListener {
    GameUI GI;
    SettingsUI SI;
    JFrame GameWindow;
    Clock clock;

    public SettingsHandler(GameUI GI, SettingsUI SI, JFrame GameWindow, Clock clock) {
        this.GI = GI;
        this.SI = SI;
        this.GameWindow = GameWindow;
        this.clock = clock;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clock.timer.stop();
        GameWindow.getRootPane().getGlassPane().setVisible(true);
            SI.setVisible(true);

        GameWindow.getRootPane().getGlassPane().setVisible(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GI.getSettingsPanel().setBackground(new Color(47, 38, 29));
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        GI.getSettingsPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        GI.getSettingsPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        GI.getSettingsPanel().setBackground(new Color(214,188,153));
        
    }
}
