package game;

import models.Entity;
import models.ObjHouse;
import models.ObjRiddle;
import models.ObjTorch;

public class InteractionChecker {

    panel gp;

    // Prompt flags
    public boolean showDoorPrompt = false;
    public boolean showExitPrompt = false;
    public boolean showWindowPrompt = false;
    public boolean showPortalPrompt = false;
    public int nearWindowNum = 0;
    public boolean showTorchPrompt = false;
    public boolean showCabinetPrompt = false;
    public boolean showAppleTablePrompt = false;
    public boolean showRiddlePrompt = false;
    public int nearRiddleIndex = -1;
    public boolean showShelterPrompt = false;

    // Feedback messages
    public String torchFeedback = "";
    public String cabinetFeedback = "";
    public String appleTableFeedback = "";

    // Feedback timers
    private int torchFeedbackTimer = 0;
    private int cabinetFeedbackTimer = 0;
    private int appleTableFeedbackTimer = 0;

    // Interior coordinates
    private int intDoorX = 136, intDoorY = 128;
    private int intWin1X = 292, intWin1Y = 128;
    private int intWin2X = 400, intWin2Y = 128;
    private int intWin3X = 528, intWin3Y = 128;

    // Distance constants
    private static final int INTERACT_DIST = 70;
    private static final int DOOR_INTERACT_DIST = 100;
    private static final int CABINET_DIST = 120;
    private static final int RIDDLE_DIST = 80;
    private static final int PORTAL_DIST = 110;

    // Debounce for interaction key
    private boolean interactKeyWasPressed = false;
    private int interactDebounceTimer = 0;
    private static final int INTERACT_DEBOUNCE = 10;

    public InteractionChecker(panel gp) {
        
        this.gp = gp;
    }

    public void checkInteraction() {
        
        showShelterPrompt = false;
        
        if (interactDebounceTimer > 0) {
            interactDebounceTimer--;
        }

        boolean interactJustPressed = false;
        
        if (gp.keyH.interactPressed && !interactKeyWasPressed && interactDebounceTimer == 0) {
            
            interactJustPressed = true;
            interactDebounceTimer = INTERACT_DEBOUNCE;
        }
        
        interactKeyWasPressed = gp.keyH.interactPressed;

        // Reset prompts
        showDoorPrompt = false;
        showRiddlePrompt = false;
        nearRiddleIndex = -1;
        showPortalPrompt = false;

        checkDoorInteraction(interactJustPressed);

        checkRiddleInteraction(interactJustPressed);

        checkPortalInteraction(interactJustPressed);
        
        checkShelterInteraction(interactJustPressed);
    }
    
    private void checkShelterInteraction(boolean interactJustPressed) {

        if (gp.objectM.ObjUndergroundShelter == null) return;

        showShelterPrompt = false;

        for (int i = 0; i < gp.objectM.ObjUndergroundShelter.length; i++) {

            if (gp.objectM.ObjUndergroundShelter[i] == null) continue;

            models.ObjUndergroundShelter shelter = (models.ObjUndergroundShelter) gp.objectM.ObjUndergroundShelter[i];

            if (shelter.used) continue;
            if (gp.dC.countdownActive) continue;

            double dist = getDistance(gp.player.worldX, gp.player.worldY, shelter.worldX, shelter.worldY);

            if (dist < INTERACT_DIST) {

                showShelterPrompt = true;

                if (interactJustPressed) {
                    
                    shelter.discovered = true;
                    shelter.used = true;
                    gp.keyH.interactPressed = false;
                    gp.switchToInterior();
                }

                break;
            }
        }
    }

