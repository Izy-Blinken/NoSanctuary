package models;

import game.panel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjStone extends GameObject {

    public ObjStone(panel gp, double scale) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/stone.png"));
            BufferedImage raw = sheet.getSubimage(387, 288, 285, 364);

            int w = (int)(raw.getWidth() * scale);
            int h = (int)(raw.getHeight() * scale);

            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = scaled.createGraphics();
            g2.drawImage(raw, 0, 0, w, h, null);
            g2.dispose();

            image = tintImage(scaled, new java.awt.Color(20, 25, 35), 0.25f);
            collision = false;
            generateNightImage();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage tintImage(BufferedImage original, java.awt.Color color, float alpha) {
        
        BufferedImage tinted = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = tinted.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        g2d.setComposite(java.awt.AlphaComposite.SrcAtop.derive(alpha));
        g2d.setColor(color);
        g2d.fillRect(0, 0, original.getWidth(), original.getHeight());
        g2d.dispose();
        
        return tinted;
    }
}