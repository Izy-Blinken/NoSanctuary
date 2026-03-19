package game;

import java.awt.*;
import models.Riddle;

public class RiddleUI {

    panel gp;

    public  boolean isOpen = false;
    private int currentIndex = -1;

    private StringBuilder inputText = new StringBuilder();
    private String feedbackText = "";
    private int feedbackTimer = 0;
    private boolean lastAnswerCorrect = false;

    private int cursorTick = 0;
    private static final int CURSOR_BLINK  = 30;
    private static final int FEEDBACK_DURATION = 90;

    private static final int PANEL_W = 580;
    private static final int PANEL_H = 280;

    private int closeBtnX, closeBtnY, closeBtnSize = 22;

    public RiddleUI(panel gp) {
        this.gp = gp;
    }

    public void open(int riddleIndex) {
        
        Riddle r = gp.riddleM.getRiddle(riddleIndex);
        
        if (r == null){
            return;
        }

        isOpen = true;
        currentIndex = riddleIndex;
        inputText.setLength(0);
        feedbackText = "";
        feedbackTimer = 0;

        gp.keyH.typingMode = true;
        gp.keyH.clearTypingFlags();
        gp.keyH.clearMovement();
    }

    public void close() {
        
        isOpen = false;
        currentIndex = -1;
        inputText.setLength(0);
        feedbackText  = "";
        feedbackTimer = 0;

        gp.keyH.typingMode = false;
        gp.keyH.clearTypingFlags();
    }

    
    public boolean handleClick(int mx, int my) {
        
        if (!isOpen){
            return false;
        }

        if (mx >= closeBtnX && mx <= closeBtnX + closeBtnSize && my >= closeBtnY && my <= closeBtnY + closeBtnSize) {
            close();
            return true;
        }
        
        return true; 
    }

    public void update() {
        
        if (!isOpen){
            return;
        }

        if (gp.keyH.hasTyped) {
            
            char c = gp.keyH.lastTyped;
            gp.keyH.hasTyped = false;
            
            if (c >= 32 && c < 127 && inputText.length() < 24) {
                inputText.append(c);
            }
        }

        // Backspace
        if (gp.keyH.backspacePressed) {
            
            gp.keyH.backspacePressed = false;
            
            if (inputText.length() > 0) {
                inputText.deleteCharAt(inputText.length() - 1);
            }
        }

        // Enter = submit
        if (gp.keyH.enterTyped) {
            
            gp.keyH.enterTyped = false;
            submitAnswer();
        }

        // Feedback timer countdown
        if (feedbackTimer > 0) {
            
            feedbackTimer--;
            
            if (feedbackTimer == 0) {
                
                feedbackText = "";
                
                if (lastAnswerCorrect){
                    close();
                }
            }
        }

        // Cursor blink
        cursorTick = (cursorTick + 1) % (CURSOR_BLINK * 2);
    }

    private void submitAnswer() {
        
        if (inputText.length() == 0) return;

        String raw = inputText.toString();
        boolean correct = gp.riddleM.tryAnswer(currentIndex, raw);

        if (correct) {
            
            lastAnswerCorrect = true;
            feedbackText = "Correct! The stone glows with ancient light.";
            feedbackTimer = FEEDBACK_DURATION;
            
        } else {
            
            lastAnswerCorrect = false;
            feedbackText = "Wrong. The day grows shorter...";
            feedbackTimer = FEEDBACK_DURATION;
            inputText.setLength(0);
        }
    }

    
    public void draw(Graphics2D g2) {
        
        if (!isOpen || currentIndex < 0){
            return;
        }

        Riddle r = gp.riddleM.getRiddle(currentIndex);
        
        if (r == null){
            return;
        }

        int px = gp.screenWidth / 2 - PANEL_W / 2;
        int py = gp.screenheight / 2 - PANEL_H / 2;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dark scrim
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenheight);

        // Panel background
        g2.setColor(new Color(22, 20, 17, 245));
        g2.fillRect(px, py, PANEL_W, PANEL_H);

        // Panel border
        g2.setColor(new Color(90, 82, 70));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(px, py, PANEL_W, PANEL_H);

        // close button 
        closeBtnX = px + PANEL_W - closeBtnSize - 8;
        closeBtnY = py + 6;

        g2.setColor(new Color(55, 50, 44));
        g2.fillRect(closeBtnX, closeBtnY, closeBtnSize, closeBtnSize);
        g2.setColor(new Color(90, 82, 70));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(closeBtnX, closeBtnY, closeBtnSize, closeBtnSize);

