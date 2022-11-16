package com.main;

import javax.swing.JDialog;

public class HelpUI extends JDialog {
    public HelpUI() {
        setLayout(null);
        //setBackground(CALICO);
        setModal(true);
        setBounds(472, 153, 590, 584);

        //getContentPane().add(layeredPane);
        
        setResizable(false);
        setUndecorated(false);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setVisible(true);
    }
}