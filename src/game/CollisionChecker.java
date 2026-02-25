/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
            if (objArray[i] == null) continue;
            if (!objArray[i].collision) continue;
            if (objArray[i].solidArea == null) continue;

            int entityLeftX   = entity.worldX + entity.solidArea.x;
            int entityRightX  = entity.worldX + entity.solidArea.x + entity.solidArea.width;
            int entityTopY    = entity.worldY + entity.solidArea.y;
            int entityBottomY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

            int objLeftX   = objArray[i].worldX + objArray[i].solidArea.x;
            int objRightX  = objArray[i].worldX + objArray[i].solidArea.x + objArray[i].solidArea.width;
            int objTopY    = objArray[i].worldY + objArray[i].solidArea.y;
            int objBottomY = objArray[i].worldY + objArray[i].solidArea.y + objArray[i].solidArea.height;

            switch (entity.direction) {
                case "up":    entityTopY    -= entity.speed; break;
                case "down":  entityBottomY += entity.speed; break;
                case "left":  entityLeftX   -= entity.speed; break;
                case "right": entityRightX  += entity.speed; break;
            }

            if (entityRightX > objLeftX && entityLeftX < objRightX &&
                entityBottomY > objTopY && entityTopY < objBottomY) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTile(Entity entity) {
        int entityLeftX   = entity.worldX + entity.solidArea.x;
        int entityRightX  = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopY    = entity.worldY + entity.solidArea.y;
        int entityBottomY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int leftCol   = entityLeftX  / gp.tileSize;
        int rightCol  = entityRightX / gp.tileSize;
        int topRow    = entityTopY    / gp.tileSize;
        int bottomRow = entityBottomY / gp.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                topRow = (entityTopY - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[leftCol][topRow];
                tileNum2 = gp.tileM.mapTileNum[rightCol][topRow];
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
            case "down":
                bottomRow = (entityBottomY + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[leftCol][bottomRow];
                tileNum2 = gp.tileM.mapTileNum[rightCol][bottomRow];
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
            case "left":
                leftCol = (entityLeftX - entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[leftCol][topRow];
                tileNum2 = gp.tileM.mapTileNum[leftCol][bottomRow];
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
            case "right":
                rightCol = (entityRightX + entity.speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[rightCol][topRow];
                tileNum2 = gp.tileM.mapTileNum[rightCol][bottomRow];
                return gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision;
        }
        return false;
    }
}
