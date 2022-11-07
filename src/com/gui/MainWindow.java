package com.gui;

import com.chessBOTP.Cells;
import com.chessBOTP.Main;

import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {
    private final Cells[][] cells = new Cells[8][8];

    public Cells[][] getCells() {
        return cells;
    }

    public MainWindow (){
      setDefaultCloseOperation(EXIT_ON_CLOSE);
		  setTitle("Chess");
		  setLayout(new BorderLayout());

      //JPanel for border numbers
      JPanel brdr_num = new JPanel ();
      brdr_num.setLayout(new GridLayout(1, 8));
      for (int x = 0; x<8; x++){
          JLabel[] num = new JLabel[8];
          num[x] = new JLabel ("" + (x+1));
        brdr_num.add(num[x]);
      }

      //JPanel for border letters
      JPanel brdr_let = new JPanel ();
      brdr_let.setLayout(new GridLayout(8, 1));
      for (char i = 'a'; i <= 'h'; i++) {
        int x = 0;
          JLabel[] letters = new JLabel[8];
          letters[x] = new JLabel(""+i);
        brdr_let.add(letters[x]);
      }

      //JPanel for cells
        JPanel grid = new JPanel();
        grid.setLayout(new GridLayout (8, 8));
      for (int y = 0; y<8; y++){
        for (int x = 0; x<8; x++){
          cells[x][y] = new Cells (x, y, 0, 0);
          cells[x][y].addActionListener(Main::buttonClickedHandler);
          grid.add (cells[x][y]);
        }
      }

      add(brdr_num, BorderLayout.NORTH);
      add(brdr_let, BorderLayout.WEST);
      add(grid, BorderLayout.CENTER);
      setSize(500, 500);
		  setVisible(true);
    } 
}
