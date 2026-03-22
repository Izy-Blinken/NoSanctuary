package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ObjInterior extends GameObject {

    panel gp;

    BufferedImage doorClosed, doorOpened;
    BufferedImage winClosed, winOpened;

    public ObjTorch torch;
    public ObjTorch torch2;
    public ObjTorch torch3;
    public ObjCabinet cabinet;
    public ObjAppleTable appleTable;
    public ObjInteriorItems items;

    private static final int torchX = 90;
    private static final int torchY = 65;
    private static final int torch2X = 282;
    private static final int torch2Y = 448;
    private static final int torch3X = 954;
    private static final int torch3Y = 400;
    private static final int cabinetX = 816;
    private static final int cabinetY = 52;
    private static final int appleTableX = 826;
    private static final int appleTableY = 228;

    public ObjInterior(panel gp) {
        
        this.gp = gp;

        try {
            
            BufferedImage doorsWin = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/Doors_windows_animation.png"));

            doorClosed = scaleImg(doorsWin.getSubimage(31, 0, 32, 32), 2.6);
            doorOpened = scaleImg(doorsWin.getSubimage(63, 96, 32, 32), 2.6);
            winClosed = scaleImg(doorsWin.getSubimage(105, 3, 20, 20), 2.5);
            winOpened = scaleImg(doorsWin.getSubimage(105, 98, 20, 20), 2.5);

        } catch (Exception e) {
            e.printStackTrace();
        }

        torch = new ObjTorch(gp);
        torch.worldX = torchX;
        torch.worldY = torchY;
        
        torch2 = new ObjTorch(gp);
        torch2.worldX = torch2X;
        torch2.worldY = torch2Y;
        
        torch3 = new ObjTorch(gp);
        torch3.worldX = torch3X;
        torch3.worldY = torch3Y;

        cabinet = new ObjCabinet(gp);
        cabinet.worldX = cabinetX;
        cabinet.worldY = cabinetY;

        appleTable = new ObjAppleTable(gp);
        appleTable.worldX = appleTableX;
        appleTable.worldY = appleTableY;

        items = new ObjInteriorItems(gp);
    }

    private BufferedImage scaleImg(BufferedImage src, double scale) {
        
        int w = (int)(src.getWidth() * scale);
        int h = (int)(src.getHeight() * scale);
        
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        
        return out;
    }

    public void update() {
        
        torch.update();
        torch2.update();
        torch3.update();
    }

    public void draw(Graphics2D g2, int screenX, int screenY, ObjHouse house) {
        
        items.draw(g2);

        // Door
        int doorSX = (3 * gp.tileSize) - gp.player.worldX + gp.player.screenX;
        int doorSY = (1 * gp.tileSize) - gp.player.worldY + gp.player.screenY + 15;
        
        g2.drawImage(house.isDoorOpen ? doorOpened : doorClosed, doorSX, doorSY, null);

        // Windows
        int[] winX = {292, 410, 528};
        int[] winY = {60, 60, 60};
        
        for (int i = 0; i < winX.length; i++) {
            
            int wx = winX[i] - gp.player.worldX + gp.player.screenX;
            int wy = winY[i] - gp.player.worldY + gp.player.screenY;
            boolean isOpen = (i == 0) ? house.isWindow1Open
                           : (i == 1) ? house.isWindow2Open
                           : house.isWindow3Open;
            g2.drawImage(isOpen ? winOpened : winClosed, wx, wy, null);
        }

        // Torch
        int torchSX = torch.worldX - gp.player.worldX + gp.player.screenX;
        int torchSY = torch.worldY - gp.player.worldY + gp.player.screenY;
        torch.draw(g2, torchSX, torchSY);
        
        int torchSX2 = torch2.worldX - gp.player.worldX + gp.player.screenX;
        int torchSY2 = torch2.worldY - gp.player.worldY + gp.player.screenY;
        torch2.draw(g2, torchSX2, torchSY2);
        
        int torchSX3 = torch3.worldX - gp.player.worldX + gp.player.screenX;
        int torchSY3 = torch3.worldY - gp.player.worldY + gp.player.screenY;
        torch3.draw(g2, torchSX3, torchSY3);

        // Cabinet
        int cabSX = cabinet.worldX - gp.player.worldX + gp.player.screenX;
        int cabSY = cabinet.worldY - gp.player.worldY + gp.player.screenY;
        cabinet.draw(g2, cabSX, cabSY);
        cabinet.drawUI(g2, cabSX, cabSY);

        // Apple
        int tableSX = appleTable.worldX - gp.player.worldX + gp.player.screenX;
        int tableSY = appleTable.worldY - gp.player.worldY + gp.player.screenY;
        appleTable.draw(g2, tableSX, tableSY);
        appleTable.drawUI(g2, tableSX, tableSY);
    }

    public boolean isInteriorLit() {
        return torch.isLit || torch2.isLit || torch3.isLit;
    }
}
