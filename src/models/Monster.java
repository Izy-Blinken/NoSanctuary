package models;

import game.panel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Monster – night-time AI entity.
 *
 * Sprite: monster1_universal.png (LPC 832×1344, 64×64 per frame)
 *
 *   Walk  rows 8-11  (up/left/down/right) – 8 frames used
 *   Slash rows 12-15 (up/left/down/right) – 6 frames
 *
 * AI states:
 *   IDLE      – daytime; hidden off-screen
 *   CHASING   – night; moves toward player / house
 *   ATTACKING – night + player outside + within range → plays slash anim
 *   KNOCKING  – night + player safe inside + at house door
 */
public class Monster extends Entity {

    // ── State machine ─────────────────────────────────────────────────────────
    public enum State { IDLE, CHASING, ATTACKING, KNOCKING }
    public State state = State.IDLE;

    // ── Directions ────────────────────────────────────────────────────────────
    private static final int DIRS      = 4;
    private static final int DIR_UP    = 0;
    private static final int DIR_LEFT  = 1;
    private static final int DIR_DOWN  = 2;
    private static final int DIR_RIGHT = 3;
    private int currentDir = DIR_DOWN;

    // ── Walk animation ────────────────────────────────────────────────────────
    private static final int WALK_FRAMES = 10;
    private static final int WALK_SPEED  = 10;   // ticks per frame
    private BufferedImage[][] walkFrames = new BufferedImage[DIRS][WALK_FRAMES];

    // ── Attack (slash) animation ──────────────────────────────────────────────
    private static final int SLASH_FRAMES = 6;
    private static final int SLASH_SPEED  = 5;  // ticks per frame (faster)
    private BufferedImage[][] slashFrames = new BufferedImage[DIRS][SLASH_FRAMES];

    // Shared animation counters
    private int animCounter = 0;
    private int animFrame   = 0;

    // ── Timers ────────────────────────────────────────────────────────────────
    private static final int KNOCK_INTERVAL = 90;
    private static final int ACTION_DISPLAY = 60;
    private int knockTimer  = 0;
    public  int actionTimer = 0;

    // ── Ranges ────────────────────────────────────────────────────────────────
    private static final double ATTACK_RANGE = 40;
    private static final double KNOCK_RANGE  = 60;

    private final panel gp;

    // ── Constructor ───────────────────────────────────────────────────────────
    public Monster(panel gp) {
        this.gp = gp;
        speed     = 4;
        solidArea = new Rectangle(8, 16, 32, 32);
        worldX    = -1000;
        worldY    = -1000;
        loadSprites();
    }

