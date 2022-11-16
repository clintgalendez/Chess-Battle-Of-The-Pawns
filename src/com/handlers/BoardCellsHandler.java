package com.handlers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.gui_components.Clock;
import com.interfaces.Mechanics;
import com.loaders.GraphicsLoader;
import com.main.GameUI;
import com.mechanics.Cells;
import com.mechanics.MoveSets;
import com.mechanics.Players;

public class BoardCellsHandler implements Mechanics, ActionListener {
    private final GameUI GI;
    private final Clock clock;

    public BoardCellsHandler(GameUI GI, Clock clock) {
        this.GI = GI;
        this.clock = clock;
    }

    public void moveHandler (Cells chosenCell) {
        if(chosenCell.getIcon() != null) {
            if(chosenCell.pieceColor == 1) {
                GI.getBoard(GI.getPlayer1()).addToCapturedBoard(chosenCell, GI.getCoordinates()[1], GI.getCoordinates()[0]);
                GI.getCoordinates()[1]++;
                if(GI.getCoordinates()[1] > 3) {
                    GI.getCoordinates()[0]++;
                    GI.getCoordinates()[1] = 0;
                }
            } else if(chosenCell.pieceColor == -1) {
                GI.getBoard(GI.getPlayer2()).addToCapturedBoard(chosenCell, GI.getCoordinates()[3], GI.getCoordinates()[2]);
                GI.getCoordinates()[3]++;
                if(GI.getCoordinates()[3] > 3) {
                    GI.getCoordinates()[2]++;
                    GI.getCoordinates()[3] = 0;
                }
            }
        }

        // Store chosen cell piece properties for undo purposes
        Cells selectedCell = new Cells(chosenCell.CONTAINS, chosenCell.pieceColor, chosenCell.piece);
        GI.getTurnHandler().getCurrentPlayer().addMove(selectedCell);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Cells chosenCell = (Cells) e.getSource();

        if(!GI.isGameStarted()) return;

        //#DESELECTING
        // Deselect a piece
        if(GI.getPrevChosenCell() == chosenCell) {
            resetAvailCells(GI.getCells());
            GI.setPrevChosenCell(null);
            GI.setAllowedToMove(false);
            GI.setSuggesting(false);
            return;
        }

        //#MOVING
        // Move a piece to a chosen suggested cell after selecting a piece to move
        if(GI.isAllowedToMove()) {
            GI.getTurnHandler().getCurrentPlayer().setCheck(false); // Change check status of current player to false after making a move

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
            GI.getTurnHandler().getCurrentPlayer().addMove(changeCellProperties(chosenCell));

            // Store previously chosen cell piece properties for undo purposes
            Cells prevSelectedCell = new Cells(GI.getPrevChosenCell().CONTAINS, GI.getPrevChosenCell().pieceColor, GI.getPrevChosenCell().piece);
            GI.getTurnHandler().getCurrentPlayer().addMove(prevSelectedCell);

            // Reset the previously clicked cell and store its properties for undo purposes
            resetCellProperties(GI.getPrevChosenCell());
            GI.getTurnHandler().getCurrentPlayer().addMove(GI.getPrevChosenCell());
            GI.setIsCastling(false);

            GI.setSuggesting(false);
            calculateFutureMove(); // Calculate future moves after making a move if they result to a check

            GI.getTurnHandler().nextTurn(); // Change the turn to the next player
            isCheck(GI.getTurnHandler().getCurrentPlayer()); // After the turn, check if the current player is checked
            
            if(GI.getTurnHandler().getCurrentPlayer().isCheck()) isCheckmate(GI.getCells()); // If the current player is checked, check for a checkmate

            GI.setAllowedToMove(false);
            resetAvailCells(GI.getCells());
            return;
        }

        //#SELECTING
        // Select which piece to move and suggest available cells
        if(GI.getTurnHandler().getCurrentPlayer().getPlayerColor() == chosenCell.pieceColor) {
            chosenCell.setBackground(Color.YELLOW);

            GI.setSuggesting(true);
            suggestAvailCells(chosenCell, GI.getTurnHandler().getCurrentPlayer().getPlayerColor());

            GI.setPrevChosenCell(chosenCell);
            GI.setAllowedToMove(true);
        }
    }

    public void resetCellProperties(Cells cells) {
        cells.CONTAINS = 0;
        cells.pieceColor = 0;
        cells.setIcon(null);
        cells.piece = cells.getIcon();
    }

