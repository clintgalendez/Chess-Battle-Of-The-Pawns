package com.handlers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.border.LineBorder;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.interfaces.Mechanics;
import com.loaders.GraphicsLoader;
import com.main.GameWindow;
import com.mechanics.Cells;
import com.mechanics.MoveSets;
import com.mechanics.Players;

public class BoardCellsHandler implements Mechanics, ActionListener {
    Cells rookDesignatedCell;
    Cells rookAfterPositioned;
    Cells rookBeforePositioned;
    Cells rookAfterLeavingInitPos;

    private final GameWindow gameWindow;
    
    public BoardCellsHandler(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Cells chosenCell = (Cells) e.getSource();

        if(!gameWindow.getPlay().isGameStarted()) return;

        //#DESELECTING
        // Deselect a piece
        if(gameWindow.getPlay().getPrevChosenCell() == chosenCell) {
            resetAvailCells(gameWindow.getPlay().getCells());
            gameWindow.getPlay().setPrevChosenCell(null);
            gameWindow.getPlay().setAllowedToMove(false);
            gameWindow.getPlay().setSuggesting(false);
            return;
        }

        //#MOVING
        // Move a piece to a chosen suggested cell after selecting a piece to move
        if(gameWindow.getPlay().isAllowedToMove()) {
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setCheck(false); // Change check status of current player to false after making a move

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
            moveHandler(chosenCell);

            // Move the clicked piece to the chosen cell and store its properties for undo purposes
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(changeCellProperties(chosenCell));

            // Store previously chosen cell piece properties for undo purposes
            Cells prevSelectedCell = new Cells(gameWindow.getPlay().getPrevChosenCell().CONTAINS, gameWindow.getPlay().getPrevChosenCell().pieceColor, gameWindow.getPlay().getPrevChosenCell().piece);
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(prevSelectedCell);

            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(gameWindow.getPlay().getPrevChosenCell());

            if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().hasCastled() && !gameWindow.getPlay().getTurnHandler().getCurrentPlayer().hasStored()) {
                gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(rookDesignatedCell);
                gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(rookAfterPositioned);
                gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(rookBeforePositioned);
                gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(rookAfterLeavingInitPos);

                Cells checkCell = new Cells(0, 2, null);
                gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(checkCell);

                gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setHasStored(true);
                gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setHasCastled(false);
            }

            Cells sample = gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getMove().peek();
            System.out.println(sample.CONTAINS + " " + sample.pieceColor + " " + sample.piece);

            System.out.println(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().hasStored() + " " + gameWindow.getPlay().getTurnHandler().getCurrentPlayer().hasCastled());

            resetCellProperties(gameWindow.getPlay().getPrevChosenCell());

            gameWindow.getPlay().setIsCastling(false);
            gameWindow.getPlay().setSuggesting(false);
            calculateFutureMove(); // Calculate future moves after making a move if they result to a check

            gameWindow.getPlay().getTurnHandler().nextTurn(); // Change the turn to the next player
            isCheck(gameWindow.getPlay().getTurnHandler().getCurrentPlayer()); // After the turn, check if the current player is checked
            
            if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().isCheck()) isCheckmate(gameWindow.getPlay().getCells()); // If the current player is checked, check for a checkmate

