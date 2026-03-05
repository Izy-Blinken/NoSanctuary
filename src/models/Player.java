package models;
import game.panel;
import game.KeyHandler;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends Entity {
    panel gp;
    KeyHandler keyH;
    
    // gaano kalapit bago pwedeng mag collect
    private final int collectRange = 50;

    public Player(panel gp, KeyHandler KeyH) {
        this.gp = gp;
        this.keyH = KeyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 30;
        worldY = gp.tileSize * 22;
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
                if (!collisionOn){
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.objects);
                }
                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.objAppleTree);
                }
                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjVehicle);
                }
                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjHouse);
                }
                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjWoods);
                }
                if (!collisionOn) {
                    collisionOn = gp.cChecker.checkObject(this, gp.objectM.FireCamp);
                }
                if (!collisionOn) collisionOn = gp.cChecker.checkObject(this, gp.objectM.ObjPineTree);
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
        }

        // collect pag E pressed - daytime lang
        if (keyH.ePressed) {
            boolean isDay = gp.dC.currentState == game.dayCounter.dayNightState.Day;
            if (isDay) {
                collectNearbyItems();
            }
            keyH.ePressed = false; // reset para hindi paulit ulit
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
                    System.out.println("Apple picked up!");
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
                    System.out.println("Wood picked up!");
                }
                break; // isa lang per press
            }
        }
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
        // Debug: show player world position
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString("X: " + worldX + "  Y: " + worldY, 10, 20);
        g2.drawString("Map: " + gp.tileM.currentMap, 10, 38);
    }
}
