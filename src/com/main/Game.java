package com.main;

import java.awt.Canvas;
import java.awt.Graphics;
import java.net.ServerSocket;

import com.gui.UI;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    
    public boolean running;

    public static BufferedImage background;

    public Thread thread;
    public ServerSocket serverSocket;

    public UI ui;
    
    public static void main(String[] args) {
        new GameWindow(WIDTH, HEIGHT, "Masterchess: Battle of the Pawns");
    }

    public synchronized void start() {
        running = true;
        thread = new Thread();
        thread.start();
        run();
    }

    public void init() {
    }

    public void tick() {

    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if(bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.dispose();
        bs.show();
    }

    @Override
    public void run() {
        init();
        this.requestFocus();

        long pastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        while(running) {
            long now = System.nanoTime();
            delta += (now - pastTime) / ns;
            pastTime = now;
            
            while(delta > 0) {
                tick();
                updates++;

                render();
                frames++;

                delta--;
            }

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames + " | TICKS: " + updates);
                updates = 0;
                frames = 0;
            }
        }
    }
}
