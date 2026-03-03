package models;

import game.panel;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

// yung literal na apple na nasa lupa, malapit sa apple trees
public class ObjAppleItem extends GameObject {

    private static BufferedImage sheet;

    public ObjAppleItem(panel gp) {
        try {
            if (sheet == null)
                sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));

            image = sheet.getSubimage(900, 298, 22, 26); // apple sprite

            collision = false; // walang collision, lalakad lang player dito
            solidArea = new Rectangle(0, 0, 22, 26);

            generateNightImage(); // dark version pag gabi

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}