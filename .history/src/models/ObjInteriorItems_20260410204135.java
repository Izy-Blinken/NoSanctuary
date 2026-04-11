package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ObjInteriorItems {

    panel gp;
    private static final double scale = 3;

    private BufferedImage bed;
    private BufferedImage bookshelf;
    private BufferedImage livingRoomTable;
    private BufferedImage firstRoomTable;
    private BufferedImage ovalRug;
    private BufferedImage bigOvalRug;
    private BufferedImage straightRug;
    private BufferedImage straightRugMid;
    private BufferedImage chair2;
    private BufferedImage oneBox;
    private BufferedImage twoBoxes;
    private BufferedImage drawer;
    private BufferedImage vase;

    // items pos
    public int bedX = 62, bedY = 484;
    public int bookshelfX = 929, bookshelfY = 52;
    public int livingTableX = 200, livingTableY = 600;
    //public int firstTableX = 126, firstTableY = 574;
    public int ovalRugX = 468, ovalRugY = 354;
    public int straightRugX = 808, straightRugY = 230;
    //public int straightRugMidX = 430, straightRugMidY = 340;
    public int bigOvalRugX = 384, bigOvalRugY = 322;
    public int chairX = 240, chairY = 535;
    public int chair2X = 243, chair2Y = 660;
    public int oneBoxX = 52, oneBoxY = 653;
    public int oneBox2X = 98, oneBox2Y = 653;
    public int oneBox3X = 348, oneBox3Y = 102;
    public int oneBox4X = 746, oneBox4Y = 434;
    public int twoBoxesX = 638, twoBoxesY = 102;
    //public int twoBoxes2X = 360, twoBoxes2Y = 668;
    public int drawerX = 780, drawerY = 110;
    public int drawer1X = 745, drawer1Y = 110;
    public int vaseX = 777, vaseY = 75;

    public ObjInteriorItems(panel gp) {
        
        this.gp = gp;
        loadSprites();
    }

    private void loadSprites() {
        
        try {
            
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/interior.png"));
            
            drawer = scale(sheet.getSubimage(139, 22, 12, 20));
            bed = scale(sheet.getSubimage(67, 16, 23, 39));
            bookshelf = scale(sheet.getSubimage(179, 97, 26, 44));
            livingRoomTable = scale(sheet.getSubimage(20, 208, 40, 35));
            //firstRoomTable = scale(sheet.getSubimage(132, 58, 26, 32));
            //ovalRug = scale(sheet.getSubimage(142, 265, 52, 62));
            straightRug = scale(sheet.getSubimage(157, 336, 37, 32));
            //straightRugMid = scale(sheet.getSubimage(16, 352, 16, 16));
            //chair = scale(sheet.getSubimage(129, 193, 14, 23));
            chair2 = scale(sheet.getSubimage(130, 228, 12, 21));
            oneBox = scale(sheet.getSubimage(176, 51, 16, 22));
            twoBoxes = scale(sheet.getSubimage(69, 65, 26, 24));
            //aroundContainer = scale(sheet.getSubimage(173, 18, 16, 24));
            vase = scale(sheet.getSubimage(16, 372, 12, 20));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage scale(BufferedImage src) {
        
        int w = (int)(src.getWidth() * scale);
        int h = (int)(src.getHeight() * scale);
        
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        
        return out;
    }

    private int toScreenX(int worldX) {
        
        return worldX - gp.player.worldX + gp.player.screenX;
    }

    private int toScreenY(int worldY) {
        
        return worldY - gp.player.worldY + gp.player.screenY;
    }

    public void draw(Graphics2D g2) {
        
        drawImg(g2, ovalRug, ovalRugX, ovalRugY);
        drawImg(g2, straightRug, straightRugX, straightRugY);
        //drawImg(g2, straightRugMid, straightRugMidX, straightRugMidY);
        drawImg(g2, bed, bedX, bedY);
        drawImg(g2, bookshelf, bookshelfX, bookshelfY);
        drawImg(g2, livingRoomTable, livingTableX, livingTableY);
        //drawImg(g2, firstRoomTable, firstTableX, firstTableY);
        drawImg(g2, drawer, drawerX, drawerY);
        drawImg(g2, drawer, drawer1X, drawer1Y);
        
        drawImg(g2, chair2, chair2X, chair2Y);
        drawImg(g2, oneBox, oneBoxX, oneBoxY);
        drawImg(g2, oneBox, oneBox2X, oneBox2Y);
        drawImg(g2, oneBox, oneBox3X, oneBox3Y);
        drawImg(g2, oneBox, oneBox4X, oneBox4Y);
        drawImg(g2, twoBoxes, twoBoxesX, twoBoxesY);
        //drawImg(g2, twoBoxes, twoBoxes2X, twoBoxes2Y);
        drawImg(g2, vase, vaseX, vaseY);
    }

    private void drawImg(Graphics2D g2, BufferedImage img, int worldX, int worldY) {
        
        if (img == null) {
            return;
        }
        
        g2.drawImage(img, toScreenX(worldX), toScreenY(worldY), null);
    }
}
