package game;

import java.awt.*;
import models.Riddle;

public class RiddleUI {
    panel gp;
    public boolean isOpen = false;
    private int currentIndex = -1;
    private StringBuilder inputText = new StringBuilder();
    private String feedbackText = "";
    private int feedbackTimer = 0;
    private boolean lastAnswerCorrect = false;
    private int cursorTick = 0;
    private static final int CURSOR_BLINK = 30;
    private static final int FEEDBACK_DURATION = 90;
    private static final int PANEL_W = 560;
    private static final int PANEL_H = 300;
    private int closeBtnX, closeBtnY;
    private static final int CLOSE_BTN_SIZE = 26;
    private static final int ROLL_H = 22;
    private static final Color PARCH_BG = new Color(235, 218, 178);
    private static final Color PARCH_DARK = new Color(210, 190, 145);
    private static final Color ROLL_COL = new Color(188, 158, 100);
    private static final Color ROLL_SHADOW = new Color(155, 125, 72);
    private static final Color BORDER_COL = new Color(130, 95, 50);
    private static final Color INK_DARK = new Color(42, 28, 12);
    private static final Color INK_MID = new Color(80, 52, 20);
    private static final Color FIELD_BG = new Color(220, 200, 155);
    private static final Color FIELD_BORDER = new Color(140, 100, 45);
    private static final Color SEAL_COL = new Color(160, 35, 25);
    private static final Color SEAL_BORDER = new Color(110, 20, 15);

    public RiddleUI(panel gp) {
        this.gp = gp;
    }

    public void open(int riddleIndex) {
        // block third riddle if first two not solved
        if (riddleIndex == 2 && !gp.riddleM.isThirdRiddleUnlocked()) return;
        Riddle r = gp.riddleM.getRiddle(riddleIndex);
        if (r == null) return;
        isOpen = true;
        currentIndex = riddleIndex;
        inputText.setLength(0);
        feedbackText = "";
        feedbackTimer = 0;
        // third riddle is read-only, no typing mode
        gp.keyH.typingMode = (riddleIndex != 2);
        gp.keyH.clearTypingFlags();
        gp.keyH.clearMovement();
    }

    public void close() {
        isOpen = false;
        currentIndex = -1;
        inputText.setLength(0);
        feedbackText = "";
        feedbackTimer = 0;
        gp.keyH.typingMode = false;
        gp.keyH.clearTypingFlags();
    }

    public boolean handleClick(int mx, int my) {
        if (!isOpen) return false;
        if (mx >= closeBtnX && mx <= closeBtnX + CLOSE_BTN_SIZE
                && my >= closeBtnY && my <= closeBtnY + CLOSE_BTN_SIZE) {
            close();
            return true;
        }
        return true;
    }

    public void update() {
        if (!isOpen) return;
        // third riddle — no input handling
        if (currentIndex == 2) {
            cursorTick = (cursorTick + 1) % (CURSOR_BLINK * 2);
            return;
        }
        if (gp.keyH.hasTyped) {
            char c = gp.keyH.lastTyped;
            gp.keyH.hasTyped = false;
            if (c >= 32 && c < 127 && inputText.length() < 24) inputText.append(c);
        }
        if (gp.keyH.backspacePressed) {
            gp.keyH.backspacePressed = false;
            if (inputText.length() > 0) inputText.deleteCharAt(inputText.length() - 1);
        }
        if (gp.keyH.enterTyped) {
            gp.keyH.enterTyped = false;
            submitAnswer();
        }
        if (feedbackTimer > 0) {
            
            feedbackTimer--;
            
            if (feedbackTimer == 0) {
                
                feedbackText = "";
                
                if (lastAnswerCorrect) {
                    close();
                }
            }
        }
        cursorTick = (cursorTick + 1) % (CURSOR_BLINK * 2);
    }

