package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, rightPressed, leftPressed;
    public boolean interactPressed;
    public boolean closePressed;

    public boolean ePressed;
    
     public boolean skipPressed = false;

    public boolean depositPressed       = false;
    public boolean withdrawPressed      = false;
    public boolean appleDepositPressed  = false;
    public boolean appleWithdrawPressed = false;

    // --- Typing mode (used by RiddleUI) ---
    public boolean typingMode       = false;
    public char    lastTyped        = 0;
    public boolean hasTyped         = false;
    public boolean backspacePressed = false;
    public boolean enterTyped       = false;

    // ---------------------------------------------------------------
    // Helpers called by RiddleUI.open() / close()
    // ---------------------------------------------------------------

    /** Resets all typing-mode flags. */
    public void clearTypingFlags() {
        lastTyped        = 0;
        hasTyped         = false;
        backspacePressed = false;
        enterTyped       = false;
    }

    /** Stops player movement when riddle UI opens. */
    public void clearMovement() {
        upPressed    = false;
        downPressed  = false;
        leftPressed  = false;
        rightPressed = false;
    }

    // ---------------------------------------------------------------
    @Override
    public void keyTyped(KeyEvent e) {
        if (typingMode) {
            char c = e.getKeyChar();
            if (c != KeyEvent.CHAR_UNDEFINED
                    && c != '\b'   // backspace handled in keyPressed
                    && c != '\n'   // enter handled in keyPressed
                    && c != '\r') {
                lastTyped = c;
                hasTyped  = true;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        // --- Typing mode: capture Enter/Backspace, block movement, allow all letters ---
        if (typingMode) {
            if (code == KeyEvent.VK_BACK_SPACE) backspacePressed = true;
            if (code == KeyEvent.VK_ENTER)      enterTyped       = true;
            // Block only movement keys — every other key (including F) types normally
            if (code == KeyEvent.VK_W  || code == KeyEvent.VK_A     ||
                code == KeyEvent.VK_S  || code == KeyEvent.VK_D     ||
                code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN  ||
                code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT) {
                return;
            }
            return; // let keyTyped() handle all printable chars
        }

        // --- Normal game keys ---
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    upPressed    = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  downPressed  = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  leftPressed  = true;

        if (code == KeyEvent.VK_E) { interactPressed = true; ePressed = true; }
        
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) { skipPressed = true;}
        
        if (code == KeyEvent.VK_F) closePressed        = true;
        if (code == KeyEvent.VK_P) depositPressed      = true;
        if (code == KeyEvent.VK_R) withdrawPressed     = true;
        if (code == KeyEvent.VK_B) appleDepositPressed = true;
        if (code == KeyEvent.VK_C) appleWithdrawPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    upPressed    = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  downPressed  = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  leftPressed  = false;

        if (code == KeyEvent.VK_E) { interactPressed = false; ePressed = false; }
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {skipPressed = false;}       
        if (code == KeyEvent.VK_F) closePressed         = false;
        if (code == KeyEvent.VK_P) depositPressed       = false;       
        if (code == KeyEvent.VK_R) withdrawPressed      = false;
        if (code == KeyEvent.VK_B) appleDepositPressed  = false;
        if (code == KeyEvent.VK_C) appleWithdrawPressed = false;

        if (code == KeyEvent.VK_BACK_SPACE) backspacePressed = false;
    }
}
