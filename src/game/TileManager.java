/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

/**
 *
 * @author ADMIN
 */
public class TileManager {

    panel gp;
    Tiles[] tile;
    public int mapTileNum[][];

    public TileManager(panel gp) {
        this.gp = gp;
        tile = new Tiles[30];
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


    public void getTileImage() {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/no_sanctuary_map/MAP TILES.png"));
            BufferedImage wallSheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/walls_floor_1.png"));
            BufferedImage wallSheet2 = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/walls_floor_2.png"));

            BufferedImage lowerBorder = getRotatedImage(wallSheet.getSubimage(32, 16, 16, 18), 90);

            // Exterior tiles
            tile[0] = new Tiles(sheet.getSubimage(732, 87, 64, 63), false); // solid grass
            tile[1] = new Tiles(sheet.getSubimage(735, 157, 58, 51), false); // grass w/ flowers

            // Interior tiles
            tile[2] = new Tiles(wallSheet.getSubimage(64, 144, 16, 16), false); // floor
            tile[3] = new Tiles(wallSheet.getSubimage(16, 32, 16, 16), true); // wall upper center
            tile[4] = new Tiles(wallSheet.getSubimage(16, 48, 16, 16), true); // wall middle center
            tile[5] = new Tiles(wallSheet.getSubimage(16, 64, 16, 16), true); // wall lower center
            tile[6] = new Tiles(wallSheet.getSubimage(48, 16, 16, 16), true); // wall upper left corner
            tile[7] = new Tiles(wallSheet.getSubimage(64, 16, 18, 16), true); // wall upper right corner
            tile[8] = new Tiles(wallSheet.getSubimage(48, 48, 16, 16), true); // wall bottom left corner
            tile[9] = new Tiles(wallSheet.getSubimage(64, 48, 16, 16), true); // wall bottom right corner
            tile[10] = new Tiles(wallSheet.getSubimage(48, 64, 18, 16), true); // bottom left border
            tile[11] = new Tiles(wallSheet.getSubimage(64, 64, 18, 16), true); // bottom right border

            tile[12] = new Tiles((lowerBorder), true); // lower center border

            tile[13] = new Tiles(wallSheet.getSubimage(48, 32, 16, 16), true); // upper left wall
            tile[14] = new Tiles(createBlackTile(), true); // void/black
            
            //papalitan nadoble
            tile[15] = new Tiles(wallSheet2.getSubimage(64, 144, 16, 16), true); // left wall floor
            
            tile[16] = new Tiles(wallSheet.getSubimage(78, 16, 16, 18), true); // right border
            
            tile[17] = new Tiles(wallSheet.getSubimage(0, 16, 16, 16), true); // left border
            tile[18] = new Tiles(wallSheet.getSubimage(48, 16, 16, 16), true); // room upper left wall corner
            tile[19] = new Tiles(wallSheet.getSubimage(48, 32, 16, 16), true); // room mid wall
            tile[20] = new Tiles(wallSheet.getSubimage(48, 48, 16, 16), true); // room left wall corner

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method for black tile
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
                mapTileNum[c][r] = 14;
            }
        }

        try {
            String mapFile = currentMap == 1
                    ? "/assets/no_sanctuary_map/lvl_1_ext.txt"
                    : "/assets/no_sanctuary_map/lvl_1_int.txt";
            InputStream is = getClass().getResourceAsStream(mapFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            for (int row = 0; row < gp.maxWorldRow; row++) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
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
                        mapTileNum[col][row] = 14;
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
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize + 4, gp.tileSize + 4, null);

            worldCol++;
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }

}
