package game;

import database.DatabaseConn;
import java.awt.BasicStroke;
import models.Monster;
import models.Player;
import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

public class panel extends JPanel implements Runnable, LandingPage.LandingPageListener {

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
    public playerDataHolder holder;
    public DatabaseConn dbConn;
    public LandingPage LPage;
    public RiddleManager riddleM = new RiddleManager(this);
    public RiddleUI riddleUI = new RiddleUI(this);
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
    public enum GameState {
        PLAYING, WIN, LOSE
    }
    public GameState gameState = GameState.PLAYING;
    public WinScreen winScreen = new WinScreen(this);
    public LoseScreen loseScreen = new LoseScreen(this);
    private long gameStartTime = System.currentTimeMillis();

    // Settings / Menu
    private boolean showMenuPanel = false;
    public boolean isMuted = false;
    
    public Sound doorCreak = new Sound();
    public Sound doorKnock = new Sound();
    public Sound heartbeat = new Sound();
    public Sound landingMusic = new Sound();
    public Sound monsterAttack = new Sound();
    public Sound hardKnock = new Sound();
    public Sound musicLose = new Sound();
    public Sound musicWin = new Sound();
    public Sound playerFootsteps = new Sound();
    public Sound torchLight = new Sound();
    public Sound torchOut = new Sound();
    public Sound typewriting = new Sound();
    public Sound windowCreak = new Sound();
    public Sound musicBox = new Sound();
    public Sound forcedEntry = new Sound();

    

    private static final int MENU_BTN_X = 900;
    private static final int MENU_BTN_Y = 10;
    private static final int MENU_BTN_W = 50;
    private static final int MENU_BTN_H = 22;

    private static final int MENU_PANEL_W = 480;
    private static final int MENU_PANEL_H = 260;

    public boolean showNarration = true;
    public String narrationText = "";
    private static final int NARRATION_W = 600;
    private static final int NARRATION_H = 290;

    public float narrationAlpha = 0f;
    public boolean narrationComplete = false;
    public boolean narrationFadeOut = false;
    
    public String username;
    public int playerID;
    public String returnUsername;

    //narration 
    private static final String[] NARRATION_PAGES = {
        "You weren't supposed to survive the crash. The forest made sure of that — or so it thought. But here you are, still breathing, still bleeding, fingers clawing at the mud while something in the treeline watches you rise. It has been watching since before you even arrived.",
        "Three days. That is all you have before the darkness consumes what little remains of this place — and of you. Not because of the cold. Not because of hunger. Something is counting down alongside you, and it is far more patient than you will ever be.",
        "It started on the first night. A sound outside the window. Not wind. Not an animal. Something deliberate. Something that already knew the layout of the house, which floorboards creak, which window latch is weak. Something that had clearly been here before.",
        "Every night, it comes back. It does not tire. It does not sleep. It does not feel the cold the way you do. It has been waiting in this forest long before you stumbled into it — and it will still be here long after your name is forgotten and your bones go cold.",
        "The others who came before you boarded up every window. Stacked furniture against the door. Lit every torch they could find and prayed the light would keep it out. You can still see the scratch marks on the outside of the shutters. Long, deep, and very deliberate.",
        "But you are still breathing. That means something — though you are not yet sure what. Maybe you are different. Maybe the thing outside is simply not done with you yet. Maybe it wants you to reach the portal first. Maybe it feeds on hope before it feeds on anything else.",
        "Find the portal before the third night ends. Gather what you can. Keep the torches burning. Keep the doors sealed. And if you hear knocking — do not mistake it for rescue. Nothing out there is coming to save you. It is only checking whether you are still afraid.",
        "They touch, they break, they steal. No one here is free. Here they come, they come for three. Until you stop the melody..."
        
    };
    private int narPageIndex = 0;
    private int narCharIndex = 0;
    private int narTickCounter = 0;
    private static final int NAR_TYPEWRITER_DELAY = 2; 
    
    private int knockDelayTimer = 0;
    private int musicBoxDelayTimer = 0;
    private boolean knockPlayed = false;
    
    
    public long getGameStartTime() {
        return gameStartTime;
    }

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
        
