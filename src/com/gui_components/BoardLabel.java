package com.gui_components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class BoardLabel extends JLabel {
    public BoardLabel(String label, Color color) {
        setText(label);
        setFont(new Font("Verdana",Font.PLAIN,15));
        setForeground(color);
        setHorizontalAlignment(JTextField.CENTER);
    }
}
