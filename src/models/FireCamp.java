/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ADMIN
 */
public class FireCamp extends GameObject{
   public FireCamp(panel gp, double scale) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));

            BufferedImage rawImage = sheet.getSubimage(853, 152, 93, 77); // fiecamp

            int newWidth = (int)(rawImage.getWidth() * scale);
            int newHeight = (int)(rawImage.getHeight() * scale);

            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.drawImage(rawImage, 0, 0, newWidth, newHeight, null);
            g2.dispose();

            
            collision = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
