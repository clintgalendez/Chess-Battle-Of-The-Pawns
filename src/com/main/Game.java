package com.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gui_components.FadingComponent;
import com.gui_components.RotateImageObject;
import com.gui_components.SideToSideImageObject;
import com.loaders.GraphicsLoader;

public class Game extends FadingComponent {
    private final int WIDTH = 1280;
    private final int HEIGHT = 720;

    private final GameWindow gameWindow;

    public Game(GameWindow gameWindow) {
        this.gameWindow = gameWindow;

        setSize(WIDTH, HEIGHT);

        init();
    }

    public void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            
            e.printStackTrace();
        }

        JLabel menuBackground = new JLabel(new ImageIcon(GraphicsLoader.loadImage("resources/MMBackground.png", WIDTH, HEIGHT)));
        menuBackground.setBounds(0, 0, 1280, 720);

        JPanel menuPanel = new JPanel(null);
        menuPanel.setBackground(Color.BLACK);
        menuPanel.setOpaque(false);
        menuPanel.setSize(WIDTH, HEIGHT);

        RotateImageObject rook = new RotateImageObject("resources/WhiteRook.png", 212, 212, 28, true);
        rook.setBounds(-85, 312, 212, 212);

        RotateImageObject queen = new RotateImageObject("resources/WhiteQueen.png", 402, 402, -12, false);
        queen.setBounds(-162, 519, 402, 402);

        SideToSideImageObject king = new SideToSideImageObject("resources/BlackKing.png", 98, 98, 34, -24, true);
        king.setBounds(503, 85, 98, 98);

        SideToSideImageObject pawn = new SideToSideImageObject("resources/BlackPawn.png", 106, 106, -28, 28, false);
        pawn.setBounds(568, 16, 106, 106);

        JLabel title = new JLabel("<html><p align = right> Masterchess: <br> Battle of the Pawns </p></html>");
        title.setFont(new Font("Julius Sans One", Font.PLAIN, 50));
        title.setForeground(Color.WHITE);
        title.setBounds(425, 120, 1280, 200);

        JLabel play = new JLabel("<html><p align = right> Play </p></html>");
        play.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PlayDialog playDialog = new PlayDialog(gameWindow);

                gameWindow.getRootPane().getGlassPane().setVisible(true);
                    playDialog.setVisible(true);

                gameWindow.getRootPane().getGlassPane().setVisible(false);
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
                play.setForeground(Color.YELLOW);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                play.setForeground(Color.WHITE);
            }
            
        }
        );
        play.setFont(new Font("Julius Sans One", Font.PLAIN, 35));
        play.setForeground(Color.WHITE);
        play.setBounds(760, 325, 100, 35);
        
        JLabel exit = new JLabel("<html><p align = right> Exit </p></html>");
        exit.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
                
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
                exit.setForeground(Color.YELLOW);
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exit.setForeground(Color.WHITE);
                
            }
            
        }
        );
        exit.setFont(new Font("Julius Sans One", Font.PLAIN, 35));
        exit.setForeground(Color.WHITE);
        exit.setBounds(760, 380, 100, 35);

        
        menuPanel.add(title);
        menuPanel.add(play);
        menuPanel.add(exit);
        menuPanel.add(rook);
        menuPanel.add(pawn);
        menuPanel.add(queen);
        menuPanel.add(king);


        add(menuBackground, Integer.valueOf(0));
        add(menuPanel, Integer.valueOf(1));
    }
}
