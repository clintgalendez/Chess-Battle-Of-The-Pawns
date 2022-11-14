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
    private boolean isSuggesting = false;
    private boolean onAuto = false;

    private int[] coordinates = {0, 0, 0, 0};
    private int checkedPiece;

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
                    turnHandler.getCurrentPlayer().setCheck(false); // Change check status of current player to false after making a move
        
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
        
                    // Move the clicked piece to the chosen cell and store its properties for undo purposes
                    turnHandler.getCurrentPlayer().addMove(changeCellProperties(chosenCell));
        
                    // Store previously chosen cell piece properties for undo purposes
                    Cells prevSelectedCell = new Cells(prevChosenCell.CONTAINS, prevChosenCell.pieceColor, prevChosenCell.piece);
                    turnHandler.getCurrentPlayer().addMove(prevSelectedCell);

                    // Reset the previously clicked cell and store its properties for undo purposes
                    prevChosenCell.CONTAINS = 0;
                    prevChosenCell.pieceColor = 0;
                    prevChosenCell.setIcon(null);
                    prevChosenCell.piece = prevChosenCell.getIcon();
                    turnHandler.getCurrentPlayer().addMove(prevChosenCell);
        
                    isSuggesting = false;
                    calculateFutureMove(); // Calculate future moves after making a move if they result to a check
        
                    turnHandler.nextTurn(); // Change the turn to the next player
                    isCheck(turnHandler.getCurrentPlayer()); // After the turn, check if the current player is checked
                    if(turnHandler.getCurrentPlayer().isCheck()) isCheckmate(chessboard.getCells()); // If the current player is checked, check for a checkmate
        
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
        } else { // For any piece aside from pawns
            selectedMove.CONTAINS = prevChosenCell.CONTAINS; // The newly clicked cell will contain the piece of the previous cell
            selectedMove.setIcon(prevChosenCell.getIcon()); // The newly clicked cell will have the icon piece of the previous cell
        }
        selectedMove.pieceColor = prevChosenCell.pieceColor; // The newly clicked cell will contain the piece color of the previous cell
        selectedMove.piece = selectedMove.getIcon(); // // The newly clicked cell will contain the icon piece of the previous cell

        return selectedMove;
    }

    public int pawnAttack(Cells chosenCell) {
        int enemyPresent = 0;

        // Check surrounding of the cell for the next player's pieces
        if (chosenCell.posX - 1 >= 0 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (chessboard.getCells()[chosenCell.posX - 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
        }
        if (chosenCell.posX + 1 < 8 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (chessboard.getCells()[chosenCell.posX + 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
            
        }

        // Return pawn attack mode if next player's pieces are present
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

            if(onAuto) { // (FOR CHECKMATE PURPOSES) Add move to movelist after it is calculated
                moveList.add(futureCells);
            }

            if(!isSuggesting) { // (FOR CHECKING PURPOSES)
                if(futureCells.CONTAINS == 2) { // And if a future move contains a king
                    checkedPiece = futureCells.pieceColor; // Get the piece color of the checked king
                    if(checkedPiece != turnHandler.getCurrentPlayer().getPlayerColor()) { // If checked piece is of the next player
                        turnHandler.getNextPlayer().setCheck(true); // Change check status of next player to true
                    } else { // else if the checked piece is of the current player
                        turnHandler.getCurrentPlayer().setCheck(true); // Change check status of current player to true
                    }
                    break;
                }
            } else futureCells.setBackground(Color.GREEN); // (FOR MAKING MOVE PURPOSES) set a cell to green

            if(futureCells.CONTAINS != 0) continue; // If a selected piece is any piece aside from a pawn and its suggested move that turned green has a piece of the next player, then proceed to other suggestions

            // If a selected piece is a bishop, a rook, or a queen
            if(chosenCell.CONTAINS == 7 || chosenCell.CONTAINS == 8 || chosenCell.CONTAINS == 9) {
                // Then iteratively suggest available moves till the end of the sides of the board
                while (true) {
                    futureCells = calculateAvailMove(futureCells, currentColorPiece, i, moves);
                    if(futureCells == null) break;

                    if(onAuto)  {
                        moveList.add(futureCells);
                    }

                    if(!isSuggesting) {
                        if(futureCells.CONTAINS == 2) {
                            checkedPiece = futureCells.pieceColor;
                            if(checkedPiece != turnHandler.getCurrentPlayer().getPlayerColor()) {
                                turnHandler.getNextPlayer().setCheck(true);
                            } else {
                                turnHandler.getCurrentPlayer().setCheck(true);
                            }
                            break;
                        }
                    } else futureCells.setBackground(Color.GREEN);
                    
                    if(futureCells.CONTAINS != 0) break;
                }
            } 
        }

        // After suggesting moves from the pieces' movesets and if a selected piece is a pawn at start or at play
        if(chosenCell.CONTAINS == 5 || chosenCell.CONTAINS == 3) {
            piece = pawnAttack(chosenCell); // Check if there are pieces of next player to capture. If there are, then pawn temporarily becomes pawn at attack
            
            if(piece != 4) return; // If there are no pieces of next player to capture, then stop method

            moves = MoveSets.getAvailableMoves(piece);
            for(int i = 0; i < MoveSets.getAvailableMoves(piece).length; i++) {
                futureCells = calculateAvailMove(chosenCell, currentColorPiece, i, moves);
                if(futureCells == null) { 
                    continue;
                }
    
                if(onAuto)  {
                    moveList.add(futureCells);
                }

                if(!isSuggesting) {
                    if(futureCells.CONTAINS == 2) { 
                        checkedPiece = futureCells.pieceColor; 
                        if(checkedPiece != turnHandler.getCurrentPlayer().getPlayerColor()) {
                            turnHandler.getNextPlayer().setCheck(true);
                        } else {
                            turnHandler.getCurrentPlayer().setCheck(true);
                        }
                        break;
                    }
                } else {
                    if(futureCells.CONTAINS != 0) {
                        futureCells.setBackground(Color.GREEN); // If a suggested move has a piece of the next player, then set a cell to green
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

    // Make changes in GUI if a player is checked
    public void isCheck(Players player) {
        if(player.isCheck() && turnHandler.getNextPlayer().isCheck()) { // (FOR CHECKMATE PURPOSES) If both player becomes checked
            Icon icon = undo();
            undoCapturedBoard(icon);
            return;
        }

        if(player.isCheck()) { // If current player is checked
            chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.YELLOW); // change own name panel to yellow
            chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(new Color(214, 188, 153)); // change next player's panel to default
        } else if(turnHandler.getNextPlayer().isCheck()) { // If the previous player is still checked
            if(turnHandler.getNextPlayer().getPlayerColor() == checkedPiece) { // If the checked piece is of the previous player
                Icon icon = undo(); // Undo, as if a move is not made
                undoCapturedBoard(icon);
                
                return;
            }
        }

        if(!player.isCheck()) { // If current player is not checked
            chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.GREEN); // Change own name panel to green
            chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(new Color(214, 188, 153)); // change next player's panel to default
        }
    }

    public void isCheckmate(Cells[][] board) {
        boolean stalemated = checkForCheckmate();
        if(stalemated == true) { // If the next player is checkmated
            // Disable all cells
            for(Cells[] cells : board) {
                for(Cells cell : cells) {
                    cell.setEnabled(false);
                    cell.setDisabledIcon(cell.getIcon());
                }
            }

            // Change the color of the name panel of the loser to red, while green for the winner
            chessboard.getNamePanel(turnHandler.getCurrentPlayer()).setBackground(Color.RED);
            chessboard.getNamePanel(turnHandler.getNextPlayer()).setBackground(Color.GREEN);
        }
    }

    private boolean checkForCheckmate() {
        for(Cells[] cells : chessboard.getCells()) {
            for(Cells cell : cells) {
                if(cell.pieceColor != turnHandler.getCurrentPlayer().getPlayerColor()) continue; // Check only the pieces of current player

                moveList.clear(); // clear the movelist
                onAuto = true; // Set the purpose to checkmate purposes
                suggestAvailCells(cell, cell.pieceColor); // Store suggested moves
                onAuto = false; // Disable purpose

                if(moveList.isEmpty()) continue; // If a piece has no move, then proceed to other pieces

                if(doMoves(cell) == false) { // If current player has more moves to disable check status
                    turnHandler.getCurrentPlayer().setCheck(true); // Set current player's check status to true
                    return false; // Return that the player is not checkmated
                }
            }
        }

        return true; // Return that the player is checkmated
    }

    // Programatically find possible move to disable check status
    private boolean doMoves(Cells cell) {
        Cells futureCells;

        for (int i = 0; i < moveList.size(); i++) {
            prevChosenCell = cell;
            futureCells = moveList.get(i);

            System.out.print("{" + futureCells.posX + ", " + futureCells.posY + "}" + "\n");

            if(futureCells.getIcon() != null) {
                System.out.println(coordinates[0] + " " + coordinates[1] + " " +
                                   coordinates[2] + " " + coordinates[3]);
                chessboard.addToCapturedBoard(futureCells, coordinates);
                if(futureCells.pieceColor == 1) {
                    coordinates[1]++;
                    if(coordinates[1] > 3) {
                        coordinates[0]++;
                        coordinates[1] = 0;
                    } 
                } else if(futureCells.pieceColor == -1) {
                    coordinates[3]++;
                    if(coordinates[3] > 3) {
                        coordinates[2]++;
                        coordinates[3] = 0;
                    }
                }
            }

            // Store chosen cell piece properties for undo purposes
            Cells selectedCell = new Cells(futureCells.CONTAINS, futureCells.pieceColor, futureCells.piece);
            turnHandler.getCurrentPlayer().addMove(selectedCell);
            
            // Move the clicked piece to the chosen cell
            turnHandler.getCurrentPlayer().addMove(changeCellProperties(futureCells));

            Cells prevSelectedCell = new Cells(prevChosenCell.CONTAINS, prevChosenCell.pieceColor, prevChosenCell.piece);
            turnHandler.getCurrentPlayer().addMove(prevSelectedCell);

            // Reset the previously clicked cell
            prevChosenCell.CONTAINS = 0;
            prevChosenCell.pieceColor = 0;
            prevChosenCell.setIcon(null);
            prevChosenCell.piece = prevChosenCell.getIcon();
            turnHandler.getCurrentPlayer().addMove(prevChosenCell);

            turnHandler.getNextPlayer().setCheck(false);
            turnHandler.getCurrentPlayer().setCheck(false);
            
            // Calculate future moves if they result to a check
            isSuggesting = false;
            calculateFutureMove();
            System.out.println(turnHandler.getCurrentPlayer().isCheck());
            turnHandler.nextTurn();
            isCheck(turnHandler.getCurrentPlayer()); // Check if the move makes a check

            if(!turnHandler.getCurrentPlayer().isCheck()) {
                System.out.println(turnHandler.getNextPlayer().isCheck());
                Icon icon = undo(); 
                undoCapturedBoard(icon);
                return false;
            }
        }
        return true;
    }

    // Revert move to previous placements
    public Icon undo() {
        if(turnHandler.getCurrentPlayer().getMove().isEmpty() && turnHandler.getNextPlayer().getMove().isEmpty())
            return null;

        resetAvailCells(chessboard.getCells());
        prevChosenCell = null;
        allowedToMove = false;

        turnHandler.nextTurn();
        Stack<Cells> prevMoves = turnHandler.getCurrentPlayer().getMove();

        Cells prevCell = prevMoves.pop(); // 0, 0, null
        Cells prevSelectedCell = prevMoves.pop();
        Cells currentCell = prevMoves.pop(); // 5, -1, pawn
        Cells selectedCell = prevMoves.pop(); // 0, 0 null

        prevCell.CONTAINS = prevSelectedCell.CONTAINS;
        prevCell.setIcon(prevSelectedCell.piece);
        prevCell.piece = prevSelectedCell.piece;
        prevCell.pieceColor = prevSelectedCell.pieceColor;

        currentCell.CONTAINS = selectedCell.CONTAINS;
        currentCell.setIcon(selectedCell.piece);
        currentCell.piece = selectedCell.piece;
        currentCell.pieceColor = selectedCell.pieceColor;

        turnHandler.getNextPlayer().setCheck(false);
        turnHandler.getCurrentPlayer().setCheck(false);
        isSuggesting = false;
        calculateFutureMove();
        isCheck(turnHandler.getCurrentPlayer());

        return currentCell.getIcon();
    }

    // Revert captured board to previous state
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
            if(x >= 0) {
            if(chessboard.getCapturedBoard(turnHandler.getCurrentPlayer())[y][x].getIcon() != null)
                chessboard.removeFromCapturedBoard(turnHandler.getNextPlayer(), y, x);
            }
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