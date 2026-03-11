/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import game.panel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A stone riddle tablet placed on the exterior map.
 * The player walks up and presses E to open the riddle UI.
 *
 * Visual is drawn programmatically — swap image for a real sprite later.
 * riddleIndex links this object to RiddleManager.riddles[riddleIndex].
 */
public class ObjRiddle extends GameObject {

    public int riddleIndex;

    public ObjRiddle(panel gp, int riddleIndex) {
        this.riddleIndex = riddleIndex;

        collision = true;
        solidArea = new Rectangle(4, 22, 32, 20);

        buildImage();
        generateNightImage();
    }

    // ---------------------------------------------------------------
    // Programmatic sprite — replace with sheet.getSubimage() later
    // ---------------------------------------------------------------
    private void buildImage() {
        int w = 40, h = 52;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Stone body
        Color stone  = new Color(105, 95, 82);
        Color border = new Color(68, 60, 52);

        g2.setColor(stone);
        g2.fillRoundRect(3, 14, 34, 34, 7, 7);

        // Pointed top
        int[] px = {20, 5,  35};
        int[] py = {1,  18, 18};
        g2.fillPolygon(px, py, 3);

        // Border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(3, 14, 34, 34, 7, 7);

        // Rune glyph — "?" placeholder
        g2.setColor(new Color(210, 190, 145));
        g2.setFont(new Font("Serif", Font.BOLD, 18));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int gx = w / 2 - g2.getFontMetrics().stringWidth("?") / 2;
        g2.drawString("?", gx, 40);

        g2.dispose();
        image = img;
    }
}