    // ── Load sprites ──────────────────────────────────────────────────────────
    private void loadSprites() {
        try {
            BufferedImage sheet = ImageIO.read(
                getClass().getResourceAsStream(
                    "/assets/Charac/edp character/monster1_universal.png"));

            // Walk: rows 8-11
            int[] walkRows  = { 8, 9, 10, 11 };
            // Slash: rows 12-15
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
                for (int f = 0; f < WALK_FRAMES;  f++) walkFrames[d][f]  = blankFrame();
                for (int f = 0; f < SLASH_FRAMES; f++) slashFrames[d][f] = blankFrame();
            }
        }
    }

    private BufferedImage blankFrame() {
        return new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    }

    // ── Spawn at world edge ───────────────────────────────────────────────────
    public void spawnNearEdge() {
        worldX      = 200;
        worldY      = gp.player.worldY;
        state       = State.CHASING;
        animFrame   = 0;
        animCounter = 0;
    }

    // ── Per-frame update ──────────────────────────────────────────────────────
    public void update() {
        if (actionTimer > 0) actionTimer--;

        boolean isNight = (gp.dC.currentState == game.dayCounter.dayNightState.Night
                        || gp.dC.currentState == game.dayCounter.dayNightState.NightTitle
                        || gp.dC.currentState == game.dayCounter.dayNightState.Sunset
                        || gp.dC.currentState == game.dayCounter.dayNightState.Sunrise);

        // ── Daytime: hide ─────────────────────────────────────────────────
        if (!isNight) {
            state  = State.IDLE;
            worldX = -1000;
            worldY = -1000;
            return;
        }

        boolean playerSafe    = gp.isPlayerSafe();
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

        // ── AI conditional logic ──────────────────────────────────────────
        if (playerOutside && !playerSafe) {
            if (distToPlayer < ATTACK_RANGE) {
                // Enter attack state
                if (state != State.ATTACKING) {
                    state       = State.ATTACKING;
                    animFrame   = 0;   // restart slash anim from frame 0
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
                    knockTimer  = 0;
                    actionTimer = ACTION_DISPLAY;
                }
            } else {
                state = State.CHASING;
                moveToward(houseX, houseY);
            }
        }

        // ── Animate ───────────────────────────────────────────────────────
        if (state == State.CHASING) {
            // Walk: loop
            animCounter++;
            if (animCounter >= WALK_SPEED) {
                animCounter = 0;
                animFrame   = (animFrame + 1) % WALK_FRAMES;
            }
        } else if (state == State.ATTACKING) {
            // Slash: play once, then hold last frame until state changes
            if (animFrame < SLASH_FRAMES - 1) {
                animCounter++;
                if (animCounter >= SLASH_SPEED) {
                    animCounter = 0;
                    animFrame++;
                }
            }
            // When player moves away, state becomes CHASING and animFrame resets
        } else {
            // KNOCKING / IDLE – stand still
            animFrame   = 0;
            animCounter = 0;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void faceToward(int tx, int ty) {
        int dx = tx - worldX, dy = ty - worldY;
        currentDir = (Math.abs(dx) > Math.abs(dy))
                   ? (dx > 0 ? DIR_RIGHT : DIR_LEFT)
                   : (dy > 0 ? DIR_DOWN  : DIR_UP);
    }

    private void moveToward(int tx, int ty) {
        int dx = tx - worldX, dy = ty - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1) return;
        worldX += (int)(dx / dist * speed);
        worldY += (int)(dy / dist * speed);
        faceToward(tx, ty);
    }

    // ── Draw ─────────────────────────────────────────────────────────────────
    public void draw(Graphics2D g2) {
        if (state == State.IDLE) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX < -96 || screenX > gp.screenWidth + 96
         || screenY < -96 || screenY > gp.screenheight + 96) return;

        // Choose correct frame array
        BufferedImage frame;
        if (state == State.ATTACKING) {
            int safeFrame = Math.min(animFrame, SLASH_FRAMES - 1);
            frame = slashFrames[currentDir][safeFrame];
        } else {
            frame = walkFrames[currentDir][animFrame];
        }

        g2.drawImage(frame, screenX, screenY, 64, 64, null);

        // Floating label
        if (actionTimer > 0) {
            String label;
            Color  col;
            if (state == State.ATTACKING) {
                label = "⚠ ATTACKED!";
                col   = new Color(220, 30, 30);
            } else if (state == State.KNOCKING) {
                label = "*KNOCK KNOCK*";
                col   = new Color(220, 140, 0);
            } else {
                return;
            }
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            int lw = g2.getFontMetrics().stringWidth(label);
            int lx = screenX + 32 - lw / 2;
            int ly = screenY - 10;
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(lx - 5, ly - 16, lw + 10, 22, 8, 8);
            g2.setColor(col);
            g2.drawString(label, lx, ly);
        }
    }

    // ── HUD banner ───────────────────────────────────────────────────────────
    public void drawAlert(Graphics2D g2) {
        if (actionTimer <= 0 || state == State.IDLE) return;

        String alert;
        Color  alertColor;
        if (state == State.ATTACKING) {
            alert      = "  THE MONSTER IS ATTACKING YOU!  ";
            alertColor = new Color(220, 30, 30);
        } else if (state == State.KNOCKING) {
            alert      = "  *KNOCK KNOCK*  Something is at the door!  ";
            alertColor = new Color(200, 130, 0);
        } else {
            return;
        }

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        int aw = g2.getFontMetrics().stringWidth(alert);
        int ay = gp.screenheight / 2 - 40;
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRoundRect(gp.screenWidth / 2 - aw / 2 - 12, ay - 26, aw + 24, 38, 14, 14);
        g2.setColor(alertColor);
        g2.drawString(alert, gp.screenWidth / 2 - aw / 2, ay);
    }
}
