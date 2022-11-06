package com.gui;

import com.chessBOTP.Cells;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;

import java.io.InputStream;

import javax.imageio.ImageIO;


public class Chessboard implements ActionListener {

    //Class Variables
    private static final String COLUMN = "ABCDEFGH";

    private JPanel chessBoard, mainPanel;

    private GradientPanel boardPanel, undoPanel, homePanel, settingsPanel;
    
    private RoundedPanel  pieceCapturedP1, pieceCapturedP2, 
                          namePanelP1, namePanelP2,
                          namePadding1, namePadding2;

    private JLayeredPane layeredPane;

    private JLabel gamebg, player1Icon, player2Icon,
                   playerName1, playerName2,
                   undoButton, homeButton, settingsButton, 
                   pCapturedP1, pCapturedP2;

    private Cells cells[][] = new Cells[8][8];
    private Cells pieceCapturedBoard1[][] = new Cells[4][4];
    private Cells pieceCapturedBoard2[][] = new Cells[4][4];

    private Image background;
    
    private Time clock;

    private static final Color LUMBER = new Color(255, 229, 204);
    private static final Color PEACH_ORANGE = new Color(252, 187, 122);

    public Chessboard() {
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            
            e.printStackTrace();
        }

        createChessPieces();
        createBackground();

        /*
         * The layeredPane allows the mainPanel to overlay above the gamebg. 
         * However, mainPanel must not be opaque.
         * layeredPane -> {gamebg, mainPanel}
         */
        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1280, 720);

        /*
         * The gamebg is background of the game
         */
        gamebg = new JLabel(new ImageIcon(background));
        gamebg.setBounds(0, 0, 1280, 720);

        /*
         * The mainPanel consists all of the elements above the gamebg
         * mainPanel -> {boardPanel, pieceCaptured1, pieceCaptured2, namePanel1, namePanel2, clock}
         */
        mainPanel = new JPanel(null);
        //mainPanel.setOpaque(false);
        mainPanel.setSize(1280,720);
        mainPanel.setBackground(new Color(40, 40, 43));

        /*
         * The chessBoard is the 8x8 playing board, columns of numbers, and rows of letters
         */
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setOpaque(false);
        chessBoard.setBounds(0,0,600,600);

        fillBoard(cells, 8, true, LUMBER, PEACH_ORANGE);

        chessBoard.add(new JLabel(""));
        for (int i = 0; i < 8; i++) {
            chessBoard.add(new JLabel(COLUMN.substring(i, i + 1),
                    SwingConstants.CENTER));
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (j) {
                    case 0:
                        chessBoard.add(new JLabel("" + (8 - i),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(cells[i][j]);
                }
            }
        }

        /*
         * The boardPanel gives the chessBoard a border
         * boardPanel -> {chessBoard}
         */
        boardPanel = new GradientPanel(null, 10);
        boardPanel.setBounds(320,50,625,615);
        boardPanel.add(chessBoard);

        /*
         * The pieceCapturedP1 is the 8x8 board that will contain the pieces captured by player 1
         */
        pieceCapturedP1 = new RoundedPanel(new GridLayout(0,4),5);
        pieceCapturedP1.setBounds(27,200,270,270);

        fillBoard(pieceCapturedBoard1, 4, false, LUMBER, LUMBER);

        for (int i = 0; i < 4; i++) {
            for (int j = 0;j < 4; j++) {
                pieceCapturedP1.add(pieceCapturedBoard1[j][i]);
            }
        }

        pCapturedP1 = new JLabel("Pieces Captured");
        pCapturedP1.setFont(new Font("Verdana",Font.PLAIN,15));
        pCapturedP1.setForeground(Color.WHITE);
        pCapturedP1.setHorizontalAlignment(JTextField.CENTER);
        pCapturedP1.setBounds(99,170,122,15);

        /*
         * The pieceCapturedP2 is the 8x8 board that will contain the pieces captured by player 2
         */
        pieceCapturedP2 = new RoundedPanel(new GridLayout(0,4),5);
        pieceCapturedP2.setBounds(967,200,270,270);

        fillBoard(pieceCapturedBoard2, 4, false, PEACH_ORANGE, PEACH_ORANGE);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pieceCapturedP2.add(pieceCapturedBoard2[j][i]);
            }
        }

        pCapturedP2 = new JLabel("Pieces Captured");
        pCapturedP2.setFont(new Font("Verdana",Font.PLAIN,15));
        pCapturedP2.setForeground(Color.WHITE);
        pCapturedP2.setHorizontalAlignment(JTextField.CENTER);
        pCapturedP2.setBounds(1051,170,122,15);

        player1Icon = new JLabel(new ImageIcon(background));
        player1Icon.setBounds(0,0,90,90);

        playerName1 = new JLabel("Player 1");
        playerName1.setFont(new Font("Verdana",Font.PLAIN,15));
        playerName1.setForeground(Color.BLACK);
        playerName1.setHorizontalAlignment(JTextField.CENTER);
        playerName1.setBounds(90,0,189,90);

        namePadding1 = new RoundedPanel(null, 10);
        namePadding1.setBackground(null);
        namePadding1.setBounds(5,5,279,90);
        namePadding1.add(player1Icon);
        namePadding1.add(playerName1);

        /*
         * The namePanelP1 will consist the name and icon of player 1
         */
        namePanelP1 = new RoundedPanel(null,10);
        namePanelP1.setBackground(LUMBER);
        namePanelP1.setBounds(18,25,289,100);
        namePanelP1.add(namePadding1);

        player2Icon = new JLabel(new ImageIcon(background));
        player2Icon.setBounds(189,0,90,90);

        playerName2 = new JLabel("Player 2");
        playerName2.setFont(new Font("Verdana",Font.PLAIN,15));
        playerName2.setForeground(Color.BLACK);
        playerName2.setHorizontalAlignment(JTextField.CENTER);
        playerName2.setBounds(0,0,189,90);

        namePadding2 = new RoundedPanel(null, 10);
        namePadding2.setBackground(null);
        namePadding2.setBounds(5,5,279,90);
        namePadding2.add(player2Icon);
        namePadding2.add(playerName2);

        /*
         * The namePanelP1 will consist the name and icon of player 2
         */
        namePanelP2 = new RoundedPanel(null, 10);
        namePanelP2.setBackground(PEACH_ORANGE);
        namePanelP2.setBounds(958,25,289,100);
        namePanelP2.add(namePadding2);

        /*
         * The clock is the timer of the game
         */
        clock = new Time();
        clock.setBounds(595,10,90,30);

        undoPanel = new GradientPanel(new BorderLayout(), 10);
        undoPanel.setBounds(18,600,60,60);


        homeButton = new JLabel("Home");
        homeButton.setFont(new Font("Verdana",Font.PLAIN,10));
        homeButton.setForeground(Color.BLACK);
        homeButton.setHorizontalAlignment(JTextField.CENTER);

        homePanel = new GradientPanel(new BorderLayout(), 10);
        homePanel.setBounds(1184,600,60,60);
        homePanel.add(homeButton, BorderLayout.CENTER);

        settingsButton = new JLabel("Settings");
        settingsButton.setFont(new Font("Verdana",Font.PLAIN,10));
        settingsButton.setForeground(Color.BLACK);
        settingsButton.setHorizontalAlignment(JTextField.CENTER);

        settingsPanel = new GradientPanel(new BorderLayout(), 10);
        settingsPanel.setBounds(1184,560,60,30);
        settingsPanel.add(settingsButton, BorderLayout.CENTER);

        mainPanel.add(boardPanel);
        mainPanel.add(pieceCapturedP1);
        mainPanel.add(pCapturedP1);
        mainPanel.add(pieceCapturedP2);
        mainPanel.add(pCapturedP2);
        mainPanel.add(namePanelP1);
        mainPanel.add(namePanelP2);
        mainPanel.add(clock);
        mainPanel.add(undoPanel);
        mainPanel.add(homePanel);
        mainPanel.add(settingsPanel);

        layeredPane.add(gamebg, Integer.valueOf(0));
        layeredPane.add(mainPanel, Integer.valueOf(1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private final void createChessPieces() {
        // To be filled
    }

    private final void createBackground() {
        try {
            InputStream in = Chessboard.class.getResourceAsStream("sample.png");
            BufferedImage bi = ImageIO.read(in);
            background = bi;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void fillBoard(Cells[][] board, int size, boolean isEnabled, Color color1, Color color2) {
        Insets buttonMargin = new Insets(0, 0, 0, 0);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Cells b = new Cells(i, j, 0);
                    b.setMargin(buttonMargin);
                    b.setEnabled(isEnabled);
                    b.addActionListener(this);
                    
                    ImageIcon icon = new ImageIcon(new BufferedImage(64, 64,
                            BufferedImage.TYPE_INT_ARGB));
                    b.setIcon(icon);

                    if (((j % 2 == 1) && (i % 2 == 1))
                            || ((j % 2 == 0) && (i % 2 == 0))) {
                        b.setBackground(color1);
                    } else {
                        b.setBackground(color2);
                    }
                    board[i][j] = b;
                }
            }
    }

    public JComponent getLayeredpane() {
        return layeredPane;
    }

    public void setColAvailCells(int[][] moveSets, Cells chosenCell) {
        for (int i = 0; i < moveSets.length; i++) {
            int x = chosenCell.posX + moveSets[i][0];
            int y = chosenCell.posY + moveSets[i][1];
            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                cells[x][y].setBackground(Color.GREEN);
            }
        }
    }

    public Cells[][] getCells() {
        return cells;
    }
}

