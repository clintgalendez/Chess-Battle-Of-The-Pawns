// Mechanics of the game

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

    static TurnBasedHandler turnHandler;

    static boolean gameStarted = false;
    static boolean allowedToMove = false;
    static boolean isCheck = false;
    static boolean isSuggesting = false;
    static boolean WKFirstMove = true;
    static boolean BKFirstMove = true;

    static Cells prevChosenCell;

    static int checkedPiece;
    static int[] coordinates = {0, 0, 0, 0};

    public static void main(String[] args) {
        //Create another thread for MainWindow
        Thread GUI = new Thread(new Runnable() {
            @Override
            public void run() {
                chessboard.GUI();
            }
        });

        //Create another thread for Gameplay
        Thread game = new Thread(new Runnable() {
            @Override
            public void run() {
                arrangeBoard();
                play();
            }
        }); 
        GUI.start();
        game.start();
    }

    public static void play() {
        // Flow of the Program 
        player1 = new Players("Player 1", -1); // Create Player 1
        player2 = new Players("Player 2", 1); // Create Player 2
        turnHandler = new TurnBasedHandler(player1, player2, chessboard); // Create Turn Handler
        gameStarted = true;
    }

    public static void buttonClickedHandler(ActionEvent e) {
        Cells chosenCell = (Cells) e.getSource();

        if(!gameStarted) return;

        // Deselect a piece
        if(prevChosenCell == chosenCell) {
            resetAvailCells(chessboard.getCells());
            prevChosenCell = null;
            allowedToMove = false;
            isSuggesting = false;
            return;
        }

        // Move a piece to a chosen suggested cell after selecting a piece to move
        if(allowedToMove) {
            isCheck = false; // Make check status to false after making a move

            /*
             * get the color of the cell that is clicked and check if it is green
             * for green means that it is available to be moved to.
             */
            Color color = chosenCell.getBackground();
            if(color != Color.GREEN) return;

            /*
             * get the icon of the cell that is clicked and check if it is a piece
             * for it means that it will be added to current player's captured board
             */
            if(chosenCell.getIcon() != null) {
                chessboard.addToCapturedBoard(chosenCell, coordinates);
                if(chosenCell.pieceColor == 1) {
                    coordinates[1]++;
                    if(coordinates[1] > 3) {
                        coordinates[0]++;
                        coordinates[1] = 0;
                    } 
                } else if(chosenCell.pieceColor == -1) {
                    coordinates[3]++;
                    if(coordinates[3] > 3) {
                        coordinates[2]++;
                        coordinates[3] = 0;
                    }
                }
            }

            if(prevChosenCell.CONTAINS == 2)
                if(checkedPiece == -1) {
                    WKFirstMove = false;
                } else {
                    BKFirstMove = false;
                }

            // Store chosen cell piece properties for undo purposes
            Cells selectedCell = new Cells(chosenCell.CONTAINS, chosenCell.pieceColor, chosenCell.piece);
            turnHandler.getCurrentPlayer().addMove(selectedCell);

            // Move the clicked piece to the chosen cell
            turnHandler.getCurrentPlayer().addMove(changeCellProperties(chosenCell));

            // Reset the previously clicked cell
            prevChosenCell.CONTAINS = 0;
            prevChosenCell.pieceColor = 0;
            prevChosenCell.setIcon(null);
            prevChosenCell.piece = prevChosenCell.getIcon();
            turnHandler.getCurrentPlayer().addMove(prevChosenCell);

            // Calculate future moves if they result to a check
            isSuggesting = false;
            calculateFutureMove(chessboard.getCells(), isSuggesting);

            turnHandler.nextTurn(); //Change the turn to the next player
            check(); // Check if the move makes a check

            allowedToMove = false;
            resetAvailCells(chessboard.getCells());
            return;
        }

        // Select which piece to move and suggest available cells
        if(turnHandler.getCurrentPlayer().getPlayerColor() == chosenCell.pieceColor) {
            chosenCell.setBackground(Color.YELLOW);
            isSuggesting = true;
            suggestAvailCells(chosenCell, turnHandler.getCurrentPlayer().getPlayerColor(), isSuggesting);
            prevChosenCell = chosenCell;
            allowedToMove = true;
        }
    }

    public static Cells changeCellProperties(Cells selectedMove) {
        if(prevChosenCell.CONTAINS == 5) {
            selectedMove.CONTAINS = 3;
            selectedMove.setIcon(prevChosenCell.getIcon()); //The newly clicked cell will contain the text of the previous cell
        }
        else if(prevChosenCell.CONTAINS == 3) {
            if(selectedMove.posY == 0) {
                selectedMove.CONTAINS = 9;
                selectedMove.setIcon(new ImageIcon(chessboard.createImage("images/WhiteQueen.png",55,55)));
            } else if(selectedMove.posY == 7) {
                selectedMove.CONTAINS = 9;
                selectedMove.setIcon(new ImageIcon(chessboard.createImage("images/BlackQueen.png",55,55)));
            } else {
                selectedMove.CONTAINS = prevChosenCell.CONTAINS; 
                selectedMove.setIcon(prevChosenCell.getIcon()); //The newly clicked cell will contain the text of the previous cell
            }   
        } else { 
            selectedMove.CONTAINS = prevChosenCell.CONTAINS; //The newly clicked cell will contain the color of the previous cell
            selectedMove.setIcon(prevChosenCell.getIcon()); //The newly clicked cell will contain the text of the previous cell
        }
        selectedMove.pieceColor = prevChosenCell.pieceColor;
        selectedMove.piece = selectedMove.getIcon();

        return selectedMove;
    }

    public static int specialPieceHandler(Cells chosenCell) {
        int enemyPresent = 0;

        //check surrounding of the cell
        if (chosenCell.posX - 1 >= 0 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (chessboard.getCells()[chosenCell.posX - 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
        }

        if (chosenCell.posX + 1 < 8 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (chessboard.getCells()[chosenCell.posX + 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
            
        }

        if (enemyPresent > 0) return 4;
        
        return chosenCell.CONTAINS;
    }

    public static Cells calculateAvailMove(Cells chosenCell, int currentColorPiece, int i, int[][]moves) {
        int x = chosenCell.posX + moves[i][0];
        int y = chosenCell.posY + (currentColorPiece * moves[i][1]);
        
        if(x >= 0 && x < 8 && y >= 0 && y < 8 && chessboard.getCells()[x][y].pieceColor != currentColorPiece)
            return chessboard.getCells()[x][y];

        return null;
    }

    public static void suggestAvailCells(Cells chosenCell, int currentColorPiece, boolean isSuggesting) {
        int piece = chosenCell.CONTAINS;
        Cells futureCells;

        if(currentColorPiece == 0) return;

        int[][] moves = MoveSets.getAvailableMoves(piece);
        for (int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
            futureCells = calculateAvailMove(chosenCell, currentColorPiece, i, moves);
            if(futureCells == null) {
                if(chosenCell.CONTAINS == 5) break;
                else continue;
            }

            if(futureCells.CONTAINS != 0) {
                if(chosenCell.CONTAINS == 3 || chosenCell.CONTAINS == 5) break;
            }

            if(!isSuggesting) {
                if(futureCells.CONTAINS == 2) {
                    checkedPiece = futureCells.pieceColor;
                    isCheck = true;
                    break;
                }
            } else futureCells.setBackground(Color.GREEN);

            if(futureCells.CONTAINS != 0) continue;

            if(chosenCell.CONTAINS == 7 || chosenCell.CONTAINS == 8 || chosenCell.CONTAINS == 9) {
                while (true) {
                    futureCells = calculateAvailMove(futureCells, currentColorPiece, i, moves);
                    if(futureCells == null) break;

                    if(!isSuggesting) {
                        if(futureCells.CONTAINS == 2) {
                            checkedPiece = futureCells.pieceColor;
                            isCheck = true;
                            break;
                        }
                    } else futureCells.setBackground(Color.GREEN);
                    
                    if(futureCells.CONTAINS != 0) break;
                }
            } 
        }

        if(chosenCell.CONTAINS == 5 || chosenCell.CONTAINS == 3) {
            piece = specialPieceHandler(chosenCell);
            moves = MoveSets.getAvailableMoves(piece);
            for(int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
                futureCells = calculateAvailMove(chosenCell, currentColorPiece, i, moves);
                if(futureCells == null) {
                    continue;
                }
    
                if(!isSuggesting) {
                    if(futureCells.CONTAINS == 2) {
                        checkedPiece = futureCells.pieceColor;
                        isCheck = true;
                        break;
                    }
                } else {
                    if(futureCells.CONTAINS != 0) {
                        if(futureCells.posX == chosenCell.posX + 0) continue;
                        futureCells.setBackground(Color.GREEN);
                    }
                }
            }
        }
    }

    public static void calculateFutureMove(Cells[][] board, boolean isSuggesting) {
        for(Cells[] cells : board) {
            for(Cells cell : cells ) {
                if(cell.CONTAINS == 0) continue;
                suggestAvailCells(cell, cell.pieceColor, isSuggesting);
            }
        }
    }

    public static void resetAvailCells(Cells[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(((j % 2 == 1) && (i % 2 == 1))
                        || ((j % 2 == 0) && (i % 2 == 0))) {
                    board[j][i].setBackground(new Color(224, 190, 145));
                } else {
                    board[j][i].setBackground(new Color(47, 38, 29));
                }
            }
        }
    }

    public static void check() {
        if(!isCheck) {
            chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.GREEN);
            chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(new Color(214, 188, 153));
        } else {
            chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.YELLOW);
            chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(new Color(214, 188, 153));

            if(turnHandler.getNextPlayer().getPlayerColor() == checkedPiece) {
                undo();
            }

            if(checkedPiece == -1) {
                WKFirstMove = false;
            } else {
                BKFirstMove = false;
            }
        }
    }

    public static void checkMate() {
        if(!WKFirstMove) {
            
        }
    }

    public static void undo() {
        if(turnHandler.getCurrentPlayer().getMove().isEmpty() && turnHandler.getNextPlayer().getMove().isEmpty())
            return;
        
        isCheck = false;

        resetAvailCells(chessboard.getCells());
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

        
        if(currentCell.getIcon() != null) {
            int y, x;
            if(turnHandler.getCurrentPlayer().getPlayerColor() == -1) {
                coordinates[1]--;
                if(coordinates[1] < 0) {
                    coordinates[0]--;
                    coordinates[1] = 3;
                }
                y = coordinates[0];
                x = coordinates[1];
                
            } else {
                coordinates[3]--;
                if(coordinates[3] < 0) {
                    coordinates[2]--;
                    coordinates[3] = 3;
                } 
                y = coordinates[2];
                x = coordinates[3];
            }

            if(chessboard.getCapturedBoard(turnHandler.getCurrentPlayer())[y][x].getIcon() != null)
                chessboard.removeFromCapturedBoard(turnHandler.getNextPlayer(), y, x);
        } 
        
        isSuggesting = false;
        calculateFutureMove(chessboard.getCells(), isSuggesting);
        check();
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
                    } else if(x == 2 || x == 5) {
                        chessboard.getCells()[x][y].CONTAINS = 7;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackBishop.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = 1;
                    } else if(x == 3) {
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
                } else if(y == 1) {
                    chessboard.getCells()[x][y].CONTAINS = 5;
                    chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/BlackPawn.png",55,55)));
                    chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                    chessboard.getCells()[x][y].pieceColor = 1;
                } else if(y == 6) {
                    chessboard.getCells()[x][y].CONTAINS = 5;
                    chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhitePawn.png",55,55)));
                    chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                    chessboard.getCells()[x][y].pieceColor = -1;
                } else if(y == 7) {
                    if(x == 0 || x == 7) {
                        chessboard.getCells()[x][y].CONTAINS = 8;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteRook.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if(x == 1 || x == 6) {
                        chessboard.getCells()[x][y].CONTAINS = 1;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteKnight.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if(x == 2 || x == 5) {
                        chessboard.getCells()[x][y].CONTAINS = 7;
                        chessboard.getCells()[x][y].setIcon(new ImageIcon(chessboard.createImage("images/WhiteBishop.png",55,55)));
                        chessboard.getCells()[x][y].piece = chessboard.getCells()[x][y].getIcon();
                        chessboard.getCells()[x][y].pieceColor = -1;
                    } else if(x == 3) {
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