            gameWindow.getPlay().setAllowedToMove(false);
            resetAvailCells(gameWindow.getPlay().getCells());
            return;
        }

        //#SELECTING
        // Select which piece to move and suggest available cells
        if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor() == chosenCell.pieceColor) {
            chosenCell.setBackground(Color.YELLOW);

            gameWindow.getPlay().setSuggesting(true);
            suggestAvailCells(chosenCell, gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor());

            gameWindow.getPlay().setPrevChosenCell(chosenCell);
            gameWindow.getPlay().setAllowedToMove(true);
        }
    }
    
    public void moveHandler (Cells chosenCell) {
        if(chosenCell.getIcon() != null) {
            if(chosenCell.pieceColor == 1) {
                gameWindow.getPlay().getBoard(gameWindow.getPlay().getPlayer1()).addToCapturedBoard(chosenCell, gameWindow.getPlay().getCoordinates()[1], gameWindow.getPlay().getCoordinates()[0]);
                gameWindow.getPlay().getCoordinates()[1]++;
                if(gameWindow.getPlay().getCoordinates()[1] > 3) {
                    gameWindow.getPlay().getCoordinates()[0]++;
                    gameWindow.getPlay().getCoordinates()[1] = 0;
                }
            } else if(chosenCell.pieceColor == -1) {
                gameWindow.getPlay().getBoard(gameWindow.getPlay().getPlayer2()).addToCapturedBoard(chosenCell, gameWindow.getPlay().getCoordinates()[3], gameWindow.getPlay().getCoordinates()[2]);
                gameWindow.getPlay().getCoordinates()[3]++;
                if(gameWindow.getPlay().getCoordinates()[3] > 3) {
                    gameWindow.getPlay().getCoordinates()[2]++;
                    gameWindow.getPlay().getCoordinates()[3] = 0;
                }
            }
        }

        // Store chosen cell piece properties for undo purposes
        Cells selectedCell = new Cells(chosenCell.CONTAINS, chosenCell.pieceColor, chosenCell.piece);
        gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(selectedCell);
    }

    public Cells changeCellProperties(Cells selectedMove) {
        if(gameWindow.getPlay().getPrevChosenCell().CONTAINS == 5) { // If a selected piece to move is a pawn at start having two moves forward
            selectedMove.CONTAINS = 3; // Then change it to pawn at play having one move forward only
            selectedMove.setIcon(gameWindow.getPlay().getPrevChosenCell().getIcon());
        }
        else if(gameWindow.getPlay().getPrevChosenCell().CONTAINS == 3) {
            if(selectedMove.posY == 0) { // If a white pawn at play reaches the black's base
                selectedMove.CONTAINS = 9; // Then it becomes a white queen
                selectedMove.setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteQueen.png",55,55)));
            } else if(selectedMove.posY == 7) { // If a black pawn at play reaches the white's base
                selectedMove.CONTAINS = 9; // Then it becomes a black queen
                selectedMove.setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackQueen.png",55,55)));
            } else { // If any pawn moves anywhere on the middle part of the board
                selectedMove.CONTAINS = gameWindow.getPlay().getPrevChosenCell().CONTAINS;  // Then they are as they are
                selectedMove.setIcon(gameWindow.getPlay().getPrevChosenCell().getIcon()); // The newly clicked cell will contain the text of the previous cell
            }   
        } else if (gameWindow.getPlay().getPrevChosenCell().CONTAINS == 2 && gameWindow.getPlay().isCastling()) { //Check if the previous cell is a king and if it is castling
            //Move the King
            selectedMove.CONTAINS = gameWindow.getPlay().getPrevChosenCell().CONTAINS;
            selectedMove.setIcon(gameWindow.getPlay().getPrevChosenCell().getIcon());

            //Move the Rook
            if(selectedMove.posX == 2) { //If the king is moving to the left
                rookDesignatedCell = new Cells(gameWindow.getPlay().getCells()[3][selectedMove.posY].CONTAINS, 
                                               gameWindow.getPlay().getCells()[3][selectedMove.posY].pieceColor, 
                                               gameWindow.getPlay().getCells()[3][selectedMove.posY].piece);

                rookAfterPositioned = changePropertiesForCastling(selectedMove, 3); // Change the properties of the cell at the right of the king
                
                rookBeforePositioned = new Cells(gameWindow.getPlay().getCells()[0][selectedMove.posY].CONTAINS, 
                                                 gameWindow.getPlay().getCells()[0][selectedMove.posY].pieceColor,
                                                 gameWindow.getPlay().getCells()[0][selectedMove.posY].piece);

                rookAfterLeavingInitPos = gameWindow.getPlay().getCells()[0][selectedMove.posY];
                resetCellProperties(gameWindow.getPlay().getCells()[0][selectedMove.posY]); // Reset the cell at the left of the king
            } else if(selectedMove.posX == 6) { //If the king is moving to the right
                rookDesignatedCell = new Cells(gameWindow.getPlay().getCells()[5][selectedMove.posY].CONTAINS, 
                                               gameWindow.getPlay().getCells()[5][selectedMove.posY].pieceColor, 
                                               gameWindow.getPlay().getCells()[5][selectedMove.posY].piece);
                                               
                rookAfterPositioned = changePropertiesForCastling(selectedMove, 5); // Change the properties of the cell at the left of the king
                
                rookBeforePositioned = new Cells(gameWindow.getPlay().getCells()[7][selectedMove.posY].CONTAINS, 
                                                 gameWindow.getPlay().getCells()[7][selectedMove.posY].pieceColor,
                                                 gameWindow.getPlay().getCells()[7][selectedMove.posY].piece);

                rookAfterLeavingInitPos = gameWindow.getPlay().getCells()[7][selectedMove.posY];
                resetCellProperties(gameWindow.getPlay().getCells()[7][selectedMove.posY]); // Reset the cell at the right of the king
            }

            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setHasCastled(true);
        } else { // For any piece aside from pawns
            selectedMove.CONTAINS = gameWindow.getPlay().getPrevChosenCell().CONTAINS; // The newly clicked cell will contain the piece of the previous cell
            selectedMove.setIcon(gameWindow.getPlay().getPrevChosenCell().getIcon()); // The newly clicked cell will have the icon piece of the previous cell
        }

        selectedMove.pieceColor = gameWindow.getPlay().getPrevChosenCell().pieceColor; // The newly clicked cell will contain the piece color of the previous cell
        selectedMove.piece = selectedMove.getIcon(); // // The newly clicked cell will contain the icon piece of the previous cell

        return selectedMove;
    }

    public void resetCellProperties(Cells cells) {
        cells.CONTAINS = 0;
        cells.pieceColor = 0;
        cells.setIcon(null);
        cells.piece = cells.getIcon();
    }

    public Cells changePropertiesForCastling(Cells selectedMove, int rookX) {
        gameWindow.getPlay().getCells()[rookX][selectedMove.posY].CONTAINS = 8;
        if (gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor() == -1) {
            gameWindow.getPlay().getCells()[rookX][selectedMove.posY].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteRook.png",55,55)));
        } else {
            gameWindow.getPlay().getCells()[rookX][selectedMove.posY].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackRook.png",55,55)));
        }

        gameWindow.getPlay().getCells()[rookX][selectedMove.posY].pieceColor = gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor();
        gameWindow.getPlay().getCells()[rookX][selectedMove.posY].piece = gameWindow.getPlay().getCells()[3][selectedMove.posY].getIcon();
        
        return gameWindow.getPlay().getCells()[rookX][selectedMove.posY];
    }
    
    public void doKingCastling(int rowToChange) {
        boolean canCastleleft = true;
        boolean canCastleRight = true;
        //check the row of the king if it is empty
        for (int i = 1; i < 4; i++) {
            if (gameWindow.getPlay().getCells()[i][rowToChange].CONTAINS != 0) {
                canCastleleft = false;
                break;
            }
        }

        if (canCastleleft) {
            gameWindow.getPlay().getCells()[2][rowToChange].setBackground(Color.GREEN);
        }

        //check the other side of the king if it is empty
        for (int i = 5; i < 7; i++) {
            if (gameWindow.getPlay().getCells()[i][rowToChange].CONTAINS != 0) {
                canCastleRight = false;
                break;
            }
        }

        if (canCastleRight) {
            gameWindow.getPlay().getCells()[6][rowToChange].setBackground(Color.GREEN);
        }

        if (canCastleleft || canCastleRight) {
            gameWindow.getPlay().setIsCastling(true);
        }
    }

    // FIXME!!!
    public void KingCastlingCheck(Cells currentCell) {
        //Check if the king has moved, check if the rook has moved, check if the cells in between are empty
        if (gameWindow.getPlay().getCells()[4][currentCell.posY].CONTAINS != 2 || gameWindow.getPlay().getCells()[0][currentCell.posY].CONTAINS != 8 || gameWindow.getPlay().getCells()[7][currentCell.posY].CONTAINS != 8) {
            return;
        }

        doKingCastling(currentCell.posY);
    }

    public int pawnAttack(Cells chosenCell) {
        int enemyPresent = 0;

        // Check surrounding of the cell for the next player's pieces
        if (chosenCell.posX - 1 >= 0 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (gameWindow.getPlay().getCells()[chosenCell.posX - 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
        }
        if (chosenCell.posX + 1 < 8 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (gameWindow.getPlay().getCells()[chosenCell.posX + 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
            
        }

        // Return pawn attack mode if next player's pieces are present
        if (enemyPresent > 0) return 4;
        
        return chosenCell.CONTAINS;
    }

    private Cells calculateAvailMove(Cells chosenCell, int currentColorPiece, int i, int[][]moves) {
        int x = chosenCell.posX + moves[i][0];
        int y = chosenCell.posY + (currentColorPiece * moves[i][1]);
        
        if(x >= 0 && x < 8 && y >= 0 && y < 8 && gameWindow.getPlay().getCells()[x][y].pieceColor != currentColorPiece)
            return gameWindow.getPlay().getCells()[x][y]; // return the cell at x-y position if a calculated available move does not contain same piece color

        return null; // else, return null
    }

    private boolean checkOnAuto(Cells futureCells) {
        if(gameWindow.getPlay().isOnAuto()) { // (FOR CHECKMATE PURPOSES) Add move to movelist after it is calculated
            gameWindow.getPlay().getMoveList().add(futureCells);
        }

        if(!gameWindow.getPlay().isSuggesting()) { // (FOR CHECKING PURPOSES)
            if(futureCells.CONTAINS == 2) { // And if a future move contains a king
                gameWindow.getPlay().setCheckedPiece(futureCells.pieceColor); // Get the piece color of the checked king
                if(gameWindow.getPlay().getCheckedPiece() != gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor()) { // If checked piece is of the next player
                    gameWindow.getPlay().getTurnHandler().getNextPlayer().setCheck(true); // Change check status of next player to true
                } else { // else if the checked piece is of the current player
                    gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setCheck(true); // Change check status of current player to true
                }
                return true;
            }
        } else {
            futureCells.setBackground(Color.GREEN); // (FOR MAKING MOVE PURPOSES) set a cell to green
            futureCells.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        }
        return false;
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

            if (checkOnAuto(futureCells)) break;

            if(futureCells.CONTAINS != 0) continue; // If a selected piece is any piece aside from a pawn and its suggested move that turned green has a piece of the next player, then proceed to other suggestions

            if (chosenCell.CONTAINS == 2) {
                KingCastlingCheck(chosenCell);
            }
            
            // If a selected piece is a bishop, a rook, or a queen
            if(chosenCell.CONTAINS == 7 || chosenCell.CONTAINS == 8 || chosenCell.CONTAINS == 9) {
                // Then iteratively suggest available moves till the end of the sides of the board
                while (true) {
                    futureCells = calculateAvailMove(futureCells, currentColorPiece, i, moves);
                    if(futureCells == null) break;

                    if (checkOnAuto(futureCells)) break;
                    
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
    
                if(gameWindow.getPlay().isOnAuto())  {
                    gameWindow.getPlay().getMoveList().add(futureCells);
                }

                if(!gameWindow.getPlay().isSuggesting()) {
                    if(futureCells.CONTAINS == 2) { 
                        gameWindow.getPlay().setCheckedPiece(futureCells.pieceColor);; 
                        if(gameWindow.getPlay().getCheckedPiece() != gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor()) {
                            gameWindow.getPlay().getTurnHandler().getNextPlayer().setCheck(true);
                        } else {
                            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setCheck(true);
                        }
                        break;
                    }
                } else {
                    if(futureCells.CONTAINS != 0) {
                        futureCells.setBackground(Color.GREEN); // If a suggested move has a piece of the next player, then set a cell to green
                        futureCells.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                    }
                }
            }
        }
    }

    // Calculate future moves for next turn and check if a move checks a king
    public void calculateFutureMove() {
        Cells[][] board = gameWindow.getPlay().getCells();
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
                    board[j][i].setBorder(new LineBorder(null, 1, false));
                } else {
                    board[j][i].setBackground(new Color(47, 38, 29));
                    board[j][i].setBorder(new LineBorder(null, 1, false));
                }
            }
        }
    }

    // Make changes in GUI if a player is checked
    public void isCheck(Players player) {
        if(player.isCheck() && gameWindow.getPlay().getTurnHandler().getNextPlayer().isCheck()) { // (FOR CHECKMATE PURPOSES) If both player becomes checked
            Icon icon = undo();
            undoCapturedBoard(icon);
            return;
        }

        if(player.isCheck()) { // If current player is checked
            gameWindow.getPlay().getNamePanel(gameWindow.getPlay().getTurnHandler().getCurrentPlayer()).setBackground(Color.YELLOW); // change own name panel to yellow
            gameWindow.getPlay().getNamePanel(gameWindow.getPlay().getTurnHandler().getNextPlayer()).setBackground(new Color(214, 188, 153)); // change next player's panel to default
        } else if(gameWindow.getPlay().getTurnHandler().getNextPlayer().isCheck()) { // If the previous player is still checked
            if(gameWindow.getPlay().getTurnHandler().getNextPlayer().getPlayerColor() == gameWindow.getPlay().getCheckedPiece()) { // If the checked piece is of the previous player
                Icon icon = undo(); // Undo, as if a move is not made
                undoCapturedBoard(icon);
                
                return;
            }
        }

        if(!player.isCheck()) { // If current player is not checked
            gameWindow.getPlay().getNamePanel(gameWindow.getPlay().getTurnHandler().getCurrentPlayer()).setBackground(Color.GREEN); // Change own name panel to green
            gameWindow.getPlay().getNamePanel(gameWindow.getPlay().getTurnHandler().getNextPlayer()).setBackground(new Color(214, 188, 153)); // change next player's panel to default
        }
    }

    public void isCheckmate(Cells[][] board) {
        boolean stalemated = checkForCheckmate();
        if(stalemated) { // If the next player is checkmated
            gameWindow.getPlay().getClock().timer.stop();
            
            // Disable all cells
            for(Cells[] cells : board) {
                for(Cells cell : cells) {
                    cell.setEnabled(false);
                    cell.setDisabledIcon(cell.getIcon());
                }
            }

            // Change the color of the name panel of the loser to red, while green for the winner
            gameWindow.getPlay().getNamePanel(gameWindow.getPlay().getTurnHandler().getCurrentPlayer()).setBackground(Color.RED);
            gameWindow.getPlay().getNamePanel(gameWindow.getPlay().getTurnHandler().getNextPlayer()).setBackground(Color.GREEN);
        }
    }

    private boolean checkForCheckmate() {
        for(Cells[] cells : gameWindow.getPlay().getCells()) {
            for(Cells cell : cells) {
                if(cell.pieceColor != gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor()) continue; // Check only the pieces of current player

                gameWindow.getPlay().getMoveList().clear(); // clear the movelist
                gameWindow.getPlay().setOnAuto(true); // Set the purpose to checkmate purposes
                suggestAvailCells(cell, cell.pieceColor); // Store suggested moves
                gameWindow.getPlay().setOnAuto(false); // Disable purpose

                if(gameWindow.getPlay().getMoveList().isEmpty()) continue; // If a piece has no move, then proceed to other pieces

                if(!doMoves(cell)) { // If current player has more moves to disable check status
                    gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setCheck(true); // Set current player's check status to true
                    return false; // Return that the player is not checkmated
                }
            }
        }

        return true; // Return that the player is checkmated
    }

    // Programatically find possible move to disable check status
    private boolean doMoves(Cells cell) {
        Cells futureCells;

        for (int i = 0; i < gameWindow.getPlay().getMoveList().size(); i++) {
            gameWindow.getPlay().setPrevChosenCell(cell);
            futureCells = gameWindow.getPlay().getMoveList().get(i);

            moveHandler(futureCells);
            
            // Move the clicked piece to the chosen cell
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(changeCellProperties(futureCells));

            Cells prevSelectedCell = new Cells(gameWindow.getPlay().getPrevChosenCell().CONTAINS, gameWindow.getPlay().getPrevChosenCell().pieceColor, gameWindow.getPlay().getPrevChosenCell().piece);
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(prevSelectedCell);

            // Reset the previously clicked cell
            gameWindow.getPlay().getPrevChosenCell().CONTAINS = 0;
            gameWindow.getPlay().getPrevChosenCell().pieceColor = 0;
            gameWindow.getPlay().getPrevChosenCell().setIcon(null);
            gameWindow.getPlay().getPrevChosenCell().piece = gameWindow.getPlay().getPrevChosenCell().getIcon();
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().addMove(gameWindow.getPlay().getPrevChosenCell());

            gameWindow.getPlay().getTurnHandler().getNextPlayer().setCheck(false);
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setCheck(false);
            
            // Calculate future moves if they result to a check
            gameWindow.getPlay().setSuggesting(false);
            calculateFutureMove();

            gameWindow.getPlay().getTurnHandler().nextTurn();
            isCheck(gameWindow.getPlay().getTurnHandler().getCurrentPlayer()); // Check if the move makes a check

            if(!gameWindow.getPlay().getTurnHandler().getCurrentPlayer().isCheck()) {
                Icon icon = undo(); 
                undoCapturedBoard(icon);
                return false;
            }
        }
        return true;
    }

    // Revert move to previous placements
    public Icon undo() {
        if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getMove().isEmpty() && gameWindow.getPlay().getTurnHandler().getNextPlayer().getMove().isEmpty())
            return null;

        gameWindow.getPlay().setPrevChosenCell(null);
        gameWindow.getPlay().setAllowedToMove(false);

        gameWindow.getPlay().getTurnHandler().nextTurn();
        Stack<Cells> prevMoves = gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getMove();

        if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getMove().peek().pieceColor == 2) {
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getMove().pop();
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setHasCastled(true);
        }
        
        Cells prevCell = prevMoves.pop(); // The state of the cell of the selected piece after moving
        Cells prevSelectedCell = prevMoves.pop(); // The previous state of the selected piece
        Cells currentCell = prevMoves.pop(); // The state of the selected cell after moving
        Cells selectedCell = prevMoves.pop(); // The previous state of the selected cell to move at

        prevCell.CONTAINS = prevSelectedCell.CONTAINS;
        prevCell.setIcon(prevSelectedCell.piece);
        prevCell.piece = prevSelectedCell.piece;
        prevCell.pieceColor = prevSelectedCell.pieceColor;

        currentCell.CONTAINS = selectedCell.CONTAINS;
        currentCell.setIcon(selectedCell.piece);
        currentCell.piece = selectedCell.piece;
        currentCell.pieceColor = selectedCell.pieceColor;

        gameWindow.getPlay().getTurnHandler().getNextPlayer().setCheck(false);
        gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setCheck(false);
        gameWindow.getPlay().setSuggesting(false);
        calculateFutureMove();
        isCheck(gameWindow.getPlay().getTurnHandler().getCurrentPlayer());

        resetAvailCells(gameWindow.getPlay().getCells());

        if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().hasCastled()) {
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setHasCastled(false);
            gameWindow.getPlay().getTurnHandler().getCurrentPlayer().setHasStored(false);

            gameWindow.getPlay().getTurnHandler().nextTurn();
            undo();
        }

        if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().hasCastled()) {

        }

        return currentCell.getIcon();
    }

    // Revert captured board to previous state
    public void undoCapturedBoard(Icon icon) {
        if(icon != null) {
            int y, x;
            if(gameWindow.getPlay().getTurnHandler().getCurrentPlayer().getPlayerColor() == -1) {
                gameWindow.getPlay().getCoordinates()[1]--;
                if(gameWindow.getPlay().getCoordinates()[1] < 0) {
                    gameWindow.getPlay().getCoordinates()[0]--;
                    gameWindow.getPlay().getCoordinates()[1] = 3;
                }
                x = gameWindow.getPlay().getCoordinates()[0];
                y = gameWindow.getPlay().getCoordinates()[1];
                
            } else {
                gameWindow.getPlay().getCoordinates()[3]--;
                if(gameWindow.getPlay().getCoordinates()[3] < 0) {
                    gameWindow.getPlay().getCoordinates()[2]--;
                    gameWindow.getPlay().getCoordinates()[3] = 3;
                } 
                x = gameWindow.getPlay().getCoordinates()[2];
                y = gameWindow.getPlay().getCoordinates()[3];
            }
            
            if(x >= 0) {
            if(gameWindow.getPlay().getBoard(gameWindow.getPlay().getTurnHandler().getCurrentPlayer()).getBoardCells()[y][x].getIcon() != null)
                gameWindow.getPlay().getBoard(gameWindow.getPlay().getTurnHandler().getCurrentPlayer()).removeFromCapturedBoard(y, x);
            }
        } 
    }    
}
