package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, rightPressed, leftPressed;
    public boolean interactPressed;
    public boolean closePressed;
    
    public boolean ePressed;

    /** Space or Enter – used to advance / skip the opening narration. */
    public boolean skipPressed = false;

    public boolean depositPressed = false;
    public boolean withdrawPressed = false; 
    public boolean appleDepositPressed = false; 
    public boolean appleWithdrawPressed = false; 

    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = true;
        }

        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }

        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        
        if (code == KeyEvent.VK_E ) {
            interactPressed = true;
            ePressed = true;
        }

        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
            skipPressed = true;
        }

        if(code == KeyEvent.VK_F) {
            closePressed = true;
        }
        
        if(code == KeyEvent.VK_P) {
            
            depositPressed = true;
        }
        
        if(code == KeyEvent.VK_R) {
            withdrawPressed = true;
        }
        
        if(code == KeyEvent.VK_B) {
            appleDepositPressed = true;
        }
        
        if(code == KeyEvent.VK_C) {
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

        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        
        if (code == KeyEvent.VK_E ) {
            interactPressed = false;
            ePressed = false;
        }

        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
            skipPressed = false;
        }

        if(code == KeyEvent.VK_F) {
            closePressed = false;
        }
        
        if(code == KeyEvent.VK_P) {
            
            depositPressed = false;
        }
        
        if(code == KeyEvent.VK_R) {
            withdrawPressed = false;
        }
        
        if(code == KeyEvent.VK_B) {
            appleDepositPressed = false;
        }
        
        if(code == KeyEvent.VK_C) {
            appleWithdrawPressed = false;
        }
        
    }
    

}
