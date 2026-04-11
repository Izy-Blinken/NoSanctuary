/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import models.Riddle;


public class RiddleManager {

    panel gp;

    public Riddle[] riddles = new Riddle[3];
    public int solvedCount = 0;
    public boolean portalSpawned = false;

    private static final int WRONG_PENALTY_TICKS = 300;

    public RiddleManager(panel gp) {
        
        this.gp = gp;
        initRiddles();
    }

    public void initRiddles() {

        riddles[0] = new Riddle(
            0,
            "I have cities, but no houses. Mountains, but no trees. " +
            "Water, but no fish. Roads, but no cars. What am I?",
            "map",
            "Riddle I — Map"
        );

        riddles[1] = new Riddle(
            1,
            "The more you take, the more you leave behind. What am I?",
            "footsteps",
            "Riddle II — Footsteps"
        );

        riddles[2] = new Riddle(
            2,
            "I speak without a mouth and hear without ears. " +
            "No body have I, yet I come alive with wind. What am I?",
            "echo",
            "Riddle III — Echo"
        );
    }

    public boolean tryAnswer(int riddleIndex, String rawAnswer) {
        
        if (riddleIndex < 0 || riddleIndex >= riddles.length) { 
            return false;
        }

        Riddle r = riddles[riddleIndex];
        
        if (r.solved){
            return true;
        } 
        
        if (rawAnswer.toLowerCase().trim().equals(r.answer)) {
            
            r.solved = true;
            solvedCount++;

            appendClueToScroll(r);

            if (solvedCount >= 3) { 
                spawnPortal();
            }
            
            return true;

        } else {
            
            applyWrongAnswerPenalty();
            return false;
        }
    }

    public Riddle getRiddle(int index) {
        
        if (index < 0 || index >= riddles.length) { 
            return null;
        }
        
        return riddles[index];
    }

    public boolean isAllSolved() { 
        return solvedCount >= 3; 
    }

    private void applyWrongAnswerPenalty() {
        
        if (gp.dC.currentState == dayCounter.dayNightState.Day) {
            
            gp.dC.stateCounter -= WRONG_PENALTY_TICKS;
            
            if (gp.dC.stateCounter < 0){
                gp.dC.stateCounter = 0;
            }
        }
    }

    private void appendClueToScroll(Riddle r) {
        
        String existing = gp.inventory.clueText == null ? "" : gp.inventory.clueText;
        
        if (!existing.contains(r.clue)) {
            
            gp.inventory.clueText = existing.isEmpty()
                ? r.clue
                : existing + "\n\n" + r.clue;
            gp.inventory.hasClue = true;
        }
    }

    
    private void spawnPortal() {
        
        portalSpawned = true;
        gp.objectM.revealPortal();
    }
}
