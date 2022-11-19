package com.gui_components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.loaders.GraphicsLoader;

public class NamePanel extends JPanel {
    JLabel playerIcon;
    JLabel playerName;

    public NamePanel(String name, Color color_one, Color color_two, boolean reverse) {
        setLayout(null);
        setBackground(color_one);
        setBorder(new LineBorder(color_two, 2, true)); 

        playerName = new JLabel(name);
        playerName.setFont(new Font("Julius Sans One",Font.BOLD,15));
        playerName.setForeground(Color.BLACK);
        playerName.setHorizontalAlignment(JTextField.CENTER);
        
        if(!reverse) {
            playerIcon = new JLabel(new ImageIcon(GraphicsLoader.loadImage("resources/Player1.png", 65, 65)));
            playerName.setBounds(96,3,168,90);
            playerName.setBackground(Color.BLACK);
            playerIcon.setBounds(6,3,90,90);
        } else { 
            playerIcon = new JLabel(new ImageIcon(GraphicsLoader.loadImage("resources/Player2.png", 65, 65)));
            playerName.setBounds(22,3,160,90);
            playerName.setBackground(Color.BLACK);
            playerIcon.setBounds(186,3,90,90);
        }
        
        add(playerName);
        add(playerIcon);
    }

    public void setPlayerName(String name) {
        playerName.setText(name);
    }
    
}
