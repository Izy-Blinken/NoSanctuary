package models;

import game.panel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ObjInteriorItems {

    panel gp;
    private static final double scale = 2.5;

    private BufferedImage bed;
    private BufferedImage bookshelf;
    private BufferedImage livingRoomTable;
    private BufferedImage firstRoomTable;
    private BufferedImage ovalRug;
    private BufferedImage bigOvalRug;
    private BufferedImage straightRug;
    private BufferedImage straightRugMid;
    private BufferedImage chair;
    private BufferedImage oneBox;
    private BufferedImage twoBoxes;
    private BufferedImage roundContainer;

    // items pos
   // public int bedX = 745, bedY = 57;
    public int bookshelfX = 735, bookshelfY = 60;
    public int livingTableX = 484, livingTableY = 372;
    public int firstTableX = 126, firstTableY = 574;
    public int ovalRugX = 121, ovalRugY = 124;
    //public int straightRugX = 430, straightRugY = 300;
    //public int straightRugMidX = 430, straightRugMidY = 340;
    //public int bigOvalRugX = 384, bigOvalRugY = 322;
    public int chair2X = 516, chair2Y = 412;
    public int oneBoxX = 300, oneBoxY = 508;
    public int twoBoxesX = 924, twoBoxesY = 128;
    public int twoBoxes2X = 360, twoBoxes2Y = 668;
    public int roundContX = 98, roundContY = 508;

    public ObjInteriorItems(panel gp) {
        
        this.gp = gp;
        loadSprites();
    }

    private void loadSprites() {
        
        try {
            
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/assets/EX_INT PNG/interior.png"));

            //bed = scale(sheet.getSubimage(67, 16, 23, 39));
            bookshelf = scale(sheet.getSubimage(179, 97, 26, 44));
            livingRoomTable = scale(sheet.getSubimage(20, 208, 40, 35));
            firstRoomTable = scale(sheet.getSubimage(132, 58, 26, 32));
            ovalRug = scale(sheet.getSubimage(142, 265, 52, 62));
            //bigOvalRug = scale(sheet.getSubimage(28, 264, 91, 80));
            straightRug = scale(sheet.getSubimage(157, 336, 37, 32));
            straightRugMid = scale(sheet.getSubimage(16, 352, 16, 16));
            chair = scale(sheet.getSubimage(130, 228, 12, 21));
            oneBox = scale(sheet.getSubimage(176, 51, 16, 22));
            twoBoxes = scale(sheet.getSubimage(69, 65, 26, 24));
            roundContainer = scale(sheet.getSubimage(173, 18, 16, 24));

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
        //drawImg(g2, bigOvalRug, bigOvalRugX, bigOvalRugY);
        //drawImg(g2, straightRug, straightRugX, straightRugY);
        //drawImg(g2, straightRugMid, straightRugMidX, straightRugMidY);
        //drawImg(g2, bed, bedX, bedY);
        drawImg(g2, bookshelf, bookshelfX, bookshelfY);
        drawImg(g2, livingRoomTable, livingTableX, livingTableY);
        drawImg(g2, firstRoomTable, firstTableX, firstTableY);
        
        drawImg(g2, chair, chair2X, chair2Y);
        drawImg(g2, oneBox, oneBoxX, oneBoxY);
        drawImg(g2, twoBoxes, twoBoxesX, twoBoxesY);
        drawImg(g2, twoBoxes, twoBoxes2X, twoBoxes2Y);
        drawImg(g2, roundContainer, roundContX, roundContY);
    }

    private void drawImg(Graphics2D g2, BufferedImage img, int worldX, int worldY) {
        
        if (img == null) {
            return;
        }
        
        g2.drawImage(img, toScreenX(worldX), toScreenY(worldY), null);
    }
}