    private void checkDoorInteraction(boolean interactJustPressed) {
        
        if (gp.objectM.ObjHouse == null || gp.objectM.ObjHouse.length == 0) {
            return;
        }

        for (int i = 0; i < gp.objectM.ObjHouse.length; i++) {
            
            if (gp.objectM.ObjHouse[i] == null) {
                
                continue;
            }

            ObjHouse house = (ObjHouse) gp.objectM.ObjHouse[i];
            
            int doorWorldX = gp.objectM.ObjHouse[i].worldX + house.doorOffsetX;
            int doorWorldY = gp.objectM.ObjHouse[i].worldY + house.doorOffsetY;
            
            double dist = getDistance(gp.player.worldX, gp.player.worldY, doorWorldX, doorWorldY);

            if (dist < DOOR_INTERACT_DIST) {
                
                showDoorPrompt = true;

                boolean facingDoor = isPlayerFacingDoor(house, doorWorldX, doorWorldY);

                if (interactJustPressed && facingDoor) {
                    
                    gp.keyH.interactPressed = false;
                    
                    if (!house.isDoorOpen && !house.doorLocked) {
                        
                        house.toggleDoor();
                        gp.doorCreak.play();
                        
                    } else if (house.isDoorOpen && !house.doorLocked){
                        
                        gp.switchToInterior();
                        showDoorPrompt = false;
                    }
                    
                    break;
                }

                if (gp.keyH.closePressed && house.isDoorOpen) {
                    
                    house.toggleDoor();
                    gp.doorCreak.play();
                    gp.keyH.closePressed = false;
                    showDoorPrompt = false;
                }
            }
        }
    }

    private void checkRiddleInteraction(boolean interactJustPressed) {
        
        if (gp.objectM.riddleObjects == null) {
            return;
        }

        double closestDist = Double.MAX_VALUE;
        int closestIndex = -1;

        for (int i = 0; i < gp.objectM.riddleObjects.length; i++) {
            
            if (gp.objectM.riddleObjects[i] == null) {
                
                continue;
            }

            double dist = getDistance(
                gp.player.worldX, 
                gp.player.worldY,
                gp.objectM.riddleObjects[i].worldX,
                gp.objectM.riddleObjects[i].worldY
            );

            if (dist < RIDDLE_DIST && dist < closestDist) {
                
                closestDist = dist;
                closestIndex = i;
            }
        }

        if (closestIndex != -1) {
            
            showRiddlePrompt = true;
            nearRiddleIndex = ((ObjRiddle) gp.objectM.riddleObjects[closestIndex]).riddleIndex;

            if (interactJustPressed) {
                
                gp.keyH.interactPressed = false;
                gp.riddleUI.open(nearRiddleIndex);
            }
        }
    }

    private void checkPortalInteraction(boolean interactJustPressed) {
        
        if (!gp.objectM.portalVisible || gp.objectM.portal == null) {
            return;
        }

        double dist = getDistance(
                
            gp.player.worldX,
            gp.player.worldY,
            gp.objectM.portal.worldX,
            gp.objectM.portal.worldY
        );

        if (dist < PORTAL_DIST) {
            
            showPortalPrompt = true;

            if (interactJustPressed) {

                gp.keyH.interactPressed = false;
                int currentLevel = gp.diffM.level;

                if (currentLevel == 3) {
                    gp.switchToLevel2();

                } else if (currentLevel == 2) {
                    gp.switchToLevel1();

                } else {
                    // Level 1 portal → WIN
                    gp.musicLose.stop();
                    gp.musicWin.stop();
                    gp.heartbeat.stop();
                    gp.playerFootsteps.stop();

                    long elapsed = (System.currentTimeMillis() - gp.getGameStartTime()) / 1000;
                    gp.winScreen.completionSeconds = (int) elapsed;
                    gp.winScreen.reset();
                    gp.isGameOver = true;
                    gp.gameState = panel.GameState.WIN;
                    gp.musicWin.loop();
                }
            }
        }
    }

    public void checkInteriorInteraction() {
        
        if (gp.objectM.ObjHouse == null || gp.objectM.ObjHouse[0] == null) {
            return;
        }

        ObjHouse house = (ObjHouse) gp.objectM.ObjHouse[0];
        
        if (gp.objectM.interior == null) {
            
            return;
        }

        tickFeedback();

        checkExitDoorInteraction(house);
        checkWindowInteraction(house);
        checkTorchInteraction();
        checkCabinetInteraction();
        checkAppleTableInteraction();
    }