    private void submitAnswer() {
        
        if (inputText.length() == 0) {
            return;
        }
        
        boolean correct = gp.riddleM.tryAnswer(currentIndex, inputText.toString());
        if (correct) {
            
            lastAnswerCorrect = true;
            feedbackText = "Correct. The forest stirs.";
            feedbackTimer = FEEDBACK_DURATION;
            
        } else {
            lastAnswerCorrect = false;
            feedbackText = "Wrong. The day grows shorter...";
            feedbackTimer = FEEDBACK_DURATION;
            inputText.setLength(0);
        }
    }

    public void draw(Graphics2D g2) {
        if (!isOpen || currentIndex < 0) return;
        Riddle r = gp.riddleM.getRiddle(currentIndex);
        if (r == null) return;
        boolean isThird = (currentIndex == 2);

        int px = gp.screenWidth / 2 - PANEL_W / 2;
        int py = gp.screenheight / 2 - PANEL_H / 2;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 155));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenheight);

        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(px + 6, py + 6, PANEL_W, PANEL_H, 8, 8);

        GradientPaint parchGrad = new GradientPaint(px, py, PARCH_BG, px, py + PANEL_H, PARCH_DARK);
        g2.setPaint(parchGrad);
        g2.fillRect(px, py + ROLL_H, PANEL_W, PANEL_H - ROLL_H * 2);

        g2.setColor(new Color(180, 155, 105, 30));
        g2.fillRect(px + PANEL_W / 3, py + ROLL_H, 2, PANEL_H - ROLL_H * 2);
        g2.fillRect(px + PANEL_W * 2 / 3, py + ROLL_H, 2, PANEL_H - ROLL_H * 2);

        GradientPaint rollTopGrad = new GradientPaint(px, py, ROLL_COL, px, py + ROLL_H, ROLL_SHADOW);
        g2.setPaint(rollTopGrad);
        g2.fillRoundRect(px, py, PANEL_W, ROLL_H + 4, 8, 8);

        g2.setColor(new Color(255, 240, 195, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(px + 10, py + 5, px + PANEL_W - 10, py + 5);

        GradientPaint rollBotGrad = new GradientPaint(px, py + PANEL_H - ROLL_H, ROLL_SHADOW, px, py + PANEL_H, ROLL_COL);
        g2.setPaint(rollBotGrad);
        g2.fillRoundRect(px, py + PANEL_H - ROLL_H - 4, PANEL_W, ROLL_H + 4, 8, 8);

        g2.setColor(new Color(100, 70, 25, 60));
        g2.drawLine(px + 10, py + PANEL_H - 5, px + PANEL_W - 10, py + PANEL_H - 5);

        g2.setPaint(BORDER_COL);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(px, py, PANEL_W, PANEL_H, 8, 8);

        g2.setColor(new Color(160, 120, 60, 80));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(px + 10, py + ROLL_H + 6, PANEL_W - 20, PANEL_H - ROLL_H * 2 - 12);

        closeBtnX = px + PANEL_W - CLOSE_BTN_SIZE - 8;
        closeBtnY = py + (ROLL_H - CLOSE_BTN_SIZE) / 2;
        g2.setColor(SEAL_BORDER);
        g2.fillOval(closeBtnX - 1, closeBtnY - 1, CLOSE_BTN_SIZE + 2, CLOSE_BTN_SIZE + 2);
        g2.setColor(SEAL_COL);
        g2.fillOval(closeBtnX, closeBtnY, CLOSE_BTN_SIZE, CLOSE_BTN_SIZE);
        g2.setColor(new Color(220, 80, 60, 60));
        g2.fillOval(closeBtnX + 4, closeBtnY + 3, CLOSE_BTN_SIZE / 2, CLOSE_BTN_SIZE / 3);
        g2.setFont(getImFell(11f));
        g2.setColor(new Color(255, 220, 210, 200));
        int xw = g2.getFontMetrics().stringWidth("x");
        g2.drawString("x", closeBtnX + CLOSE_BTN_SIZE / 2 - xw / 2, closeBtnY + 17);

        g2.setFont(getImFell(11f));
        g2.setColor(INK_DARK);
        String titleLabel = isThird
            ? "~ The Third Riddle ~"
            : (r.solved ? "~ Ancient Riddle  ·  Solved ~" : "~ Ancient Riddle  ·  " + (currentIndex + 1) + " of 3 ~");
        int tlw = g2.getFontMetrics().stringWidth(titleLabel);
        g2.drawString(titleLabel, px + PANEL_W / 2 - tlw / 2, py + 15);

        g2.setColor(BORDER_COL);
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawLine(px + 20, py + ROLL_H + 8, px + PANEL_W - 20, py + ROLL_H + 8);
        g2.setFont(getImFell(10f));
        g2.setColor(INK_MID);
        String orn = "✦";
        int ornW = g2.getFontMetrics().stringWidth(orn);
        g2.drawString(orn, px + PANEL_W / 2 - ornW / 2, py + ROLL_H + 7);

        int textX = px + 22;
        int textY = py + ROLL_H + 28;
        g2.setFont(getImFell(15f));
        g2.setColor(INK_DARK);
        int qy = drawWrappedText(g2, r.question, textX, textY, PANEL_W - 44, 22);

        if (isThird) {
            // read-only — no answer field, just a bottom note
            g2.setFont(getImFell(11f));
            g2.setColor(new Color(100, 70, 30, 180));
            String note = "Ponder this. The answer lies in the world.";
            int nw = g2.getFontMetrics().stringWidth(note);
            g2.drawString(note, px + PANEL_W / 2 - nw / 2, qy + 30);
        } else {
            g2.setColor(new Color(160, 120, 60, 100));
            g2.setStroke(new BasicStroke(0.8f));
            g2.drawLine(px + 20, qy + 12, px + PANEL_W - 20, qy + 12);

            int fieldBaseY = qy + 24;
            g2.setFont(getImFell(12f));
            g2.setColor(INK_MID);
            g2.drawString("Answer :", textX, fieldBaseY + 14);

            int fieldX = textX + 72;
            int fieldW = PANEL_W - 100;
            int fieldH = 26;
            g2.setColor(FIELD_BG);
            g2.fillRect(fieldX, fieldBaseY, fieldW, fieldH);
            g2.setColor(FIELD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(fieldX, fieldBaseY, fieldW, fieldH);
            g2.setColor(new Color(150, 115, 60, 40));
            g2.drawLine(fieldX + 1, fieldBaseY + 1, fieldX + fieldW - 1, fieldBaseY + 1);

            g2.setFont(getImFell(14f));
            if (r.solved) {
                g2.setColor(new Color(50, 110, 40));
                g2.drawString("(already solved)", fieldX + 8, fieldBaseY + 18);
            } else {
                g2.setColor(INK_DARK);
                String display = inputText.toString();
                g2.drawString(display, fieldX + 8, fieldBaseY + 18);
                if (cursorTick < CURSOR_BLINK) {
                    int cx = fieldX + 8 + g2.getFontMetrics().stringWidth(display);
                    g2.setColor(INK_MID);
                    g2.fillRect(cx + 1, fieldBaseY + 5, 2, 16);
                }
            }

            if (feedbackTimer > 0 && feedbackText.length() > 0) {
                Color fc = lastAnswerCorrect ? new Color(45, 105, 35) : new Color(160, 40, 25);
                g2.setFont(getImFell(13f));
                g2.setColor(fc);
                int fw = g2.getFontMetrics().stringWidth(feedbackText);
                g2.drawString(feedbackText, px + PANEL_W / 2 - fw / 2, fieldBaseY + 48);
            }
        }

        g2.setFont(getImFell(10f));
        g2.setColor(new Color(100, 70, 30, 180));
        String hints = isThird
            ? "Click the seal to close"
            : (r.solved ? "Click the seal to close" : "Enter — Submit     Backspace — Delete     Click the seal to close");
        int hw = g2.getFontMetrics().stringWidth(hints);
        g2.drawString(hints, px + PANEL_W / 2 - hw / 2, py + PANEL_H - 8);

        g2.setStroke(new BasicStroke(1f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
    }

    private int drawWrappedText(Graphics2D g2, String text, int x, int y, int maxW, int lineH) {
        if (text == null || text.isEmpty()) return y;
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
        if (line.length() > 0) g2.drawString(line.toString(), x, y);
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