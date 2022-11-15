package com.main;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gui_components.Board;
import com.gui_components.BoardLabel;
import com.gui_components.Chessboard;
import com.gui_components.Clock;
import com.gui_components.NamePanel;
import com.gui_components.RoundPanel;
import com.handlers.BoardCellsHandler;
import com.handlers.HomeHandler;
import com.handlers.SettingsHandler;
import com.handlers.TurnBasedHandler;
import com.handlers.UndoHandler;
import com.loaders.GraphicsLoader;
import com.mechanics.Cells;
import com.mechanics.Players;

public class GameUI {
    private static final Color CALICO = new Color(224, 190, 145);
    private static final Color ZEUS = new Color(47, 38, 29);
    private static final Color CAMEO = new Color(214, 188, 153);

    public final Cells[][] cells = new Cells[8][8];

    private final int WIDTH = 1280;
    private final int HEIGHT = 720;

    private JLayeredPane layeredPane;

    private RoundPanel undoPanel;
    private RoundPanel homePanel;
    private RoundPanel settingsPanel;

    private NamePanel namePanelOne;
    private NamePanel namePanelTwo;

    private Board piecesBoardOne;
    private Board piecesBoardTwo;

    private ArrayList<Cells> moveList = new ArrayList<>();

    private boolean gameStarted = false;
    private boolean allowedToMove = false;
    private boolean isSuggesting = false;
    private boolean onAuto = false;

    private int[] coordinates = {0, 0, 0, 0};
    private int checkedPiece;

    private Cells prevChosenCell;

    private Players player1;
    private Players player2;

    private TurnBasedHandler turnHandler;

    private BoardCellsHandler bch;

