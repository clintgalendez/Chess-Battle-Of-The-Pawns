package com.handlers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.main.GameWindow;
import javax.swing.Icon;

public class UndoHandler implements MouseListener{
    BoardCellsHandler bch;
    final GameWindow gameWindow;

    public UndoHandler(BoardCellsHandler bch, GameWindow gameWindow) {
        this.bch = bch;
        this.gameWindow = gameWindow;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Icon icon = bch.undo();
        bch.undoCapturedBoard(icon);

    }

    @Override
    public void mousePressed(MouseEvent e) {
        gameWindow.getPlay().getUndoPanel().setBackground(new Color(47, 38, 29));
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        gameWindow.getPlay().getUndoPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        gameWindow.getPlay().getUndoPanel().setBackground(new Color(71, 64, 55));
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        gameWindow.getPlay().getUndoPanel().setBackground(new Color(214,188,153));
        
    }
}
