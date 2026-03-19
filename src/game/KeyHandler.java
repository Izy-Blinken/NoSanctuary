package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, rightPressed, leftPressed;
    public boolean interactPressed;
    public boolean closePressed;

    public boolean ePressed;
    
     public boolean skipPressed = false;

    public boolean depositPressed = false;
    public boolean withdrawPressed = false;
    public boolean appleDepositPressed  = false;
    public boolean appleWithdrawPressed = false;

    public boolean typingMode = false;
    public char lastTyped = 0;
    public boolean hasTyped = false;
    public boolean backspacePressed = false;
    public boolean enterTyped = false;

    public boolean interactWasPressed = false;
    public int interactDebounce = 0;
    private static final int INTERACT_DEBOUNCE_FRAMES = 5;


    public void clearTypingFlags() {
        
        lastTyped = 0;
        hasTyped = false;
        backspacePressed = false;
        enterTyped = false;
    }

    public void clearMovement() {
        
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    public void update() {
        
        if (interactDebounce > 0) {
            
            interactDebounce--;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
        if (typingMode) {
            
            char c = e.getKeyChar();
            
            if (c != KeyEvent.CHAR_UNDEFINED && c != '\b' && c != '\n' && c != '\r') {
                
                lastTyped = c;
                hasTyped = true;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if (typingMode) {
            
            if (code == KeyEvent.VK_BACK_SPACE) { 
                backspacePressed = true;
            }
            
            if (code == KeyEvent.VK_ENTER) { 
                enterTyped = true;
            }
            
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_A ||
                code == KeyEvent.VK_S || code == KeyEvent.VK_D ||
                code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN ||
                code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
                
                return;
            }
            
            return; 
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { 
            upPressed    = true;
        }
        
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){ 
            downPressed  = true;
        }
        
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            rightPressed = true;
        }
        
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            leftPressed  = true;
        }

        // Interact key with debounce
        if (code == KeyEvent.VK_E) { 
            
            if (interactDebounce == 0 && !interactWasPressed) {
                
                interactPressed = true;
                ePressed = true;
                interactDebounce = INTERACT_DEBOUNCE_FRAMES;
            }
        }
        
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) { 
            skipPressed = true;
        }
        
        if (code == KeyEvent.VK_F) { 
            closePressed = true;
        }
        
        if (code == KeyEvent.VK_P) { 
            depositPressed = true;
        }
        
        if (code == KeyEvent.VK_R) { 
            withdrawPressed = true;
        }
        
        if (code == KeyEvent.VK_B) { 
            appleDepositPressed = true;
        }
        
        if (code == KeyEvent.VK_C){
            appleWithdrawPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { 
            upPressed = false;
        }
        
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { 
            downPressed = false;
        }
        
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }
        
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){ 
            leftPressed = false;
        }

        if (code == KeyEvent.VK_E) { 
            
            interactPressed = false; 
            ePressed = false; 
            interactWasPressed = false;
        }
        
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
            skipPressed = false;
        }       
        
        if (code == KeyEvent.VK_F) { 
            closePressed = false;
        }
        
        if (code == KeyEvent.VK_P){
            depositPressed = false;
        }       
        
        if (code == KeyEvent.VK_R) { 
            withdrawPressed = false;
        }
        
        if (code == KeyEvent.VK_B) { 
            appleDepositPressed = false;
        }
        
        if (code == KeyEvent.VK_C) { 
            appleWithdrawPressed = false;
        }

        if (code == KeyEvent.VK_BACK_SPACE){
            backspacePressed = false;
        }
    }
}
