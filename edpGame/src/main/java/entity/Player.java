package entity;

import com.mycompany.edpgame.panel;
import com.mycompany.edpgame.KeyHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends Entity {

    panel gp;
    KeyHandler keyH;

    public Player(panel gp, KeyHandler KeyH) {

        this.gp = gp;
        this.keyH = KeyH;
        setDefaultValues();
        getPlayerImage();

    }

    public void setDefaultValues() {

        x = 100;
        y = 100;
        speed = 4;
        direction = "down";

        screenX = gp.screenWidth / 2 - gp.tileSize / 2;
        screenY = gp.screenheight / 2 - gp.tileSize / 2;

    }

    public void getPlayerImage() {

        try {

            down1 = ImageIO.read(getClass().getResourceAsStream("/player/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/right2.png"));
            top1 = ImageIO.read(getClass().getResourceAsStream("/player/top1.png"));
            top2 = ImageIO.read(getClass().getResourceAsStream("/player/top2.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void update() {

        if (keyH.upPressed || keyH.downPressed || keyH.rightPressed || keyH.leftPressed) {

            if (keyH.upPressed) {
                direction = "up";
                y -= speed;
            } else if (keyH.downPressed) {
                direction = "down";
                y += speed;
            } else if (keyH.rightPressed) {
                direction = "right";
                x += speed;
            } else if (keyH.leftPressed) {
                direction = "left";
                x -= speed;
            }

            spriteCounter++;
            if (spriteCounter > 7) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
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

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

    }

}
