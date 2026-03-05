package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Inventory {

    panel gp;

    public int wood  = 0;
    public int apple = 0;

    // max per day based sa pdf
    public final int MAX_WOOD  = 10;
    public final int MAX_APPLE = 5;

    // apple sprites galing sa sprite sheet
    private BufferedImage appleDay;
    private BufferedImage appleNight;

    public Inventory(panel gp) {
        this.gp = gp;
        loadAppleSprite();
    }

    private void loadAppleSprite() {
        try {
            BufferedImage daySheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            appleDay = daySheet.getSubimage(900, 298, 22, 26); // apple sa sprite sheet

            BufferedImage nightSheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP_TILES_NIGHT.png"));
            appleNight = nightSheet.getSubimage(900, 298, 22, 26); // night version
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addWood() {
        if (wood < MAX_WOOD) {
            wood++;
            System.out.println("Wood collected: " + wood + "/" + MAX_WOOD);
            return true;
        }
        return false; // puno na
    }

    public boolean addApple() {
        if (apple < MAX_APPLE) {
            apple++;
            System.out.println("Apple collected: " + apple + "/" + MAX_APPLE);
            return true;
        }
        return false; // puno na
    }

    // reset every new day
    public void resetDaily() {
        wood  = 0;
        apple = 0;
    }

    public void draw(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 16));

        // check kung gabi para gamitin night apple sprite
        boolean isNight = gp.dC.currentState == dayCounter.dayNightState.Night
                       || gp.dC.currentState == dayCounter.dayNightState.Sunset
                       || gp.dC.currentState == dayCounter.dayNightState.Sunrise;

        // Wood
        g2.setColor(new Color(120, 70, 20));
        g2.fillRect(10, 60, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawString("Wood:  " + wood + "/" + MAX_WOOD, 35, 76);

        // Apple - galing na sa sprite sheet
        BufferedImage appleImg = (isNight && appleNight != null) ? appleNight : appleDay;
        if (appleImg != null) {
            g2.drawImage(appleImg, 10, 85, 20, 20, null); // scaled to 20x20 para uniform sa HUD
        }
        g2.setColor(Color.WHITE);
        g2.drawString("Apple: " + apple + "/" + MAX_APPLE, 35, 101);

        // hint text
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Press E near wood/apple tree to collect", 10, 122);
        g2.drawString("Press E to eat an apple to heal", 10, 140);
    }
}