    public void changePropertiesForCastling(Cells selectedMove, int rookX) {
        if (GI.getTurnHandler().getCurrentPlayer().getPlayerColor() == -1) {
            GI.getCells()[rookX][selectedMove.posY].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteRook.png",55,55)));
        } else {
            GI.getCells()[rookX][selectedMove.posY].setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackRook.png",55,55)));
        }

        GI.getCells()[rookX][selectedMove.posY].pieceColor = GI.getTurnHandler().getCurrentPlayer().getPlayerColor();
        GI.getCells()[rookX][selectedMove.posY].piece = GI.getCells()[3][selectedMove.posY].getIcon();
    }

    public Cells changeCellProperties(Cells selectedMove) {
        if(GI.getPrevChosenCell().CONTAINS == 5) { // If a selected piece to move is a pawn at start having two moves forward
            selectedMove.CONTAINS = 3; // Then change it to pawn at play having one move forward only
            selectedMove.setIcon(GI.getPrevChosenCell().getIcon());
        }

        else if(GI.getPrevChosenCell().CONTAINS == 3) {
            if(selectedMove.posY == 0) { // If a white pawn at play reaches the black's base
                selectedMove.CONTAINS = 9; // Then it becomes a white queen
                selectedMove.setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/WhiteQueen.png",55,55)));
            } else if(selectedMove.posY == 7) { // If a black pawn at play reaches the white's base
                selectedMove.CONTAINS = 9; // Then it becomes a black queen
                selectedMove.setIcon(new ImageIcon(GraphicsLoader.loadImage("resources/BlackQueen.png",55,55)));
            } else { // If any pawn moves anywhere on the middle part of the board
                selectedMove.CONTAINS = GI.getPrevChosenCell().CONTAINS;  // Then they are as they are
                selectedMove.setIcon(GI.getPrevChosenCell().getIcon()); // The newly clicked cell will contain the text of the previous cell
            }

        } else if (GI.getPrevChosenCell().CONTAINS == 2 && GI.isCastling()) { //Check if the previous cell is a king and if it is castling
            //Move the King
            selectedMove.CONTAINS = GI.getPrevChosenCell().CONTAINS;
            selectedMove.setIcon(GI.getPrevChosenCell().getIcon());

            //Move the Rook
            if(selectedMove.posX == 2) { //If the king is moving to the left
                GI.getCells()[3][selectedMove.posY].CONTAINS = 8; // Change the cell at the right of the king to a rook
                resetCellProperties(GI.getCells()[0][selectedMove.posY]); // Reset the cell at the left of the king
                changePropertiesForCastling(selectedMove, 3); // Change the properties of the cell at the right of the king
            } else if(selectedMove.posX == 5) { //If the king is moving to the right
                GI.getCells()[4][selectedMove.posY].CONTAINS = 8; // Change the cell at the left of the king to a rook
                resetCellProperties(GI.getCells()[7][selectedMove.posY]); // Reset the cell at the right of the king
                changePropertiesForCastling(selectedMove, 4); // Change the properties of the cell at the left of the king
            }

        } else { // For any piece aside from pawns
            selectedMove.CONTAINS = GI.getPrevChosenCell().CONTAINS; // The newly clicked cell will contain the piece of the previous cell
            selectedMove.setIcon(GI.getPrevChosenCell().getIcon()); // The newly clicked cell will have the icon piece of the previous cell
        }

        selectedMove.pieceColor = GI.getPrevChosenCell().pieceColor; // The newly clicked cell will contain the piece color of the previous cell
        selectedMove.piece = selectedMove.getIcon(); // // The newly clicked cell will contain the icon piece of the previous cell

        return selectedMove;
    }

