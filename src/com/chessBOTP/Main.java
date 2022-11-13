// Mechanics of the game

package com.chessBOTP;

import com.gui.Chessboard;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class Main {
    private ArrayList<Cells> moveList = new ArrayList<>();

    private boolean gameStarted = false;
    private boolean allowedToMove = false;
    private boolean isCheck = false;
    private boolean isSuggesting = false;
    private boolean onAuto = false;

    private int checkedPiece;
    private int[] coordinates = {0, 0, 0, 0};

    private Cells prevChosenCell;

    private Players player1;
    private Players player2;

    private TurnBasedHandler turnHandler;

    private Chessboard chessboard;
    
    public Main() {
        Action action = new AbstractAction("Undo") {
            private static final long serialVersionUID = 1L;
    
                @Override
                public void actionPerformed(ActionEvent e) {
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
    
                
                isSuggesting = false;
                calculateFutureMove(); // Calculate future moves if they result to a check
    
                turnHandler.nextTurn(); // Change the turn to the next player
                isCheck(); // Check if the move makes a check
    
                if(isCheck == true) isCheckmate(); // If a king is checked, check for a checkmate
    
                allowedToMove = false;
                resetAvailCells(chessboard.getCells());
                return;
            }
    
            // Select which piece to move and suggest available cells
            if(turnHandler.getCurrentPlayer().getPlayerColor() == chosenCell.pieceColor) {
                chosenCell.setBackground(Color.YELLOW);
                isSuggesting = true;
                suggestAvailCells(chosenCell, turnHandler.getCurrentPlayer().getPlayerColor());
                prevChosenCell = chosenCell;
                allowedToMove = true;
            }
                }
        };
        
        chessboard = new Chessboard(action, this);

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

    public static void main(String[] args) {
        new Main();
    }

    public void play() {
        // Flow of the Program 
        player1 = new Players("Player 1", -1); // Create Player 1
        player2 = new Players("Player 2", 1); // Create Player 2
        turnHandler = new TurnBasedHandler(player1, player2, chessboard); // Create Turn Handler
        gameStarted = true;
    }

    public Cells changeCellProperties(Cells selectedMove) {
        if(prevChosenCell.CONTAINS == 5) { // If a selected piece to move is a pawn at start having two moves forward
            selectedMove.CONTAINS = 3; // Then change it to pawn at play having one move forward only
            selectedMove.setIcon(prevChosenCell.getIcon());
        }
        else if(prevChosenCell.CONTAINS == 3) {
            if(selectedMove.posY == 0) { // If a white pawn at play reaches the black's base
                selectedMove.CONTAINS = 9; // Then it becomes a white queen
                selectedMove.setIcon(new ImageIcon(chessboard.createImage("images/WhiteQueen.png",55,55)));
            } else if(selectedMove.posY == 7) { // If a black pawn at play reaches the white's base
                selectedMove.CONTAINS = 9; // Then it becomes a black queen
                selectedMove.setIcon(new ImageIcon(chessboard.createImage("images/BlackQueen.png",55,55)));
            } else { // If any pawn moves anywhere on the middle part of the board
                selectedMove.CONTAINS = prevChosenCell.CONTAINS;  // Then they are as they are
                selectedMove.setIcon(prevChosenCell.getIcon()); // The newly clicked cell will contain the text of the previous cell
            }   
        } else { 
            selectedMove.CONTAINS = prevChosenCell.CONTAINS; // The newly clicked cell will contain the piece of the previous cell
            selectedMove.setIcon(prevChosenCell.getIcon()); // The newly clicked cell will have the icon piece of the previous cell
        }
        selectedMove.pieceColor = prevChosenCell.pieceColor; // The newly clicked cell will contain the piece color of the previous cell
        selectedMove.piece = selectedMove.getIcon(); // // The newly clicked cell will contain the icon piece of the previous cell

        return selectedMove;
    }

    public int pawnAttack(Cells chosenCell) {
        int enemyPresent = 0;

        // Check surrounding of the cell for an enemy
        if (chosenCell.posX - 1 >= 0 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (chessboard.getCells()[chosenCell.posX - 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
        }
        if (chosenCell.posX + 1 < 8 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (chessboard.getCells()[chosenCell.posX + 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
            
        }

        // Return pawn attack mode if enemy is present
        if (enemyPresent > 0) return 4;
        
        return chosenCell.CONTAINS;
    }

    private Cells calculateAvailMove(Cells chosenCell, int currentColorPiece, int i, int[][]moves) {
        int x = chosenCell.posX + moves[i][0];
        int y = chosenCell.posY + (currentColorPiece * moves[i][1]);
        
        if(x >= 0 && x < 8 && y >= 0 && y < 8 && chessboard.getCells()[x][y].pieceColor != currentColorPiece)
            return chessboard.getCells()[x][y]; // return the cell at x-y position if a calculated available move does not contain same piece color

        return null; // else, return null
    }

    public void suggestAvailCells(Cells chosenCell, int currentColorPiece) {
        int piece = chosenCell.CONTAINS; // Get what piece the chosen cell is
        Cells futureCells;

        if(currentColorPiece == 0) return; 

        // Suggest moves from the pieces' movesets
        int[][] moves = MoveSets.getAvailableMoves(piece); // Get the moveset of the chosen piece

        // Loop through the moveset of the chosen piece
        for (int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
            futureCells = calculateAvailMove(chosenCell, currentColorPiece, i, moves); // Get the possible move from a moveset
            if(futureCells == null) { // If a suggested move contains a piece of same color
                if(chosenCell.CONTAINS == 5) break; // If a selected piece is a pawn at start, then it will stop suggesting moves
                else continue; // else if a selected piece is any piece aside from a pawn and its suggested move is blocked by a piece of same color, then proceed to other suggestions
            }

            if(futureCells.CONTAINS != 0 && (chosenCell.CONTAINS == 3 || chosenCell.CONTAINS == 5)) { // If a selected piece is a pawn at start and its first suggested move is blocked by a piece of different color
                break; // Stop suggesting moves
            }

            if(onAuto) { // If a move is calculated only for checkmate purposes
                moveList.add(futureCells);
            }

            if(!isSuggesting) { // If a move is calculated only for checking purposes
                if(futureCells.CONTAINS == 2) { // And if a future move contains a king
                    checkedPiece = futureCells.pieceColor; // Get the piece color of the checked king
                    isCheck = true; // Make check status to true
                    break; // Stop calculation
                }
            } else futureCells.setBackground(Color.GREEN); // else if a move of any piece is calculated for suggesting, then set a cell to green

            if(futureCells.CONTAINS != 0) continue; // If a selected piece is any piece aside from a pawn and its suggested move that turned green has a piece of the enemy, then proceed to other suggestions

            // If a selected piece is a bishop, a rook, or a queen
            if(chosenCell.CONTAINS == 7 || chosenCell.CONTAINS == 8 || chosenCell.CONTAINS == 9) {
                // Then iteratively suggest available moves till the end of the sides of the board
                while (true) {
                    futureCells = calculateAvailMove(futureCells, currentColorPiece, i, moves);
                    if(futureCells == null) break;

                    if(onAuto) {
                        moveList.add(futureCells);
                    }

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

        // After suggesting moves from the pieces' movesets and if a selected piece is a pawn at start or at play
        if(chosenCell.CONTAINS == 5 || chosenCell.CONTAINS == 3) {
            piece = pawnAttack(chosenCell); // Check if there are enemy to capture. If there are, then pawn temporarily becomes pawn at attack
            
            if(piece != 4) return; // If there are no enemy to capture, then stop method

            // Suggest moves from the piece's moveset
            moves = MoveSets.getAvailableMoves(piece);
            for(int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
                futureCells = calculateAvailMove(chosenCell, currentColorPiece, i, moves);
                if(futureCells == null) { 
                    continue; // If a suggested move is blocked by a piece of same color, then proceed to other suggestions
                }
    
                if(onAuto) {
                    moveList.add(futureCells);
                }

                if(!isSuggesting) {
                    if(futureCells.CONTAINS == 2) {
                        checkedPiece = futureCells.pieceColor;
                        isCheck = true;
                        break;
                    }
                } else {
                    if(futureCells.CONTAINS != 0) {
                        futureCells.setBackground(Color.GREEN); // If a suggested move has an enemy, then set a cell to green
                    }
                }
            }
        }
    }

    // Calculate future moves for next turn and check if a move checks a king
    public void calculateFutureMove() {
        Cells[][] board = chessboard.getCells();
        for(Cells[] cells : board) {
            for(Cells cell : cells ) {
                if(cell.CONTAINS == 0) continue;
                suggestAvailCells(cell, cell.pieceColor);
            }
        }
    }

    // Repaint the board to default
    public void resetAvailCells(Cells[][] board) {
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

    public void isCheck() {
        if(!isCheck) { // If a king is not checked
            chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.GREEN);
            chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(new Color(214, 188, 153));
        } else { // else if a king is checked
            chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.YELLOW);
            chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(new Color(214, 188, 153));

            // If a king is checked and another move made makes a king still checked
            if(turnHandler.getNextPlayer().getPlayerColor() == checkedPiece) {
                undo(); // Undo, as if a move is not made
            }
        }
    }

    public void isCheckmate() {
        boolean stalemated = checkForCheckmate();
        if(stalemated == true) {
            for(Cells[] cells : chessboard.getCells()) {
                for(Cells cell : cells) 
                    cell.setEnabled(false);
                    chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.RED);
                    chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(Color.GREEN);
            }
        }
    }

    private boolean checkForCheckmate() {
        for(Cells[] cells : chessboard.getCells()) {
            for(Cells cell : cells) {
                if(cell.pieceColor != checkedPiece) continue;
                System.out.println(cell.CONTAINS);

                moveList.clear();
                onAuto = true;
                suggestAvailCells(cell, cell.pieceColor);

                if(moveList.isEmpty()) continue;

                if(doMoves(cell) == false) {
                    isCheck = true;
                    return false;
                }
            }
        }

        return true;
    }

    private boolean doMoves(Cells cell) {
        Cells futureCells;

        for (int i = 0; i < moveList.size(); i++) {
            futureCells = moveList.get(i);

            System.out.print("{" + futureCells.posX + ", " + futureCells.posY + "}" + "\n");

            // Store chosen cell piece properties for undo purposes
            Cells selectedCell = new Cells(futureCells.CONTAINS, futureCells.pieceColor, futureCells.piece);
            turnHandler.getCurrentPlayer().addMove(selectedCell);
            prevChosenCell = cell;
            
            // Move the clicked piece to the chosen cell
            turnHandler.getCurrentPlayer().addMove(changeCellProperties(futureCells));

            // Reset the previously clicked cell
            prevChosenCell.CONTAINS = 0;
            prevChosenCell.pieceColor = 0;
            prevChosenCell.setIcon(null);
            prevChosenCell.piece = prevChosenCell.getIcon();
            turnHandler.getCurrentPlayer().addMove(prevChosenCell);

            onAuto = false;
            isCheck = false;
            // Calculate future moves if they result to a check
            isSuggesting = false;
            calculateFutureMove();

            turnHandler.nextTurn(); // Change the turn to the next player
            isCheck(); // Check if the move makes a check

            if(isCheck == false) {
                undo(); 
                return false;
            }
        }
        return true;
    }

    // Revert move to previous placements
    public Icon undo() {
        if(turnHandler.getCurrentPlayer().getMove().isEmpty() && turnHandler.getNextPlayer().getMove().isEmpty())
            return null;
        
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

        if(prevCell.CONTAINS == 3 && (prevCell.posY == 1 || prevCell.posY == 6)) // If a pawn at play is undone and is at its starting point
            prevCell.CONTAINS = 5; // Make it into a pawn at start

        isSuggesting = false;
        calculateFutureMove();
        isCheck();

        return currentCell.getIcon();
    }

    public void undoCapturedBoard(Icon icon) {
        if(icon != null) {
            int y, x;
            if(turnHandler.getCurrentPlayer().getPlayerColor() == -1) {
                coordinates[1]--;
                if(coordinates[1] < 0) {
                    coordinates[0]--;
                    coordinates[1] = 3;
                }
                x = coordinates[0];
                y = coordinates[1];
                
            } else {
                coordinates[3]--;
                if(coordinates[3] < 0) {
                    coordinates[2]--;
                    coordinates[3] = 3;
                } 
                x = coordinates[2];
                y = coordinates[3];
            }

            if(chessboard.getCapturedBoard(turnHandler.getCurrentPlayer())[y][x].getIcon() != null)
                chessboard.removeFromCapturedBoard(turnHandler.getNextPlayer(), y, x);
        } 
    }

    private void arrangeBoard() {
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