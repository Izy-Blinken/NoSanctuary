/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author ADMIN
 */
public class GameObject {
    public BufferedImage image;
    public int worldX, worldY;
    public boolean collision = false;
    public Rectangle solidArea;

    public void draw(Graphics2D g2, int screenX, int screenY) {
    }

    public void toggleDoor() {
    }

    
}
