package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NPC extends Entity {

    public enum State {
        INACTIVE, IDLE, FOLLOWING, HELPING
    }

    public State state = State.INACTIVE;

    private static final int DIRS = 4;
    private static final int DIR_UP = 0;
    private static final int DIR_LEFT = 1;
    private static final int DIR_DOWN = 2;
    private static final int DIR_RIGHT = 3;

    private int currentDir = DIR_DOWN;

    private static final int WALK_FRAMES = 10;
    private static final int WALK_SPEED = 10;

    private BufferedImage[][] walkFrames = new BufferedImage[DIRS][WALK_FRAMES];
    private int animCounter = 0;
    private int animFrame = 0;

    private final panel gp;

    private static final double FOLLOW_RANGE = 200;
    private static final double HELP_RANGE = 60;

    private int gatherTimer = 0;
    private static final int GATHER_INTERVAL = 60 * 15;

    public boolean NPC_is_Inside = false;

    public NPC(panel gp) {
        this.gp = gp;
        speed = 3;
        solidArea = new Rectangle(8, 16, 32, 32);

        worldX = -1000;
        worldY = -1000;

        loadSprites();
    }

    private void loadSprites() {
        try {
            BufferedImage sheet = ImageIO.read(
                    getClass().getResourceAsStream("/assets/Charac/edp character/human2_universal.png")
            );
            int[] walkRows = {8, 9, 10, 11};
            for (int d = 0; d < DIRS; d++) {
                for (int f = 0; f < WALK_FRAMES; f++) {
                    walkFrames[d][f] = sheet.getSubimage(f * 64, walkRows[d] * 64, 64, 64);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("NPC sprite load failed: " + e.getMessage());
        }
    }

    public void spawnNearEdge() {
        worldX = 200;
        worldY = gp.player.worldY;
        animFrame = 0;
        animCounter = 0;
    }

    public void enterHouse() {
        spawnNearEdge();
        NPC_is_Inside = true;
        state = State.FOLLOWING;
        animFrame = 0;
        animCounter = 0;
        gatherTimer = 0;
    }

    public void update() {

        if (state == State.INACTIVE) {
            return;
        }

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= HELP_RANGE) {
            state = State.HELPING;
            gatherResources();
        } else {
            state = State.FOLLOWING;
            moveToward(gp.player.worldX, gp.player.worldY);
        }

        if (state == State.FOLLOWING) {
            animCounter++;
            if (animCounter >= WALK_SPEED) {
                animCounter = 0;
                animFrame = (animFrame + 1) % WALK_FRAMES;
            }
        } else {
            animFrame = 0;
        }
    }

    private void gatherResources() {
        gatherTimer++;

        if (gatherTimer >= GATHER_INTERVAL) {
            gatherTimer = 0;

            boolean woodAdded = gp.inventory.addWood();
            boolean appleAdded = gp.inventory.addApple();

            //debugging lang reh hehez, para lang makita if naadd ba sa inventory ung resources
            if (woodAdded || appleAdded) {
                System.out.println("[NPC] Deposited — wood: " + gp.inventory.wood
                        + "/" + gp.inventory.MAX_WOOD
                        + "  apple: " + gp.inventory.apple
                        + "/" + gp.inventory.MAX_APPLE);
            }
        }
    }

    private void moveToward(int tx, int ty) {
        int dx = tx - worldX;
        int dy = ty - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 1) {
            return;
        }

        worldX += (int) (dx / dist * speed);
        worldY += (int) (dy / dist * speed);
        faceToward(tx, ty);
    }

    private void faceToward(int tx, int ty) {
        int dx = tx - worldX;
        int dy = ty - worldY;
        currentDir = (Math.abs(dx) > Math.abs(dy))
                ? (dx > 0 ? DIR_RIGHT : DIR_LEFT)
                : (dy > 0 ? DIR_DOWN : DIR_UP);
    }

    public void draw(Graphics2D g2) {

        if (state == State.INACTIVE) {
            return;
        }

        BufferedImage frame = walkFrames[currentDir][animFrame];
        if (frame == null) {
            return;
        }

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2.drawImage(frame, screenX, screenY, 64, 64, null);
    }
}
