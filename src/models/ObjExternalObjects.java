package models;

import game.panel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObjExternalObjects extends GameObject {

    private BufferedImage objectImage;

    public ObjExternalObjects(panel gp, String type, double scale) {

        try {

            BufferedImage raw = null;

            switch (type) {

                case "deadTree1":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/deadTree1.png"));
                    break;

                case "deadTree2":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/deadTree2.png"));
                    break;

                case "brokenTree":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/brokenTree.png"));
                    break;

                case "thornBig":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/thornPlantBig.png"));
                    break;

                case "thornMedium":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/thornPlantMedium.png"));
                    break;

                case "thornSmall":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/thornPlantSmall.png"));
                    break;

                case "ruinPebbles":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/ruinPebble.png"));
                    break;

                case "ruinWall":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/ruinWall.png"));
                    break;

                case "ruinPillar":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/ruinOnePillar.png"));
                    break;

                case "ruinArch":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/ruinArch.png"));
                    break;

                case "ruinFourPillar":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/ruinsFourPillar.png"));
                    break;

                case "rockXS":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/rockExtraSmall.png"));
                    break;

                case "rockSmall":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/rockSmall.png"));
                    break;

                case "rockMedium":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/rockMedium.png"));
                    break;

                case "rockBig":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/rockBig.png"));
                    break;

                case "plantSmall":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/plantSmall.png"));
                    break;

                case "plantMedium":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/plantMedium.png"));
                    break;

                case "plantBig":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/plantBig.png"));
                    break;

                case "pileSkull":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/pileSkull.png"));
                    break;

                case "leftGrave1":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/leftGrave1.png"));
                    break;

                case "leftGrave2":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/leftGrave2.png"));
                    break;

                case "rightGrave":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/rightGrave.png"));
                    break;

                case "greenPlantSmall":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/greenPlantSmall.png"));
                    break;

                case "greenPlantMedium":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/greenPlantMedium.png"));
                    break;

                case "greenPlantBig":
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/greenPlantBig.png"));
                    break;

                case "shelter":
                    BufferedImage shelterSheet = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/undergroundShelter.png"));
                    raw = shelterSheet.getSubimage(11, 51, 40, 22);
                    break;

                default:
                    raw = ImageIO.read(getClass().getResourceAsStream("/assets/Exterior_Objects/rockSmall.png"));
                    break;
            }

            BufferedImage scaledRaw = scaled(raw, scale);
            
            if (objectImage == null) {
                objectImage = scaledRaw;
            }

            // Apply tint based on type
            switch (type) {
                
                case "plantSmall":
                case "plantMedium":
                case "plantBig":
                case "greenPlantSmall":
                case "greenPlantMedium":
                case "greenPlantBig":
                case "ruinPebbles":
                    collision = false;
                    break;

                default:
                    collision = true;
                    solidArea = getVisibleBounds(objectImage);
                    break;
            }

                    generateNightImage();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    
    private java.awt.Rectangle getVisibleBounds(BufferedImage img) {
        
        int minX = img.getWidth(), minY = img.getHeight();
        int maxX = 0, maxY = 0;

        for (int y = 0; y < img.getHeight(); y++) {
            
            for (int x = 0; x < img.getWidth(); x++) {
                
                int alpha = (img.getRGB(x, y) >> 24) & 0xff;
                
                if (alpha > 10) { // ignore near-transparent pixels
                    
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        return new java.awt.Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public void draw(Graphics2D g2, int screenX, int screenY) {
        g2.drawImage(objectImage, screenX, screenY, null);
    }

    private BufferedImage tint(BufferedImage original, Color color, float alpha) {

        BufferedImage tinted = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tinted.createGraphics();

        g2d.drawImage(original, 0, 0, null);
        g2d.setComposite(java.awt.AlphaComposite.SrcAtop.derive(alpha));
        g2d.setColor(color);
        g2d.fillRect(0, 0, original.getWidth(), original.getHeight());
        g2d.dispose();

        return tinted;
    }

    private BufferedImage scaled(BufferedImage src, double scale) {

        int w = (int)(src.getWidth() * scale);
        int h = (int)(src.getHeight() * scale);

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();

        return out;
    }
}