
package game;

import java.awt.Rectangle;
import models.Entity;
import models.GameObject;

/**
 *
 * @author ADMIN
 */
public class CollisionChecker {

    panel gp;

    public CollisionChecker(panel gp) {
        this.gp = gp;
    }

    public boolean checkObject(Entity entity, GameObject[] objArray) {
        
        for (int i = 0; i < objArray.length; i++) {
            if (objArray[i] == null) {
                continue;
            }
            if (!objArray[i].collision) {
                continue;
            }
            if (objArray[i].solidArea == null) {
                continue;
            }

            int entityLeftX = entity.worldX + entity.solidArea.x;
            int entityRightX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
            int entityTopY = entity.worldY + entity.solidArea.y;
            int entityBottomY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

            int objLeftX = objArray[i].worldX + objArray[i].solidArea.x;
            int objRightX = objArray[i].worldX + objArray[i].solidArea.x + objArray[i].solidArea.width;
            int objTopY = objArray[i].worldY + objArray[i].solidArea.y;
            int objBottomY = objArray[i].worldY + objArray[i].solidArea.y + objArray[i].solidArea.height;

            switch (entity.direction) {
                case "up":
                    entityTopY -= entity.speed;
                    break;
                case "down":
                    entityBottomY += entity.speed;
                    break;
                case "left":
                    entityLeftX -= entity.speed;
                    break;
                case "right":
                    entityRightX += entity.speed;
                    break;
            }

            if (entityRightX > objLeftX && entityLeftX < objRightX
                    && entityBottomY > objTopY && entityTopY < objBottomY) {
                return true;
            }
        }
        
        return false;
    }

    public boolean checkTile(Entity entity) {
        
        int entityLeftX = entity.worldX + entity.solidArea.x;
        int entityRightX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopY = entity.worldY + entity.solidArea.y;
        int entityBottomY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int maxCol = gp.tileM.currentMap == 1 ? gp.maxWorldCol - 1 : 21;
        int maxRow = gp.tileM.currentMap == 1 ? gp.maxWorldRow - 1 : 15;

        int leftCol = Math.max(0, Math.min(entityLeftX / gp.tileSize, maxCol));
        int rightCol = Math.max(0, Math.min(entityRightX / gp.tileSize, maxCol));
        int topRow = Math.max(0, Math.min(entityTopY / gp.tileSize, maxRow));
        int bottomRow = Math.max(0, Math.min(entityBottomY / gp.tileSize, maxRow));

        int tileNum1, tileNum2;
        
        switch (entity.direction) {
            
            case "up":
                topRow = Math.max(0, (entityTopY - entity.speed) / gp.tileSize);
                tileNum1 = gp.tileM.mapTileNum[leftCol][topRow];
                tileNum2 = gp.tileM.mapTileNum[rightCol][topRow];
                
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
            
            case "down":
                bottomRow = Math.min(maxRow, (entityBottomY + entity.speed) / gp.tileSize);
                tileNum1 = gp.tileM.mapTileNum[leftCol][bottomRow];
                tileNum2 = gp.tileM.mapTileNum[rightCol][bottomRow];
                
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
            
            case "left":
                leftCol = Math.max(0, (entityLeftX - entity.speed) / gp.tileSize);
                tileNum1 = gp.tileM.mapTileNum[leftCol][topRow];
                tileNum2 = gp.tileM.mapTileNum[leftCol][bottomRow];
                
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
           
            case "right":
                rightCol = Math.min(maxCol, (entityRightX + entity.speed) / gp.tileSize);
                tileNum1 = gp.tileM.mapTileNum[rightCol][topRow];
                
                tileNum2 = gp.tileM.mapTileNum[rightCol][bottomRow];
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
        }
        
        return false;
    }
}
