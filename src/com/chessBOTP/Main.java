package com.chessBOTP;

import com.gui.MainWindow;

import java.awt.*;

public class Main {
    static MainWindow mainWindow;
    static Players player1;
    static Players player2;

    public static void main(String[] args) {
        //Create another thread for MainWindow
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                    arrangeBoard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void play() {
        //**Flow of the Program**//
        arrangeBoard(); //Arrange The Board
        player1 = new Players("Player 1"); //Create Player 1
        player2 = new Players("Player 2"); //Create Player 2
        TurnBasedHandler turnHandler = new TurnBasedHandler(player1, player2); //Create Turn Handler
    }


    public static void pause() {
    }

    public static void arrangeBoard() {
        //arrange the board
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (x == 0) {
                    if (y == 0 || y == 7) {
                        mainWindow.getCells()[x][y].CONTAINS = 1;
                        mainWindow.getCells()[x][y].setText("R");
                    } else if (y == 1 || y == 6) {
                        mainWindow.getCells()[x][y].CONTAINS = 2;
                        mainWindow.getCells()[x][y].setText("N");
                    } else if (y == 2 || y == 5) {
                        mainWindow.getCells()[x][y].CONTAINS = 3;
                        mainWindow.getCells()[x][y].setText("B");
                    } else if (y == 3) {
                        mainWindow.getCells()[x][y].CONTAINS = 4;
                        mainWindow.getCells()[x][y].setText("Q");
                    } else if (y == 4) {
                        mainWindow.getCells()[x][y].CONTAINS = 5;
                        mainWindow.getCells()[x][y].setText("K");
                    }
                } else if (x == 1) {
                    mainWindow.getCells()[x][y].CONTAINS = 6;
                    mainWindow.getCells()[x][y].setText("P");
                } else if (x == 6) {
                    mainWindow.getCells()[x][y].CONTAINS = 12;
                    mainWindow.getCells()[x][y].setText("P");
                } else if (x == 7) {
                    if (y == 0 || y == 7) {
                        mainWindow.getCells()[x][y].CONTAINS = 7;
                        mainWindow.getCells()[x][y].setText("R");
                    } else if (y == 1 || y == 6) {
                        mainWindow.getCells()[x][y].CONTAINS = 8;
                        mainWindow.getCells()[x][y].setText("N");
                    } else if (y == 2 || y == 5) {
                        mainWindow.getCells()[x][y].CONTAINS = 9;
                        mainWindow.getCells()[x][y].setText("B");
                    } else if (y == 3) {
                        mainWindow.getCells()[x][y].CONTAINS = 10;
                        mainWindow.getCells()[x][y].setText("Q");
                    } else if (y == 4) {
                        mainWindow.getCells()[x][y].CONTAINS = 11;
                        mainWindow.getCells()[x][y].setText("K");
                    }
                }
            }
        }
    }

    public static void nextTurn() {
        //next turn
    }
}
