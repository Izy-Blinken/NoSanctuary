package models;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Base class for all game objects with improved collision
 */
public class GameObject {
    public BufferedImage image;
    public BufferedImage nightImage;
    public int worldX, worldY;
    public boolean collision = false;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // Default size

    public boolean collected = false;

    /**
     * Draw the object - override in subclasses for custom drawing
     */
    public void draw(Graphics2D g2, int screenX, int screenY) {
        if (image != null) {
            g2.drawImage(image, screenX, screenY, null);
        }
    }

    /**
     * Toggle door state - for door objects
     */
    public void toggleDoor() {
        // Override in door objects
    }

    /**
     * Generate night version of the sprite
     */
    protected void generateNightImage() {
        if (image == null) return;

        int w = image.getWidth();
        int h = image.getHeight();

        nightImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = nightImage.createGraphics();

        g2.drawImage(image, 0, 0, null);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f));
        g2.setColor(new Color(0, 0, 30));
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }

    /**
     * Check if this object collides with entity at given position
     */
    public boolean collidesWith(Entity entity, int entityX, int entityY) {
        if (!collision || solidArea == null) return false;
        
        int objLeft = worldX + solidArea.x;
        int objRight = worldX + solidArea.x + solidArea.width;
        int objTop = worldY + solidArea.y;
        int objBottom = worldY + solidArea.y + solidArea.height;
        
        int entLeft = entityX + entity.solidArea.x;
        int entRight = entityX + entity.solidArea.x + entity.solidArea.width;
        int entTop = entityY + entity.solidArea.y;
        int entBottom = entityY + entity.solidArea.y + entity.solidArea.height;
        
        return entRight > objLeft && entLeft < objRight
            && entBottom > objTop && entTop < objBottom;
    }

    /**
     * Get distance to entity
     */
    public double distanceTo(Entity entity) {
        int dx = (worldX + solidArea.x + solidArea.width/2) 
               - (entity.worldX + entity.solidArea.x + entity.solidArea.width/2);
        int dy = (worldY + solidArea.y + solidArea.height/2) 
               - (entity.worldY + entity.solidArea.y + entity.solidArea.height/2);
        return Math.sqrt(dx*dx + dy*dy);
    }
}