    public int pawnAttack(Cells chosenCell) {
        int enemyPresent = 0;

        // Check surrounding of the cell for the next player's pieces
        if (chosenCell.posX - 1 >= 0 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (GI.getCells()[chosenCell.posX - 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
        }
        if (chosenCell.posX + 1 < 8 && chosenCell.posY + (chosenCell.pieceColor) >= 0) {
            if (GI.getCells()[chosenCell.posX + 1][chosenCell.posY + (chosenCell.pieceColor)].pieceColor == -chosenCell.pieceColor)
                enemyPresent++;
            
        }

        // Return pawn attack mode if next player's pieces are present
        if (enemyPresent > 0) return 4;
        
        return chosenCell.CONTAINS;
    }

    public void doKingCastling(int rowToChange) {
        boolean canCastleleft = true;
        boolean canCastleRight = true;
        //check the row of the king if it is empty
        for (int i = 1; i < 4; i++) {
            if (GI.getCells()[i][rowToChange].CONTAINS != 0) {
                canCastleleft = false;
                break;
            }
        }

        if (canCastleleft) {
            GI.getCells()[2][rowToChange].setBackground(Color.GREEN);
        }

        //check the other side of the king if it is empty
        for (int i = 5; i < 7; i++) {
            if (GI.getCells()[i][rowToChange].CONTAINS != 0) {
                canCastleRight = false;
                break;
            }
        }

        if (canCastleRight) {
            GI.getCells()[5][rowToChange].setBackground(Color.GREEN);
        }

        if (canCastleleft || canCastleRight) {
            GI.setIsCastling(true);
        }
    }

    public void KingCastlingCheck(Cells currentCell) {
        //Check if the king has moved, check if the rook has moved, check if the cells in between are empty
        if (GI.getCells()[4][currentCell.posY].CONTAINS != 2 || GI.getCells()[0][currentCell.posY].CONTAINS != 8 || GI.getCells()[7][currentCell.posY].CONTAINS != 8) {
            return;
        }

        doKingCastling(currentCell.posY);
    }

    private Cells calculateAvailMove(Cells chosenCell, int currentColorPiece, int i, int[][]moves) {
        int x = chosenCell.posX + moves[i][0];
        int y = chosenCell.posY + (currentColorPiece * moves[i][1]);
        
        if(x >= 0 && x < 8 && y >= 0 && y < 8 && GI.getCells()[x][y].pieceColor != currentColorPiece)
            return GI.getCells()[x][y]; // return the cell at x-y position if a calculated available move does not contain same piece color

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
                assert futureCells != null;
                if(futureCells.CONTAINS == 0) { //if cell is empty then continue
                    continue;
                }

                if (checkOnAuto(futureCells)) break;
            }
        }
    }

    private boolean checkOnAuto(Cells futureCells) {
        if(GI.isOnAuto()) { // (FOR CHECKMATE PURPOSES) Add move to movelist after it is calculated
            GI.getMoveList().add(futureCells);
        }

        if(GI.isSuggesting()) { // (FOR CHECKING PURPOSES)
            if(futureCells.CONTAINS == 2) { // And if a future move contains a king
                GI.setCheckedPiece(futureCells.pieceColor); // Get the piece color of the checked king
                if(GI.getCheckedPiece() != GI.getTurnHandler().getCurrentPlayer().getPlayerColor()) { // If checked piece is of the next player
                    GI.getTurnHandler().getNextPlayer().setCheck(true); // Change check status of next player to true
                } else { // else if the checked piece is of the current player
                    GI.getTurnHandler().getCurrentPlayer().setCheck(true); // Change check status of current player to true
                }
                return true;
            }
        } else {
            futureCells.setBackground(Color.GREEN); // (FOR MAKING MOVE PURPOSES) set a cell to green
            futureCells.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        }
        return false;
    }

    // Calculate future moves for next turn and check if a move checks a king
    public void calculateFutureMove() {
        Cells[][] board = GI.getCells();
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
        if(player.isCheck() && GI.getTurnHandler().getNextPlayer().isCheck()) { // (FOR CHECKMATE PURPOSES) If both player becomes checked
            Icon icon = undo();
            undoCapturedBoard(icon);
            return;
        }

        if(player.isCheck()) { // If current player is checked
            GI.getNamePanel(GI.getTurnHandler().getCurrentPlayer()).setBackground(Color.YELLOW); // change own name panel to yellow
            GI.getNamePanel(GI.getTurnHandler().getNextPlayer()).setBackground(new Color(214, 188, 153)); // change next player's panel to default
        } else if(GI.getTurnHandler().getNextPlayer().isCheck()) { // If the previous player is still checked
            if(GI.getTurnHandler().getNextPlayer().getPlayerColor() == GI.getCheckedPiece()) { // If the checked piece is of the previous player
                Icon icon = undo(); // Undo, as if a move is not made
                undoCapturedBoard(icon);
                
                return;
            }
        }

        if(!player.isCheck()) { // If current player is not checked
            GI.getNamePanel(GI.getTurnHandler().getCurrentPlayer()).setBackground(Color.GREEN); // Change own name panel to green
            GI.getNamePanel(GI.getTurnHandler().getNextPlayer()).setBackground(new Color(214, 188, 153)); // change next player's panel to default
        }
    }

    public void isCheckmate(Cells[][] board) {
        boolean stalemated = checkForCheckmate();
        if(stalemated) { // If the next player is checkmated
            clock.timer.stop();
            
            // Disable all cells
            for(Cells[] cells : board) {
                for(Cells cell : cells) {
                    cell.setEnabled(false);
                    cell.setDisabledIcon(cell.getIcon());
                }
            }

            // Change the color of the name panel of the loser to red, while green for the winner
            GI.getNamePanel(GI.getTurnHandler().getCurrentPlayer()).setBackground(Color.RED);
            GI.getNamePanel(GI.getTurnHandler().getNextPlayer()).setBackground(Color.GREEN);
        }
    }

