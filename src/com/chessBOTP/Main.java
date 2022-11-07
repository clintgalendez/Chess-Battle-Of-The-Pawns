package com.chessBOTP;

import com.gui.MainWindow;

import java.awt.*;
import java.awt.event.ActionEvent;

public class Main {
    static MainWindow mainWindow = new MainWindow();
    static Players player1;
    static Players player2;
    static boolean gameStarted = false;
    static TurnBasedHandler turnHandler;
    static boolean allowedToMove = false;
    static Cells tempCell;

    public static void main(String[] args) {
        //Create another thread for MainWindow
        EventQueue.invokeLater(() -> {
            try {
                mainWindow.setVisible(true);
                arrangeBoard();
                play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void play() {
        //**Flow of the Program**//
        player1 = new Players("Player 1", -1); //Create Player 1
        player2 = new Players("Player 2", 1); //Create Player 2
        turnHandler = new TurnBasedHandler(player1, player2); //Create Turn Handler
        gameStarted = true;
    }

    public static void buttonClickedHandler(ActionEvent e) {
        Cells chosenCell = (Cells) e.getSource();

        if (!gameStarted) {
            return;
        }

        if (tempCell == chosenCell) {
            resetColAvailCells();
            tempCell = null;
            allowedToMove = false;
            return;
        }

        //Check if the cell is now available to choose among the available cells
        if (allowedToMove) {
            /*
            get the color of the cell that is clicked and check if it is green
            for green means that it is available to be moved to.
             */
            Color color = chosenCell.getBackground();
            if (color != Color.GREEN) {
                return;
            }

            //Check if the cell is occupied by another player, and if it is, add it to the destroyed pieces
            if (turnHandler.getCurrentPlayer().getPlayerColor() == turnHandler.getNextPlayer().getPlayerColor()) {
                turnHandler.getCurrentPlayer().addDestroyedPiece(chosenCell.CONTAINS);
            }

            //**Move the Piece**//
            chosenCell.CONTAINS = tempCell.CONTAINS; //The previous cell will now move to the new cell
            chosenCell.pieceColor = tempCell.pieceColor; //The newly clicked cell will contain the color of the previous cell
            chosenCell.setText(tempCell.getText()); //The newly clicked cell will contain the text of the previous cell
            turnHandler.getCurrentPlayer().addMove(chosenCell); //Add the move to the current player's moves

            //**Reset The previous Cell**//
            tempCell.CONTAINS = 0;
            tempCell.pieceColor = 0;
            tempCell.setText("");
            turnHandler.nextTurn(); //Change the turn to the next player
            allowedToMove = false;
            resetColAvailCells();
            return;
        }

        if (turnHandler.getCurrentPlayer().getPlayerColor() == chosenCell.pieceColor) {
            chosenCell.setBackground(Color.YELLOW);
            setColAvailCells(chosenCell, turnHandler.getCurrentPlayer().getPlayerColor());
        }
    }

    public static Cells calculateFutureMove(Cells chosenCell, int currentColorPiece, int i, int[][]moves) {
        int x = chosenCell.posX + moves[i][0];
        int y = chosenCell.posY + (currentColorPiece * moves[i][1]);
        if (x >= 0 && x < 8 && y >= 0 && y < 8 && mainWindow.getCells()[x][y].pieceColor != currentColorPiece) {
            return mainWindow.getCells()[x][y];
        }

        return null;
    }

    public static void setColAvailCells(Cells chosenCell, int currentColorPiece) {
        int piece = chosenCell.CONTAINS;
        Cells futureCells;

        if (currentColorPiece == 0) {
            return;
        }

        if (chosenCell.CONTAINS == 7 || chosenCell.CONTAINS == 8 || chosenCell.CONTAINS == 9) {
            for (int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
                int[][] moves = MoveSets.getAvailableMoves(piece);

                futureCells = calculateFutureMove(chosenCell, currentColorPiece, i, moves);
                if (futureCells == null) {
                    continue;
                }

                futureCells.setBackground(Color.GREEN);

                while (true) {
                    futureCells = calculateFutureMove(futureCells, currentColorPiece, i, moves);
                    if (futureCells == null) {
                        break;
                    }

                    futureCells.setBackground(Color.GREEN);

                    if (futureCells.CONTAINS != 0) {
                        break;
                    }
                }
            }

        } else {
            for (int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
                int[][] moves = MoveSets.getAvailableMoves(piece);

                futureCells = calculateFutureMove(chosenCell, currentColorPiece, i, moves);
                if (futureCells == null) {
                    continue;
                }

                futureCells.setBackground(Color.GREEN);
            }
        }

        tempCell = chosenCell;
        allowedToMove = true;
    }

    public static void resetColAvailCells() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mainWindow.getCells()[i][j].setBackground(Color.WHITE);
            }
        }
    }

    public static void pause() {
    }

    public static void arrangeBoard() {
        //arrange the board
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (y == 0) {
                    if (x == 0 || x == 7) {
                        mainWindow.getCells()[x][y].CONTAINS = 8;
                        mainWindow.getCells()[x][y].setText("R");
                        mainWindow.getCells()[x][y].pieceColor = 1;
                    } else if (x == 1 || x == 6) {
                        mainWindow.getCells()[x][y].CONTAINS = 1;
                        mainWindow.getCells()[x][y].setText("N");
                        mainWindow.getCells()[x][y].pieceColor = 1;
                    } else if (x == 2 || x == 5) {
                        mainWindow.getCells()[x][y].CONTAINS = 7;
                        mainWindow.getCells()[x][y].setText("B");
                        mainWindow.getCells()[x][y].pieceColor = 1;
                    } else if (x == 3) {
                        mainWindow.getCells()[x][y].CONTAINS = 9;
                        mainWindow.getCells()[x][y].setText("Q");
                        mainWindow.getCells()[x][y].pieceColor = 1;
                    } else {
                        mainWindow.getCells()[x][y].CONTAINS = 2;
                        mainWindow.getCells()[x][y].setText("K");
                        mainWindow.getCells()[x][y].pieceColor = 1;
                    }
                } else if (y == 1) {
                    mainWindow.getCells()[x][y].CONTAINS = 5;
                    mainWindow.getCells()[x][y].setText("P");
                    mainWindow.getCells()[x][y].pieceColor = 1;
                } else if (y == 6) {
                    mainWindow.getCells()[x][y].CONTAINS = 5;
                    mainWindow.getCells()[x][y].setText("P");
                    mainWindow.getCells()[x][y].pieceColor = -1;
                } else if (y == 7) {
                    if (x == 0 || x == 7) {
                        mainWindow.getCells()[x][y].CONTAINS = 8;
                        mainWindow.getCells()[x][y].setText("R");
                        mainWindow.getCells()[x][y].pieceColor = -1;
                    } else if (x == 1 || x == 6) {
                        mainWindow.getCells()[x][y].CONTAINS = 1;
                        mainWindow.getCells()[x][y].setText("N");
                        mainWindow.getCells()[x][y].pieceColor = -1;
                    } else if (x == 2 || x == 5) {
                        mainWindow.getCells()[x][y].CONTAINS = 7;
                        mainWindow.getCells()[x][y].setText("B");
                        mainWindow.getCells()[x][y].pieceColor = -1;
                    } else if (x == 3) {
                        mainWindow.getCells()[x][y].CONTAINS = 9;
                        mainWindow.getCells()[x][y].setText("Q");
                        mainWindow.getCells()[x][y].pieceColor = -1;
                    } else {
                        mainWindow.getCells()[x][y].CONTAINS = 2;
                        mainWindow.getCells()[x][y].setText("K");
                        mainWindow.getCells()[x][y].pieceColor = -1;
                    }
                }
            }
        }
    }

}
