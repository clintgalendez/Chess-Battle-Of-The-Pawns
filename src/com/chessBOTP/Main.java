package com.chessBOTP;

import com.gui.Chessboard;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

public class Main {
    static Chessboard chessboard = new Chessboard();
    static Players player1;
    static Players player2;
    static boolean gameStarted = false;
    static TurnBasedHandler turnHandler;
    static boolean allowedToMove = false;
    static Cells prevChosenCell;

    public static void main(String[] args) {
        //Create another thread for MainWindow
        EventQueue.invokeLater(() -> {
            try {
                chessboard.setVisible(true);
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

        if (prevChosenCell == chosenCell) {
            chessboard.repaintBoard(chessboard.getCells());
            prevChosenCell = null;
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
            chosenCell.CONTAINS = prevChosenCell.CONTAINS; //The previous cell will now move to the new cell
            chosenCell.pieceColor = prevChosenCell.pieceColor; //The newly clicked cell will contain the color of the previous cell
            chosenCell.setIcon(prevChosenCell.getIcon()); //The newly clicked cell will contain the text of the previous cell
            turnHandler.getCurrentPlayer().addMove(chosenCell); //Add the move to the current player's moves

            //**Reset The previous Cell**//
            prevChosenCell.CONTAINS = 0;
            prevChosenCell.pieceColor = 0;
            prevChosenCell.setIcon(null);
            turnHandler.nextTurn(); //Change the turn to the next player
            allowedToMove = false;
            chessboard.repaintBoard(chessboard.getCells());
            return;
        }

        if (turnHandler.getCurrentPlayer().getPlayerColor() == chosenCell.pieceColor) {
            chosenCell.setBackground(Color.YELLOW);
            setColAvailCells(chosenCell, turnHandler.getCurrentPlayer().getPlayerColor());
        }
    }

    public static Cells calculateFutureMove(Cells chosenCell, int currentColorPiece, int i, int[][]moves) {
        int x = chosenCell.posX + (currentColorPiece * moves[i][1]);
        int y = chosenCell.posY + moves[i][0];
        if (x >= 0 && x < 8 && y >= 0 && y < 8 && chessboard.getCells()[x][y].pieceColor != currentColorPiece) {
            return chessboard.getCells()[x][y];
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

                if (futureCells.CONTAINS != 0) {
                    continue;
                }

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

        prevChosenCell = chosenCell;
        allowedToMove = true;
    }

    public static void resetColAvailCells() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard.getCells()[i][j].setBackground(Color.WHITE);
            }
        }
    }

    public static void pause() {
    }

    public static void arrangeBoard() {
        //arrange the board
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (x == 0) {
                    if (y == 0 || y == 7) {
                        chessboard.getCells()[x][y].CONTAINS = 8;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("BlackRook.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else if (y == 1 || y == 6) {
                        chessboard.getCells()[x][y].CONTAINS = 1;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("BlackKnight.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else if (y == 2 || y == 5) {
                        chessboard.getCells()[x][y].CONTAINS = 7;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("BlackBishop.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else if (y == 3) {
                        chessboard.getCells()[x][y].CONTAINS = 9;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("BlackQueen.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else {
                        chessboard.getCells()[x][y].CONTAINS = 2;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("BlackKing.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = 1;
                    }
                } else if (x == 1) {
                    chessboard.getCells()[x][y].CONTAINS = 5;
                    chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("BlackPawn.png",55,55)));
                    chessboard.getCells()[x][y].pieceColor = 1;
                } else if (x == 6) {
                    chessboard.getCells()[x][y].CONTAINS = 5;
                    chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("WhitePawn.png",55,55)));
                    chessboard.getCells()[x][y].pieceColor = -1;
                } else if (x == 7) {
                    if (y == 0 || y == 7) {
                        chessboard.getCells()[x][y].CONTAINS = 8;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("WhiteRook.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if (y == 1 || y == 6) {
                        chessboard.getCells()[x][y].CONTAINS = 1;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("WhiteKnight.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if (y == 2 || y == 5) {
                        chessboard.getCells()[x][y].CONTAINS = 7;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("WhiteBishop.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if (y == 3) {
                        chessboard.getCells()[x][y].CONTAINS = 9;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("WhiteQueen.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else {
                        chessboard.getCells()[x][y].CONTAINS = 2;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("WhiteKing.png",55,55)));
                        chessboard.getCells()[x][y].pieceColor = -1;
                    }
                }
            }
        }
    }
}