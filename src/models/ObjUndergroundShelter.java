/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import game.panel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ADMIN
 */
public class ObjUndergroundShelter extends GameObject {
    
    panel gp;
    
    public boolean discovered = false;
    public boolean used = false;
    
    public ObjUndergroundShelter(panel gp) {
        this.gp = gp;
        
        
        try{
            
            BufferedImage shelter = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/undergroundShelter.png"));
            image = scale(shelter.getSubimage(11, 51, 40, 22), 2.0);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private BufferedImage scale(BufferedImage src, double scale) {
        
        int w = (int)(src.getWidth() * scale);
        int h = (int)(src.getHeight() * scale);
        
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        
        return scaled;
    }
        
        
    public void draw(Graphics2D g2, int screenX, int screenY) {
        
        g2.drawImage(image, screenX, screenY, null);
        
        if(discovered){
            
            g2.setFont(new Font("Serif", Font.PLAIN, 11));
            g2.setColor(new Color(200, 0, 0));
            g2.drawString("Already Used", screenX - 10, screenY - 6);
        }
    }
        
        
        
    
}
