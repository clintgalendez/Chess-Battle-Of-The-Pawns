// Graphical User Interface of the actual game

package com.gui;

import com.chessBOTP.Cells;
import com.chessBOTP.Main;
import com.chessBOTP.Players;

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
    private JPanel mainPanel, boardPanel, 
                   capturedBoardPanel1, capturedBoardPanel2;
    
    private RoundedPanel  namePanelP1, namePanelP2,
                          undoPanel, homePanel, settingsPanel;

    private JLayeredPane layeredPane;

    private JLabel player1Icon, player2Icon,
                   player1Name, player2Name,
                   capturedBoardLabel1, capturedBoardLabel2;

    public final Cells cells[][] = new Cells[8][8];
    private JButton capturedBoard1[][] = new JButton[4][4];
    private JButton capturedBoard2[][] = new JButton[4][4];
    
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
    public void GUI() {
        setTitle("Masterchess: The Battle of the Pawns");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().add(getLayeredpane());

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        int width = 1280;
        int height = 720;
        setBounds(center.x - width / 2, center.y - height / 2, width, height);

        setVisible(true);
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

        // The gamebg is background of the game
        JLabel gamebg = new JLabel(new ImageIcon(createImage("images/GameBackground.png", 1280,720)));
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

        // The boardPanel is the 8x8 chessboard including the letters and numbers
        boardPanel = createChessBoardPanel(cells, CALICO, ZEUS);
        boardPanel.setBounds(320,50,625,625);

        // The pieceCapturedP1 is the 8x8 board that will contain the pieces captured by player 1
        capturedBoardPanel1 = createCapturedBoardPanel(capturedBoard1, ZEUS, CALICO);
        capturedBoardPanel1.setBounds(27,200,270,270);

        // Label for the piece captured board of player 1
        capturedBoardLabel1 = createBoardLabel();
        capturedBoardLabel1.setBounds(102,170,122,15);

        // The pieceCapturedP2 is the 8x8 board that will contain the pieces captured by player 2
        capturedBoardPanel2 = createCapturedBoardPanel(capturedBoard2, CALICO, ZEUS);
        capturedBoardPanel2.setBounds(967,200,270,270);

        // Label of the piece captured board of player 2
        capturedBoardLabel2 = createBoardLabel();
        capturedBoardLabel2.setBounds(1041,170,122,15);

        // Icon for player 1
        player1Icon = new JLabel(new ImageIcon(createImage("images/Player1.png",65,65)));
        player1Icon.setBounds(6,3,90,90);

        // Name of player 1
        player1Name = createPlayerName(player1Name, "Player 1");
        player1Name.setBounds(78,3,196,90);

        // The namePanelP1 will consist the name and icon of player 1
        namePanelP1 = createPlayerNamePanel(namePanelP1);
        namePanelP1.setBorder(new LineBorder(ZEUS, 2, true));
        namePanelP1.setBounds(18,25,289,100);
        namePanelP1.add(player1Icon);
        namePanelP1.add(player1Name);

        // Icon for player 2
        player2Icon = new JLabel(new ImageIcon(createImage("images/Player2.png",65,65)));
        player2Icon.setBounds(186,3,90,90);

        // Name of player 2
        player2Name = createPlayerName(player2Name, "Player 2");
        player2Name.setBounds(10,3,196,90);

        // The namePanelP1 will consist the name and icon of player 2
        namePanelP2 = createPlayerNamePanel(namePanelP2);
        namePanelP2.setBorder(new LineBorder(CALICO, 2, true));
        namePanelP2.setBounds(958,25,289,100);
        namePanelP2.add(player2Icon);
        namePanelP2.add(player2Name);

        // The clock is the timer of the game
        clock = new Time();
        clock.setBounds(595,10,90,30);

        // Contains the undo button
        undoPanel = new RoundedPanel(null, 10);
        undoPanel.setBackground(CAMEO);
        undoPanel.setBounds(18,600,60,60);

        undobutton = new UndoButton(undoPanel);
        undobutton.setBounds(0,0,60,60);

        undoPanel.add(undobutton);

        // Contains the home button
        homePanel = new RoundedPanel(null, 10);
        homePanel.setBackground(CAMEO);
        homePanel.setBounds(1184,600,60,60);
        
        homebutton = new HomeButton(homePanel);
        homebutton.setBounds(0,0,60,60);

        homePanel.add(homebutton);

        // Contains the settings button
        settingsPanel = new RoundedPanel(null, 10);
        settingsPanel.setBackground(CAMEO);
        settingsPanel.setBounds(1184,530,60,60);
        
        settingsbutton = new SettingsButton(clock, settingsPanel, mainPanel);
        settingsbutton.setBounds(0,0,60,60);
        
        settingsPanel.add(settingsbutton);

        mainPanel.add(boardPanel);
        mainPanel.add(capturedBoardPanel1);
        mainPanel.add(capturedBoardLabel1);
        mainPanel.add(capturedBoardPanel2);
        mainPanel.add(capturedBoardLabel2);
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

    // Initialize chessboard cells
    private void initChessBoardCells(Cells[][] cells, int dimension, boolean isEnabled, Color color1, Color color2, Color color3) {
        Insets buttonMargin = new Insets(0, 0, 0, 0);

            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    Cells cell = new Cells(j, i, 0, 0,null);
                    cell.setMargin(buttonMargin);
                    cell.setEnabled(isEnabled);
                    cell.setFocusable(false);
                    cell.setBorder(new LineBorder(color3, 1, false));
                    cell.addActionListener(Main::buttonClickedHandler);
                    
                    ImageIcon icon = new ImageIcon(new BufferedImage(64, 64,
                            BufferedImage.TYPE_INT_ARGB));
                    cell.setIcon(icon);

                    if (((j % 2 == 1) && (i % 2 == 1))
                            || ((j % 2 == 0) && (i % 2 == 0))) {
                        cell.setBackground(color1);
                    } else {
                        cell.setBackground(color2);
                    }
                    cells[j][i] = cell;
                }
            }
    }

    // Create letters of the chessboard
    private void createLetters(JPanel panel, int row, boolean isReverse) {
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
            panel.add(letter,gbc);
        }
    }

    // Create numbers of the chessboard
    private void createNumbers(JPanel panel, int column, boolean isReverse) {
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
            panel.add(number,gbc);
        }
    }

    // Create the 8x8 chessboard with its numbers and letters
    private JPanel createChessBoardPanel(Cells[][] cells, Color color1, Color color2) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(49,42,33));
        panel.setBorder(new LineBorder(color1, 2));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel topleft = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(topleft, gbc);
        
        createLetters(panel, 0, true);

        JLabel topright = new JLabel("");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridwidth = 2;
        gbc.gridx = 12;
        gbc.gridy = 12;
        panel.add(topright, gbc);

        createNumbers(panel, 0, false);

        JLabel bottomleft = new JLabel("");
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        gbc.gridx = 0;
        gbc.gridy = 11;
        panel.add(bottomleft, gbc);

        // Initialize the cells of the chessboard
        initChessBoardCells(cells, 8, true, color1, color2, null);

        JPanel board = new JPanel(new GridLayout(0,8));
        board.setBorder(new LineBorder(color1, 2));
        board.setOpaque(false);
        board.setPreferredSize(new Dimension(550,550));
 
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {   
                board.add(cells[i][j]);
            }
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 10;
        gbc.gridheight = 10;
        panel.add(board, gbc);

        createNumbers(panel, 12, true);

        createLetters(panel, 12, false);

        JLabel bottomright = new JLabel("");
        gbc.fill = GridBagConstraints.REMAINDER;
        panel.add(bottomright, gbc);

        return panel;
    }

    // Initialize captured board cells
    private void initCapturedBoard(JButton[][] cells, Color color1, Color color2) {
        Insets buttonMargin = new Insets(0, 0, 0, 0);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    JButton cell = new JButton();
                    cell.setMargin(buttonMargin);
                    cell.setEnabled(false);
                    cell.setFocusable(false);
                    cell.setBorder(new LineBorder(color1, 1, false));
                    ImageIcon icon = new ImageIcon(new BufferedImage(64, 64,
                            BufferedImage.TYPE_INT_ARGB));
                    cell.setIcon(icon);
                    cell.setBackground(color2);
                    cells[j][i] = cell;
                }
            }
    }

    // Create captured board
    private JPanel createCapturedBoardPanel(JButton[][] cells, Color color1, Color color2) {
        JPanel panel = new JPanel(new GridLayout(0,4));

        initCapturedBoard(cells, color1, color2);

        for (int i = 0; i < 4; i++) {
            for (int j = 0;j < 4; j++) {
                panel.add(cells[i][j]);
            }
        }

        return panel;
    }

    // Create captured board label
    private JLabel createBoardLabel() {
        JLabel label = new JLabel("Pieces Captured");
        label.setFont(new Font("Verdana",Font.PLAIN,15));
        label.setForeground(CALICO);
        label.setHorizontalAlignment(JTextField.CENTER);

        return label;
    }

    // Add piece to captured board
    public void addToCapturedBoard(Cells chosenCell, int[] coordinates) {
        int i = coordinates[0]; int j = coordinates[1];
        int m = coordinates[2]; int n = coordinates[3];
        if(chosenCell.pieceColor == 1) {
            capturedBoard1[i][j].setIcon(chosenCell.piece);
            capturedBoard1[i][j].setDisabledIcon(chosenCell.piece);
        } else {
            capturedBoard2[m][n].setIcon(chosenCell.piece);
            capturedBoard2[m][n].setDisabledIcon(chosenCell.piece);
        }
    }

    // Remove piece from captured bored
    public void removeFromCapturedBoard(Players player, int y, int x) {
        if(player.getPlayerColor() == 1) {
            capturedBoard1[y][x].setIcon(null);
        } else {
            capturedBoard2[y][x].setIcon(null);
        }
    }

    // Create player name
    private JLabel createPlayerName(JLabel label, String name) {
        label = new JLabel(name);
        label.setFont(new Font("Verdana",Font.BOLD,15));
        label.setForeground(Color.BLACK);
        label.setHorizontalAlignment(JTextField.CENTER);
        
        return label;
    }

    // Contain player name
    private RoundedPanel createPlayerNamePanel(RoundedPanel panel) {
        panel = new RoundedPanel(null,10);
        panel.setBackground(CAMEO);
        panel.setBorder(new LineBorder(ZEUS, 2, true));

        return panel;
    }

    // Create images and icons for the game
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

    // Return a captured board
    public JButton[][] getCapturedBoard(Players player) {
        if (player.getPlayerColor() == -1) {
            return capturedBoard1;
        } else {
            return capturedBoard2;
        }
    }

    // Return a name panel
    public RoundedPanel getNamePanel(Players player) {
        if (player.getPlayerColor() == -1) {
            return namePanelP1;
        } else {
            return namePanelP2;
        }
    }

    // Return chessboard cells
    public Cells[][] getCells() {
        return cells;
    }

    // Return the layered pane of the GUI
    public JComponent getLayeredpane() {
        return layeredPane;
    }
}
