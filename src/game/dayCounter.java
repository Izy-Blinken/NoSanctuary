package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.Timer;
import models.Player;

public class dayCounter{
    
    panel gp;
    Graphics2D g2;
    Font arial_40;
    
    private int dayCount = 1;
    private enum dayNightState {
        Day,
        Sunset,
        Night,
        Sunrise
    }
    public dayNightState currentState = dayNightState.Day;

    private final float day_duration = 27f; // 27 seconds per day time
    private final float night_duration = 27f; // 27 seconds din pag night time
    private final float transition_duration = 3f; // transition durarion lang from day to night vice versa

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
            case Sunrise:
                if (stateCounter >= transition_duration) {
                    currentState = dayNightState.Day;
                    stateCounter = 0f;
                    if(dayCount <= 4){
                        dayCount++;
                    }
                    else{
                        dayCount++;
                        gp.player.speed += 10;
                    }
                    System.out.println("Umaga");
                }
                break;
            case Day:
                if (stateCounter >= day_duration) {
                    currentState = dayNightState.Sunset;
                    stateCounter = 0f;
                    System.out.println("Hapon");
                }
                break;
            case Sunset:
                if (stateCounter >= transition_duration) {
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
        }
    }

    public void drawOverlay(Graphics2D g2) {

        int transparencyControl = 0;

        if (currentState == dayNightState.Sunset) {
            float progress = stateCounter / transition_duration;
            transparencyControl = (int) (progress * 180);
        } else if (currentState == dayNightState.Sunrise) {
            float progress = stateCounter / transition_duration;
            transparencyControl = (int) ((1 - progress) * 180);
        } else if (currentState == dayNightState.Night) {
            transparencyControl = 180;
        }

        // Day = back to normal ung transparency control kasi hindi naman namodify ung transparency ng normal na mapa eh
        transparencyControl = Math.max(0, Math.min(180, transparencyControl));

        g2.setColor(new Color(0, 0, 0, transparencyControl));
        g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
    }
}
