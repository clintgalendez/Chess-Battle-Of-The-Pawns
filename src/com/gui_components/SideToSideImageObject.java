package com.gui_components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JLabel;

import com.loaders.GraphicsLoader;

public class SideToSideImageObject extends JLabel {
    private Image image;
    private int width;
    private int height;
    private int frames = 0;

    private float temp;
    private float currentDegree;
    private float changedDegree;
    private float updateValue = 0.03f;

    private long lastCheck = 0;

    boolean rotate = false;
    boolean moveToLeft;

    public SideToSideImageObject(String path, int width, int height, int currentDegree, int changedDegree, boolean moveToLeft) {
        this.width = width;
        this.height = height;
        this.currentDegree = currentDegree;
        this.changedDegree = changedDegree;
        this.moveToLeft = moveToLeft;

        temp = currentDegree;

        image = GraphicsLoader.loadImage(path, width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        moveSideToSide();

        g2d.rotate(Math.toRadians((int) currentDegree), width/2, height/2);
        
        g2d.drawImage(image, 0, 0, null);

        frames++;
        if(System.currentTimeMillis() - lastCheck >= 1000) {
            lastCheck = System.currentTimeMillis();
            System.out.println("FPS: " + frames);
            frames = 0;
        }

        repaint();
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public void moveSideToSide() {
        if(moveToLeft) {
            if(currentDegree > changedDegree || currentDegree > temp) {
                currentDegree -= updateValue;
            } else {
                moveToLeft = false;
            }
        } else {
            if(currentDegree < changedDegree || currentDegree < temp) {
                currentDegree += updateValue;
            } else {
                moveToLeft = true;
            }
        }
    }
}
