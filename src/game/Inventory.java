package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import models.Player;

public class Inventory {

    panel gp;

    public int wood = 0;
    public int apple = 0;

    public final int MAX_WOOD = 10;
    public final int MAX_APPLE = 5;

    public boolean showPanel = false;
    public boolean showScroll = false;

    public String clueText = "";
    public boolean hasClue = false;

    private BufferedImage appleDay;
    private BufferedImage appleNight;

    private Font imFellBase = null;

    public static final int BTN_X = 10;
    public static final int BTN_W = 50;
    public static final int BTN_H = 22;
    public static final int BAG_BTN_Y = 60;
    public static final int SCROLL_BTN_Y = 90;

    private static final int PANEL_W = 480;
    private static final int PANEL_H = 300;

    public Inventory(panel gp) {

        this.gp = gp;
        loadAppleSprite();
    }

    private void loadAppleSprite() {

        try {

            BufferedImage daySheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            appleDay = daySheet.getSubimage(900, 298, 22, 26);

            BufferedImage nightSheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP_TILES_NIGHT.png"));
            appleNight = nightSheet.getSubimage(900, 298, 22, 26);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Font getImFell(float size) {

        if (imFellBase == null) {

            try {

                InputStream is = getClass().getResourceAsStream("/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");

                if (is != null) {

                    imFellBase = Font.createFont(Font.TRUETYPE_FONT, is);
                }

            } catch (FontFormatException | IOException e) {

                imFellBase = new Font("Serif", Font.PLAIN, 12);
            }
        }

        return (imFellBase != null) ? imFellBase.deriveFont(size) : new Font("Serif", Font.PLAIN, (int) size);
    }

    public boolean addWood() {

        if (wood < MAX_WOOD) {
            wood++;
            return true;
        }

        return false;
    }

    public boolean addApple() {

        if (apple < MAX_APPLE) {
            apple++;
            return true;
        }

        return false;
    }

    public boolean eatApple() {

        if (apple > 0) {

            apple--;

            return true;
        }

        return false;
    }

    public void drawButtons(Graphics2D g2) {

        drawTextButton(g2, "Bag", BTN_X, BAG_BTN_Y, BTN_W, BTN_H, showPanel);
        drawTextButton(g2, "Scroll", BTN_X, SCROLL_BTN_Y, BTN_W, BTN_H, showScroll);
    }

    private void drawTextButton(Graphics2D g2, String label, int x, int y, int w, int h, boolean active) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.setColor(active ? new Color(50, 48, 45, 230) : new Color(30, 28, 25, 200));
        g2.fillRect(x, y, w, h);

        g2.setColor(active ? new Color(160, 155, 148) : new Color(90, 85, 78));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(x, y, w, h);

        g2.setFont(getImFell(12f));
        g2.setColor(active ? new Color(210, 205, 195) : new Color(140, 135, 128));
        int sw = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, x + (w - sw) / 2, y + h - 6);

        g2.setStroke(new BasicStroke(1f));
    }

    // inv panel
    public void drawInventoryPanel(Graphics2D g2) {

        if (!showPanel) {
            return;
        }

        int sw = gp.screenWidth;
        int sh = gp.screenheight;
        int px = sw / 2 - PANEL_W / 2;
        int py = sh / 2 - PANEL_H / 2;

        boolean isNight = gp.dC.currentState == dayCounter.dayNightState.Night;

        drawPanelBase(g2, px, py, PANEL_W, PANEL_H, "Inventory");

        // wood (left col) , apple (right col)
        int rowY = py + 75;
        int col1X = px + 40;
        int col2X = px + PANEL_W / 2 + 20;
        int rowH = 60;

        drawGridItem(g2, col1X, rowY, rowH, null, new Color(100, 65, 20), "Wood", wood, MAX_WOOD);

        BufferedImage appleImg = (isNight && appleNight != null) ? appleNight : appleDay;
        drawGridItem(g2, col2X, rowY, rowH, appleImg, null, "Apple", apple, MAX_APPLE);

        // Eat btn
        int btnW = 180;
        int btnH = 34;
        int btnX = px + PANEL_W / 2 - btnW / 2;
        int btnY = rowY + rowH + 28;

        boolean canEat = apple > 0 && gp.player.hp < 100;

        g2.setColor(canEat ? new Color(35, 45, 28, 215) : new Color(30, 28, 25, 180));
        g2.fillRect(btnX, btnY, btnW, btnH);

        g2.setColor(canEat ? new Color(90, 105, 75) : new Color(75, 70, 65));
        g2.setStroke(new BasicStroke(1f));

        g2.drawRect(btnX, btnY, btnW, btnH);
        g2.setFont(getImFell(14f));
        g2.setColor(canEat ? new Color(190, 205, 170) : new Color(100, 95, 88));
        String eatLabel = "Eat Apple  (+20 HP)";

        int eatW = g2.getFontMetrics().stringWidth(eatLabel);
        g2.drawString(eatLabel, btnX + (btnW - eatW) / 2, btnY + 22);

        g2.setStroke(new BasicStroke(1f));
    }

