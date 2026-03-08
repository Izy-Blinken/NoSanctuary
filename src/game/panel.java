package game;

import java.awt.BasicStroke;
import models.Monster;
import models.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class panel extends JPanel implements Runnable {

    final int origtileSize = 16;
    public final int charHeight = origtileSize * 2;
    public final int charWidth = origtileSize;
    final int scale = 3;

    public final int tileSize = origtileSize * scale;
    public final int screenWidth = 960;
    public final int screenheight = 540;
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 45;
    private boolean monsterDialogueResponded = false;
    public boolean playerHealedThisNight = false;

    KeyHandler keyH = new KeyHandler();
    public TileManager tileM = new TileManager(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Player player = new Player(this, keyH);
    public ObjectManager objectM = new ObjectManager(this);
    public Monster monster = new Monster(this);
    public dayCounter dC = new dayCounter(this);
    public Inventory inventory = new Inventory(this);
    public boolean isGameOver = false;
    private boolean showMonsterDialogue = false;
    private String dialogueText = "";
    private Runnable onYesAction = null;
    private Runnable onNoAction = null;

    // Typewriter state
    private String dialogueFullText = "";
    private int dialogueCharIndex = 0;
    private int dialogueTickCounter = 0;
    private static final int TYPEWRITER_DELAY = 3;

    // Win / Lose
    public enum GameState { PLAYING, WIN, LOSE }
    public GameState gameState = GameState.PLAYING;
    public WinScreen winScreen = new WinScreen(this);
    public LoseScreen loseScreen = new LoseScreen(this);
    private long gameStartTime = System.currentTimeMillis();

    // Settings / Menu
    private boolean showMenuPanel = false;
    public boolean isMuted = false;

    private static final int MENU_BTN_X = 900;
    private static final int MENU_BTN_Y = 10;
    private static final int MENU_BTN_W = 50;
    private static final int MENU_BTN_H = 22;

    private static final int MENU_PANEL_W = 480;
    private static final int MENU_PANEL_H = 260;
    
    public boolean showNarration = true;
    public String narrationText = "";
    private static final int NARRATION_W = 600;
    private static final int NARRATION_H = 220;

    private JFrame parentFrame;

    public InteractionChecker interactionChecker = new InteractionChecker(this);
    Thread GameThread;

    int fps = 60;

    private Font imFellBase = null;

    private Font getImFell(float size) {

        if (imFellBase == null) {

            try {

                InputStream is = getClass().getResourceAsStream("/assets/game_ui/fonts/IMFellEnglish-Regular.ttf");
                if (is != null) {
                    imFellBase = Font.createFont(Font.TRUETYPE_FONT, is);
                }

            } catch (FontFormatException | IOException e) {
                imFellBase = new Font("Serif", Font.PLAIN, 12);
            }
        }

        return (imFellBase != null) ? imFellBase.deriveFont(size) : new Font("Serif", Font.PLAIN, (int) size);
    }

    public panel(JFrame frame) {

        this.parentFrame = frame;

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {

                
                int mx = e.getX();
                int my = e.getY();
                
                if (showNarration) {
                    
                    int px = screenWidth  / 2 - NARRATION_W / 2;
                    int py = screenheight / 2 - NARRATION_H / 2;
                    int btnW = 120, btnH = 28;
                    int btnX = px + NARRATION_W / 2 - btnW / 2;
                    int btnY = py + NARRATION_H - 38;
                    if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                        showNarration = false;
                    }
                    
                    return;
                }

                handleDialogueClick(mx, my);

                if (gameState == GameState.WIN && winScreen.handleClick(mx, my)) {
                    returnToMenu();
                }
                if (gameState == GameState.LOSE && loseScreen.handleClick(mx, my)) {
                    returnToMenu();
                }

                if (!isGameOver) {
                    handleHUDClick(mx, my);
                }
            }
        });

        this.setPreferredSize(new Dimension(screenWidth, screenheight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void handleHUDClick(int mx, int my) {

        // Bag button
        if (inventory.isBagBtnClicked(mx, my)) {
            inventory.showPanel = !inventory.showPanel;
            if (inventory.showPanel) {
                inventory.showScroll = false;
                showMenuPanel = false;
            }
            return;
        }

        // Scroll button
        if (inventory.isScrollBtnClicked(mx, my)) {
            inventory.showScroll = !inventory.showScroll;
            if (inventory.showScroll) {
                inventory.showPanel = false;
                showMenuPanel = false;
            }
            return;
        }

        // Menu button
        if (isMenuBtnClicked(mx, my)) {
            showMenuPanel = !showMenuPanel;
            if (showMenuPanel) {
                inventory.showPanel = false;
                inventory.showScroll = false;
            }
            return;
        }

        // Inventory panel interactions
        if (inventory.showPanel) {

            if (inventory.isInventoryXClicked(mx, my)) {
                inventory.showPanel = false;
                return;
            }

            if (inventory.isEatClicked(mx, my)) {
                if (inventory.eatApple()) {
                    player.hp = Math.min(player.maxHP, player.hp + 20);
                }
                return;
            }
        }

        // Scroll panel close
        if (inventory.showScroll && inventory.isScrollXClicked(mx, my)) {
            inventory.showScroll = false;
            return;
        }

        // Menu panel interactions
        if (showMenuPanel) {

            if (isMenuPanelXClicked(mx, my)) {
                showMenuPanel = false;
                return;
            }

            if (isMuteRowClicked(mx, my)) {
                isMuted = !isMuted;
                return;
            }

            if (isQuitRowClicked(mx, my)) {
                returnToMenu();
                return;
            }
        }
    }

    private void returnToMenu() {

        GameThread = null;
        parentFrame.getContentPane().removeAll();
        LandingPage landingPage = new LandingPage(() -> {
            parentFrame.getContentPane().removeAll();
            panel gamePanel = new panel(parentFrame);
            parentFrame.add(gamePanel);
            parentFrame.pack();
            parentFrame.revalidate();
            parentFrame.repaint();
            parentFrame.setLocationRelativeTo(null);
            gamePanel.startThread();
            gamePanel.requestFocusInWindow();
        });
        parentFrame.add(landingPage);
        parentFrame.pack();
        parentFrame.revalidate();
        parentFrame.repaint();
        parentFrame.setLocationRelativeTo(null);
    }

    public void startThread() {

        GameThread = new Thread(this);
        GameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000 / fps;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (GameThread != null) {

            update();
            repaint();

            try {

                double remaining = nextDrawTime - System.nanoTime();
                remaining /= 1000000;
                if (remaining < 0) remaining = 0;
                Thread.sleep((long) remaining);
                nextDrawTime += drawInterval;

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean wasNight = false;

    public void update() {
        if (showNarration) {
            return;
        }

        if (isGameOver) {

            if (gameState == GameState.WIN) {
                winScreen.update();
            } else if (gameState == GameState.LOSE) {
                loseScreen.update();
            }
            return;
        }

        boolean isNight = (dC.currentState == dayCounter.dayNightState.Night);

        // Typewriter tick while dialogue is open, skip everything else
        if (showMonsterDialogue) {

            if (dialogueCharIndex < dialogueFullText.length()) {

                dialogueTickCounter++;

                if (dialogueTickCounter >= TYPEWRITER_DELAY) {

                    dialogueTickCounter = 0;
                    dialogueCharIndex++;
                    dialogueText = dialogueFullText.substring(0, dialogueCharIndex);
                }
            }
            return;
        }

        if (isNight && !wasNight) {
            monster.spawnNearEdge();
        }

        player.update();
        monster.update();
        objectM.update();

        if (tileM.currentMap == 1) {
            interactionChecker.checkInteraction();
        } else {
            interactionChecker.checkInteriorInteraction();
        }

        // Monster knock dialogue
        if (monster.state == Monster.State.KNOCKING && !showMonsterDialogue && !monsterDialogueResponded && monster.actionTimer > 0) {

            showMonsterDialogue = true;
            dialogueFullText = "Don't choose 'yes', if you don't want me to come!";
            dialogueText = "";
            dialogueCharIndex = 0;
            dialogueTickCounter = 0;

            onYesAction = () -> {
                player.takeDamage(25);
                showMonsterDialogue = false;
                monsterDialogueResponded = true;
            };

            onNoAction = () -> {
                showMonsterDialogue = false;
                monsterDialogueResponded = true;
            };
        }

        if (!isNight || monster.state != Monster.State.KNOCKING) {
            monsterDialogueResponded = false;
        }

        dC.update();

        if (dC.dayCount > 3 || player.hp <= 0) {
            isGameOver = true;
            gameState = GameState.LOSE;
            loseScreen.causeOfDeath = player.hp <= 0 ? "hp" : "time";
            loseScreen.reset();
        }

        /* Night-end check
        if (wasNight && !isNight) {
            if (!playerHealedThisNight) {
                isGameOver = true;
                System.out.println("Game Over! You didn't heal during the night.");
            }
            playerHealedThisNight = false;
        }*/

        wasNight = isNight;
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileM.draw(g2);
        objectM.draw(g2);
        if (tileM.currentMap == 1) { monster.draw(g2); }
        player.draw(g2);
        dC.draw(g2);
        dC.drawOverlay(g2);
        monster.drawAlert(g2);

        drawHUD(g2);
        drawHPBar(g2);
        drawMenuButton(g2);
        inventory.drawButtons(g2);
        inventory.drawInventoryPanel(g2);
        inventory.drawScrollPanel(g2);
        drawMenuPanel(g2);

        if (isGameOver) {
            if (gameState == GameState.WIN) winScreen.draw(g2);
            else if (gameState == GameState.LOSE) loseScreen.draw(g2);
        }

        drawNarrationPanel(g2);
        g2.dispose();
    }

    // HP bar
    private void drawHPBar(Graphics2D g2) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int barWidth = 200;
        int barHeight = 22;
        int x = Inventory.BTN_X;
        int y = 10;

        // Background panel
        g2.setColor(new Color(30, 28, 25, 200));
        g2.fillRect(x, y - 4, barWidth + 14, barHeight + 8);
        g2.setColor(new Color(90, 85, 78));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(x, y - 4, x + barWidth + 14, y - 4);
        g2.drawLine(x, y + barHeight + 4, x + barWidth + 14, y + barHeight + 4);

        // Bar track
        g2.setColor(new Color(20, 18, 15));
        g2.fillRect(x + 22, y, barWidth - 22, barHeight);

        double hpPercent = (double) player.hp / player.maxHP;
        int currentWidth = (int)((barWidth - 22) * hpPercent);

        if (player.hp > 60) {
            g2.setColor(new Color(45, 110, 55));
        } else if (player.hp > 30) {
            g2.setColor(new Color(140, 90, 20));
        } else {
            g2.setColor(new Color(120, 25, 25));
        }
        g2.fillRect(x + 22, y, currentWidth, barHeight);

        g2.setColor(new Color(65, 60, 55));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(x + 22, y, barWidth - 22, barHeight);

        // + icon
        g2.setFont(getImFell(16f));
        g2.setColor(new Color(140, 135, 128));
        g2.drawString("+", x + 4, y + 16);

        // Percentage text
        g2.setFont(getImFell(13f));
        g2.setColor(new Color(210, 205, 195));
        String text = player.hp + "%";
        int textWidth = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, x + 22 + (barWidth - 22) / 2 - textWidth / 2, y + 15);

        g2.setStroke(new BasicStroke(1f));
    }

    // Menu text button
    private void drawMenuButton(Graphics2D g2) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.setColor(showMenuPanel ? new Color(50, 48, 45, 230) : new Color(30, 28, 25, 200));
        g2.fillRect(MENU_BTN_X, MENU_BTN_Y, MENU_BTN_W, MENU_BTN_H);

        g2.setColor(showMenuPanel ? new Color(160, 155, 148) : new Color(90, 85, 78));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(MENU_BTN_X, MENU_BTN_Y, MENU_BTN_W, MENU_BTN_H);

        g2.setFont(getImFell(12f));
        g2.setColor(showMenuPanel ? new Color(210, 205, 195) : new Color(140, 135, 128));
        String label = "Menu";
        int sw = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, MENU_BTN_X + (MENU_BTN_W - sw) / 2, MENU_BTN_Y + MENU_BTN_H - 6);

        g2.setStroke(new BasicStroke(1f));
    }

    // Menu panel
    private void drawMenuPanel(Graphics2D g2) {

        if (!showMenuPanel) return;

        int px = screenWidth  / 2 - MENU_PANEL_W / 2;
        int py = screenheight / 2 - MENU_PANEL_H / 2;

        inventory.drawPanelBase(g2, px, py, MENU_PANEL_W, MENU_PANEL_H, "Menu");

        int rowH = 44;
        int rowY = py + 62;

        drawMenuRow(g2, px, rowY, MENU_PANEL_W, isMuted ? "Unmute" : "Mute");
        rowY += rowH;

        // Divider
        g2.setColor(new Color(65, 60, 55));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(px + 30, rowY - 2, px + MENU_PANEL_W - 30, rowY - 2);
        rowY += 8;

        drawMenuRow(g2, px, rowY, MENU_PANEL_W, "Quit Game");

        g2.setStroke(new BasicStroke(1f));
    }

    private void drawMenuRow(Graphics2D g2, int px, int rowY, int pw, String label) {

        int rowH = 36;

        g2.setColor(new Color(45, 43, 40, 200));
        g2.fillRect(px + 12, rowY, pw - 24, rowH);


        g2.setFont(getImFell(15f));
        g2.setColor(new Color(190, 185, 175));
        int lw = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, px + pw / 2 - lw / 2, rowY + 24);

        g2.setStroke(new BasicStroke(1f));
    }


    private boolean isMenuBtnClicked(int mx, int my) {
        return mx >= MENU_BTN_X && mx <= MENU_BTN_X + MENU_BTN_W
            && my >= MENU_BTN_Y && my <= MENU_BTN_Y + MENU_BTN_H;
    }

    private boolean isMenuPanelXClicked(int mx, int my) {

        int px = screenWidth  / 2 - MENU_PANEL_W / 2;
        int py = screenheight / 2 - MENU_PANEL_H / 2;
        return mx >= px + MENU_PANEL_W - 24 && mx <= px + MENU_PANEL_W - 6
            && my >= py + 8 && my <= py + 28;
    }

    private boolean isMuteRowClicked(int mx, int my) {

        int px   = screenWidth  / 2 - MENU_PANEL_W / 2;
        int py   = screenheight / 2 - MENU_PANEL_H / 2;
        int rowY = py + 62;
        return mx >= px + 12 && mx <= px + MENU_PANEL_W - 12
            && my >= rowY && my <= rowY + 36;
    }

    private boolean isQuitRowClicked(int mx, int my) {

        int px   = screenWidth  / 2 - MENU_PANEL_W / 2;
        int py   = screenheight / 2 - MENU_PANEL_H / 2;
        int rowY = py + 62 + 44 + 8;
        return mx >= px + 12 && mx <= px + MENU_PANEL_W - 12
            && my >= rowY && my <= rowY + 36;
    }

    private void handleDialogueClick(int mx, int my) {

        if (!showMonsterDialogue) return;

        // Show full text kapag nagclick while still typing
        if (dialogueCharIndex < dialogueFullText.length()) {

            dialogueCharIndex = dialogueFullText.length();
            dialogueText = dialogueFullText;

            return;
        }

        int w = 460;
        int h = 160;
        int x = screenWidth / 2 - w / 2;
        int y = screenheight / 2 - h / 2;

        int yesX = x + 60;
        int yesY = y + 105;
        int yesW = 110;
        int yesH = 32;

        int noX = x + 290;
        int noY = y + 105;
        int noW = 110;
        int noH = 32;

        if (mx >= yesX && mx <= yesX + yesW && my >= yesY && my <= yesY + yesH) {

            if (onYesAction != null) {
                onYesAction.run();
            }
        }

        if (mx >= noX && mx <= noX + noW && my >= noY && my <= noY + noH) {

            if (onNoAction != null) {
                onNoAction.run();
            }
        }
    }

    private void drawNarrationPanel(Graphics2D g2) {
        if (!showNarration) return;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, screenWidth, screenheight);

        int px = screenWidth  / 2 - NARRATION_W / 2;
        int py = screenheight / 2 - NARRATION_H / 2;

        //bg
        g2.setColor(new Color(30, 28, 25, 235));
        g2.fillRect(px, py, NARRATION_W, NARRATION_H);

        //border
        g2.setColor(new Color(90, 85, 78));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(px, py, NARRATION_W, NARRATION_H);

        //accent line sa top 
        g2.setColor(new Color(65, 60, 55));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(px + 20, py + 36, px + NARRATION_W - 20, py + 36);

        //title 
        g2.setFont(getImFell(15f));
        g2.setColor(new Color(210, 205, 195));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("No Sanctuary", px + 20, py + 26);

        // narration text
        g2.setFont(getImFell(13f));
        g2.setColor(new Color(160, 155, 145));
        drawWrappedText(g2, narrationText, px + 20, py + 58, NARRATION_W - 40, 20);

        // accent line bottom 
        g2.setColor(new Color(65, 60, 55));
        g2.drawLine(px + 20, py + NARRATION_H - 46, px + NARRATION_W - 20, py + NARRATION_H - 46);

        //Continue button
        int btnW = 120;
        int btnH = 28;
        int btnX = px + NARRATION_W / 2 - btnW / 2;
        int btnY = py + NARRATION_H - 38;

       
        g2.setColor(new Color(90, 85, 78));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(btnX, btnY, btnW, btnH);

        g2.setFont(getImFell(13f));
        g2.setColor(new Color(190, 185, 175));
        String btnLabel = "Continue";
        int lw = g2.getFontMetrics().stringWidth(btnLabel);
        g2.drawString(btnLabel, btnX + btnW / 2 - lw / 2, btnY + 19);

        g2.setStroke(new BasicStroke(1f));
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.isEmpty()) return;
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (g2.getFontMetrics().stringWidth(test) > maxWidth) {
                g2.drawString(line.toString(), x, y);
                y += lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) g2.drawString(line.toString(), x, y);
    }

    private void drawPlayerHP(Graphics2D g2) {

        int barWidth = 200;
        int barHeight = 20;
        int x = 20;
        int y = 20;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, barWidth, barHeight);

        double hpPercent = (double) player.hp / player.maxHP;
        int currentWidth = (int)(barWidth * hpPercent);

        if (player.hp > 60) {
            g2.setColor(Color.GREEN);
        } else if (player.hp > 30) {
            g2.setColor(Color.ORANGE);
        } else {
            g2.setColor(Color.RED);
        }

        g2.fillRect(x, y, currentWidth, barHeight);

        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, barWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawString(player.hp + " % ", x + 70, y + 15);
    }

    // Panel - single line
    private void drawPromptBox(Graphics2D g2, String text, int cx, int y, float fontSize) {

        g2.setFont(getImFell(fontSize));
        int textW = g2.getFontMetrics().stringWidth(text);
        int padX = 18;
        int padY = 8;
        int boxW = textW + padX * 2;
        int boxH = (int) fontSize + padY * 2;
        int boxX = cx - boxW / 2;

        g2.setColor(new Color(8, 8, 6, 210));
        g2.fillRect(boxX, y, boxW, boxH);

        g2.setColor(new Color(200, 190, 165));
        g2.drawString(text, boxX + padX, y + padY + (int) fontSize - 2);
        g2.setStroke(new BasicStroke(1f));
    }

    // Panel with key box
    private void drawKeyPromptBox(Graphics2D g2, String key, String label, int cx, int y, float fontSize) {

        g2.setFont(getImFell(fontSize));
        int keyBoxSize = (int) fontSize + 6;
        int labelW = g2.getFontMetrics().stringWidth(label);
        int keyW = g2.getFontMetrics().stringWidth(key);
        int gap = 8;
        int totalW = keyBoxSize + gap + labelW;
        int padX = 18;
        int padY = 8;
        int boxW = totalW + padX * 2;
        int boxH = keyBoxSize + padY * 2;
        int boxX = cx - boxW / 2;

        // Outer panel
        g2.setColor(new Color(8, 8, 6, 210));
        g2.fillRect(boxX, y, boxW, boxH);
        
        g2.drawRect(boxX, y, boxW, boxH);

        // Key box
        int keyX = boxX + padX;
        int keyY = y + padY;
        
        g2.setColor(new Color(220, 210, 180));
        g2.setFont(getImFell(fontSize));
        g2.drawString(key, keyX + (keyBoxSize - keyW) / 2, keyY + keyBoxSize - 4);

        // Label
        g2.setColor(new Color(180, 170, 145));
        g2.drawString(label, keyX + keyBoxSize + gap, keyY + keyBoxSize - 4);

        g2.setStroke(new BasicStroke(1f));
    }

    private void drawHUD(Graphics2D g2) {

        int cx = screenWidth / 2;

        if (tileM.currentMap == 1) {

            if (interactionChecker.showDoorPrompt) {

                models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
                if (house.isDoorOpen) {
                    drawKeyPromptBox(g2, "E", "Enter", cx - 80, screenheight - 70, 15f);
                    drawKeyPromptBox(g2, "F", "Close Door", cx + 60, screenheight - 70, 15f);
                } else {
                    drawKeyPromptBox(g2, "E", "Open Door", cx, screenheight - 70, 15f);
                }
            }

            return;
        }

        // Exit door
        if (interactionChecker.showExitPrompt) {

            models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
            if (house.isDoorOpen) {
                drawKeyPromptBox(g2, "E", "Exit", cx - 80, screenheight - 70, 15f);
                drawKeyPromptBox(g2, "F", "Close Door", cx + 60, screenheight - 70, 15f);
            } else {
                drawKeyPromptBox(g2, "F", "Open Door", cx, screenheight - 70, 15f);
            }
        }

        // Window
        if (interactionChecker.showWindowPrompt) {

            drawKeyPromptBox(g2, "E", "Open / Close Window", cx, screenheight - 70, 15f);
        }

        // Torch
        if (interactionChecker.showTorchPrompt) {

            models.ObjTorch torch = objectM.interior.torch;
            String statusText = torch.isLit
                    ? "Torch burning  —  " + torch.getSecondsLeft() + "s left"
                    : "Torch is OUT";
            Color statusColor = torch.isLit ? new Color(180, 140, 60) : new Color(120, 80, 80);

            // Torch status box
            g2.setFont(getImFell(13f));
            int statusW = g2.getFontMetrics().stringWidth(statusText);
            int sPadX = 14;
            int sPadY = 6;
            int sBoxW = statusW + sPadX * 2;
            int sBoxH = 13 + sPadY * 2;
            int sBoxX = cx - sBoxW / 2;
            int sBoxY = screenheight - 110;
            
            g2.setColor(new Color(8, 8, 6, 200));
            g2.fillRect(sBoxX, sBoxY, sBoxW, sBoxH);
            g2.setColor(new Color(70, 60, 45));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(sBoxX, sBoxY, sBoxW, sBoxH);
            g2.setColor(statusColor);
            g2.drawString(statusText, sBoxX + sPadX, sBoxY + sPadY + 11);

            drawKeyPromptBox(g2, "E", "Fuel Torch  (costs 3 wood)", cx, screenheight - 70, 15f);
        }

        if (interactionChecker.hasTorchFeedback()) {

            drawPromptBox(g2, interactionChecker.torchFeedback, cx, screenheight - 140, 13f);
        }

        // Cabinet
        if (interactionChecker.showCabinetPrompt && !objectM.interior.cabinet.isOpen) {

            drawKeyPromptBox(g2, "E", "Open Cabinet", cx, screenheight - 70, 15f);
        }

        if (interactionChecker.hasCabinetFeedback()) {

            drawPromptBox(g2, interactionChecker.cabinetFeedback, cx, screenheight - 140, 13f);
        }

        // Apple table
        if (interactionChecker.showAppleTablePrompt && !objectM.interior.appleTable.isOpen) {

            drawKeyPromptBox(g2, "E", "Open Table (Apple)", cx, screenheight - 70, 15f);
        }

        if (interactionChecker.hasAppleTableFeedback()) {

            drawPromptBox(g2, interactionChecker.appleTableFeedback, cx, screenheight - 140, 13f);
        }

        // Darkness overlay
        boolean isNight = dC.currentState == dayCounter.dayNightState.Night;

        if (isNight && !objectM.interior.torch.isLit) {

            g2.setColor(new Color(0, 0, 8, 130));
            g2.fillRect(0, 0, screenWidth, screenheight);
        }

        if (showMonsterDialogue) {

            int w = 460;
            int h = 160;
            int x = screenWidth / 2 - w / 2;
            int y = screenheight / 2 - h / 2;

            // Outer panel
            g2.setColor(new Color(6, 5, 4, 230));
            g2.fillRect(x, y, w, h);

            // Label
            g2.setFont(getImFell(12f));
            g2.setColor(new Color(120, 100, 70));
            g2.drawString("Outside :", x + 16, y + 22);

            // divider
            g2.setColor(new Color(55, 45, 35));
            g2.drawLine(x + 10, y + 30, x + w - 10, y + 30);

            // Dialogue text (typewriter)
            g2.setFont(getImFell(15f));
            g2.setColor(new Color(195, 182, 155));
            g2.drawString(dialogueText, x + 16, y + 60);

            if (dialogueCharIndex < dialogueFullText.length()) {

                long blink = (System.currentTimeMillis() / 400) % 2;

                if (blink == 0) {

                    g2.setColor(new Color(150, 130, 100));
                    int cursorX = x + 16 + g2.getFontMetrics().stringWidth(dialogueText);
                    g2.fillRect(cursorX + 2, y + 44, 2, 16);
                }
            }

            // YES / NO btn
            boolean typingDone = dialogueCharIndex >= dialogueFullText.length();
            int btnAlpha = typingDone ? 220 : 80;

            // YES button
            
            g2.setColor(new Color(55, 80, 45, btnAlpha));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(x + 60, y + 105, 110, 32);
            g2.setFont(getImFell(14f));
            g2.setColor(new Color(140, 170, 110, btnAlpha));
            g2.drawString("Yes", x + 103, y + 126);

            // NO button
           
            g2.setColor(new Color(90, 40, 40, btnAlpha));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(x + 290, y + 105, 110, 32);
            g2.setColor(new Color(180, 100, 90, btnAlpha));
            g2.drawString("No", x + 336, y + 126);

            g2.setStroke(new BasicStroke(1f));
        }
    }

    public void switchToInterior() {

        tileM.switchMap(2);
        player.worldX = 160;
        player.worldY = 144;
    }

    public void switchToExterior() {

        tileM.switchMap(1);
        player.worldX = 1455;
        player.worldY = 1056;
    }

    public boolean isPlayerSafe() {

        if (tileM.currentMap != 2) {
            return false;
        }

        models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
        return !house.isDoorOpen && !house.isWindow1Open && !house.isWindow2Open && !house.isWindow3Open;
    }
}