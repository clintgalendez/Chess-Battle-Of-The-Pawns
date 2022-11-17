package com.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PlayDialog extends JDialog {
    private JLayeredPane layeredPane;
    private JTextField player1TextField;
    private JTextField player2TextField;
    
    private static final Color CALICO = new Color(224, 190, 145);
    private static final Color ZEUS = new Color(47, 38, 29);

    private final GameWindow gameWindow;

    public PlayDialog(GameWindow gameWindow) {
        this.gameWindow = gameWindow;

        init();

        setLayout(null);
        setBackground(CALICO);
        setModal(true);
        setSize(490, 300);
        setLocationRelativeTo(null);

        getContentPane().add(layeredPane);
        
        setResizable(false);
        setUndecorated(true);
    }

    private void init() {
        layeredPane = new JLayeredPane();
        layeredPane.setSize(490, 300);

        JPanel outerBorder = new JPanel();
        outerBorder.setBackground(CALICO);
        outerBorder.setSize(490, 300);

        GridBagLayout layout = new GridBagLayout();
        JPanel innerBorder = new JPanel();
        innerBorder.setLayout(layout);
        innerBorder.setBackground(ZEUS);
        innerBorder.setBounds(2, 2, 486, 296);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.25;
        gbc.gridwidth = 2;
        JLabel dialogTitle = new JLabel("Create Players");
        dialogTitle.setFont(new Font("Julius Sans One",Font.PLAIN,40));
        dialogTitle.setForeground(CALICO);
        dialogTitle.setHorizontalAlignment(JTextField.CENTER);
        dialogTitle.setSize(200, 80);
        innerBorder.add(dialogTitle, gbc);

        JLabel player1 = new JLabel("Player 1 Name: ");
        player1.setFont(new Font("Julius Sans One",Font.PLAIN,20));
        player1.setForeground(CALICO);
        player1.setHorizontalAlignment(JTextField.CENTER);
        player1.setSize(200, 20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 60, 5, 0);
        layout.setConstraints(player1, gbc);
        innerBorder.add(player1);
        
        player1TextField = new JTextField();
        player1TextField.setEditable(true);
        player1TextField.setSize(200, 20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 60);
        layout.setConstraints(player1TextField, gbc);
        innerBorder.add(player1TextField);

        JLabel player2 = new JLabel("Player 2 Name: ");
        player2.setFont(new Font("Julius Sans One",Font.PLAIN,20));
        player2.setForeground(CALICO);
        player2.setHorizontalAlignment(JTextField.CENTER);
        player2.setSize(200, 20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 60, 5, 0);
        layout.setConstraints(player2, gbc);
        innerBorder.add(player2);
        
        player2TextField = new JTextField();
        player2TextField.setEditable(true);
        player2TextField.setSize(200, 20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 60);
        layout.setConstraints(player2TextField, gbc);
        innerBorder.add(player2TextField);

        JLabel back = new JLabel("Return   ");
        back.setFont(new Font("Julius Sans One",Font.PLAIN,20));
        back.setForeground(CALICO);
        back.setSize(200, 20);
        back.setHorizontalAlignment(JTextField.RIGHT);
        back.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {
                back.setForeground(CALICO);
            } 
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(50, 0, 40, 0);
        layout.setConstraints(back, gbc);
        innerBorder.add(back);

        JLabel play = new JLabel("   Play");
        play.setFont(new Font("Julius Sans One",Font.PLAIN,20));
        play.setForeground(CALICO);
        play.setSize(200, 20);
        play.setHorizontalAlignment(JTextField.LEFT);
        play.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();

                gameWindow.getGame().fadeOut();

                gameWindow.getContentPane().remove(gameWindow.getGame());
                gameWindow.getContentPane().add(gameWindow.getPlay());

                gameWindow.getPlay().fadeIn();
                gameWindow.getPlay().play(player1TextField.getText(), player2TextField.getText());
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                play.setForeground(Color.WHITE);   
            }

            @Override
            public void mouseExited(MouseEvent e) {
                play.setForeground(CALICO);    
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(50, 0, 40, 0);
        layout.setConstraints(play, gbc);
        innerBorder.add(play);

        layeredPane.add(outerBorder, Integer.valueOf(0));
        layeredPane.add(innerBorder, Integer.valueOf(1));
    }
}