    private void drawGridItem(Graphics2D g2, int x, int y, int rowH,
            BufferedImage img, Color fallbackColor,
            String label, int count, int max) {

        int iconSize = 28;
        int iy = y + (rowH - iconSize) / 2;

        if (img != null) {

            g2.drawImage(img, x, iy, iconSize, iconSize, null);

        } else {

            g2.setColor(fallbackColor);
            g2.fillRect(x, iy, iconSize, iconSize);
            g2.setColor(new Color(75, 70, 65));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(x, iy, iconSize, iconSize);
        }

        g2.setFont(getImFell(13f));
        g2.setColor(new Color(140, 135, 128));
        g2.drawString(label, x + iconSize + 10, y + rowH / 2 - 2);

        g2.setFont(getImFell(18f));
        g2.setColor(new Color(210, 205, 195));
        g2.drawString(count + " / " + max, x + iconSize + 10, y + rowH / 2 + 18);

        g2.setStroke(new BasicStroke(1f));
    }

    // clue panel
    public void drawScrollPanel(Graphics2D g2) {

        if (!showScroll) {
            return;
        }

        int sw = gp.screenWidth;
        int sh = gp.screenheight;
        int px = sw / 2 - PANEL_W / 2;
        int py = sh / 2 - PANEL_H / 2;

        drawPanelBase(g2, px, py, PANEL_W, PANEL_H, "Clue Scroll");

        g2.setFont(getImFell(14f));

        if (hasClue && clueText != null && !clueText.isEmpty()) {

            g2.setColor(new Color(190, 185, 175));
            drawWrappedText(g2, clueText, px + 40, py + 80, PANEL_W - 80, 22);

        } else {

            g2.setColor(new Color(100, 95, 88));
            String empty = "No clue found yet.";
            int ew = g2.getFontMetrics().stringWidth(empty);

            g2.drawString(empty, px + PANEL_W / 2 - ew / 2, py + PANEL_H / 2);
        }
    }

    // panel
    public void drawPanelBase(Graphics2D g2, int px, int py, int pw, int ph, String title) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        // panel bg
        g2.setColor(new Color(30, 28, 25, 225));
        g2.fillRect(px, py, pw, ph);

        // Title
        g2.setFont(getImFell(20f));
        g2.setColor(new Color(210, 205, 195));
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, px + pw / 2 - tw / 2, py + 32);

        // Title underline
        g2.setColor(new Color(65, 60, 55));
        g2.drawLine(px + 30, py + 42, px + pw - 30, py + 42);

        // X button
        g2.setFont(getImFell(14f));
        g2.setColor(new Color(140, 135, 128));
        g2.drawString("x", px + pw - 20, py + 22);

        g2.setStroke(new BasicStroke(1f));
    }

    public boolean isBagBtnClicked(int mx, int my) {

        return mx >= BTN_X && mx <= BTN_X + BTN_W
                && my >= BAG_BTN_Y && my <= BAG_BTN_Y + BTN_H;
    }

    public boolean isScrollBtnClicked(int mx, int my) {

        return mx >= BTN_X && mx <= BTN_X + BTN_W
                && my >= SCROLL_BTN_Y && my <= SCROLL_BTN_Y + BTN_H;
    }

    public boolean isInventoryXClicked(int mx, int my) {

        int px = gp.screenWidth / 2 - PANEL_W / 2;
        int py = gp.screenheight / 2 - PANEL_H / 2;

        return mx >= px + PANEL_W - 24 && mx <= px + PANEL_W - 6
                && my >= py + 8 && my <= py + 28;
    }

    public boolean isScrollXClicked(int mx, int my) {

        int px = gp.screenWidth / 2 - PANEL_W / 2;
        int py = gp.screenheight / 2 - PANEL_H / 2;

        return mx >= px + PANEL_W - 24 && mx <= px + PANEL_W - 6
                && my >= py + 8 && my <= py + 28;
    }

    public boolean isEatClicked(int mx, int my) {

        if (!showPanel) {
            return false;
        }

        if (apple <= 0 || gp.player.hp >= 100) {
            return false;
        }

        int px = gp.screenWidth / 2 - PANEL_W / 2;
        int py = gp.screenheight / 2 - PANEL_H / 2;
        int rowY = py + 75;
        int btnW = 180;
        int btnH = 34;
        int btnX = px + PANEL_W / 2 - btnW / 2;
        int btnY = rowY + 60 + 28;

        return mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH;
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y;

        for (String word : words) {

            String test = line.length() == 0 ? word : line + " " + word;

            if (g2.getFontMetrics().stringWidth(test) > maxWidth) {

                g2.drawString(line.toString(), x, curY);
                line = new StringBuilder(word);
                curY += lineHeight;

            } else {

                line = new StringBuilder(test);
            }
        }

        if (line.length() > 0) {
            g2.drawString(line.toString(), x, curY);
        }
    }
}
