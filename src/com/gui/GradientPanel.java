package com.gui;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class GradientPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius = 15;
    private static final Color LUMBER = new Color(255, 229, 204);
    private static final Color PEACH_ORANGE = new Color(252, 187, 122);

    public GradientPanel(LayoutManager layout, int radius) {
        super(layout);
        cornerRadius = radius;
        setOpaque(false);
    }

    @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getBounds().width;
            int height = getBounds().height;
            
            Graphics2D g2d = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(0,0,LUMBER,180,height,PEACH_ORANGE);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //Draws the rounded panel with borders.
            if (backgroundColor != null) {
                g2d.setPaint(backgroundColor);
            } else {
                g2d.setPaint(gp);
            }
            g2d.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint background
            g2d.setColor(getForeground());
            //graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint border
        }  
}
