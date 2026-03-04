package game;

public class InteractionChecker {

    panel gp;

    //outside
    public boolean showDoorPrompt = false;

    //inside
    public boolean showExitPrompt = false;
    public boolean showWindowPrompt = false;
    public int nearWindowNum = 0;

    private int intDoorX = 136, intDoorY = 128;
    private int intWin1X = 292, intWin1Y = 128;
    private int intWin2X = 400, intWin2Y = 128;
    private int intWin3X = 528, intWin3Y = 128;

    public InteractionChecker(panel gp) {
        this.gp = gp;
    }

    public void checkInteraction() {

        for (int i = 0; i < gp.objectM.ObjHouse.length; i++) {

            if (gp.objectM.ObjHouse[i] == null) {
                continue;
            }

            models.ObjHouse house = (models.ObjHouse) gp.objectM.ObjHouse[i];

            // Actual door position sa world
            int doorWorldX = gp.objectM.ObjHouse[i].worldX + ((models.ObjHouse) gp.objectM.ObjHouse[i]).doorOffsetX;
            int doorWorldY = gp.objectM.ObjHouse[i].worldY + ((models.ObjHouse) gp.objectM.ObjHouse[i]).doorOffsetY;

            int dist = Math.abs(gp.player.worldX - doorWorldX)
                    + Math.abs(gp.player.worldY - doorWorldY);

            if (dist < 150) {
                
                showDoorPrompt = true;

                if (gp.keyH.interactPressed) {
                    
                    if (!house.isDoorOpen) {
                        house.toggleDoor(); // open door
                        
                    } else {
                        gp.switchToInterior(); //if nag 'E' while door is open
                        showDoorPrompt = false;
                    }

                    gp.keyH.interactPressed = false;
                    break;
                }

                // F key -> close door
                if (gp.keyH.closePressed && house.isDoorOpen) {
                    house.toggleDoor();
                    showDoorPrompt = false;
                    gp.keyH.closePressed = false;

                }
            } else {
                showDoorPrompt = false;
            }
        }

    }

    public void checkInteriorInteraction() {
        models.ObjHouse house = (models.ObjHouse) gp.objectM.ObjHouse[0];

        // Door (exit)
        int distDoor = Math.abs(gp.player.worldX - intDoorX) + Math.abs(gp.player.worldY - intDoorY);
        if (distDoor < 70) {
            
            showExitPrompt = true;
            
            if (gp.keyH.closePressed) {
                house.toggleDoor();
                gp.keyH.closePressed = false;
            }
            if (gp.keyH.interactPressed && house.isDoorOpen) {
                gp.switchToExterior();
                gp.keyH.interactPressed = false;
                showExitPrompt = false;
                return;
            }
        } else {
            showExitPrompt = false;
        }

        // Windows
        int[][] winPos = {{intWin1X, intWin1Y}, {intWin2X, intWin2Y}, {intWin3X, intWin3Y}};
        nearWindowNum = 0;
        
        for (int i = 0; i < 3; i++) {
            
            int dist = Math.abs(gp.player.worldX - winPos[i][0]) + Math.abs(gp.player.worldY - winPos[i][1]);
            if (dist < 70) {
                
                nearWindowNum = i + 1;
                showWindowPrompt = true;
                
                if (gp.keyH.interactPressed) {
                    
                    house.toggleWindow(nearWindowNum);
                    gp.keyH.interactPressed = false;
                }
                return;
            }
        }
        showWindowPrompt = false;
    }
}
