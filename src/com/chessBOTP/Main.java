package com.chessBOTP;

import com.gui.Chessboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Stack;

import javax.swing.ImageIcon;

public class Main {
    static Chessboard chessboard = new Chessboard();
    static Players player1;
    static Players player2;
    static boolean gameStarted = false;
    static TurnBasedHandler turnHandler;
    static boolean allowedToMove = false;
    static Cells prevChosenCell;
    static int i = 0, j = 0, m = 0, n = 0;

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
        turnHandler = new TurnBasedHandler(player1, player2, chessboard); //Create Turn Handler
        gameStarted = true;
    }

    public static void buttonClickedHandler(ActionEvent e) {
        Cells chosenCell = (Cells) e.getSource();

        if (!gameStarted) {
            return;
        }

        if (prevChosenCell == chosenCell) {
            resetColAvailCells(chessboard.getCells());
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

            if (chosenCell.getIcon() != null) {
                chessboard.addToCapturedBoard(chosenCell, i, j, m , n);
                if (chosenCell.pieceColor == 1) {
                    j++;
                    if (j > 3) {
                        i++;
                        j = 0;
                    } 
                } else if(chosenCell.pieceColor == -1) {
                    n++;
                    if (n > 3) {
                        m++;
                        n = 0;
                    }
                }
            }

            Cells cell = new Cells(chosenCell.CONTAINS, chosenCell.pieceColor, chosenCell.piece);
            turnHandler.getCurrentPlayer().addMove(cell);

            //**Move the Piece**//
            turnHandler.getCurrentPlayer().addMove(changeCellProperties(chosenCell)); //Add the move to the current player's moves

            //**Reset The previous Cell**//
            prevChosenCell.CONTAINS = 0;
            prevChosenCell.pieceColor = 0;
            prevChosenCell.setIcon(null);
            prevChosenCell.piece = prevChosenCell.getIcon();
            turnHandler.getCurrentPlayer().addMove(prevChosenCell);
            turnHandler.nextTurn(); //Change the turn to the next player

            allowedToMove = false;
            resetColAvailCells(chessboard.getCells());
            return;
        }

        if (turnHandler.getCurrentPlayer().getPlayerColor() == chosenCell.pieceColor) {
            chosenCell.setBackground(Color.YELLOW);
            setColAvailCells(chosenCell, turnHandler.getCurrentPlayer().getPlayerColor());
        }
    }

    public static Cells changeCellProperties(Cells cell) {
        if (prevChosenCell.CONTAINS == 5) {
            cell.CONTAINS = 3;
        } else {
            cell.CONTAINS = prevChosenCell.CONTAINS; //The newly clicked cell will contain the color of the previous cell
        }
        cell.pieceColor = prevChosenCell.pieceColor; //The previous cell will now move to the new cell
        cell.setIcon(prevChosenCell.getIcon()); //The newly clicked cell will contain the text of the previous cell
        cell.piece = cell.getIcon();

        return cell;
    }

    public static Cells calculateFutureMove(Cells chosenCell, int currentColorPiece, int i, int[][]moves) {
        int x = chosenCell.posX + moves[i][0];
        int y = chosenCell.posY + (currentColorPiece * moves[i][1]);
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
            piece = specialPieceHandler(chosenCell);
            for (int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
                int[][] moves = MoveSets.getAvailableMoves(piece);

                futureCells = calculateFutureMove(chosenCell, currentColorPiece, i, moves);
                if (futureCells == null) {
                    continue;
                }

                if (piece == 5 && futureCells.CONTAINS != 0) {
                    break;
                }

                if (piece == 4 && futureCells.CONTAINS == 0) {
                    continue;
                }

                futureCells.setBackground(Color.GREEN);
            }
        }

        prevChosenCell = chosenCell;
        allowedToMove = true;
    }

    public static int specialPieceHandler(Cells chosenCell) {
        int enemyPresent = 0;
        if (chosenCell.CONTAINS == 3 || chosenCell.CONTAINS == 5) {
            //check surrounding of the cell
            if (chosenCell.posX - 1 >= 0 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
                if (chessboard.getCells()[chosenCell.posX - 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor) {
                    enemyPresent++;
                }
            }

            if (chosenCell.posX + 1 < 8 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
                if (chessboard.getCells()[chosenCell.posX + 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor) {
                    enemyPresent++;
                }
            }
        }

        if (enemyPresent > 0) {
            return 4;
        }

        return chosenCell.CONTAINS;
    }

    public static void resetColAvailCells(Cells[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (((j % 2 == 1) && (i % 2 == 1))
                        || ((j % 2 == 0) && (i % 2 == 0))) {
                    board[i][j].setBackground(new Color(224, 190, 145));
                } else {
                    board[i][j].setBackground(new Color(47, 38, 29));
                }
            }
        }
    }

    public static void undo() {
        if (turnHandler.getCurrentPlayer().getMove().isEmpty() && turnHandler.getNextPlayer().getMove().isEmpty())
            return;

        resetColAvailCells(chessboard.getCells());
        prevChosenCell = null;
        allowedToMove = false;

        turnHandler.nextTurn();
        Stack<Cells> prevMoves = turnHandler.getCurrentPlayer().getMove();

        Cells prevCell = prevMoves.pop();
        Cells currentCell = prevMoves.pop();
        Cells chosenCell = prevMoves.pop();

        prevCell.CONTAINS = currentCell.CONTAINS;
        prevCell.setIcon(currentCell.piece);
        prevCell.piece = currentCell.piece;
        prevCell.pieceColor = currentCell.pieceColor;

        currentCell.CONTAINS = chosenCell.CONTAINS;
        currentCell.setIcon(chosenCell.piece);
        currentCell.piece = chosenCell.piece;
        currentCell.pieceColor = chosenCell.pieceColor;

        if (currentCell.getIcon() != null) {
            int y, x;
            if(turnHandler.getCurrentPlayer().getPlayerColor() == -1) {
                j--;
                if (j < 0) {
                    i--;
                    j = 3;
                }
                y = i;
                x = j;
                
            } else {
                n--;
                if (n < 0) {
                    m--;
                    n = 3;
                } 
                y = m;
                x = n;
            }

            if(chessboard.getCapturedBoard(turnHandler.getCurrentPlayer())[y][x].getIcon() != null)
                chessboard.removeFromCapturedBoard(turnHandler.getNextPlayer(), y, x);
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
                        chessboard.getCells()[x][y].CONTAINS = 8;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackRook.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else if (x == 1 || x == 6) {
                        chessboard.getCells()[x][y].CONTAINS = 1;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackKnight.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else if (x == 2 || x == 5) {
                        chessboard.getCells()[x][y].CONTAINS = 7;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackBishop.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else if (x == 3) {
                        chessboard.getCells()[x][y].CONTAINS = 9;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackQueen.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else {
                        chessboard.getCells()[x][y].CONTAINS = 2;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackKing.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = 1;
                    }
                } else if (y == 1) {
                    chessboard.getCells()[x][y].CONTAINS = 5;
                    chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackPawn.png",55,55)));
                    chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                    chessboard.getCells()[x][y].pieceColor = 1;
                } else if (y == 6) {
                    chessboard.getCells()[x][y].CONTAINS = 5;
                    chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhitePawn.png",55,55)));
                    chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                    chessboard.getCells()[x][y].pieceColor = -1;
                } else if (y == 7) {
                    if (x == 0 || x == 7) {
                        chessboard.getCells()[x][y].CONTAINS = 8;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteRook.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if (x == 1 || x == 6) {
                        chessboard.getCells()[x][y].CONTAINS = 1;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteKnight.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if (x == 2 || x == 5) {
                        chessboard.getCells()[x][y].CONTAINS = 7;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteBishop.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if (x == 3) {
                        chessboard.getCells()[x][y].CONTAINS = 9;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteQueen.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else {
                        chessboard.getCells()[x][y].CONTAINS = 2;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteKing.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    }
                }
            }
        }
    }
}