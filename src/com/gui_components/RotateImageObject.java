package com.gui_components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JLabel;

import com.loaders.GraphicsLoader;

public class RotateImageObject extends JLabel {
    private Image image;
    private int width;
    private int height;
    private int frames = 0;

    private float currentDegree;
    private float updateValue = 0.05f;

    private long lastCheck = 0;

    boolean rotate = false;
    boolean rotateToLeft;

    public RotateImageObject(String path, int width, int height, int currentDegree, boolean rotateToLeft) {
        this.width = width;
        this.height = height;
        this.currentDegree = currentDegree;
        this.rotateToLeft = rotateToLeft;

        image = GraphicsLoader.loadImage(path, width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        rotate();

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

    public void rotate() {
        if(rotateToLeft) {
            currentDegree -= updateValue;
        } else {
            currentDegree += updateValue;
        }
    }
}