    private boolean checkForCheckmate() {
        for(Cells[] cells : GI.getCells()) {
            for(Cells cell : cells) {
                if(cell.pieceColor != GI.getTurnHandler().getCurrentPlayer().getPlayerColor()) continue; // Check only the pieces of current player

                GI.getMoveList().clear(); // clear the movelist
                GI.setOnAuto(true); // Set the purpose to checkmate purposes
                suggestAvailCells(cell, cell.pieceColor); // Store suggested moves
                GI.setOnAuto(false); // Disable purpose

                if(GI.getMoveList().isEmpty()) continue; // If a piece has no move, then proceed to other pieces

                if(!doMoves(cell)) { // If current player has more moves to disable check status
                    GI.getTurnHandler().getCurrentPlayer().setCheck(true); // Set current player's check status to true
                    return false; // Return that the player is not checkmated
                }
            }
        }

        return true; // Return that the player is checkmated
    }

    // Programatically find possible move to disable check status
    private boolean doMoves(Cells cell) {
        Cells futureCells;

        for (int i = 0; i < GI.getMoveList().size(); i++) {
            GI.setPrevChosenCell(cell);
            futureCells = GI.getMoveList().get(i);

            moveHandler(futureCells);

            // Move the clicked piece to the chosen cell
            GI.getTurnHandler().getCurrentPlayer().addMove(changeCellProperties(futureCells));

            Cells prevSelectedCell = new Cells(GI.getPrevChosenCell().CONTAINS, GI.getPrevChosenCell().pieceColor, GI.getPrevChosenCell().piece);
            GI.getTurnHandler().getCurrentPlayer().addMove(prevSelectedCell);

            // Reset the previously clicked cell
            GI.getPrevChosenCell().CONTAINS = 0;
            GI.getPrevChosenCell().pieceColor = 0;
            GI.getPrevChosenCell().setIcon(null);
            GI.getPrevChosenCell().piece = GI.getPrevChosenCell().getIcon();
            GI.getTurnHandler().getCurrentPlayer().addMove(GI.getPrevChosenCell());

            GI.getTurnHandler().getNextPlayer().setCheck(false);
            GI.getTurnHandler().getCurrentPlayer().setCheck(false);
            
            // Calculate future moves if they result to a check
            GI.setSuggesting(false);
            calculateFutureMove();

            GI.getTurnHandler().nextTurn();
            isCheck(GI.getTurnHandler().getCurrentPlayer()); // Check if the move makes a check

            if(!GI.getTurnHandler().getCurrentPlayer().isCheck()) {
                Icon icon = undo(); 
                undoCapturedBoard(icon);
                return false;
            }
        }
        return true;
    }

    // Revert move to previous placements
    public Icon undo() {
        if(GI.getTurnHandler().getCurrentPlayer().getMove().isEmpty() && GI.getTurnHandler().getNextPlayer().getMove().isEmpty())
            return null;

        GI.setPrevChosenCell(null);
        GI.setAllowedToMove(false);

        GI.getTurnHandler().nextTurn();
        Stack<Cells> prevMoves = GI.getTurnHandler().getCurrentPlayer().getMove();

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

        GI.getTurnHandler().getNextPlayer().setCheck(false);
        GI.getTurnHandler().getCurrentPlayer().setCheck(false);
        GI.setSuggesting(false);
        calculateFutureMove();
        isCheck(GI.getTurnHandler().getCurrentPlayer());

        resetAvailCells(GI.getCells());
        return currentCell.getIcon();
    }

    // Revert captured board to previous state
    public void undoCapturedBoard(Icon icon) {
        if(icon != null) {
            int y, x;
            if(GI.getTurnHandler().getCurrentPlayer().getPlayerColor() == -1) {
                GI.getCoordinates()[1]--;
                if(GI.getCoordinates()[1] < 0) {
                    GI.getCoordinates()[0]--;
                    GI.getCoordinates()[1] = 3;
                }
                x = GI.getCoordinates()[0];
                y = GI.getCoordinates()[1];
                
            } else {
                GI.getCoordinates()[3]--;
                if(GI.getCoordinates()[3] < 0) {
                    GI.getCoordinates()[2]--;
                    GI.getCoordinates()[3] = 3;
                } 
                x = GI.getCoordinates()[2];
                y = GI.getCoordinates()[3];
            }
            
            if(x >= 0) {
            if(GI.getBoard(GI.getTurnHandler().getCurrentPlayer()).getBoardCells()[y][x].getIcon() != null)
                GI.getBoard(GI.getTurnHandler().getCurrentPlayer()).removeFromCapturedBoard(y, x);
            }
        } 
    }    
}
