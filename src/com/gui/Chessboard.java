package com.gui;

import com.chessBOTP.Cells;
import com.chessBOTP.Main;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.GraphicsEnvironment;


public class Chessboard extends JFrame {

    //Class Variables
    private JPanel chessBoard, mainPanel, boardPanel;
    
    private RoundedPanel  pieceCapturedP1, pieceCapturedP2, 
                          namePanelP1, namePanelP2,
                          undoPanel, homePanel, settingsPanel;

    private JLayeredPane layeredPane;

    private JLabel player1Icon, player2Icon,
                   player1Name, player2Name,
                   pCapturedP1, pCapturedP2;

    public final Cells cells[][] = new Cells[8][8];
    private JButton pieceCapturedBoard1[][] = new JButton[4][4];
    private JButton pieceCapturedBoard2[][] = new JButton[4][4];
    
    private Time clock;

    private UndoButton undobutton;
    private HomeButton homebutton;
    private SettingsButton settingsbutton;

    private static final Color CALICO = new Color(224, 190, 145);
    private static final Color ZEUS = new Color(47, 38, 29);
    private static final Color CAMEO = new Color(214, 188, 153);

    public Chessboard() {
        initialize();
        GUI();
    }

    // Create the user interface of the actual game
    private void GUI() {
        setTitle("Masterchess: The Battle of the Pawns");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().add(getLayeredpane());

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        int width = 1280;
        int height = 720;
        setBounds(center.x - width / 2, center.y - height / 2, width, height);
    }

