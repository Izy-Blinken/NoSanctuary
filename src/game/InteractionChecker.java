package game;

public class InteractionChecker {

    panel gp;

    public boolean showDoorPrompt = false;
    public boolean showExitPrompt = false;
    public boolean showWindowPrompt = false;
    public int nearWindowNum = 0;
    public boolean showTorchPrompt = false;
    public boolean showCabinetPrompt = false;
    public boolean showAppleTablePrompt = false;

    public String torchFeedback = "";
    public String cabinetFeedback = "";
    public String appleTableFeedback = "";

    private int torchFeedbackTimer = 0;
    private int cabinetFeedbackTimer = 0;
    private int appleTableFeedbackTimer = 0;

    private int intDoorX = 136, intDoorY = 128;
    private int intWin1X = 292, intWin1Y = 128;
    private int intWin2X = 400, intWin2Y = 128;
    private int intWin3X = 528, intWin3Y = 128;

    private static final int INTERACT_DIST = 70;
    private static final int CABINET_DIST = 120;

    public InteractionChecker(panel gp) {
        this.gp = gp;
    }

    public void checkInteraction() {
        
        for (int i = 0; i < gp.objectM.ObjHouse.length; i++) {
            
            if (gp.objectM.ObjHouse[i] == null) {
                continue;
            }

            models.ObjHouse house = (models.ObjHouse) gp.objectM.ObjHouse[i];
            
            int doorWorldX = gp.objectM.ObjHouse[i].worldX + house.doorOffsetX;
            int doorWorldY = gp.objectM.ObjHouse[i].worldY + house.doorOffsetY;
            
            int dist = Math.abs(gp.player.worldX - doorWorldX)
                     + Math.abs(gp.player.worldY - doorWorldY);

            if (dist < 150) {
                
                showDoorPrompt = true;
                
                if (gp.keyH.interactPressed) {
                    
                    if (!house.isDoorOpen) {
                        
                        house.toggleDoor();
                        
                    }else { 
                        
                        gp.switchToInterior(); showDoorPrompt = false;
                    }
                    
                    gp.keyH.interactPressed = false;
                    
                    break;
                }
                
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
        
        models.ObjHouse house  = (models.ObjHouse) gp.objectM.ObjHouse[0];
        
        models.ObjInterior interior = gp.objectM.interior;
        models.ObjCabinet cabinet = interior.cabinet;
        models.ObjAppleTable appleTable = interior.appleTable;

        tickFeedback();

        // Exit door
        int distDoor = Math.abs(gp.player.worldX - intDoorX)
                     + Math.abs(gp.player.worldY - intDoorY);
        
        if (distDoor < INTERACT_DIST) {
            
            showExitPrompt = true;
            
            if (gp.keyH.closePressed) { 
                
                house.toggleDoor(); gp.keyH.closePressed = false;
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
            
            int dist = Math.abs(gp.player.worldX - winPos[i][0])
                     + Math.abs(gp.player.worldY - winPos[i][1]);
            
            if (dist < INTERACT_DIST) {
                
                nearWindowNum = i + 1;
                showWindowPrompt = true;
                
                if (gp.keyH.interactPressed) {
                    
                    house.toggleWindow(nearWindowNum);
                    gp.keyH.interactPressed = false;
                }
                
                break;
            }
        }
        
        if (nearWindowNum == 0) {
            
            showWindowPrompt = false;
        }

        // Torches
        models.ObjTorch[] torches = {interior.torch, interior.torch2, interior.torch3};
        showTorchPrompt = false;
        
        for (models.ObjTorch t : torches) {
            
            int torchDist = Math.abs(gp.player.worldX - t.worldX)
                          + Math.abs(gp.player.worldY - t.worldY);
            
            if (torchDist < INTERACT_DIST) {
                
                showTorchPrompt = true;
                
                if (gp.keyH.interactPressed) {
                    
                    boolean ok = t.refuel(gp.inventory);
                    torchFeedback = ok ? "+3 Wood burned! (" + t.getSecondsLeft() + "s)" : "Need " + models.ObjTorch.WOOD_BATCH + " woods! (Have: " + gp.inventory.wood + ")";
                    torchFeedbackTimer = 120;
                    gp.keyH.interactPressed = false;
                }
                
                break;
            }
        }

        // Cabinet
        int cabDist = Math.abs(gp.player.worldX - cabinet.worldX)
                    + Math.abs(gp.player.worldY - cabinet.worldY);
        
        if (cabDist < CABINET_DIST) {
            
            showCabinetPrompt = true;
            
            if (gp.keyH.interactPressed) {
                
                cabinet.toggleUI();
                gp.keyH.interactPressed = false;
            }
            if (cabinet.isOpen && gp.keyH.depositPressed) {
                
                int w = cabinet.depositWood(gp.inventory);
                
                cabinetFeedback = "Deposited: " + w + " wood";
                cabinetFeedbackTimer = 120;
                gp.keyH.depositPressed = false;
            }
            if (cabinet.isOpen && gp.keyH.withdrawPressed) {
                
                int w = cabinet.withdrawWood(gp.inventory);
                
                cabinetFeedback = "Withdrew: " + w + " wood";
                cabinetFeedbackTimer = 120;
                gp.keyH.withdrawPressed = false;
            }
            
        } else {
            
            showCabinetPrompt = false;
            cabinet.closeUI();
        }

        // Apple
        int tblDist = Math.abs(gp.player.worldX - appleTable.worldX)
                    + Math.abs(gp.player.worldY - appleTable.worldY);
        
        if (tblDist < INTERACT_DIST) {
            
            showAppleTablePrompt = true;
            
            if (gp.keyH.interactPressed) {
                
                appleTable.toggleUI();
                gp.keyH.interactPressed = false;
            }
            
            if (appleTable.isOpen && gp.keyH.appleDepositPressed) {
                
                int a = appleTable.depositApple(gp.inventory);
                
                appleTableFeedback = "Deposited: " + a + " apple";
                appleTableFeedbackTimer = 120;
                gp.keyH.appleDepositPressed = false;
            }
            if (appleTable.isOpen && gp.keyH.appleWithdrawPressed) {
                
                int a = appleTable.withdrawApple(gp.inventory);
                
                appleTableFeedback = "Withdrew: " + a + " apple";
                appleTableFeedbackTimer = 120;
                gp.keyH.appleWithdrawPressed = false;
            }
            
        } else {
            showAppleTablePrompt = false;
            appleTable.closeUI();
        }
    }

    private void tickFeedback() {
        
        if (torchFeedbackTimer > 0) {
            
            torchFeedbackTimer--;
            
        } else {
            torchFeedback = "";
        }
        
        if (cabinetFeedbackTimer > 0) {
            
            cabinetFeedbackTimer--;
            
        } else {
            cabinetFeedback = "";
        }
        
        if (appleTableFeedbackTimer > 0){
            
            appleTableFeedbackTimer--;
            
        } else {
            appleTableFeedback = "";
        }
    }

    public boolean hasTorchFeedback() { 
        
        return !torchFeedback.isEmpty(); 
    }
    
    public boolean hasCabinetFeedback() { 
        
        return !cabinetFeedback.isEmpty();
    }
    
    public boolean hasAppleTableFeedback() {
        
        return !appleTableFeedback.isEmpty(); 
    }
}