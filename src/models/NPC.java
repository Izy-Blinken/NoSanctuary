package models;

import game.dayCounter;
import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NPC extends Entity {

    public enum State {
        INACTIVE,
        ENTERING,   
        GATHERING,
        RETURNING,
        DEPOSITING,
        IDLE_INSIDE
    }

    public State state = State.INACTIVE;

    private static final int DIRS        = 4;
    private static final int DIR_UP      = 0;
    private static final int DIR_LEFT    = 1;
    private static final int DIR_DOWN    = 2;
    private static final int DIR_RIGHT   = 3;

    private static final int WALK_FRAMES = 10;
    private static final int WALK_SPEED  = 8;  
    private int currentDir  = DIR_DOWN;
    private int animCounter = 0;
    private int animFrame   = 0;


    private final BufferedImage[][] walkFrames     = new BufferedImage[DIRS][WALK_FRAMES];

    private final BufferedImage[][] walkFramesWLog = new BufferedImage[DIRS][WALK_FRAMES];

    private boolean isCarrying = false;


    private static final double PICK_RANGE       = 48.0;  
    private static final double DOOR_RANGE       = 80.0;  
    private static final double TABLE_RANGE      = 80.0; 
    private static final int    CARRY_THRESHOLD  = 10;   
    private static final int    COLLECT_COOLDOWN = 30;    


    private int collectCooldown = 0;
    private int heldWood  = 0;
    private int heldApple = 0;

    private int targetAppleIdx = -1;  
    private int targetWoodIdx  = -1;  

    public boolean NPC_is_Inside = false;

    private boolean hasSpentNight = false;

    private static final int HOUSE_DOOR_X = 644 + 32;
    private static final int HOUSE_DOOR_Y = 530 + 48;

    private static final int TABLE_X = 826 + 16;
    private static final int TABLE_Y = 228 + 16;


    private static final int IDLE_INSIDE_X = TABLE_X + 60;
    private static final int IDLE_INSIDE_Y = TABLE_Y + 20;

    private final panel gp;

    public NPC(panel gp) {
        this.gp = gp;
        speed     = 3;
        solidArea = new Rectangle(8, 16, 32, 32);

        worldX = -2000;
        worldY = -2000;

        loadSprites();
    }


    private void loadSprites() {
        loadSheet("/assets/Charac/edp character/human2_universal.png",     walkFrames);
        loadSheet("/assets/Charac/edp character/human2wlog_universal.png", walkFramesWLog);
    }

    private void loadSheet(String path, BufferedImage[][] target) {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream(path));
            int[] walkRows = {8, 9, 10, 11}; // up, left, down, right
            for (int d = 0; d < DIRS; d++) {
                for (int f = 0; f < WALK_FRAMES; f++) {
                    target[d][f] = sheet.getSubimage(f * 64, walkRows[d] * 64, 64, 64);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[NPC] Sprite load failed (" + path + "): " + e.getMessage());
        }
    }


    public void enterHouse() {

        worldX         = gp.player.worldX + 80;
        worldY         = gp.player.worldY;
        animFrame      = 0;
        animCounter    = 0;
        heldWood       = 0;
        heldApple      = 0;
        isCarrying     = false;
        NPC_is_Inside  = false;
        targetAppleIdx = -1;
        targetWoodIdx  = -1;
        hasSpentNight  = false;
        state          = State.ENTERING;
        System.out.println("[NPC] Spawned outside. Walking toward player then entering house.");
    }

    public void update() {

        if (state == State.INACTIVE) return;

        boolean isNight = (gp.dC.currentState == dayCounter.dayNightState.Night);

        switch (state) {
            case ENTERING:    updateEntering();           break;
            case GATHERING:   updateGathering(isNight);  break;
            case RETURNING:   updateReturning();          break;
            case DEPOSITING:  updateDepositing();         break;
            case IDLE_INSIDE: updateIdleInside(isNight);  break;
            default: break;
        }

        boolean moving = (state == State.ENTERING
                       || state == State.GATHERING
                       || state == State.RETURNING
                       || state == State.DEPOSITING);
        if (moving) {
            animCounter++;
            if (animCounter >= WALK_SPEED) {
                animCounter = 0;
                animFrame   = (animFrame + 1) % WALK_FRAMES;
            }
        } else {
            animFrame = 0;
        }
    }

    private void updateEntering() {

        isCarrying = false;

        double distToPlayer = dist(worldX, worldY, gp.player.worldX, gp.player.worldY);
        double distToDoor   = dist(worldX, worldY, HOUSE_DOOR_X, HOUSE_DOOR_Y);

        if (distToPlayer > 80) {
            moveToward(gp.player.worldX, gp.player.worldY);
        } else if (distToDoor > DOOR_RANGE) {

            moveToward(HOUSE_DOOR_X, HOUSE_DOOR_Y);
        } else {

            NPC_is_Inside = true;
            worldX = IDLE_INSIDE_X;
            worldY = IDLE_INSIDE_Y;
            state  = State.IDLE_INSIDE;
            System.out.println("[NPC] Walked in. Now resting inside until morning.");
        }
    }

    private void updateGathering(boolean isNight) {

        isCarrying = (heldWood + heldApple) > 0;


        if (isNight) {
            state = State.RETURNING;
            return;
        }


        if (heldWood + heldApple >= CARRY_THRESHOLD) {
            state = State.RETURNING;
            return;
        }

        if (collectCooldown > 0) collectCooldown--;


        if (targetAppleIdx == -1 && targetWoodIdx == -1) {
            findNearestCollectible();
        }

        if (targetAppleIdx >= 0) {
            GameObject item = gp.objectM.appleItems[targetAppleIdx];
            if (item == null || item.collected) { targetAppleIdx = -1; return; }

            if (dist(worldX, worldY, item.worldX, item.worldY) <= PICK_RANGE
                    && collectCooldown == 0) {
                item.collected = true;
                heldApple++;
                collectCooldown = COLLECT_COOLDOWN;
                targetAppleIdx  = -1;
                System.out.println("[NPC] Picked apple. Holding: wood=" + heldWood + " apple=" + heldApple);
            } else {
                moveToward(item.worldX, item.worldY);
            }

        } else if (targetWoodIdx >= 0) {
            GameObject item = gp.objectM.woodItems[targetWoodIdx];
            if (item == null || item.collected) { targetWoodIdx = -1; return; }

            if (dist(worldX, worldY, item.worldX, item.worldY) <= PICK_RANGE
                    && collectCooldown == 0) {
                item.collected = true;
                heldWood++;
                collectCooldown = COLLECT_COOLDOWN;
                targetWoodIdx   = -1;
                System.out.println("[NPC] Picked wood. Holding: wood=" + heldWood + " apple=" + heldApple);
            } else {
                moveToward(item.worldX, item.worldY);
            }
        }

    }

    private void updateReturning() {

        isCarrying = true;   

        if (dist(worldX, worldY, HOUSE_DOOR_X, HOUSE_DOOR_Y) <= DOOR_RANGE) {

            NPC_is_Inside = true;
            worldX = TABLE_X + 60;
            worldY = TABLE_Y + 60;
            state  = State.DEPOSITING;
            System.out.println("[NPC] Entered house. Heading to table.");
        } else {
            moveToward(HOUSE_DOOR_X, HOUSE_DOOR_Y);
        }
    }

    private void updateDepositing() {

        isCarrying = (heldWood + heldApple) > 0;

        if (gp.objectM.interior == null || gp.objectM.interior.appleTable == null) {
            state = State.IDLE_INSIDE;
            return;
        }

        if (dist(worldX, worldY, TABLE_X, TABLE_Y) <= TABLE_RANGE) {

            if (heldApple > 0) {
                int space     = ObjAppleTable.MAX_APPLE - gp.objectM.interior.appleTable.storedApple;
                int deposited = Math.min(heldApple, Math.max(0, space));
                gp.objectM.interior.appleTable.storedApple += deposited;
                heldApple -= deposited;
                System.out.println("[NPC] Deposited " + deposited + " apple(s) on table.");
            }


            if (heldWood > 0) {
                int space     = gp.inventory.MAX_WOOD - gp.inventory.wood;
                int deposited = Math.min(heldWood, Math.max(0, space));
                gp.inventory.wood += deposited;
                heldWood -= deposited;
                System.out.println("[NPC] Deposited " + deposited + " wood into inventory.");
            }

            isCarrying = false;
            state      = State.IDLE_INSIDE;
            System.out.println("[NPC] Deposit done. Resting inside.");

        } else {
            moveToward(TABLE_X, TABLE_Y);
        }
    }

    private void updateIdleInside(boolean isNight) {

        isCarrying = false;

        if (isNight) hasSpentNight = true;

        if (dist(worldX, worldY, IDLE_INSIDE_X, IDLE_INSIDE_Y) > 8) {
            moveToward(IDLE_INSIDE_X, IDLE_INSIDE_Y);
        }

        if (hasSpentNight
                && !isNight
                && gp.dC.currentState == dayCounter.dayNightState.Day) {
            NPC_is_Inside  = false;
            hasSpentNight  = false;
            worldX         = HOUSE_DOOR_X + 30;
            worldY         = HOUSE_DOOR_Y + 30;
            heldWood       = 0;
            heldApple      = 0;
            targetAppleIdx = -1;
            targetWoodIdx  = -1;
            state          = State.GATHERING;
            System.out.println("[NPC] Morning! Going out to gather.");
        }
    }



    private void moveToward(int tx, int ty) {
        int    dx   = tx - worldX;
        int    dy   = ty - worldY;
        double d    = Math.sqrt(dx * dx + dy * dy);
        if (d < 1) return;
        worldX += (int) (dx / d * speed);
        worldY += (int) (dy / d * speed);
        faceToward(tx, ty);
    }

    private void faceToward(int tx, int ty) {
        int dx = tx - worldX;
        int dy = ty - worldY;
        currentDir = (Math.abs(dx) > Math.abs(dy))
                ? (dx > 0 ? DIR_RIGHT : DIR_LEFT)
                : (dy > 0 ? DIR_DOWN  : DIR_UP);
    }

    private double dist(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1, dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void findNearestCollectible() {

        double bestApple = Double.MAX_VALUE;
        double bestWood  = Double.MAX_VALUE;
        targetAppleIdx   = -1;
        targetWoodIdx    = -1;

        if (gp.objectM.appleItems != null) {
            for (int i = 0; i < gp.objectM.appleItems.length; i++) {
                GameObject obj = gp.objectM.appleItems[i];
                if (obj == null || obj.collected) continue;
                double d = dist(worldX, worldY, obj.worldX, obj.worldY);
                if (d < bestApple) { bestApple = d; targetAppleIdx = i; }
            }
        }

        if (gp.objectM.woodItems != null) {
            for (int i = 0; i < gp.objectM.woodItems.length; i++) {
                GameObject obj = gp.objectM.woodItems[i];
                if (obj == null || obj.collected) continue;
                double d = dist(worldX, worldY, obj.worldX, obj.worldY);
                if (d < bestWood) { bestWood = d; targetWoodIdx = i; }
            }
        }

        if (bestApple <= bestWood) targetWoodIdx  = -1;
        else                       targetAppleIdx = -1;
    }


    public void draw(Graphics2D g2) {

        if (state == State.INACTIVE) return;

        boolean onInteriorMap = (gp.tileM.currentMap == 2);
        if (NPC_is_Inside != onInteriorMap) return;

        BufferedImage[][] frames = isCarrying ? walkFramesWLog : walkFrames;
        BufferedImage frame = frames[currentDir][animFrame];
        if (frame == null) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2.drawImage(frame, screenX, screenY, 64, 64, null);
    }
}