    public GameUI() {
        init();
        arrangeBoard();
        //Create another thread for Gameplay
        Thread game = new Thread(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }); 
        game.start();
    }

    public void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            
            e.printStackTrace();
        }

        layeredPane = new JLayeredPane();
        layeredPane.setSize(WIDTH, HEIGHT);

        JLabel gameBackground = new JLabel(new ImageIcon(GraphicsLoader.loadImage("resources/GameBackground.png", WIDTH, HEIGHT)));
        gameBackground.setSize(WIDTH, HEIGHT);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setOpaque(false);
        mainPanel.setSize(WIDTH, HEIGHT);

        Chessboard chessboard = new Chessboard(cells, CALICO, ZEUS, this);
        chessboard.setBounds(320, 50, 625, 625);

        piecesBoardOne = new Board(ZEUS, CALICO);
        piecesBoardOne.setBounds(27,200,270,270);

        piecesBoardTwo = new Board(CALICO, ZEUS);
        piecesBoardTwo.setBounds(967,200,270,270);

        BoardLabel boardLabelOne = new BoardLabel("Pieces Captured", CALICO);
        boardLabelOne.setBounds(102,170,122,15);

        BoardLabel boardLabelTwo = new BoardLabel("Pieces Captured", CALICO);
        boardLabelTwo.setBounds(1041,170,122,15);

        namePanelOne = new NamePanel("Player 1", CAMEO, ZEUS, false);
        namePanelOne.setBounds(18,25,289,100);

        namePanelTwo = new NamePanel("Player 2", CAMEO, ZEUS, true);
        namePanelTwo.setBounds(958,25,289,100);

        Clock clock = new Clock();
        clock.setBounds(595,10,90,30);

        undoPanel = new RoundPanel(CAMEO, 10, "resources/Undo.png", new UndoHandler(bch, this));
        undoPanel.setBounds(18,600,60,60);

        homePanel = new RoundPanel(CAMEO, 10, "resources/Home.png", new HomeHandler(this));
        homePanel.setBounds(1184,600,60,60);

        settingsPanel = new RoundPanel(CAMEO, 10, "resources/Settings.png", new SettingsHandler(this));
        settingsPanel.setBounds(1184,530,60,60);

        mainPanel.add(chessboard);
        mainPanel.add(piecesBoardOne);
        mainPanel.add(piecesBoardTwo);
        mainPanel.add(boardLabelOne);
        mainPanel.add(boardLabelTwo);
        mainPanel.add(namePanelOne);
        mainPanel.add(namePanelTwo);
        mainPanel.add(clock);
        mainPanel.add(undoPanel);
        mainPanel.add(homePanel);
        mainPanel.add(settingsPanel);

        layeredPane.add(gameBackground, Integer.valueOf(0));
        layeredPane.add(mainPanel, Integer.valueOf(1));
    }

    public void play() {
        // Flow of the Program 
        player1 = new Players("Player 1", -1); // Create Player 1
        player2 = new Players("Player 2", 1); // Create Player 2
        turnHandler = new TurnBasedHandler(player1, player2, this); // Create Turn Handler
        gameStarted = true;
    }

    private void arrangeBoard() {
        //arrange the board
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (y == 0) {
                    if (x == 0 || x == 7) {
                        cells[x][y].CONTAINS = 8;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackRook.png", 55, 55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = 1;
                    } else if (x == 1 || x == 6) {
                        cells[x][y].CONTAINS = 1;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackKnight.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = 1;
                    } else if(x == 2 || x == 5) {
                        cells[x][y].CONTAINS = 7;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackBishop.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = 1;
                    } else if(x == 3) {
                        cells[x][y].CONTAINS = 9;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackQueen.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = 1;
                    } else {
                        cells[x][y].CONTAINS = 2;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackKing.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = 1;
                    }
                } else if(y == 1) {
                    cells[x][y].CONTAINS = 5;
                    cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackPawn.png",55,55)));
                    cells[x][y].piece = cells[x][y].getIcon();
                    cells[x][y].pieceColor = 1;
                } else if(y == 6) {
                    cells[x][y].CONTAINS = 5;
                    cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhitePawn.png",55,55)));
                    cells[x][y].piece = cells[x][y].getIcon();
                    cells[x][y].pieceColor = -1;
                } else if(y == 7) {
                    if(x == 0 || x == 7) {
                        cells[x][y].CONTAINS = 8;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteRook.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = -1;
                    } else if(x == 1 || x == 6) {
                        cells[x][y].CONTAINS = 1;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteKnight.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = -1;
                    } else if(x == 2 || x == 5) {
                        cells[x][y].CONTAINS = 7;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteBishop.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = -1;
                    } else if(x == 3) {
                        cells[x][y].CONTAINS = 9;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteQueen.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = -1;
                    } else {
                        cells[x][y].CONTAINS = 2;
                        cells[x][y].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteKing.png",55,55)));
                        cells[x][y].piece = cells[x][y].getIcon();
                        cells[x][y].pieceColor = -1;
                    }
                }
            }
        }
    }

    public NamePanel getNamePanel(Players player) {
        if (player.getPlayerColor() == -1) {
            return namePanelOne;
        } else {
            return namePanelTwo;
        }
    }

    public Board getBoard(Players player) {
        if (player.getPlayerColor() == -1) {
            return piecesBoardOne;
        } else {
            return piecesBoardTwo;
        }
    }

    public RoundPanel getUndoPanel() {
        return undoPanel;
    }

    public RoundPanel getHomePanel() {
        return homePanel;
    }

    public RoundPanel getSettingsPanel() {
        return settingsPanel;
    }

    public Cells[][] getCells() {
        return cells;
    }

    public JLayeredPane getLayeredPane() {
        return layeredPane;
    }

    public ArrayList<Cells> getMoveList() {
        return moveList;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isAllowedToMove() {
        return allowedToMove;
    }

    public void setAllowedToMove(boolean allowedToMove) {
        this.allowedToMove = allowedToMove;
    }

    public boolean isSuggesting() {
        return isSuggesting;
    }

    public void setSuggesting(boolean isSuggesting) {
        this.isSuggesting = isSuggesting;
    }

    public boolean isOnAuto() {
        return onAuto;
    }

    public void setOnAuto(boolean onAuto) {
        this.onAuto = onAuto;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public int getCheckedPiece() {
        return checkedPiece;
    }

    public void setCheckedPiece(int checkedPiece) {
        this.checkedPiece = checkedPiece;
    }

    public Cells getPrevChosenCell() {
        return prevChosenCell;
    }

    public void setPrevChosenCell(Cells prevChosenCell) {
        this.prevChosenCell = prevChosenCell;
    }

    public Players getPlayer1() {
        return player1;
    }

    public Players getPlayer2() {
        return player2;
    }

    public TurnBasedHandler getTurnHandler() {
        return turnHandler;
    }

    public void setBCH(BoardCellsHandler bch) {
        this.bch = bch;
    }
}