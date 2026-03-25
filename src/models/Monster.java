package models;

import game.panel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Monster extends Entity {

    public enum State { 
        IDLE, CHASING, ATTACKING, KNOCKING 
    }
    
    public State state = State.IDLE;

    private static final int DIRS = 4;
    private static final int DIR_UP = 0;
    private static final int DIR_LEFT = 1;
    private static final int DIR_DOWN = 2;
    private static final int DIR_RIGHT = 3;
    private int currentDir = DIR_DOWN;

    private static final int WALK_FRAMES = 10;
    private static final int WALK_SPEED = 10;
    private BufferedImage[][] walkFrames = new BufferedImage[DIRS][WALK_FRAMES];

    private static final int SLASH_FRAMES = 6;
    private static final int SLASH_SPEED = 5;
    private BufferedImage[][] slashFrames = new BufferedImage[DIRS][SLASH_FRAMES];

    private int animCounter = 0;
    private int animFrame = 0;

    private static final int KNOCK_INTERVAL = 90;
    private static final int ACTION_DISPLAY = 60;
    private int knockTimer = 0;
    public int actionTimer = 0;

    private int damageCooldown = 0;

    private static final double ATTACK_RANGE = 40;
    private static final double KNOCK_RANGE = 60;

    private final panel gp;

    public boolean forceEnter = false;
    public boolean isEnraged = false;

    public Monster(panel gp) {
        
        this.gp = gp;
        speed = 4;
        solidArea = new Rectangle(8, 16, 32, 32);
        worldX = -1000;
        worldY = -1000;
        loadSprites();
    }

    private void loadSprites() {
        
        try {
            
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/Charac/edp character/monster5_universal.png"));

            int[] walkRows = { 8, 9, 10, 11 };
            int[] slashRows = { 12, 13, 14, 15 };

            for (int d = 0; d < DIRS; d++) {
                
                for (int f = 0; f < WALK_FRAMES; f++) {
                    walkFrames[d][f] = sheet.getSubimage(f * 64, walkRows[d] * 64, 64, 64);
                }
                
                for (int f = 0; f < SLASH_FRAMES; f++) {
                    slashFrames[d][f] = sheet.getSubimage(f * 64, slashRows[d] * 64, 64, 64);
                }
            }
            
        } catch (IOException | IllegalArgumentException e) {
            
            System.err.println("Monster sprite load failed: " + e.getMessage());
            
            for (int d = 0; d < DIRS; d++) {
                
                for (int f = 0; f < WALK_FRAMES; f++)   { 
                    walkFrames[d][f] = blankFrame();
                }
                
                for (int f = 0; f < SLASH_FRAMES; f++){
                    slashFrames[d][f] = blankFrame();
                }
            }
        }
    }

    private BufferedImage blankFrame() {
        return new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    }

    public void spawnNearEdge() {
        
        worldX = 200;
        worldY = gp.player.worldY;
        state = State.CHASING;
        animFrame = 0;
        animCounter = 0;
    }

    public void update() {

        if (damageCooldown > 0) {
            damageCooldown--;
        }

        if (actionTimer > 0) { 
            actionTimer--;
        }

        boolean isNight = (gp.dC.currentState == game.dayCounter.dayNightState.Night);

        if (!isNight && !isEnraged && !forceEnter) {
            state = State.IDLE;
            worldX = -1000;
            worldY = -1000;
            
            return;
        }

        boolean playerSafe = gp.isPlayerSafe();
        boolean playerOutside = (gp.tileM.currentMap == 1);

        double distToPlayer = Double.MAX_VALUE;
        
        if (playerOutside) {
            
            int dx = gp.player.worldX - worldX;
            int dy = gp.player.worldY - worldY;
            
            distToPlayer = Math.sqrt(dx * dx + dy * dy);
        }

        int houseX = (gp.objectM.ObjHouse[0] != null) ? gp.objectM.ObjHouse[0].worldX + 75 : 1375;
        int houseY = (gp.objectM.ObjHouse[0] != null) ? gp.objectM.ObjHouse[0].worldY + 200 : 1050;
        double distToHouse = Math.sqrt(Math.pow(houseX - worldX, 2) + Math.pow(houseY - worldY, 2));
        
        if ((!playerOutside && !playerSafe || !playerOutside && gp.hasOpenEntry() && distToHouse < KNOCK_RANGE) && !forceEnter && gp.interiorGraceTimer <= 0) {
            forceEnter = true;
            spawnInsideHouse();
        }

        if (playerOutside && !playerSafe) {
            
            if (distToPlayer < ATTACK_RANGE) {
                
                if (state != State.ATTACKING) {
                    
                    state = State.ATTACKING;
                    animFrame = 0;
                    animCounter = 0;
                    actionTimer = ACTION_DISPLAY;
                }
                faceToward(gp.player.worldX, gp.player.worldY);
                
            } else {
                state = State.CHASING;
                moveToward(gp.player.worldX, gp.player.worldY);
            }
            
        } else if (forceEnter) {

            double dx = gp.player.worldX - worldX;
            double dy = gp.player.worldY - worldY;
            distToPlayer = Math.sqrt(dx * dx + dy * dy);

            if (distToPlayer < ATTACK_RANGE) {

                if (state != State.ATTACKING) {

                    state = State.ATTACKING;
                    animFrame = 0;
                    animCounter = 0;
                    actionTimer = ACTION_DISPLAY;
                }
                faceToward(gp.player.worldX, gp.player.worldY);

            } else {
                state = State.CHASING;
                moveToward(gp.player.worldX, gp.player.worldY);
            }
            
        } else {
            
            if (distToHouse < KNOCK_RANGE) {
                
                state = State.KNOCKING;
                faceToward(houseX, houseY);
                knockTimer++;
                
                if (knockTimer >= KNOCK_INTERVAL) {
                    
                    knockTimer = 0;
                    actionTimer = ACTION_DISPLAY;
                }
                
            } else {
                state = State.CHASING;
                moveToward(houseX, houseY);
            }
        }

        if (state == State.CHASING) {
            
            animCounter++;
            
            if (animCounter >= WALK_SPEED) {
                
                animCounter = 0;
                animFrame = (animFrame + 1) % WALK_FRAMES;
            }
            
        } else if (state == State.ATTACKING) {
            
            if (damageCooldown == 0) {
                
                int damage = isEnraged ? 100 : (forceEnter ? 30 : 30);
                
                gp.player.takeDamage(damage);
                damageCooldown = 60;
            }
            
        } else {
            animFrame = 0;
            animCounter = 0;
        }
    }

    private void faceToward(int tx, int ty) {
        
        int dx = tx - worldX, dy = ty - worldY;
        
        currentDir = (Math.abs(dx) > Math.abs(dy))
                   ? (dx > 0 ? DIR_RIGHT : DIR_LEFT)
                   : (dy > 0 ? DIR_DOWN : DIR_UP);
    }

    private void moveToward(int tx, int ty) {
        
        int dx = tx - worldX, dy = ty - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist < 1) { 
            return;
        }
        
        int currentSpeed = isEnraged ? speed * 2 : speed;
        
        worldX += (int)(dx / dist * currentSpeed);
        worldY += (int)(dy / dist * currentSpeed);
        faceToward(tx, ty);
    }

    public void draw(Graphics2D g2) {
        
        if (state == State.IDLE) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX < -96 || screenX > gp.screenWidth + 96 || screenY < -96 || screenY > gp.screenheight + 96){
            
            return;
        }

        BufferedImage frame;
        
        if (state == State.ATTACKING) {
            
            int safeFrame = Math.min(animFrame, SLASH_FRAMES - 1);
            frame = slashFrames[currentDir][safeFrame];
            
        } else {
            frame = walkFrames[currentDir][animFrame];
        }

        g2.drawImage(frame, screenX, screenY, 64, 64, null);

    }

    public void spawnInsideHouse() {
        
        gp.forcedEntry.play();
        worldX = 160;
        worldY = 200;
        state = State.CHASING;
    }

}