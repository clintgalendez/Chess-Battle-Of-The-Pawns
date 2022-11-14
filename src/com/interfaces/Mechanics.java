package com.interfaces;

import com.mechanics.Cells;
import com.mechanics.Players;

import javax.swing.Icon;

public interface Mechanics {
    abstract Cells changeCellProperties(Cells selectedMove);
    abstract void suggestAvailCells(Cells chosenCell, int currentColorPiece);
    abstract void calculateFutureMove();
    abstract void isCheck(Players player);
    abstract void isCheckmate(Cells[][] board);
    abstract Icon undo();
}
