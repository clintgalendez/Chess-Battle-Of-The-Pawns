package com.handlers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.main.GameUI;

public class HomeHandler implements MouseListener {
    GameUI GI;

    public HomeHandler(GameUI GI) {
        this.GI = GI;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GI.getUndoPanel().setBackground(new Color(47, 38, 29));
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        GI.getUndoPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        GI.getUndoPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        GI.getUndoPanel().setBackground(new Color(214,188,153));
        
    }
    
}