    private void checkExitDoorInteraction(ObjHouse house) {
        
        double dist = getDistance(gp.player.worldX, gp.player.worldY, intDoorX, intDoorY);

        if (dist < INTERACT_DIST) {
            
            showExitPrompt = true;

            if (gp.keyH.closePressed) {
                
                house.toggleDoor();
                gp.doorCreak.play();
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
    }

    private void checkWindowInteraction(ObjHouse house) {
        
        int[][] winPos = {{intWin1X, intWin1Y}, {intWin2X, intWin2Y}, {intWin3X, intWin3Y}};
        
        nearWindowNum = 0;
        showWindowPrompt = false;

        for (int i = 0; i < 3; i++) {
            
            double dist = getDistance(gp.player.worldX, gp.player.worldY, winPos[i][0], winPos[i][1]);

            if (dist < INTERACT_DIST) {
                
                nearWindowNum = i + 1;
                showWindowPrompt = true;

                if (gp.keyH.interactPressed) {
                    
                    gp.keyH.interactPressed = false;
                    house.toggleWindow(nearWindowNum);
                    gp.windowCreak.play();
                }
                
                break;
            }
        }
    }

    private void checkTorchInteraction() {
        
        if (gp.objectM.interior == null) {
            return;
        }

        ObjTorch[] torches = {gp.objectM.interior.torch, gp.objectM.interior.torch2, gp.objectM.interior.torch3};
        showTorchPrompt = false;

        for (ObjTorch t : torches) {
            
            if (t == null){
                
                continue;
            }
            
            double dist = getDistance(gp.player.worldX, gp.player.worldY, t.worldX, t.worldY);

            if (dist < INTERACT_DIST) {
                
                showTorchPrompt = true;

                if (gp.keyH.interactPressed) {
                    
                    gp.keyH.interactPressed = false;
                    boolean ok = t.refuel(gp.inventory);
                    
                    if (ok){
                        gp.torchLight.play();
                    }
                    
                    torchFeedback = ok
                        ? "+3 Wood burned! (" + t.getSecondsLeft() + "s)" 
                        : "Need " + ObjTorch.WOOD_BATCH + " woods! (Have: " + gp.inventory.wood + ")";
                    torchFeedbackTimer = 120;
                }
                
                break;
            }
        }
    }

    private void checkCabinetInteraction() {
        
        if (gp.objectM.interior == null || gp.objectM.interior.cabinet == null) {
            showCabinetPrompt = false;
            
            return;
        }

        models.ObjCabinet cabinet = gp.objectM.interior.cabinet;
        double dist = getDistance(gp.player.worldX, gp.player.worldY, cabinet.worldX, cabinet.worldY);

        if (dist < CABINET_DIST) {
            
            showCabinetPrompt = true;

            
            if (gp.keyH.interactPressed) {
                
                gp.keyH.interactPressed = false;
                cabinet.toggleUI();
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
    }

    private void checkAppleTableInteraction() {
        
        if (gp.objectM.interior == null || gp.objectM.interior.appleTable == null) {
            showAppleTablePrompt = false;
            
            return;
        }

        models.ObjAppleTable appleTable = gp.objectM.interior.appleTable;
        double dist = getDistance(gp.player.worldX, gp.player.worldY, appleTable.worldX, appleTable.worldY);

        
        if (dist < INTERACT_DIST) {
            
            showAppleTablePrompt = true;

            if (gp.keyH.interactPressed) {
                
                gp.keyH.interactPressed = false;
                appleTable.toggleUI();
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

    
    private double getDistance(int x1, int y1, int x2, int y2) {
        
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private boolean isPlayerFacingDoor(ObjHouse house, int doorX, int doorY) {

        int dx = doorX - gp.player.worldX;
        int dy = doorY - gp.player.worldY;
        
        String requiredDir;
        
        if (Math.abs(dx) > Math.abs(dy)) {
            
            requiredDir = (dx > 0) ? "right" : "left";
            
        } else {
            
            requiredDir = (dy > 0) ? "down" : "up";
        }
        
        return true; 
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
        
        if (appleTableFeedbackTimer > 0) {
            
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