        g2.setFont(getImFell(14f));
        g2.setColor(new Color(180, 165, 140));
        int xw = g2.getFontMetrics().stringWidth("x");
        g2.drawString("x", closeBtnX + closeBtnSize / 2 - xw / 2, closeBtnY + 16);

        // Title row
        g2.setFont(getImFell(13f));
        g2.setColor(new Color(115, 100, 75));
        String titleLabel = r.solved
            ? "Ancient Riddle  — Solved"
            : "Ancient Riddle  — " + (currentIndex + 1) + " of 3";
        g2.drawString(titleLabel, px + 16, py + 22);

        // Accent line
        g2.setColor(new Color(60, 55, 48));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(px + 12, py + 30, px + PANEL_W - 12, py + 30);

        //Question text 
        g2.setFont(getImFell(15f));
        g2.setColor(new Color(210, 200, 182));
        int qy = drawWrappedText(g2, r.question, px + 18, py + 52, PANEL_W - 36, 22);

        // Separator
        g2.setColor(new Color(60, 55, 48));
        g2.drawLine(px + 12, qy + 10, px + PANEL_W - 12, qy + 10);

        // answer label
        int fieldY = qy + 28;
        g2.setFont(getImFell(12f));
        g2.setColor(new Color(120, 108, 88));
        g2.drawString("Answer:", px + 18, fieldY);

        // Input field
        int fieldX = px + 80;
        int fieldW = PANEL_W - 100;
        int fieldH = 26;
        int fieldBaseY = fieldY - 18;

        g2.setColor(new Color(14, 13, 11, 220));
        g2.fillRect(fieldX, fieldBaseY, fieldW, fieldH);
        g2.setColor(new Color(75, 68, 58));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(fieldX, fieldBaseY, fieldW, fieldH);

        g2.setFont(getImFell(14f));
        
        if (r.solved) {
            
            g2.setColor(new Color(120, 180, 100));
            g2.drawString("(already solved)", fieldX + 8, fieldBaseY + 18);
            
        } else {
            
            g2.setColor(new Color(210, 200, 175));
            String display = inputText.toString();
            g2.drawString(display, fieldX + 8, fieldBaseY + 18);

            // Blinking cursor
            if (cursorTick < CURSOR_BLINK) {
                
                int cx = fieldX + 8 + g2.getFontMetrics().stringWidth(display);
                g2.setColor(new Color(180, 165, 130));
                g2.fillRect(cx + 1, fieldBaseY + 5, 2, 16);
            }
        }

        // Feedback text
        if (feedbackTimer > 0 && feedbackText.length() > 0) {
            
            Color fc = lastAnswerCorrect
                ? new Color(100, 190, 110)
                : new Color(200, 80, 70);
            
            g2.setFont(getImFell(13f));
            g2.setColor(fc);
            
            int fw = g2.getFontMetrics().stringWidth(feedbackText);
            g2.drawString(feedbackText, px + PANEL_W / 2 - fw / 2, fieldBaseY + 52);
        }

        // Key hints at bottom 
        int hintY = py + PANEL_H - 14;
        g2.setFont(getImFell(11f));
        g2.setColor(new Color(85, 78, 65));
        
        String hints = r.solved
            ? "Click  X  to close"
            : "Enter — Submit     Backspace — Delete     Click  X  to close";
        
        int hw = g2.getFontMetrics().stringWidth(hints);
        g2.drawString(hints, px + PANEL_W / 2 - hw / 2, hintY);

        g2.setStroke(new BasicStroke(1f));
    }

    private int drawWrappedText(Graphics2D g2, String text, int x, int y, int maxW, int lineH) {
        
        if (text == null || text.isEmpty()){
            return y;
        }
        
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            
            String test = line.length() == 0 ? word : line + " " + word;
            
            if (g2.getFontMetrics().stringWidth(test) > maxW) {
                
                g2.drawString(line.toString(), x, y);
                y += lineH;
                line = new StringBuilder(word);
                
            } else {
                
                line = new StringBuilder(test);
            }
        }
        
        if (line.length() > 0) { 
            g2.drawString(line.toString(), x, y);
        }
        
        return y;
    }

    private Font getImFell(float size) {
        
        try {
            
            java.io.InputStream is = getClass().getResourceAsStream("/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
            
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            
        } catch (Exception e) {
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }
}