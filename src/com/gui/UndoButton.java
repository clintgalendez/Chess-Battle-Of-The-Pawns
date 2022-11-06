package com.gui;

import com.chessBOTP.Cells;

import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

import java.util.Stack;

public class UndoButton extends JLabel implements MouseListener {

    Stack<Cells> undo = new Stack<Cells>();
    
    UndoButton() {
        setText("Undo");
        setFont(new Font("Verdana",Font.PLAIN,10));
        setForeground(Color.BLACK);
        setHorizontalAlignment(JTextField.CENTER);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
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
