package models;

import game.panel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ObjTorch extends GameObject {

    panel gp;

    public boolean isLit = false;
    public int woodFuel = 0;

    public static final int WOOD_BATCH = 3;
    public static final int SECONDS_PER_WOOD = 20;
    public static final int FPS = 60;

    private int timerTicks = 0;
    private int maxTicks = 0;
    private boolean warningActive = false;
    private int warningFlashTick  = 0;

    private BufferedImage frameRight;
    private BufferedImage frameLeft;
    private BufferedImage frameSteady;
    private BufferedImage frameUnlit;

    private int animTick = 0;
    private int animFrame = 0;

    public ObjTorch(panel gp) {
        
        this.gp = gp;
        collision = true;
        solidArea = new Rectangle(8, 20, 24, 20);
        loadSprites();
        image = frameUnlit;
    }

    private BufferedImage scaleImg(BufferedImage src, double scale) {
        
        int w = (int)(src.getWidth() * scale);
        int h = (int)(src.getHeight() * scale);
        
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        
        return out;
    }

    private void loadSprites() {
        
        try {
            
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/Torch.png"));

            double scale = 1;
            
            frameRight = scaleImg(sheet.getSubimage(24, 10, 17, 45), scale);
            frameLeft = scaleImg(sheet.getSubimage(148, 72, 18, 47), scale);
            frameSteady = scaleImg(sheet.getSubimage(215,  8, 18, 47), scale);
            frameUnlit = darken(frameSteady);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* to darken sprites kapag hindi lit ang torch */
    private BufferedImage darken(BufferedImage src) {
    
        int w = src.getWidth(), h = src.getHeight();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            
            for (int x = 0; x < w; x++) {

                int argb = src.getRGB(x, y);
                int alpha = (argb >> 24) & 0xff;

                if (alpha == 0) {
                    continue;
                }

                int r = (int)(((argb >> 16) & 0xff) * 0.3);
                int g = (int)(((argb >> 8) & 0xff) * 0.3);
                int b = (int)(((argb) & 0xff) * 0.3);

                out.setRGB(x, y, (alpha << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return out;
    }

    /* animation */
    private BufferedImage getAnimFrame() {
        
        switch (animFrame % 4) {
            
            case 0:  
                return frameRight;
                
            case 1:  
                return frameSteady;
                
            case 2:  
                return frameLeft;
                
            default: 
                return frameSteady;
        }
    }

    public void update() {
        
        if (!isLit) {
            
            image = frameUnlit; 
            return; 
        }

        animTick++;
        
        if (animTick >= 12) {
            
            animTick = 0; animFrame++; 
        }
        
        image = getAnimFrame();

        timerTicks--;
        warningActive = timerTicks > 0 && timerTicks <= (15 * FPS);
        
        if (warningActive) {
            warningFlashTick++;
        }

        if (timerTicks <= 0) {
            
            isLit = false;
            woodFuel = 0;
            timerTicks = 0;
            warningActive = false;
            image = frameUnlit;
            gp.torchOut.play();
        }
    }

    /* 3 woods from inventory = 90 secs */
    public boolean refuel(game.Inventory inv) {
        
        if (inv.wood < WOOD_BATCH) {
            return false;
        }
        
        inv.wood -= WOOD_BATCH;
        woodFuel += WOOD_BATCH;
        timerTicks += WOOD_BATCH * SECONDS_PER_WOOD * FPS;
        maxTicks = timerTicks;
        isLit = true;
        warningActive = false;
        
        return true;
    }

    public int getSecondsLeft() { 
        
        return timerTicks / FPS; 
    }
    
    public float getFuelPercent() { 
        
        return maxTicks <= 0 ? 0f : Math.max(0f, (float) timerTicks / maxTicks);
    }
    
    public boolean isWarning() {
        
        return warningActive; 
    }
    
    public boolean getWarningFlash() { 
        
        return (warningFlashTick / 20) % 2 == 0; 
    }

    public void draw(Graphics2D g2, int screenX, int screenY) {
        
        g2.drawImage(image, screenX, screenY, null);
        
        if (isLit) {
            
            drawGlow(g2, screenX + image.getWidth() / 2, screenY + 10);
            drawFuelBar(g2, screenX, screenY);
        }
    }

    /* glow sa torch kapag may fire */
    private void drawGlow(Graphics2D g2, int cx, int cy) {
        
        int[] radii = {70, 50, 35, 20};
        int[] alphas = {15, 28, 45, 65};
        
        for (int i = 0; i < radii.length; i++) {
            
            int r = radii[i];
            g2.setColor(new Color(255, 140, 20, alphas[i]));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        }
    }

    /* fuel progress bar ng torch */
    private void drawFuelBar(Graphics2D g2, int screenX, int screenY) {

        int barW = 44, barH = 5;
        int barX = screenX - 2, barY = screenY - 12;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(barX - 1, barY - 1, barW + 2, barH + 2, 3, 3);

        float pct = getFuelPercent();
        
        Color barColor = pct > 0.5f ? new Color(50, 200, 50)
                       : pct > 0.25f ? new Color(220, 180, 0)
                       : new Color(220, 50, 50);
        
        if (warningActive && !getWarningFlash()) barColor = new Color(255, 80, 80);

        g2.setColor(barColor);
        g2.fillRoundRect(barX, barY, (int)(barW * pct), barH, 3, 3);

        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.setColor(Color.WHITE);
        g2.drawString(getSecondsLeft() + "s", barX + barW + 4, barY + barH);
    }
}
