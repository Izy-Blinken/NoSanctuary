package models;

import game.panel;
import game.KeyHandler;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends Entity {
    
    panel gp;
    KeyHandler keyH;
    
    public int maxHP = 100;
    public int hp = 100;
    public int damageFlash = 0;
    private static final int DAMAGE_FLASH_DURATION = 20;
    
    // gaano kalapit bago pwedeng mag collect
    private final int collectRange = 50;
    
    public int heartbeatTimer = 0;

    public Player(panel gp, KeyHandler KeyH) {
        this.gp = gp;
        this.keyH = KeyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = 784;
        worldY = 744;
        speed = 4;
        direction = "down";
        screenX = gp.screenWidth / 2 - gp.tileSize / 2;
        screenY = gp.screenheight / 2 - gp.tileSize / 2;
        solidArea = new Rectangle(8, 16, 16, 16);
    }

    public void getPlayerImage() {
        
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/right2.png"));
            top1 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/top1.png"));
            top2 = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/player/top2.png"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        
        if (heartbeatTimer > 0) {
            heartbeatTimer--;
        }
        
        if (hp <= 40) {
            
            if (!gp.heartbeat.isRunning()) {
                gp.heartbeat.loop();
            }
            
        } else if (heartbeatTimer > 0) {
            
            if (!gp.heartbeat.isRunning()) {
                gp.heartbeat.loop();
            }
            
        } else {
            gp.heartbeat.stop();
        }
        
        if (keyH.upPressed || keyH.downPressed || keyH.rightPressed || keyH.leftPressed) {
            
            if (keyH.upPressed) {
                direction = "up";
                
            } else if (keyH.downPressed) {
                
                direction = "down";
              
            } else if (keyH.rightPressed) {
                direction = "right";
                
            } else if (keyH.leftPressed) {
                direction = "left";
            }
            
            boolean collisionOn = gp.cChecker.checkTile(this);
            
            if (gp.tileM.currentMap == 1) {
                
                
                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.objects);
                }

                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.objAppleTree);
                }

                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjHouse);
                }

                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.extObjects);
                }
                
            }
            
            if (!gp.playerFootsteps.isRunning()) {
                gp.playerFootsteps.loop();
            }

            if (!collisionOn) {
                
                if (keyH.upPressed) {
                    worldY -= speed;
                    
                } else if (keyH.downPressed) {
                    worldY += speed;
                    
                } else if (keyH.rightPressed) {
                    worldX += speed;
                    
                } else if (keyH.leftPressed) {
                    worldX -= speed;
                }
            }
            
            spriteCounter++;
            
            if (spriteCounter > 7) {
                
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
            
        }else{
            gp.playerFootsteps.stop();
        }
        

        // collect pag E pressed
        if (keyH.ePressed) {
            
            collectNearbyItems();
            keyH.ePressed = false;
        }
    }
    

    // check kung malapit sa apple item o wood item tapos kuha
    private void collectNearbyItems() {

        // check apple items na nasa lupa
        for (int i = 0; i < gp.objectM.appleItems.length; i++) {
            
            if (gp.objectM.appleItems[i] == null) continue;
            if (gp.objectM.appleItems[i].collected) continue;

            int dx = Math.abs(worldX - gp.objectM.appleItems[i].worldX);
            int dy = Math.abs(worldY - gp.objectM.appleItems[i].worldY);

            if (dx <= collectRange && dy <= collectRange) {
                
                boolean added = gp.inventory.addApple();
                
                if (added) {
                    
                    gp.objectM.appleItems[i].collected = true;
                }
                
                break; // isa lang per press
            }
        }

        // check wood items na nasa lupa
        for (int i = 0; i < gp.objectM.woodItems.length; i++) {
            
            if (gp.objectM.woodItems[i] == null) continue;
            if (gp.objectM.woodItems[i].collected) continue;

            int dx = Math.abs(worldX - gp.objectM.woodItems[i].worldX);
            int dy = Math.abs(worldY - gp.objectM.woodItems[i].worldY);

            if (dx <= collectRange && dy <= collectRange) {
                
                boolean added = gp.inventory.addWood();
                
                if (added) {
                    
                    gp.objectM.woodItems[i].collected = true;
                }
                break; // isa lang per press
            }
        }
    }
    
    public void triggerDamage() {
        damageFlash = DAMAGE_FLASH_DURATION;
    }
    

public void takeDamage(int amount) {
    
    hp -= amount;

    if (hp < 0) {
        hp = 0;
        
    }
    
    triggerDamage();
    
    if (hp == 0) {
        
        gp.heartbeat.stop();
        heartbeatTimer = 0;
        
        return;
    }
    
    gp.monsterAttack.play();
    gp.heartbeat.loop(); 
    heartbeatTimer = 300;
}

    public void draw(Graphics2D g2) {
        
        BufferedImage image = null;
        
        switch (direction) {
            
            case "up":
                image = (spriteNum == 1) ? top1 : top2;
                break;
                
            case "down":
                image = (spriteNum == 1) ? down1 : down2;
                break;
                
            case "right":
                image = (spriteNum == 1) ? right1 : right2;
                break;
                
            case "left":
                image = (spriteNum == 1) ? left1 : left2;
                break;
        }
        
        g2.drawImage(image, screenX + (gp.tileSize - 32) / 2, screenY + (gp.tileSize - 48) / 2, 32, 48, null);
        
        String pos = "X: " + worldX + "  Y: " + worldY;
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        int textWidth = g2.getFontMetrics().stringWidth(pos);
        int boxX = gp.screenWidth / 2 - textWidth / 2 - 8;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(boxX, gp.screenheight - 55, textWidth + 16, 25);
        g2.setColor(Color.YELLOW);
        g2.drawString(pos, boxX + 8, gp.screenheight - 37);
    }
}