    // Initialize the components
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            
            e.printStackTrace();
        }

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
        JLabel gamebg = new JLabel(new ImageIcon(createImage("GameBackground.png", 1280,720)));
        gamebg.setBounds(0, 0, 1280, 720);

        /*
         * The mainPanel consists all of the elements above the gamebg
         * mainPanel -> {boardPanel, pieceCaptured1, pieceCaptured2, 
         *               namePanel1, namePanel2, clock,
         *               undobutton, settingsbutton, homebutton}
         */
        mainPanel = new JPanel(null);
        mainPanel.setOpaque(false);
        mainPanel.setSize(1280,720);

        // Contain the chess gaming board
        boardPanel = new JPanel(new GridBagLayout());
        boardPanel.setBounds(320,50,625,625);
        boardPanel.setBackground(new Color(49,42,33));
        boardPanel.setBorder(new LineBorder(CALICO, 2));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel topleft = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        boardPanel.add(topleft, gbc);
        
        // Create the upper letters of the chessboard
        createLetters(boardPanel, 0, false);

        JLabel topright = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridwidth = 2;
        gbc.gridx = 12;
        gbc.gridy = 12;
        boardPanel.add(topright, gbc);

        // Create the left side numbers of the chessboard
        createNumbers(boardPanel, 0, false);

        JLabel bottomleft = new JLabel("");
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.gridx = 0;
        gbc.gridy = 11;
        boardPanel.add(bottomleft, gbc);

        // Initialize the cells of the chessboard
        initChessBoardCells(cells, 8, true, CALICO, ZEUS,null);

        /*
         * Contains the cells of the chessboard alone
         */
        chessBoard = new JPanel(new GridLayout(0,8));
        chessBoard.setBorder(new LineBorder(CALICO, 2));
        chessBoard.setOpaque(false);
        chessBoard.setPreferredSize(new Dimension(550,550));
 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {   
                chessBoard.add(cells[i][j]);
            }
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 10;
        gbc.gridheight = 10;
        boardPanel.add(chessBoard, gbc);

        // Create the right side numbers of the chessboard
        createNumbers(boardPanel, 12, true);

        // Create the lower letters of the chessboard
        createLetters(boardPanel, 12, true);

        JLabel bottomright = new JLabel("");
        gbc.fill = GridBagConstraints.REMAINDER;
        boardPanel.add(bottomright, gbc);

        /*
         * The pieceCapturedP1 is the 8x8 board that will contain the pieces captured by player 1
         */
        pieceCapturedP1 = new RoundedPanel(new GridLayout(0,4),0);
        pieceCapturedP1.setBounds(27,200,270,270);

        initCapturedBoard(pieceCapturedBoard1, ZEUS, CALICO);

        for (int i = 0; i < 4; i++) {
            for (int j = 0;j < 4; j++) {
                pieceCapturedP1.add(pieceCapturedBoard1[i][j]);
            }
        }

        // Label for the piece captured board of player 1
        pCapturedP1 = new JLabel("Pieces Captured");
        pCapturedP1.setFont(new Font("Verdana",Font.PLAIN,15));
        pCapturedP1.setForeground(CALICO);
        pCapturedP1.setHorizontalAlignment(JTextField.CENTER);
        pCapturedP1.setBounds(102,170,122,15);

        /*
         * The pieceCapturedP2 is the 8x8 board that will contain the pieces captured by player 2
         */
        pieceCapturedP2 = new RoundedPanel(new GridLayout(0,4),0);
        pieceCapturedP2.setBounds(967,200,270,270);

        initCapturedBoard(pieceCapturedBoard2, CALICO, ZEUS);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pieceCapturedP2.add(pieceCapturedBoard2[i][j]);
            }
        }

        // Label of the piece captured board of player 2
        pCapturedP2 = new JLabel("Pieces Captured");
        pCapturedP2.setFont(new Font("Verdana",Font.PLAIN,15));
        pCapturedP2.setForeground(CALICO);
        pCapturedP2.setHorizontalAlignment(JTextField.CENTER);
        pCapturedP2.setBounds(1041,170,122,15);

        player1Icon = new JLabel(new ImageIcon(createImage("Player1.png",65,65)));
        player1Icon.setBounds(6,3,90,90);

        player1Name = new JLabel("Player 1");
        player1Name.setFont(new Font("Verdana",Font.BOLD,15));
        player1Name.setForeground(Color.BLACK);
        player1Name.setHorizontalAlignment(JTextField.CENTER);
        player1Name.setBounds(78,3,196,90);

        /*
         * The namePanelP1 will consist the name and icon of player 1
         */
        namePanelP1 = new RoundedPanel(null,10);
        namePanelP1.setBackground(CAMEO);
        namePanelP1.setBounds(18,25,289,100);
        namePanelP1.setBorder(new LineBorder(ZEUS, 2, true));
        namePanelP1.add(player1Icon);
        namePanelP1.add(player1Name);

        player2Icon = new JLabel(new ImageIcon(createImage("Player2.png",65,65)));
        player2Icon.setBounds(186,3,90,90);

        player2Name = new JLabel("Player 2");
        player2Name.setFont(new Font("Verdana",Font.BOLD,15));
        player2Name.setForeground(Color.BLACK);
        player2Name.setHorizontalAlignment(JTextField.CENTER);
        player2Name.setBounds(10,3,196,90);

        /*
         * The namePanelP1 will consist the name and icon of player 2
         */
        namePanelP2 = new RoundedPanel(null, 10);
        namePanelP2.setBackground(CAMEO);
        namePanelP2.setBounds(958,25,289,100);
        namePanelP2.setBorder(new LineBorder(CALICO, 2, true));
        namePanelP2.add(player2Icon);
        namePanelP2.add(player2Name);

        /*
         * The clock is the timer of the game
         */
        clock = new Time();
        clock.setBounds(595,10,90,30);

        undoPanel = new RoundedPanel(null, 10);
        undoPanel.setBackground(CAMEO);
        undoPanel.setBounds(18,600,60,60);

        undobutton = new UndoButton(undoPanel);
        undobutton.setBounds(0,0,60,60);

        undoPanel.add(undobutton);

        homePanel = new RoundedPanel(null, 10);
        homePanel.setBackground(CAMEO);
        homePanel.setBounds(1184,600,60,60);
        
        homebutton = new HomeButton(homePanel);
        homebutton.setBounds(0,0,60,60);

        homePanel.add(homebutton);

        settingsPanel = new RoundedPanel(null, 10);
        settingsPanel.setBackground(CAMEO);
        settingsPanel.setBounds(1184,530,60,60);
        
        settingsbutton = new SettingsButton(clock, settingsPanel);
        settingsbutton.setBounds(0,0,60,60);
        
        settingsPanel.add(settingsbutton);

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

        clock.timer.start();
    }

    // Method for initializing images
    public Image createImage(String filename, int width, int height) {
        Image background = null;
        try {
            InputStream in = Chessboard.class.getResourceAsStream(filename);
            BufferedImage bi = ImageIO.read(in);
            ImageIcon icon = new ImageIcon(bi);
            background = icon.getImage().getScaledInstance(width, height,Image.SCALE_SMOOTH);;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return background;
    }

    // Method for initializing chessboard cells
    public void initChessBoardCells(Cells[][] board, int size, boolean isEnabled, Color color1, Color color2, Color color3) {
        Insets buttonMargin = new Insets(0, 0, 0, 0);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Cells b = new Cells(j, i, 0, 0);
                    b.setMargin(buttonMargin);
                    b.setEnabled(isEnabled);
                    b.setFocusable(false);
                    b.setBorder(new LineBorder(color3, 1, false));
                    b.addActionListener(Main::buttonClickedHandler);
                    
                    ImageIcon icon = new ImageIcon(new BufferedImage(64, 64,
                            BufferedImage.TYPE_INT_ARGB));
                    b.setIcon(icon);

                    if (((j % 2 == 1) && (i % 2 == 1))
                            || ((j % 2 == 0) && (i % 2 == 0))) {
                        b.setBackground(color1);
                    } else {
                        b.setBackground(color2);
                    }
                    board[j][i] = b;
                }
            }
    }

    // Method for initializing captured board cells
    public void initCapturedBoard(JButton[][] board, Color color1, Color color2) {
        Insets buttonMargin = new Insets(0, 0, 0, 0);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    JButton b = new JButton();
                    b.setMargin(buttonMargin);
                    b.setEnabled(false);
                    b.setFocusable(false);
                    b.setBorder(new LineBorder(color1, 1, false));
                    b.addActionListener(Main::buttonClickedHandler);
                    
                    ImageIcon icon = new ImageIcon(new BufferedImage(64, 64,
                            BufferedImage.TYPE_INT_ARGB));
                    b.setIcon(icon);
                    b.setBackground(color2);
                    board[i][j] = b;
                }
            }
    }

    // Method for repainting cells of the chessboard
    public void repaintBoard(Cells[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (((j % 2 == 1) && (i % 2 == 1))
                        || ((j % 2 == 0) && (i % 2 == 0))) {
                    board[i][j].setBackground(CALICO);
                } else {
                    board[i][j].setBackground(ZEUS);
                }
            }
        }
    }

    // Method for creating the chessboard letters
    private void createLetters(JPanel boardPanel, int row, boolean isReverse) {
        GridBagConstraints gbc = new GridBagConstraints();
        String letters = "ABCDEFGH";
        String reverse = "";
        if(isReverse) {
            for(int i = 0; i<letters.length(); i++) {
                char ch = letters.charAt(i);
                reverse = ch + reverse;
            }
        } else {
            reverse = letters;
        }
        
        for (int i = 0; i < 8; i++) {
            JLabel letter = new JLabel(reverse.substring(i, i + 1),
                            SwingConstants.CENTER);
            letter.setForeground(CALICO);
            letter.setFont(new Font("Times New Roman",Font.PLAIN,12) );
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = i+2;
            gbc.gridy = row;
            gbc.weightx = 1;
            gbc.weighty = 1;
            boardPanel.add(letter,gbc);
        }
    }

    // Method for creating the chessboard numbers
    private void createNumbers(JPanel boardPanel, int column, boolean isReverse) {
        GridBagConstraints gbc = new GridBagConstraints();
        int num;
        for (int i = 0; i < 8; i++) {
            if(isReverse) {
                num = i + 1;
            } else {
                num = 8 - i;
            }
            JLabel number = new JLabel("" + num,
                            SwingConstants.CENTER);
            number.setForeground(CALICO);
            number.setFont(new Font("Times New Roman",Font.PLAIN,12) );
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = column;
            gbc.gridy = i+2;
            gbc.weightx = 1;
            gbc. weighty = 1;
            boardPanel.add(number,gbc);
        }
    }
    

    public JComponent getLayeredpane() {
        return layeredPane;
    }

    public Cells[][] getCells() {
        return cells;
    }
}
