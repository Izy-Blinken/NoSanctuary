package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;

public class dayCounter {

    panel gp;
    Graphics2D g2;

    private Font imFellLarge;   // for "Day X" text
    private Font imFellSmall;   // for phase label (DAY / NIGHT)

    public int dayCount = 1;
    
    public float startTime;
    public float completion_time;

    public enum dayNightState {
        Day,
        Night,
        DayTitle
    }

    public dayNightState currentState = dayNightState.Day;

    private final float day_duration    = 27f;
    private final float night_duration  = 27f;
    private final float fade_duration   = 1.5f;  // fade to black + fade back in
    private final float hold_duration   = 3.5f;  // suspense hold with text visible
    // total DayTitle = fade_duration + hold_duration + fade_duration = 6.5f

    public float stateCounter = 0f;

    public dayCounter(panel gp) {
        this.gp = gp;
        loadFonts();
    }

    private void loadFonts() {
        try {
            InputStream is = getClass().getResourceAsStream("/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
            Font base = Font.createFont(Font.TRUETYPE_FONT, is);
            imFellLarge = base.deriveFont(Font.PLAIN, 28f);
            imFellSmall = base.deriveFont(Font.PLAIN, 11f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // fallback so game doesn't crash
            imFellLarge = new Font("Serif", Font.PLAIN, 28);
            imFellSmall = new Font("Serif", Font.PLAIN, 11);
        }
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String dayText  = "Day " + dayCount;
        String phase = (currentState == dayNightState.Night) ? "N I G H T" : "D A Y";

        g2.setFont(imFellLarge);
        int textW = g2.getFontMetrics().stringWidth(dayText);
        int boxW = textW + 40;
        int boxH = 52;
        int boxX = gp.screenWidth / 2 - boxW / 2;
        int boxY = 12;

        // dark semi-transparent background
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
        g2.setColor(new Color(5, 8, 5));
        g2.fillRect(boxX, boxY, boxW, boxH);

        // border
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.setColor(new Color(90, 110, 70));
        g2.drawLine(boxX, boxY, boxX + boxW, boxY);
        g2.drawLine(boxX, boxY + boxH, boxX + boxW, boxY + boxH);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // phase label
        g2.setFont(imFellSmall);
        int phaseW = g2.getFontMetrics().stringWidth(phase);
        g2.setColor(new Color(120, 140, 95, 180));
        g2.drawString(phase, gp.screenWidth / 2 - phaseW / 2, boxY + 16);

        // "Day X" main text
        g2.setFont(imFellLarge);
        g2.setColor(new Color(210, 220, 190));
        g2.drawString(dayText, gp.screenWidth / 2 - textW / 2, boxY + 42);
    }

    public void update() {
        stateCounter += 0.016f;
        completion_time = Math.round(((System.nanoTime() - startTime) / 1_000_000_000f) * 100f) / 100f;
        switch (currentState) {
            case Day:
                if (stateCounter >= day_duration) {
                    currentState = dayNightState.Night;
                    stateCounter = 0f;
                }
                break;
                
            case Night:
                if (stateCounter >= night_duration) {
                    currentState = dayNightState.DayTitle;
                    stateCounter = 0f;
                }
                break;
                
            case DayTitle:
                if (stateCounter >= fade_duration + hold_duration + fade_duration) {
                    currentState = dayNightState.Day;
                    stateCounter = 0f;
                    if (dayCount <= 4) { dayCount++; }
                    else { dayCount++; gp.player.speed += 10; }
                    gp.objectM.respawnResources();
                    //gp.inventory.resetDaily();
                }
                break;
        }
    }

    public void drawOverlay(Graphics2D g2) {
        switch (currentState) {
            case Day: break;

            case Night: {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
                break;
            }

            case DayTitle: {
                int alpha;
                float t = stateCounter;

                if (t < fade_duration) {

                    alpha = Math.min(255, (int)((t / fade_duration) * 255));
                    
                } else if (t < fade_duration + hold_duration) {
                    
                    alpha = 255;
                    
                } else {

                    float fadeOutProgress = (t - fade_duration - hold_duration) / fade_duration;
                    alpha = Math.max(0, (int)((1f - fadeOutProgress) * 255));
                }

                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());

                // show only yung day x during and near fade out
                if (t >= fade_duration && alpha > 220) {
                    drawTransitionTitle(g2, "Day " + (dayCount + 1), new Color(200, 215, 175));
                }
                break;
            }
        }
    }

    private void drawTransitionTitle(Graphics2D g2, String text, Color color) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        try {
            InputStream is = getClass().getResourceAsStream("/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
            Font titleFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 52f);
            g2.setFont(titleFont);
        } catch (Exception e) {
            g2.setFont(new Font("Serif", Font.PLAIN, 52));
        }

        int tw = g2.getFontMetrics().stringWidth(text);
        int tx = gp.getWidth()  / 2 - tw / 2;
        int ty = gp.getHeight() / 2 + 18;

        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 58f));
        g2.drawString(text, tx - 1, ty + 2);

        try {
            InputStream is = getClass().getResourceAsStream("/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
            g2.setFont(Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 52f));
            
        } catch (Exception e) {
            g2.setFont(new Font("Serif", Font.PLAIN, 52));
        }

        g2.setColor(color);
        g2.drawString(text, tx, ty);
    }

    public int getDayCount() {
        return dayCount;
    }
}