class Time extends JPanel {
    JLabel timeLabel = new JLabel();
    int elapsedTime = 0;
    int seconds =0;
    int minutes =0;
    int hours =0;
    boolean started = false;
    String seconds_string = String.format("%02d", seconds);
    String minutes_string = String.format("%02d", minutes);
    String hours_string = String.format("%02d", hours);
    Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            elapsedTime=elapsedTime+1000;
            hours = (elapsedTime/3600000);
            minutes = (elapsedTime/60000) % 60;
            seconds = (elapsedTime/1000) % 60;
            seconds_string = String.format("%02d", seconds);
            minutes_string = String.format("%02d", minutes);
            hours_string = String.format("%02d", hours);
            timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string); 
        }
    });

    private static final Color LUMBER = new Color(255, 229, 204);
    private static final Color PEACH_ORANGE = new Color(252, 187, 122);

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = 90;
        int height = 30;

        GradientPaint gp = new GradientPaint(0,0,LUMBER,180,height,PEACH_ORANGE);
        g2d.setPaint(gp);
        g2d.fillRect(0,0,width,height);
    }

    Time() {
        setLayout(new BorderLayout());

        timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string);
        timeLabel.setBounds(0,0,30,10);
        timeLabel.setFont(new Font("Verdana",Font.PLAIN,15));
        timeLabel.setForeground(Color.BLACK);
        timeLabel.setBorder(BorderFactory.createBevelBorder(1));
        timeLabel.setHorizontalAlignment(JTextField.CENTER);

        add(timeLabel, BorderLayout.CENTER);
    }
}
