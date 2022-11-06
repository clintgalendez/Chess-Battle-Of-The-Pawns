package com.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;


public class Time extends JPanel {
    JLabel timeLabel = new JLabel();
    int elapsedTime = 0;
    int seconds =0;
    int minutes =0;
    int hours =0;
    boolean started = false;
    String seconds_string = String.format("%02d", seconds);
    String minutes_string = String.format("%02d", minutes);
    String hours_string = String.format("%02d", hours);
    Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            elapsedTime=elapsedTime+1000;
            hours = (elapsedTime/3600000);
            minutes = (elapsedTime/60000) % 60;
            seconds = (elapsedTime/1000) % 60;
            seconds_string = String.format("%02d", seconds);
            minutes_string = String.format("%02d", minutes);
            hours_string = String.format("%02d", hours);
            timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string); 
        }
    });

    private static final Color LUMBER = new Color(255, 229, 204);
    private static final Color PEACH_ORANGE = new Color(252, 187, 122);

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = 90;
        int height = 30;

        GradientPaint gp = new GradientPaint(0,0,LUMBER,180,height,PEACH_ORANGE);
        g2d.setPaint(gp);
        g2d.fillRect(0,0,width,height);
    }

    Time() {
        setLayout(new BorderLayout());

        timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string);
        timeLabel.setBounds(0,0,30,10);
        timeLabel.setFont(new Font("Verdana",Font.PLAIN,15));
        timeLabel.setForeground(Color.BLACK);
        timeLabel.setBorder(BorderFactory.createBevelBorder(1));
        timeLabel.setHorizontalAlignment(JTextField.CENTER);

        add(timeLabel, BorderLayout.CENTER);
    }
}