        doorCreak.load("door_creak.wav");
        doorKnock.load("door_knock.wav");
        heartbeat.load("heartbeat.wav");
        landingMusic.load("landingPage_music.wav");
        monsterAttack.load("monster_attack.wav");
        hardKnock.load("monster_hardKnock.wav");
        musicLose.load("music_lose.wav");
        musicWin.load("music_win.wav");
        playerFootsteps.load("player_footsteps.wav");
        torchLight.load("torch_light.wav");
        torchOut.load("torch_out.wav");
        typewriting.load("typewriting.wav");
        windowCreak.load("window_creak.wav");
        musicBox.load("musicBox.wav");
        forcedEntry.load("forcedEntry.wav");

        this.parentFrame = frame;
        dbConn = new DatabaseConn(this);
        dC = new dayCounter(this);
        holder = new playerDataHolder();
        LPage = new LandingPage(this,this);

        this.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {

                int mx = e.getX();
                int my = e.getY();

                if (riddleUI.isOpen) {
                    riddleUI.handleClick(mx, my);
                    return;
                }

                if (showNarration) {

                    int px = screenWidth / 2 - NARRATION_W / 2;
                    int py = screenheight / 2 - NARRATION_H / 2;
                    int btnW = 100, btnH = 32;
                    int btnX = px + NARRATION_W - btnW - 16;
                    int btnY = py + NARRATION_H - 48;

                    if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH
                            && narPageIndex < NARRATION_PAGES.length) {
                        String fullPage = NARRATION_PAGES[narPageIndex];
                        
                        if (narCharIndex < fullPage.length()) {
                            // First click reveals remaining text
                            narCharIndex = fullPage.length();
                            narrationText = fullPage;
                        } else {
                            advanceNarration();
                        }
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
        
        musicLose.stop();
        musicWin.stop(); 
        heartbeat.stop();
        playerFootsteps.stop();

        GameThread = null;
        parentFrame.getContentPane().removeAll();
        LandingPage landingPage = new LandingPage(this,() -> {
            parentFrame.getContentPane().removeAll();
            panel gamePanel = new panel(parentFrame);
            parentFrame.add(gamePanel);
            parentFrame.pack();
            parentFrame.revalidate();
            parentFrame.repaint();
            parentFrame.setLocationRelativeTo(null);

            gamePanel.showNarration = true;
            gamePanel.narrationAlpha = 0f;
            gamePanel.narrationComplete = true;
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
                if (remaining < 0) {
                    remaining = 0;
                }
                Thread.sleep((long) remaining);
                nextDrawTime += drawInterval;

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean wasNight = false;

    public void update() {

        // Update key handler debounce - CRITICAL for new interaction system
        keyH.update();

        if (narrationComplete) {

            narrationAlpha += 0.02f;

            if (narrationAlpha >= 1f) {

                narrationAlpha = 1f;
                narrationComplete = false;
                
                // Start typewriter from first page
                narPageIndex = 0;
                narCharIndex = 0;

                narTickCounter = 0;
                narrationText = "";
            }
            return;
        }

        if (narrationFadeOut) {

            narrationAlpha -= 0.02f;

            if (narrationAlpha <= 0f) {

                narrationAlpha = 0f;
                narrationFadeOut = false;
                showNarration = false;
            }

            return;
        }

        if (showNarration) {

            // Typewriter
            String fullPage = NARRATION_PAGES[narPageIndex];
            if (narCharIndex < fullPage.length()) {
                narTickCounter++;
                
                if (narTickCounter >= NAR_TYPEWRITER_DELAY) {
                    narTickCounter = 0;
                    narCharIndex++;
                    narrationText = fullPage.substring(0, narCharIndex);
                    
                    if (!typewriting.isRunning()) {
                        typewriting.loop();
                    }
                }
                
            } else {
                typewriting.stop();
            }

            // Space / Enter: skip or advance
            if (keyH.skipPressed) {
                
                keyH.skipPressed = false;
                
                if (narCharIndex < fullPage.length()) {
                    
                    typewriting.stop();
                    narCharIndex  = fullPage.length();
                    narrationText = fullPage;
                    
                } else {
                    // Advance to next page or close
                    advanceNarration();
                }
            }

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

        if (showMonsterDialogue) {

            if (dialogueCharIndex < dialogueFullText.length()) {

                dialogueTickCounter++;

                if (dialogueTickCounter >= TYPEWRITER_DELAY) {

                    dialogueTickCounter = 0;
                    dialogueCharIndex++;
                    dialogueText = dialogueFullText.substring(0, dialogueCharIndex);
                    
                    if (!typewriting.isRunning()) {
                        typewriting.loop(); 
                    }
                }
                
            }else {
                 typewriting.stop();
            }
            
            return;
        }

        if (isNight && !wasNight) {
            monster.spawnNearEdge();
            musicBox.loop();
        }

        //Riddle UI
        if (riddleUI.isOpen) {
            riddleUI.update();
        }

        player.update();
        monster.update();
        objectM.update();

        if (tileM.currentMap == 1) {
            
            interactionChecker.checkInteraction();
            
            if (monster.forceEnter) {
                monster.forceEnter = false;
                monster.worldX = 1515;
                monster.worldY = 975;
                monster.state = Monster.State.CHASING;
            }
            
        } else {
            interactionChecker.checkInteriorInteraction();
        }

        // Monster knock dialogue
        if (monster.state == Monster.State.KNOCKING && !showMonsterDialogue && !monsterDialogueResponded && monster.actionTimer > 0) {
            
            if (knockDelayTimer == 0 && !knockPlayed) {
                
                musicBox.stop();
                knockDelayTimer = 4 * 60; 
            }
        }

        if (knockDelayTimer > 0) {
            
            knockDelayTimer--;

            if (knockDelayTimer == 0 && !knockPlayed) {
                
                doorKnock.play();
                knockPlayed = true;
                knockDelayTimer = (3 * 60); 
            }
            else if (knockDelayTimer == 0 && knockPlayed && !showMonsterDialogue && !monsterDialogueResponded) {
                
                showMonsterDialogue = true;
                dialogueFullText = "Hello?! Is anyone in there?! Please, I need help!      \nSomething is out here with me!     \nPlease just open the door! I am just a person, I promise!";
                dialogueText = "";
                dialogueCharIndex = 0;
                dialogueTickCounter = 0;

                onYesAction = () -> {
                    showMonsterDialogue = false;
                    monsterDialogueResponded = true;
                    hardKnock.play();
                    musicBox.stop();
                    tileM.switchMap(2);
                    monster.forceEnter = true;
                    monster.spawnInsideHouse();
                };

                onNoAction = () -> {
                    
                    showMonsterDialogue = false;
                    monsterDialogueResponded = true;
                    knockPlayed = false;
                    hardKnock.play();
                    heartbeat.loop();
                    musicBox.stop();
                    player.heartbeatTimer = 3*60;
                    musicBoxDelayTimer = hardKnock.getDurationTicks() + (4 * 60);
                };
            }
        }

        if (!isNight || monster.state != Monster.State.KNOCKING) {
            
            monsterDialogueResponded = false;
            knockDelayTimer = 0;
            knockPlayed = false;
        }
      
        if (!isNight && wasNight) {
            
            musicBox.stop();
            
            if (monster.forceEnter) {
                
                monster.forceEnter = false;
                monster.worldX = -1000;
                monster.worldY = -1000;
                monster.state = Monster.State.IDLE;
            }
        }

        dC.update();

        if (player.hp <= 0) {
            
            isGameOver = true;
            gameState = GameState.LOSE;
            loseScreen.causeOfDeath = player.hp <= 0 ? "hp" : "time";
            loseScreen.reset();
            heartbeat.stop();
            musicBox.stop();
            musicLose.play();
        }
        
        if (musicBoxDelayTimer > 0) {
            
            musicBoxDelayTimer--;
            
            if (musicBoxDelayTimer == 0) {
                
                if (isNight && !musicBox.isRunning()) {
                    
                    musicBox.loop(); 
                }
            }
        }
        


        wasNight = isNight;
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (!showNarration) {
            
            tileM.draw(g2);
            objectM.draw(g2);

            if (tileM.currentMap == 1 || monster.forceEnter) {
                monster.draw(g2);
            }

            player.draw(g2);
            dC.draw(g2);
            dC.drawOverlay(g2);

            drawHUD(g2);
            drawHPBar(g2);
            drawMenuButton(g2);
            inventory.drawButtons(g2);
            inventory.drawInventoryPanel(g2);
            inventory.drawScrollPanel(g2);
            drawMenuPanel(g2);
        }
        if (isGameOver) {
            
            if (gameState == GameState.WIN) {
                
                winScreen.draw(g2);
                
            } else if (gameState == GameState.LOSE) {
                
                loseScreen.draw(g2);
            }
        }

        drawNarrationPanel(g2);
        if (riddleUI.isOpen) {
            riddleUI.draw(g2);
        }
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
        int currentWidth = (int) ((barWidth - 22) * hpPercent);

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

        if (!showMenuPanel) {
            return;
        }

        int px = screenWidth / 2 - MENU_PANEL_W / 2;
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
        

        int px = screenWidth / 2 - MENU_PANEL_W / 2;
        int py = screenheight / 2 - MENU_PANEL_H / 2;
        return mx >= px + MENU_PANEL_W - 24 && mx <= px + MENU_PANEL_W - 6
                && my >= py + 8 && my <= py + 28;
    }

    private boolean isMuteRowClicked(int mx, int my) {

        int px = screenWidth / 2 - MENU_PANEL_W / 2;
        int py = screenheight / 2 - MENU_PANEL_H / 2;
        int rowY = py + 62;
        return mx >= px + 12 && mx <= px + MENU_PANEL_W - 12
                && my >= rowY && my <= rowY + 36;
    }

    private boolean isQuitRowClicked(int mx, int my) {

        int px = screenWidth / 2 - MENU_PANEL_W / 2;
        int py = screenheight / 2 - MENU_PANEL_H / 2;
        int rowY = py + 62 + 44 + 8;
        return mx >= px + 12 && mx <= px + MENU_PANEL_W - 12
                && my >= rowY && my <= rowY + 36;
    }

    private void handleDialogueClick(int mx, int my) {

        if (!showMonsterDialogue) {
            return;
        }

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
        int yesY = y + 140;
        int yesW = 110;
        int yesH = 32;

        int noX = x + 290;
        int noY = y + 140;
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
        
        if (!showNarration) {
            return;
        }

        if (narPageIndex >= NARRATION_PAGES.length) {
            Composite orig2 = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, narrationAlpha));
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRect(0, 0, screenWidth, screenheight);
            g2.setComposite(orig2);
            
            return;
        }

        Composite orig = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, narrationAlpha));

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, screenWidth, screenheight);

        int px = screenWidth / 2 - NARRATION_W / 2;
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
        g2.drawLine(px + 20, py + 40, px + NARRATION_W - 20, py + 40);

        //title 
        g2.setFont(getImFell(15f));
        g2.setColor(new Color(210, 205, 195));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("No Sanctuary", px + 20, py + 28);

        // narration text
        g2.setFont(getImFell(15f));
        g2.setColor(new Color(168, 162, 150));
        
        int textStartY = py + 84;
        drawWrappedText(g2, narrationText, px + 28, textStartY, NARRATION_W - 56, 26);

        // Bottom accent line
        g2.setColor(new Color(55, 50, 46));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(px + 20, py + NARRATION_H - 48, px + NARRATION_W - 20, py + NARRATION_H - 48);

        // Footer
        int footerY = py + NARRATION_H - 22;

        // Page indicator
        g2.setFont(getImFell(12f));
        String pageLabel = (narPageIndex + 1) + " / " + NARRATION_PAGES.length;
        
        g2.setColor(new Color(110, 105, 98));
        g2.drawString(pageLabel, px + 24, footerY);

        // Skip hint
        String hint = "Space · Enter · Click to advance";
        int hintW = g2.getFontMetrics().stringWidth(hint);
        
        g2.setColor(new Color(90, 86, 80));
        g2.drawString(hint, px + NARRATION_W / 2 - hintW / 2, footerY);

        // Blinking cursor
        String fullPage = NARRATION_PAGES[narPageIndex];
        
        if (narCharIndex < fullPage.length()) {
            
            long blink = (System.currentTimeMillis() / 400) % 2;
            
            if (blink == 0) {
                
                g2.setFont(getImFell(15f));
                g2.setColor(new Color(160, 155, 145, 180));
                
                FontMetrics fm = g2.getFontMetrics();
                int maxWidth = NARRATION_W - 56;
                String[] words = narrationText.split(" ");
                String currentLine = "";
                int lineCount = 0;

                for (String word : words) {
                    
                    String test = currentLine.isEmpty() 
                                ? word 
                                : currentLine + " " + word;
                    if (fm.stringWidth(test) > maxWidth) {
                        lineCount++;
                        currentLine = word;
                    } else {
                        currentLine = test;
                    }
                }

                int cursorX = px + 28 + fm.stringWidth(currentLine);
                int cursorY = textStartY + (lineCount * 26);
                
                g2.setColor(new Color(160, 155, 145, 180));
                g2.fillRect(cursorX + 2, cursorY - 14, 2, 16);
            }
        }

        // Continue button
        int btnW = 100;
        int btnX = px + NARRATION_W - btnW - 16;
        int btnY = py + NARRATION_H - 48;
        g2.setFont(getImFell(13f));
        String btnLabel = narCharIndex < fullPage.length()
                ? "Skip >"
                : (narPageIndex + 1 < NARRATION_PAGES.length ? "Next >" : "Begin");
        g2.setColor(new Color(195, 188, 174));
        int lw = g2.getFontMetrics().stringWidth(btnLabel);
        g2.drawString(btnLabel, btnX + btnW / 2 - lw / 2, btnY + 28);

        g2.setStroke(new BasicStroke(1f));

        g2.setComposite(orig);
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.isEmpty()) {
            return;
        }
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
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, y);
        }
    }

    private void drawPlayerHP(Graphics2D g2) {

        int barWidth = 200;
        int barHeight = 20;
        int x = 20;
        int y = 20;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, barWidth, barHeight);

        double hpPercent = (double) player.hp / player.maxHP;
        int currentWidth = (int) (barWidth * hpPercent);

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

            if (interactionChecker.showPortalPrompt) {
                drawKeyPromptBox(g2, "E", "Enter the Portal", cx, screenheight - 110, 15f);
            }

            if (interactionChecker.showRiddlePrompt) {
                
                int ri = interactionChecker.nearRiddleIndex;
                boolean solved = ri >= 0 && riddleM.getRiddle(ri) != null && riddleM.getRiddle(ri).solved;
                
                if (!solved) {
                    drawKeyPromptBox(g2, "E", "Read the Riddle  (" + (riddleM.solvedCount) + "/3 solved)", cx, screenheight - 110, 15f);
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
            int h = 180;
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
            FontMetrics dlgFm = g2.getFontMetrics();
            
            int lineHeight = 22;
            int textStartX = x + 16;
            int textStartY = y + 60;

            String[] lines = dialogueText.split("\n", -1);
            
            for (int i = 0; i < lines.length; i++) {
                
                g2.drawString(lines[i], textStartX, textStartY + (i * lineHeight));
            }

            if (dialogueCharIndex < dialogueFullText.length()) {
                
                long blink = (System.currentTimeMillis() / 400) % 2;
                
                if (blink == 0) {
                    
                    String lastLine = lines[lines.length - 1];
                    int cursorX = textStartX + dlgFm.stringWidth(lastLine);
                    int cursorY = textStartY + ((lines.length - 1) * lineHeight);
                    
                    g2.setColor(new Color(150, 130, 100));
                    g2.fillRect(cursorX + 2, cursorY - 14, 2, 16);
                }
            }

            // YES / NO btn
            boolean typingDone = dialogueCharIndex >= dialogueFullText.length();
            int btnAlpha = typingDone ? 220 : 80;

            // YES button
            g2.setFont(getImFell(14f));
            g2.setColor(new Color(140, 170, 110, btnAlpha));
            g2.drawString("Yes", x + 103, y + 166);

            // NO button
            g2.setColor(new Color(180, 100, 90, btnAlpha));
            g2.drawString("No", x + 336, y + 166);

            g2.setStroke(new BasicStroke(1f));
        }
    }

    private void advanceNarration() {
        narPageIndex++;
        
        if (narPageIndex >= NARRATION_PAGES.length) {
            
            narrationFadeOut = true;
            
        } else {
            narCharIndex = 0;
            narTickCounter = 0;
            narrationText = "";
        }
    }

    public void switchToInterior() {

        tileM.switchMap(2);
        player.worldX = 160;
        player.worldY = 144;
    }

    public void switchToExterior() {

        tileM.switchMap(1);
        player.worldX = 872;
        player.worldY = 700;
    }

    public boolean isPlayerSafe() {

        if (tileM.currentMap != 2) {
            return false;
        }

        models.ObjHouse house = (models.ObjHouse) objectM.ObjHouse[0];
        return !house.isDoorOpen && !house.isWindow1Open && !house.isWindow2Open && !house.isWindow3Open;
    }

    @Override
    public void startGame() {
        
        showNarration = true;
        narrationAlpha = 0f;
        narrationComplete = true;
        narrationFadeOut = false;
        dC.startTime = System.nanoTime();
    }
}
