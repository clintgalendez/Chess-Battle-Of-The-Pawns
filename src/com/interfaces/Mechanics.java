package com.Interfaces;

import com.chessBOTP.Cells;
import javax.swing.Icon;

public interface Mechanics {
    abstract void play();
    abstract Cells changeCellProperties(Cells selectedMove);
    abstract void suggestAvailCells(Cells chosenCell, int currentColorPiece);
    abstract void calculateFutureMove();
    abstract void isCheck();
    abstract void isCheckmate();
    abstract Icon undo();
}
