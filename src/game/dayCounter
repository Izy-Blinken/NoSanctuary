package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class dayCounter {

    panel gp;
    Graphics2D g2;
    Font arial_40;

    private int dayCount = 1;

    public enum dayNightState {
        Day,
        Sunset,      // fade to black
        NightTitle,  // full black + Night Time text
        Night,
        Sunrise,     // fade from black
        DayTitle     // full black + Day Time text
    }

    public dayNightState currentState = dayNightState.Day;

    private final float day_duration        = 27f;
    private final float night_duration      = 27f;
    private final float transition_duration = 3f;
    private final float title_duration      = 2f;

    private float stateCounter = 0f;

    public dayCounter(panel gp) {
        this.gp = gp;
        arial_40 = new Font("Arial", Font.PLAIN, 40);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(arial_40);
        g2.setColor(Color.WHITE);
        g2.drawString("Day " + dayCount + "/5", 390, 50);
    }

    public void update() {
        stateCounter += 0.016f;
        switch (currentState) {
            case Day:
                if (stateCounter >= day_duration) {
                    currentState = dayNightState.Sunset;
                    stateCounter = 0f;
                    System.out.println("Hapon");
                }
                break;
            case Sunset:
                if (stateCounter >= transition_duration) {
                    currentState = dayNightState.NightTitle;
                    stateCounter = 0f;
                    System.out.println("Gabi na");
                }
                break;
            case NightTitle:
                if (stateCounter >= title_duration) {
                    currentState = dayNightState.Night;
                    stateCounter = 0f;
                    System.out.println("Gabi");
                }
                break;
            case Night:
                if (stateCounter >= night_duration) {
                    currentState = dayNightState.Sunrise;
                    stateCounter = 0f;
                    System.out.println("Madaling araw");
                }
                break;
            case Sunrise:
                if (stateCounter >= transition_duration) {
                    currentState = dayNightState.DayTitle;
                    stateCounter = 0f;
                    System.out.println("Umaga na");
                }
                break;
            case DayTitle:
                if (stateCounter >= title_duration) {
                    currentState = dayNightState.Day;
                    stateCounter = 0f;
                    if (dayCount <= 4) { dayCount++; }
                    else { dayCount++; gp.player.speed += 10; }
                    gp.objectM.respawnResources();
                    gp.inventory.resetDaily();
                    System.out.println("Umaga");
                }
                break;
        }
    }

    public void drawOverlay(Graphics2D g2) {
        switch (currentState) {
            case Day: break;
            case Sunset: {
                float progress = stateCounter / transition_duration;
                int alpha = Math.max(0, Math.min(255, (int)(progress * 255)));
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
                break;
            }
            case NightTitle: {
                g2.setColor(new Color(0, 0, 0, 255));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
                g2.setFont(new Font("Arial", Font.BOLD, 50));
                g2.setColor(new Color(100, 100, 255));
                String nt = "Night Time";
                g2.drawString(nt, gp.getWidth()/2 - g2.getFontMetrics().stringWidth(nt)/2, gp.getHeight()/2);
                break;
            }
            case Night: {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
                break;
            }
            case Sunrise: {
                float progress = stateCounter / transition_duration;
                int alpha = Math.max(0, Math.min(255, (int)((1 - progress) * 255)));
                g2.setColor(new Color(255, 140, 0, alpha / 3));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
                break;
            }
            case DayTitle: {
                g2.setColor(new Color(0, 0, 0, 255));
                g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
                g2.setFont(new Font("Arial", Font.BOLD, 50));
                g2.setColor(new Color(255, 220, 50));
                String dt = "Day Time";
                g2.drawString(dt, gp.getWidth()/2 - g2.getFontMetrics().stringWidth(dt)/2, gp.getHeight()/2);
                break;
            }
        }
    }
}
