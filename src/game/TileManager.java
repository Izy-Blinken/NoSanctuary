package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public class TileManager {

    panel gp;
    Tiles[] tile;
    Tiles[] nightTile;
    public int mapTileNum[][];

    public TileManager(panel gp) {
        
        this.gp = gp;
        tile = new Tiles[50];
        nightTile = new Tiles[50];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap();
    }

    public BufferedImage getRotatedImage(BufferedImage original, int degrees) {
        
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(Math.toRadians(degrees), w / 2.0, h / 2.0);
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        
        return rotated;
    }
    
    public BufferedImage tintImage(BufferedImage original, Color color, float alpha) {

        BufferedImage tinted = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tinted.createGraphics();
        
        g2d.drawImage(original, 0, 0, null);
        g2d.setComposite(java.awt.AlphaComposite.SrcAtop.derive(alpha));
        g2d.setColor(color);
        g2d.fillRect(0, 0, original.getWidth(), original.getHeight());
        g2d.dispose();

        return tinted;
    }

    public void getTileImage() {
        //still need: set the objects
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            BufferedImage wallSheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/walls_floor_1.png"));
            BufferedImage wallSheet2 = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/walls_floor_2.png"));
            BufferedImage nightSheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP_TILES_NIGHT.png"));
            
            BufferedImage bottomRoad = tintImage(getRotatedImage(sheet.getSubimage(619, 11, 61, 61), 90), new Color(55, 42, 25), 0.45f);
            BufferedImage topRoad = tintImage(getRotatedImage(sheet.getSubimage(619, 11, 61, 61), 270), new Color(55, 42, 25), 0.45f);
            BufferedImage leftRoad = tintImage(getRotatedImage(sheet.getSubimage(619, 11, 61, 61), 180), new Color(55, 42, 25), 0.45f);
            
           
            //day tile

            tile[0] = new Tiles(tintImage(sheet.getSubimage(732, 87, 61, 61), new Color(55, 42, 25), 0.45f), false);
            tile[1] = new Tiles(tintImage(sheet.getSubimage(649, 160, 61, 61), new Color(55, 42, 25), 0.45f), false);
                        
            tile[2] = new Tiles(tintImage(sheet.getSubimage(810, 12, 61, 61), new Color(55, 42, 25), 0.45f), false); // bottom left corner intersection
            tile[3] = new Tiles(tintImage(sheet.getSubimage(877, 12, 61, 61), new Color(55, 42, 25), 0.45f), false); // bottom right corner intersection
            tile[4] = new Tiles(tintImage(sheet.getSubimage(683, 12, 61, 61), new Color(55, 42, 25), 0.45f), false); // top left left corner intersection
            tile[5] = new Tiles(tintImage(sheet.getSubimage(747, 12, 61, 61), new Color(55, 42, 25), 0.45f), false); // top right corner intersection
               
            
            // road edges
            tile[6] = new Tiles(bottomRoad, false); // bottom edge
            tile[7] = new Tiles(topRoad, false); // top edge
            tile[8] = new Tiles(leftRoad, false); // left edge
            tile[9] = new Tiles(tintImage(sheet.getSubimage(619, 11, 61, 61), new Color(55, 42, 25), 0.45f), false); // right edge
            
            
            //night tile
            BufferedImage bottomRoadD = getRotatedImage(nightSheet.getSubimage(619, 11, 61, 61), 90);
            BufferedImage topRoadD = getRotatedImage(nightSheet.getSubimage(619, 11, 61, 61), 270);
            BufferedImage leftRoadD = getRotatedImage(nightSheet.getSubimage(619, 11, 61, 61), 180);

            nightTile[0] = new Tiles(nightSheet.getSubimage(732, 87, 61, 61), false);
            nightTile[1] = new Tiles(sheet.getSubimage(649, 80, 61, 61), false);
            
            nightTile[2] = new Tiles(nightSheet.getSubimage(810, 12, 61, 61), false); // bottom left corner intersection
            nightTile[3] = new Tiles(nightSheet.getSubimage(877, 12, 61, 61), false); // bottom right corner intersection
            nightTile[4] = new Tiles(nightSheet.getSubimage(683, 12, 61, 61), false); // top left left corner intersection
            nightTile[5] = new Tiles(nightSheet.getSubimage(747, 12, 61, 61), false); // top right corner intersection

            nightTile[6] = new Tiles(bottomRoadD, false); // bottom 
            nightTile[7] = new Tiles(topRoadD, false); //top
            nightTile[8] = new Tiles(leftRoadD, false); // left
            nightTile[9] = new Tiles(nightSheet.getSubimage(619, 11, 61, 61), false); // right
            
            
            BufferedImage lowerBorder = getRotatedImage(wallSheet.getSubimage(32, 16, 16, 18), 90); //lower border center

            tile[10] = new Tiles(wallSheet.getSubimage(64, 144, 16, 16), false); //center floor 2
            tile[11] = new Tiles(wallSheet.getSubimage(16, 32, 16, 16), true); //top center wall 3
            tile[12] = new Tiles(wallSheet.getSubimage(16, 48, 16, 16), true); // top center middle wall 4
            tile[13] = new Tiles(wallSheet.getSubimage(16, 64, 16, 16), true); // top center bottom wall 5
            tile[14] = new Tiles(wallSheet.getSubimage(48, 16, 16, 16), true); //top left corner wall 6
            tile[15] = new Tiles(wallSheet.getSubimage(64, 16, 18, 16), true); // top right corner wall 7
            tile[16] = new Tiles(wallSheet.getSubimage(48, 48, 16, 16), true); // bottom left corner wall 8
            tile[17] = new Tiles(wallSheet.getSubimage(64, 48, 16, 16), true); // bottom right corner wall 9
            tile[18] = new Tiles(wallSheet.getSubimage(48, 64, 18, 16), true);  // bottom left border 10
            tile[19] = new Tiles(wallSheet.getSubimage(64, 64, 18, 16), true); //bottom right border 11
            tile[20] = new Tiles(lowerBorder, true); //12
            tile[21] = new Tiles(wallSheet.getSubimage(48, 32, 16, 16), true); // left wall 13
            tile[22] = new Tiles(createBlackTile(), true); //14
            tile[23] = new Tiles(wallSheet2.getSubimage(64, 144, 16, 16), true); // left floor 15
            tile[24] = new Tiles(wallSheet.getSubimage(78, 16, 16, 18), true); //top right wall 16
            tile[25] = new Tiles(wallSheet.getSubimage(0, 16, 16, 16), true); // top center right wall 17
            tile[26] = new Tiles(wallSheet.getSubimage(48, 16, 16, 16), true); //18
            tile[27] = new Tiles(wallSheet.getSubimage(48, 32, 16, 16), true); //19
            tile[28] = new Tiles(wallSheet.getSubimage(48, 48, 16, 16), true); //20

            for (int i = 10; i <= 38; i++) {
                
                nightTile[i] = tile[i];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage createBlackTile() {
        BufferedImage black = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = black.createGraphics();
        g2.setColor(java.awt.Color.BLACK);
        g2.fillRect(0, 0, 16, 16);
        g2.dispose();
        
        return black;
    }

    public int currentMap = 1;

    public void loadMap() {
        for (int c = 0; c < gp.maxWorldCol; c++) {
            for (int r = 0; r < gp.maxWorldRow; r++) {
                mapTileNum[c][r] = 22;
            }
        }
        
        try {
            String mapFile = currentMap == 1
                    ? "/assets/no_sanctuary_map/map1_level1.txt"
                    : "/assets/no_sanctuary_map/lvl_1_int.txt";
            InputStream is = getClass().getResourceAsStream(mapFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            for (int row = 0; row < gp.maxWorldRow; row++) {
                
                String line = br.readLine();
                
                if (line == null) break;
                
                line = line.trim();
                
                if (line.isEmpty()) {
                    
                    row--;
                    continue;
                }
                
                String[] numbers = line.split("\\s+");
                
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    
                    if (col < numbers.length && !numbers[col].isEmpty()) {
                        
                        mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                        
                    } else {
                        mapTileNum[col][row] = 22;
                    }
                }
            }
            
            br.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchMap(int mapNum) {
        
        currentMap = mapNum;
        loadMap();
    }

    public void draw(Graphics2D g2) {
        
        boolean isNight = gp.dC.currentState == dayCounter.dayNightState.Night;
        Tiles[] currentTile = isNight ? nightTile : tile;
        
        for (int row = 0; row < gp.maxWorldRow; row++) {
            
            for (int col = 0; col < gp.maxWorldCol; col++) {
                
                int tileNum = mapTileNum[col][row];
                if (tileNum != 2) continue;
                int screenX = (col * gp.tileSize) - gp.player.worldX + gp.player.screenX;
                int screenY = (row * gp.tileSize) - gp.player.worldY + gp.player.screenY;
                
                g2.drawImage(currentTile[tileNum].image, screenX, screenY, gp.tileSize + 4, gp.tileSize + 4, null);
            }
        }

        for (int row = 0; row < gp.maxWorldRow; row++) {
            
            for (int col = 0; col < gp.maxWorldCol; col++) {
                
                int tileNum = mapTileNum[col][row];
                
                if (tileNum == 2){
                    continue;
                }
                
                int screenX = (col * gp.tileSize) - gp.player.worldX + gp.player.screenX;
                int screenY = (row * gp.tileSize) - gp.player.worldY + gp.player.screenY;
                                
                g2.drawImage(currentTile[tileNum].image, screenX, screenY, gp.tileSize + 4, gp.tileSize + 4, null);
            }
        }
    }
}