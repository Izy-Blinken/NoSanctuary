package game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class LoseScreen {

    private final panel gp;
    private Font imFellBase = null;
    private BufferedImage bg = null;

    private float alpha = 0f;
    private boolean fadeComplete = false;
    private static final float FADE_SPEED = 0.008f;

    public String causeOfDeath = "hp";

    public LoseScreen(panel gp) {
        
        this.gp = gp;
        
        try {
            
            InputStream is = getClass().getResourceAsStream("/assets/game_ui/winloseBG/loseBG.png");
            if (is != null) bg = ImageIO.read(is);
            
        } catch (IOException e) {
            bg = null;
        }
    }

    public void reset() {
        alpha = 0f;
        fadeComplete = false;
    }

    private Font getImFell(float size) {
        
        if (imFellBase == null) {
            
            try {
                
                InputStream is = getClass().getResourceAsStream("/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
                if (is != null) imFellBase = Font.createFont(Font.TRUETYPE_FONT, is);
                
            } catch (FontFormatException | IOException e) {
                imFellBase = new Font("Serif", Font.PLAIN, 12);
            }
        }
        
        return (imFellBase != null) ? imFellBase.deriveFont(size) : new Font("Serif", Font.PLAIN, (int) size);
    }

    public void update() {
        
        if (alpha < 1f) {
            
            alpha = Math.min(1f, alpha + FADE_SPEED);
            
        } else {
            
            fadeComplete = true;
        }
    }

    public void draw(Graphics2D g2) {
        
        int w = gp.screenWidth;
        int h = gp.screenheight;
        int cx = w / 2;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Background image
        if (bg != null) {
            g2.drawImage(bg, 0, 0, w, h, null);
        } else {
            g2.setColor(new Color(10, 5, 4));
            g2.fillRect(0, 0, w, h);
        }

        // Title
        g2.setFont(getImFell(62f));
        g2.setColor(new Color(160, 60, 55));
        
        String title = "GAME OVER";
        
        int titleW = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, cx - titleW / 2, h / 2 - 30);

        // kemeng msg
        g2.setFont(getImFell(15f));
        g2.setColor(new Color(110, 75, 45));
        
        String cause = causeOfDeath.equals("time")
                ? "Some sanctuaries are not meant to be escaped."
                : "No more hiding..";
        
        int causeW = g2.getFontMetrics().stringWidth(cause);
        g2.drawString(cause, cx - causeW / 2, h / 2 + 18);



        // Button
        if (fadeComplete) {
            drawButton(g2, "Return to Menu", cx, h / 2 + 108);
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2.setStroke(new BasicStroke(1f));
    }

    public boolean handleClick(int mx, int my) {
        
        if (!fadeComplete) {
            return false;
        }
        
        int cx = gp.screenWidth / 2;
        int cy = gp.screenheight / 2 + 108;
        
        return mx >= cx - 80 && mx <= cx + 80 && my >= cy - 16 && my <= cy + 6;
    }

    private void drawButton(Graphics2D g2, String label, int cx, int cy) {
        
        g2.setFont(getImFell(15f));
        
        int labelW = g2.getFontMetrics().stringWidth(label);
        int bx = cx - labelW / 2;

        g2.setColor(new Color(160, 60, 55));
        g2.drawString(label, bx, cy);
        
        g2.setColor(new Color(160, 60, 55));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(bx, cy + 3, bx + labelW, cy + 3);
    }
}