package com.loaders;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GraphicsLoader {

    public static Image loadImage(String path, int width, int height) {
        Image image = null;
        try {
            InputStream input = GraphicsLoader.class.getResourceAsStream(path);
            assert input != null;
            
            BufferedImage bi = ImageIO.read(input);
            
            ImageIcon icon = new ImageIcon(bi);
            image = icon.getImage().getScaledInstance(width, height,Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return image;
    }
}
