package com.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;

public class UndoHandler implements MouseListener{
    BoardCellsHandler bch;
    public UndoHandler(BoardCellsHandler bch) {
        this.bch = bch;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Icon icon = bch.undo();
        bch.undoCapturedBoard(icon);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
}
