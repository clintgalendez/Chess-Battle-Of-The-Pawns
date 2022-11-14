package com.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class BoardLabel extends JLabel {
    BoardLabel(String label, Color color) {
        setText(label);
        setFont(new Font("Verdana",Font.PLAIN,15));
        setForeground(color);
        setHorizontalAlignment(JTextField.CENTER);
    }
}
