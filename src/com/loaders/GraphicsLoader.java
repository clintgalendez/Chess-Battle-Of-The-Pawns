package com.loaders;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GraphicsLoader {

    public static Image loadImage(String path, int width, int height) {
        Image image = null;
            
        BufferedImage bi;
        try {
            bi = ImageIO.read(ResourceLoader.load(path));
            ImageIcon icon = new ImageIcon(bi);
            image = icon.getImage().getScaledInstance(width, height,Image.SCALE_SMOOTH);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return image;
    }
}
