package com.handlers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.main.GameUI;
import javax.swing.Icon;

public class UndoHandler implements MouseListener{
    BoardCellsHandler bch;
    GameUI GI;

    public UndoHandler(BoardCellsHandler bch, GameUI GI) {
        this.bch = bch;
        this.GI = GI;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Icon icon = bch.undo();
        bch.undoCapturedBoard(icon);
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
