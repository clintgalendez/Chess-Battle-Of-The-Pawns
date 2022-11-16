package com.gui_components;

import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.loaders.GraphicsLoader;
import com.main.GameUI;
import com.main.HelpUI;
import com.main.SettingsUI;


public class ButtonSliders extends JLabel {

    private String path_one;
    private String path_two;

    private SettingsUI SI;
    private GameUI GI;
    private Clock clock;

    private int buttonNumber;
    private int frames = 0;

    private float deltaX = 10;
    private float updateValue = 5f;

    private long lastCheck = 0;

    private boolean update = false;
    private boolean reverse = false;

    private ImageIcon icon;

    public ButtonSliders(MouseListener l, String path_one, String path_two, SettingsUI SI, GameUI GI, Clock clock, int buttonNumber) {
        this.path_one = path_one;
        this.path_two = path_two;
        this.SI = SI;
        this.GI = GI;
        this.clock = clock;
        this.buttonNumber = buttonNumber;

        addMouseListener(l);

        if(buttonNumber == 4) {
            deltaX = 355;
            icon = new ImageIcon(GraphicsLoader.loadImage(path_two, 65, 65));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(update) {
            updateIcon();
        }

        if(deltaX >= 355) {
            icon = new ImageIcon(GraphicsLoader.loadImage(path_two, 65, 65));
            icon.paintIcon(this, g, (int)deltaX, 9); 
            update = false; reverse = true;
            
            if(buttonNumber == 1) {
                deltaX = 10;
                clock.timer.restart();
                SI.dispose();
            } else if(buttonNumber == 2) {
                deltaX = 10;
                GI.restart();
                SI.dispose();
            } else if(buttonNumber == 3) {
                update = true;
                repaint();
                new HelpUI();
            }
        } else {
            icon = new ImageIcon(GraphicsLoader.loadImage(path_one, 65, 65));
            icon.paintIcon(this, g, (int)deltaX, 9);
            if(deltaX == 10) {
                update = false;reverse = false;
            }
        }

        frames++;
        if(System.currentTimeMillis() - lastCheck >= 1000) {
            lastCheck = System.currentTimeMillis();
            System.out.println("FPS: " + frames);
            frames = 0;
        }

        if(update) {
            repaint();
        }
    }

    public void updateIcon() {
        if(reverse && deltaX != 0) {
            deltaX -= updateValue;
        } else if (!reverse && deltaX < 355){
            deltaX += updateValue;
        }
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
