package game;

import database.DatabaseConn;
import java.awt.BasicStroke;
import models.*;
import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import models.GameObject;

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
    public DifficultyManager diffM = new DifficultyManager(this);
    public Monster monster2 = new Monster(this);
    public Monster monster3 = new Monster(this);
    public NPC npc = new NPC(this);

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

    private String dialogueFullText = "";
    private String dialogueFullTextMonster;
    private String dialogueFullTextNPC;
    private int dialogueCharIndex = 0;
    private int dialogueTickCounter = 0;
    private static final int TYPEWRITER_DELAY = 3;

    public enum GameState { PLAYING, WIN, LOSE }
    public GameState gameState = GameState.PLAYING;
    public WinScreen winScreen = new WinScreen(this);
    public LoseScreen loseScreen = new LoseScreen(this);
    private long gameStartTime = System.currentTimeMillis();

    private boolean showMenuPanel = false;
    public boolean isMuted = false;

    public Sound doorCreak = new Sound();
    public Sound doorKnock = new Sound();
    public Sound heartbeat = new Sound();
    public Sound landingMusic= new Sound();
    public Sound monsterAttack=new Sound();
    public Sound hardKnock = new Sound();
    public Sound musicLose = new Sound();
    public Sound musicWin = new Sound();
    public Sound playerFootsteps=new Sound();
    public Sound torchLight = new Sound();
    public Sound torchOut = new Sound();
    public Sound typewriting = new Sound();
    public Sound windowCreak = new Sound();
    public Sound musicBox = new Sound();
    public Sound forcedEntry = new Sound();

    private static final int MENU_BTN_X = 900, MENU_BTN_Y = 10, MENU_BTN_W = 50, MENU_BTN_H = 22;
    private static final int MENU_PANEL_W = 480, MENU_PANEL_H = 260;

    public boolean showNarration = true;
    public String narrationText = "";
    private static final int NARRATION_W = 600, NARRATION_H = 290;
    public float narrationAlpha = 0f;
    public boolean narrationComplete = false;
    public boolean narrationFadeOut = false;
    public boolean gameWorldFadeIn = false;
    public float gameWorldAlpha = 1f;
    public int interiorGraceTimer = 0;

    { monster2.isPrimary = false; monster3.isPrimary = false; }

    public String username;
    public int playerID;
    public String returnUsername;
    public boolean isNPCKnocking = false;
    public boolean npcIsAlly = false;
    public boolean showNPCDialogue = false;

    public float musicVolume = 0.7f;
    public float sfxVolume = 0.7f;
    
    // LEVEL TRANSITION
    public enum TransitionState { NONE, FADE_OUT, HOLD, FADE_IN }
    public TransitionState transitionState = TransitionState.NONE;
    private float transitionAlpha = 0f;
    private int transitionHoldTimer = 0;
    private static final int TRANSITION_HOLD_TICKS = 120;
    private static final float FADE_OUT_SPEED = 0.018f;
    private static final float FADE_IN_SPEED = 0.012f;    
    private int pendingLevel = -1;
    private String levelLabelText = "";
    private float levelLabelAlpha = 0f;
    private int levelLabelTimer = 0;
    private static final int LEVEL_LABEL_DURATION = 160;

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
    
    private int narPageIndex = 0, narCharIndex = 0, narTickCounter = 0;
    private static final int NAR_TYPEWRITER_DELAY = 2;
    private int knockDelayTimer = 0, musicBoxDelayTimer = 0;
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
                if (is != null) imFellBase = Font.createFont(Font.TRUETYPE_FONT, is);
            
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
        LPage = new LandingPage(this, this);
        landingMusic.loop();

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                
                int mx = e.getX(), my = e.getY();
                if (riddleUI.isOpen) { 
                    riddleUI.handleClick(mx, my); 
                    return; 
                }
                
                if (showNarration) {
                    
                    int px = screenWidth/2-NARRATION_W/2, py = screenheight/2-NARRATION_H/2;
                    int btnW=100, btnH=32, btnX=px+NARRATION_W-btnW-16, btnY=py+NARRATION_H-48;
                    
                    if (mx>=btnX&&mx<=btnX+btnW&&my>=btnY&&my<=btnY+btnH&&narPageIndex<NARRATION_PAGES.length) {
                        String fp=NARRATION_PAGES[narPageIndex];
                        if (narCharIndex<fp.length()) { narCharIndex=fp.length(); narrationText=fp; }
                        else { advanceNarration(); }
                    }
                    
                    return;
                }
                
                handleDialogueClick(mx, my);
                
                if (gameState==GameState.WIN && winScreen.handleClick(mx,my)) {
                    returnToMenu();
                }
                
                if (gameState==GameState.LOSE && loseScreen.handleClick(mx,my)) {
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
        
        if (inventory.isBagBtnClicked(mx,my)) { 
            inventory.showPanel=!inventory.showPanel; 
            
            if(inventory.showPanel){
                inventory.showScroll=false;
                showMenuPanel=false;
            } 
            return; 
        }
        
        if (inventory.isScrollBtnClicked(mx,my)) { 
            inventory.showScroll=!inventory.showScroll; 
            
            if(inventory.showScroll){
                inventory.showPanel=false;
                showMenuPanel=false;
            } 
            return; 
        }
        
        if (isMenuBtnClicked(mx,my)) { 
            showMenuPanel=!showMenuPanel; 
            
            if(showMenuPanel){
                inventory.showPanel=false;
                inventory.showScroll=false;
            } return; 
        }
        
        if (inventory.showPanel) {
            
            if (inventory.isInventoryXClicked(mx,my)) { 
                inventory.showPanel=false;
                return; 
            }
            
            if (inventory.isEatClicked(mx,my)) { 
                if(inventory.eatApple()) {
                    player.hp=Math.min(player.maxHP,player.hp+20);
                    
                } return; 
            }
        }
        
        if (inventory.showScroll&&inventory.isScrollXClicked(mx,my)) { 
            inventory.showScroll=false;
            return;
        }
        if (showMenuPanel) {
            
            if (isMenuPanelXClicked(mx,my)) { 
                showMenuPanel=false; return; 
            }
            
            
            if (isQuitRowClicked(mx,my)) {
                returnToMenu();
                return; 
            }
            
        }
    }
    
    private boolean isMusicMinusClicked(int mx, int my) {
        int px=screenWidth/2-MENU_PANEL_W/2, py=screenheight/2-MENU_PANEL_H/2;
        int rowY=py+62, minusX=px+MENU_PANEL_W-90;
        
        return mx>=minusX&&mx<=minusX+24&&my>=rowY+7&&my<=rowY+29;
    }
    
    private boolean isMusicPlusClicked(int mx, int my) {
        int px=screenWidth/2-MENU_PANEL_W/2, py=screenheight/2-MENU_PANEL_H/2;
        int rowY=py+62, plusX=px+MENU_PANEL_W-90+24+4+60+4;
        
        return mx>=plusX&&mx<=plusX+24&&my>=rowY+7&&my<=rowY+29;
    }
    
    private boolean isSFXMinusClicked(int mx, int my) {
        int px=screenWidth/2-MENU_PANEL_W/2, py=screenheight/2-MENU_PANEL_H/2;
        int rowY=py+62+50+8, minusX=px+MENU_PANEL_W-90;
        
        return mx>=minusX&&mx<=minusX+24&&my>=rowY+7&&my<=rowY+29;
    }
    
    private boolean isSFXPlusClicked(int mx, int my) {
        int px=screenWidth/2-MENU_PANEL_W/2, py=screenheight/2-MENU_PANEL_H/2;
        int rowY=py+62+50+8, plusX=px+MENU_PANEL_W-90+24+4+60+4;
        
        return mx>=plusX&&mx<=plusX+24&&my>=rowY+7&&my<=rowY+29;
        
    }

    private void returnToMenu() {
        musicLose.stop(); musicWin.stop(); heartbeat.stop(); playerFootsteps.stop(); musicBox.stop();
        landingMusic.loop(); GameThread=null; parentFrame.getContentPane().removeAll();
        LandingPage landingPage=new LandingPage(this, () -> {
            parentFrame.getContentPane().removeAll();
            panel gamePanel=new panel(parentFrame);
            parentFrame.add(gamePanel); parentFrame.pack(); parentFrame.revalidate(); parentFrame.repaint(); parentFrame.setLocationRelativeTo(null);
            gamePanel.showNarration=true; gamePanel.narrationAlpha=0f; gamePanel.narrationComplete=true;
            gamePanel.landingMusic.stop(); gamePanel.startThread(); gamePanel.requestFocusInWindow();
        });
        parentFrame.add(landingPage); parentFrame.pack(); parentFrame.revalidate(); parentFrame.repaint(); parentFrame.setLocationRelativeTo(null);
    }

    public void startThread() { GameThread=new Thread(this); GameThread.start(); }

    @Override
    public void run() {
        double drawInterval=1000000000/fps, nextDrawTime=System.nanoTime()+drawInterval;
        while (GameThread!=null) {
            update(); repaint();
            try {
                double remaining=(nextDrawTime-System.nanoTime())/1000000;
                if (remaining<0) remaining=0;
                Thread.sleep((long)remaining); nextDrawTime+=drawInterval;
            } catch (InterruptedException ex) { ex.printStackTrace(); }
        }
    }

    private boolean wasNight=false;

    public void update() {
        keyH.update();

        // TRANSITION STATE MACHINE
        if (transitionState==TransitionState.FADE_OUT) {
            
            transitionAlpha+=FADE_OUT_SPEED;
            
            if (transitionAlpha>=1f) {
                
                transitionAlpha=1f;
                transitionState=TransitionState.HOLD;
                transitionHoldTimer=TRANSITION_HOLD_TICKS;
                
                // Execute level switch while screen is black
                if (pendingLevel==2) {
                    doSwitchToLevel2();
                }
                else if (pendingLevel==1) {
                    doSwitchToLevel1();
                }
                
                levelLabelAlpha=1f; levelLabelTimer=LEVEL_LABEL_DURATION;
            }
            
            return;
        }
        
        if (transitionState==TransitionState.HOLD) {
            
            transitionHoldTimer--;
            if (transitionHoldTimer<=0) {
                transitionState=TransitionState.FADE_IN;
            }
            
            return;
        }
        
        if (transitionState==TransitionState.FADE_IN) {
            transitionAlpha-=FADE_IN_SPEED;
            
            if (transitionAlpha<=0f) { 
                transitionAlpha=0f; transitionState=TransitionState.NONE; pendingLevel=-1; 
            }
            return;
        }

        // Level label fade out after transition
        if (levelLabelTimer>0) {
            levelLabelTimer--;
            if (levelLabelTimer<40) levelLabelAlpha=levelLabelTimer/40f;
        }

        if (narrationComplete) {
            narrationAlpha+=0.02f;
            if (narrationAlpha>=1f) { narrationAlpha=1f; narrationComplete=false; narPageIndex=0; narCharIndex=0; narTickCounter=0; narrationText=""; }
            return;
        }
        if (narrationFadeOut) {
            narrationAlpha-=0.02f;
            if (narrationAlpha<=0f) { narrationAlpha=0f; narrationFadeOut=false; showNarration=false; }
            return;
        }
        if (showNarration) {
            String fullPage=NARRATION_PAGES[narPageIndex];
            if (narCharIndex<fullPage.length()) {
                narTickCounter++;
                if (narTickCounter>=NAR_TYPEWRITER_DELAY) { narTickCounter=0; narCharIndex++; narrationText=fullPage.substring(0,narCharIndex); if(!typewriting.isRunning()) typewriting.loop(); }
            } else { typewriting.stop(); }
            if (keyH.skipPressed) {
                keyH.skipPressed=false;
                if (narCharIndex<fullPage.length()) { typewriting.stop(); narCharIndex=fullPage.length(); narrationText=fullPage; }
                else { advanceNarration(); }
            }
            return;
        }
        if (isGameOver) { if(gameState==GameState.WIN) winScreen.update(); else if(gameState==GameState.LOSE) loseScreen.update(); return; }

        boolean isNight=(dC.currentState==dayCounter.dayNightState.Night);

        if (showMonsterDialogue) {
            
            if (dialogueCharIndex<dialogueFullTextMonster.length()) {
                
                dialogueTickCounter++;
                if (dialogueTickCounter>=TYPEWRITER_DELAY) { 
                    dialogueTickCounter=0; dialogueCharIndex++; dialogueText=dialogueFullTextMonster.substring(0,dialogueCharIndex);
                    if(!typewriting.isRunning()) {
                        typewriting.loop();
                    } 
                }
            } else {
                typewriting.stop();
            }
            
            return;
        }
        
        if (showNPCDialogue) {
            
            if (dialogueCharIndex<dialogueFullTextNPC.length()) {
                dialogueTickCounter++;
                
                if (dialogueTickCounter>=TYPEWRITER_DELAY) { 
                    dialogueTickCounter=0; 
                    dialogueCharIndex++; 
                    dialogueText=dialogueFullTextNPC.substring(0,dialogueCharIndex); 
                    if(!typewriting.isRunning()) {
                        typewriting.loop();
                    } 
                }
            } else { typewriting.stop(); }
            return;
        }

        if (isNight&&!wasNight) {
            
            int mc=diffM.getMonsterCount(); monster.applyDifficulty(); monster.spawnNearEdge();
            if (mc>=2) { 
                monster2.applyDifficulty(); monster2.spawnFromRight(); 
            }
            if (mc>=3) { 
                monster3.applyDifficulty(); monster3.spawnFromTop(); 
            }
            musicBox.loop();
        }

        if (riddleUI.isOpen) {
            riddleUI.update();
        }
        
        if (interiorGraceTimer>0) {
            interiorGraceTimer--;
        }
        
        player.update();  
        npc.update(); 
        monster.update();
        monster2.update();
        monster3.update();
        objectM.update();

        if (tileM.currentMap==1) {
            
            interactionChecker.checkInteraction();
            if (monster.forceEnter) {
                monster.forceEnter=false; 
                monster.worldX=1515; 
                monster.worldY=975; 
                monster.state=Monster.State.CHASING;
            }
            
        } else {
            interactionChecker.checkInteriorInteraction(); 
        }

        if (monster.state==Monster.State.KNOCKING&&!showMonsterDialogue&&!monsterDialogueResponded&&monster.actionTimer>0&&!npcIsAlly) {
            
            if (knockDelayTimer==0&&!knockPlayed) { 
                isNPCKnocking=Math.random()<0.5;
                musicBox.stop();
                knockDelayTimer=4*60; 
            }
        }
        
        if (knockDelayTimer>0) {
            
            knockDelayTimer--;
            
            if (knockDelayTimer==0&&!knockPlayed) { 
                doorKnock.play(); 
                knockPlayed=true; 
                knockDelayTimer=3*60; 
            }
            else if (knockDelayTimer==0&&knockPlayed&&!showMonsterDialogue&&!showNPCDialogue&&!monsterDialogueResponded) {
                
                if (isNPCKnocking) {
                    
                    dialogueFullTextNPC=diffM.getNPCKnockDialogue(); 
                    dialogueText=""; 
                    dialogueCharIndex=0; 
                    dialogueTickCounter=0; 
                    showNPCDialogue=true;
                    
                    onYesAction=()->{
                        showNPCDialogue=false;
                        monsterDialogueResponded=true;
                        npcIsAlly=true;
                        npc.enterHouse();
                    };
                    
                    onNoAction=()->{
                        showNPCDialogue=false;
                        monsterDialogueResponded=true;
                        knockPlayed=false;
                    };
                    
                } else {
                    dialogueFullTextMonster=diffM.getMonsterKnockDialogue(); 
                    dialogueText="";
                    dialogueCharIndex=0; 
                    dialogueTickCounter=0; 
                    showMonsterDialogue=true;
                    onYesAction=()->{
                        showMonsterDialogue=false;
                        monsterDialogueResponded=true;
                        hardKnock.play();
                        musicBox.stop();
                        tileM.switchMap(2);
                        monster.forceEnter=true;
                        monster.spawnInsideHouse();
                    };
                    
                    onNoAction=()->{
                        showMonsterDialogue=false;
                        monsterDialogueResponded=true;
                        knockPlayed=false;
                        hardKnock.play();
                        heartbeat.loop();
                        musicBox.stop();
                        player.heartbeatTimer=3*70;
                        musicBoxDelayTimer=hardKnock.getDurationTicks()+(4*60);
                    };
                }
            }
        }
        if (!isNight||monster.state!=Monster.State.KNOCKING) {
            monsterDialogueResponded=false; 
            knockDelayTimer=0; 
            knockPlayed=false; 
        }
        
        if (!isNight&&wasNight) {
            musicBox.stop();
            if (monster.forceEnter) { 
                monster.forceEnter=false;
                monster.worldX=-1000; 
                monster.worldY=-1000; 
                monster.state=Monster.State.IDLE;
            }
            
            monster2.worldX=-1000; 
            monster2.worldY=-1000; 
            monster2.state=Monster.State.IDLE;
            monster3.worldX=-1000;
            monster3.worldY=-1000;
            monster3.state=Monster.State.IDLE;
        }
        
        dC.update();
        if (player.hp<=0) { 
            
            isGameOver=true; 
            gameState=GameState.LOSE; 
            loseScreen.causeOfDeath="hp"; 
            loseScreen.reset(); 
            heartbeat.stop(); 
            musicBox.stop(); 
            musicLose.play();
        }
        
        if (musicBoxDelayTimer>0) { 
            musicBoxDelayTimer--; 
            if(musicBoxDelayTimer==0&&isNight&&!musicBox.isRunning()) {
                musicBox.loop();
            } 
        }
        wasNight=isNight;
        
    }

    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;

        if (!showNarration) {
            tileM.draw(g2); 
            objectM.draw(g2);
            
            if (tileM.currentMap==1||monster.forceEnter) {
                monster.draw(g2); monster2.draw(g2); 
                monster3.draw(g2); 
            }
            
            npc.draw(g2); 
            player.draw(g2); 
            dC.draw(g2);
            dC.drawOverlay(g2);
            drawSpotlight(g2);
            drawHUD(g2); 
            drawHPBar(g2); 
            drawMenuButton(g2);
            inventory.drawButtons(g2); 
            inventory.drawInventoryPanel(g2); 
            inventory.drawScrollPanel(g2);
            drawMenuPanel(g2);
        }
        
        if (isGameOver) { 
            if(gameState==GameState.WIN) {
                winScreen.draw(g2);
            } else if(gameState==GameState.LOSE) {
                loseScreen.draw(g2);
            } 
        }
        
        drawNarrationPanel(g2);
        
        if (riddleUI.isOpen) {
            riddleUI.draw(g2);
        }
        
        if (gameWorldFadeIn) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,gameWorldAlpha));
            g2.setColor(Color.BLACK); 
            g2.fillRect(0,0,screenWidth,screenheight);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
        }
        
        

        // DRAW TRANSITION OVERLAY
        if (transitionState!=TransitionState.NONE) {
            
            Composite orig=g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,transitionAlpha));
            g2.setColor(Color.BLACK); g2.fillRect(0,0,screenWidth,screenheight);
            g2.setComposite(orig);

            // Show label during HOLD 
            if (transitionState==TransitionState.HOLD&&levelLabelText!=null&&!levelLabelText.isEmpty()) {
                
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                g2.setFont(getImFell(16f)); g2.setColor(new Color(130,120,110));
                String sub=pendingLevel==2?"The forest grows darker...":"There is no turning back now.";
                int subW=g2.getFontMetrics().stringWidth(sub);
                g2.drawString(sub,screenWidth/2-subW/2,screenheight/2+40);
                
                // Main label
                g2.setFont(getImFell(42f)); g2.setColor(new Color(215,205,190));
                
                int lw=g2.getFontMetrics().stringWidth(levelLabelText);
                g2.drawString(levelLabelText,screenWidth/2-lw/2,screenheight/2);
            }
        }

        // Floating label lingers a bit after fade-in completes
        if (transitionState==TransitionState.NONE&&levelLabelTimer>0&&levelLabelAlpha>0f) {
            
            Composite orig=g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,levelLabelAlpha));
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getImFell(28f)); g2.setColor(new Color(215,205,190));
            
            int lw=g2.getFontMetrics().stringWidth(levelLabelText);
            g2.drawString(levelLabelText,screenWidth/2-lw/2,70);
            g2.setComposite(orig);
        }
        
  

        g2.dispose();
    }
    
    //  SPOTLIGHT EFFECT 
    private void drawSpotlight(Graphics2D g2) {
        int level = tileM.currentLevel;
        if (level == 3) return;

        int radius = (level == 2) ? 370 : 240;

        int screenCenterX = player.screenX + tileSize / 2;
        int screenCenterY = player.screenY + tileSize / 2;

        RadialGradientPaint spotlight = new RadialGradientPaint(
            new java.awt.geom.Point2D.Float(screenCenterX, screenCenterY),
            radius,
            new float[]{0.0f, 0.10f, 0.35f, 0.60f, 1.0f},
            new Color[]{
                new Color(0, 0, 0, 0),    // center clear
                new Color(0, 0, 0, 30),    // still clear
                new Color(0, 0, 0, 60),   // very subtle dim
                new Color(0, 0, 0, 160),  // noticeable dark
                new Color(0, 0, 0, 235)   // black
            }
        );

        g2.setPaint(spotlight);
        g2.fillRect(0, 0, screenWidth, screenheight);
    }
   

    // TRANSITION TRIGGERS — called by InteractionChecker portal
    public void beginTransitionToLevel(int level) {
        if (transitionState!=TransitionState.NONE) return;
        pendingLevel=level;
        levelLabelText=(level==2)?"Level II":"Level I";
        transitionAlpha=0f;
        transitionState=TransitionState.FADE_OUT;
        musicBox.stop();
    }

    private void doSwitchToLevel2() {
        tileM.switchLevel(2); tileM.switchMap(1);
        player.worldX=ObjectManager.SPAWN_X[2]; player.worldY=ObjectManager.SPAWN_Y[2];
        diffM.level=2; riddleM.solvedCount=0; riddleM.portalSpawned=false; riddleM.initRiddles();
        objectM.portalVisible=false; objectM.setObjects(); objectM.spawnCollectibles();
        dC.dayCount=1; dC.stateCounter=0f; dC.currentState=dayCounter.dayNightState.Day;
    }

    private void doSwitchToLevel1() {
        tileM.switchLevel(1); tileM.switchMap(1);
        player.worldX=ObjectManager.SPAWN_X[1]; player.worldY=ObjectManager.SPAWN_Y[1];
        diffM.level=1; riddleM.solvedCount=0; riddleM.portalSpawned=false; riddleM.initRiddles();
        objectM.portalVisible=false; objectM.setObjects(); objectM.spawnCollectibles();
        dC.dayCount=1; dC.stateCounter=0f; dC.currentState=dayCounter.dayNightState.Day;
    }

    // Public wrappers — InteractionChecker calls these
    public void switchToLevel2() { beginTransitionToLevel(2); }
    public void switchToLevel1() { beginTransitionToLevel(1); }

    public void switchToInterior() {
        tileM.switchMap(2); player.worldX=160; player.worldY=144; interiorGraceTimer=240;
    }

    public void switchToExterior() {
        tileM.switchMap(1);
        int lv=tileM.currentLevel;
        player.worldX=ObjectManager.SPAWN_X[lv]; player.worldY=ObjectManager.SPAWN_Y[lv];
    }

    public boolean isPlayerSafe() {
        if (tileM.currentMap!=2) return false;
        models.ObjHouse house=(models.ObjHouse)objectM.ObjHouse[0];
        return !house.isDoorOpen&&!house.isWindow1Open&&!house.isWindow2Open&&!house.isWindow3Open;
    }

    public boolean hasOpenEntry() {
        models.ObjHouse house=(models.ObjHouse)objectM.ObjHouse[0];
        return house.isDoorOpen||house.isWindow1Open||house.isWindow2Open||house.isWindow3Open;
    }

    @Override
    public void startGame() {
        landingMusic.stop(); showNarration=true; narrationAlpha=0f; narrationComplete=true; narrationFadeOut=false; dC.startTime=System.nanoTime();
    }

    // DRAW HELPERS — all unchanged from original

    private void drawHPBar(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        int barWidth=200,barHeight=22,x=Inventory.BTN_X,y=10;
        g2.setColor(new Color(30,28,25,200)); g2.fillRect(x,y-4,barWidth+14,barHeight+8);
        g2.setColor(new Color(90,85,78)); g2.setStroke(new BasicStroke(1f));
        g2.drawLine(x,y-4,x+barWidth+14,y-4); g2.drawLine(x,y+barHeight+4,x+barWidth+14,y+barHeight+4);
        g2.setColor(new Color(20,18,15)); g2.fillRect(x+22,y,barWidth-22,barHeight);
        double hpPct=(double)player.hp/player.maxHP; int cw=(int)((barWidth-22)*hpPct);
        if (player.hp>60) g2.setColor(new Color(45,110,55)); else if (player.hp>30) g2.setColor(new Color(140,90,20)); else g2.setColor(new Color(120,25,25));
        g2.fillRect(x+22,y,cw,barHeight); g2.setColor(new Color(65,60,55)); g2.setStroke(new BasicStroke(1f)); g2.drawRect(x+22,y,barWidth-22,barHeight);
        g2.setFont(getImFell(16f)); g2.setColor(new Color(140,135,128)); g2.drawString("+",x+4,y+16);
        g2.setFont(getImFell(13f)); g2.setColor(new Color(210,205,195));
        String text=player.hp+"%"; int tw=g2.getFontMetrics().stringWidth(text);
        g2.drawString(text,x+22+(barWidth-22)/2-tw/2,y+15); g2.setStroke(new BasicStroke(1f));
    }

    private void drawMenuButton(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setColor(showMenuPanel?new Color(50,48,45,230):new Color(30,28,25,200)); g2.fillRect(MENU_BTN_X,MENU_BTN_Y,MENU_BTN_W,MENU_BTN_H);
        g2.setColor(showMenuPanel?new Color(160,155,148):new Color(90,85,78)); g2.setStroke(new BasicStroke(1f)); g2.drawRect(MENU_BTN_X,MENU_BTN_Y,MENU_BTN_W,MENU_BTN_H);
        g2.setFont(getImFell(12f)); g2.setColor(showMenuPanel?new Color(210,205,195):new Color(140,135,128));
        String label="Menu"; int sw=g2.getFontMetrics().stringWidth(label);
        g2.drawString(label,MENU_BTN_X+(MENU_BTN_W-sw)/2,MENU_BTN_Y+MENU_BTN_H-6); g2.setStroke(new BasicStroke(1f));
    }

    private void drawMenuPanel(Graphics2D g2) {
        
        if (!showMenuPanel) {
            return;
        }
        
        int px = screenWidth/2-MENU_PANEL_W/2, py = screenheight/2-MENU_PANEL_H/2;
        inventory.drawPanelBase(g2, px, py, MENU_PANEL_W, MENU_PANEL_H, "Menu");
        int rowY = py+62;
        
        drawVolumeRow(g2, px, rowY, MENU_PANEL_W, "Music", musicVolume, true);
        rowY += 50;
        g2.setColor(new Color(30,50,25,140)); g2.setStroke(new BasicStroke(1f));
        g2.drawLine(px+30, rowY-2, px+MENU_PANEL_W-30, rowY-2);
        rowY += 8;
        drawVolumeRow(g2, px, rowY, MENU_PANEL_W, "SFX", sfxVolume, false);
        rowY += 50;
        g2.setColor(new Color(30,50,25,140)); g2.setStroke(new BasicStroke(1f));
        g2.drawLine(px+30, rowY-2, px+MENU_PANEL_W-30, rowY-2);
        
        rowY += 8;
        drawMenuRow(g2, px, rowY, MENU_PANEL_W, "Quit Game");
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawVolumeRow(Graphics2D g2, int px, int rowY, int pw, String label, float vol, boolean isMusic) {
        int rowH = 36;
        g2.setColor(new Color(2,8,2,225)); g2.fillRect(px+12, rowY, pw-24, rowH);
        g2.setStroke(new BasicStroke(1f)); g2.setColor(new Color(35,55,30,160));
        g2.drawRect(px+12, rowY, pw-24, rowH);
        g2.setFont(getImFell(14f)); g2.setColor(new Color(150,190,130));
        g2.drawString(label, px+28, rowY+24);
        int btnW=22, btnH=20, btnY=rowY+8;
        int minusX = px+pw-120;
        int barX = minusX+btnW+6, barW=70, barH=10, barY=rowY+13;
        int plusX = barX+barW+6;
        
        // minus
        g2.setColor(new Color(20,40,15,200)); g2.fillRect(minusX, btnY, btnW, btnH);
        g2.setColor(new Color(35,55,30,200)); g2.drawRect(minusX, btnY, btnW, btnH);
        g2.setFont(getImFell(16f)); g2.setColor(new Color(150,190,130));
        g2.drawString("-", minusX+7, btnY+16);
        
        // bar
        g2.setColor(new Color(20,18,15)); g2.fillRect(barX, barY, barW, barH);
        g2.setColor(new Color(45,110,55)); g2.fillRect(barX, barY, (int)(barW*vol), barH);
        g2.setColor(new Color(65,60,55)); g2.drawRect(barX, barY, barW, barH);
        
        // plus
        g2.setColor(new Color(20,40,15,200)); g2.fillRect(plusX, btnY, btnW, btnH);
        g2.setColor(new Color(35,55,30,200)); g2.drawRect(plusX, btnY, btnW, btnH);
        g2.setFont(getImFell(16f)); g2.setColor(new Color(150,190,130));
        g2.drawString("+", plusX+5, btnY+16);
    }

    private void drawMenuRow(Graphics2D g2,int px,int rowY,int pw,String label) {
        int rowH=36;
        g2.setColor(new Color(2,8,2,225)); g2.fillRect(px+12,rowY,pw-24,rowH);
        g2.setStroke(new BasicStroke(1f)); g2.setColor(new Color(35,55,30,160)); g2.drawRect(px+12,rowY,pw-24,rowH);
        g2.setFont(getImFell(15f)); g2.setColor(new Color(150,190,130));
        int lw=g2.getFontMetrics().stringWidth(label); g2.drawString(label,px+pw/2-lw/2,rowY+24); g2.setStroke(new BasicStroke(1f));
    }

    private boolean isMenuBtnClicked(int mx,int my) { return mx>=MENU_BTN_X&&mx<=MENU_BTN_X+MENU_BTN_W&&my>=MENU_BTN_Y&&my<=MENU_BTN_Y+MENU_BTN_H; }
    private boolean isMenuPanelXClicked(int mx,int my) { int px=screenWidth/2-MENU_PANEL_W/2,py=screenheight/2-MENU_PANEL_H/2; return mx>=px+MENU_PANEL_W-24&&mx<=px+MENU_PANEL_W-6&&my>=py+8&&my<=py+28; }
    private boolean isMuteRowClicked(int mx,int my) { int px=screenWidth/2-MENU_PANEL_W/2,py=screenheight/2-MENU_PANEL_H/2,rowY=py+62; return mx>=px+12&&mx<=px+MENU_PANEL_W-12&&my>=rowY&&my<=rowY+36; }
    private boolean isQuitRowClicked(int mx,int my) { int px=screenWidth/2-MENU_PANEL_W/2,py=screenheight/2-MENU_PANEL_H/2,rowY=py+62+44+8; return mx>=px+12&&mx<=px+MENU_PANEL_W-12&&my>=rowY&&my<=rowY+36; }

    private void handleDialogueClick(int mx,int my) {
        if (!showMonsterDialogue&&!showNPCDialogue) return;
        String aft=showMonsterDialogue?dialogueFullTextMonster:dialogueFullTextNPC;
        if (dialogueCharIndex<aft.length()) { dialogueCharIndex=aft.length(); dialogueText=aft; return; }
        int w=460,h=160,x=screenWidth/2-w/2,y=screenheight/2-h/2;
        int yesX=x+60,yesY=y+140,yesW=110,yesH=32,noX=x+290,noY=y+140,noW=110,noH=32;
        if (mx>=yesX&&mx<=yesX+yesW&&my>=yesY&&my<=yesY+yesH) { if(onYesAction!=null) onYesAction.run(); }
        if (mx>=noX&&mx<=noX+noW&&my>=noY&&my<=noY+noH) { if(onNoAction!=null) onNoAction.run(); }
    }

    private void drawNarrationPanel(Graphics2D g2) {
        if (!showNarration) return;
        if (narPageIndex>=NARRATION_PAGES.length) {
            Composite o=g2.getComposite(); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,narrationAlpha));
            g2.setColor(Color.BLACK); g2.fillRect(0,0,screenWidth,screenheight); g2.setComposite(o); return;
        }
        Composite orig=g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,narrationAlpha));
        g2.setColor(new Color(0,0,0,160)); g2.fillRect(0,0,screenWidth,screenheight);
        int px=screenWidth/2-NARRATION_W/2,py=screenheight/2-NARRATION_H/2;
        
        g2.setColor(new Color(0,0,0,240)); g2.fillRect(px,py,NARRATION_W,NARRATION_H);
        g2.setStroke(new BasicStroke(10f)); g2.setColor(new Color(20,35,20,40)); g2.drawRect(px-5,py-5,NARRATION_W+10,NARRATION_H+10);
        g2.setStroke(new BasicStroke(7f)); g2.setColor(new Color(25,42,22,70)); g2.drawRect(px-3,py-3,NARRATION_W+6,NARRATION_H+6);
        g2.setStroke(new BasicStroke(4f)); g2.setColor(new Color(30,50,25,100)); g2.drawRect(px-1,py-1,NARRATION_W+2,NARRATION_H+2);
        g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,180)); g2.drawRect(px,py,NARRATION_W,NARRATION_H);
        int cs=14; g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,200));
        
        g2.drawLine(px,py,px+cs,py); g2.drawLine(px,py,px,py+cs);
        g2.drawLine(px+NARRATION_W-cs,py+NARRATION_H,px+NARRATION_W,py+NARRATION_H);
        g2.drawLine(px+NARRATION_W,py+NARRATION_H-cs,px+NARRATION_W,py+NARRATION_H);
        g2.setStroke(new BasicStroke(1f)); g2.setColor(new Color(30,50,25,140)); g2.drawLine(px+20,py+40,px+NARRATION_W-20,py+40);
        g2.setFont(getImFell(15f)); g2.setColor(new Color(210,205,195));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("No Sanctuary",px+20,py+28);
        g2.setFont(getImFell(15f)); g2.setColor(new Color(168,162,150));
        
        int textStartY=py+84; drawWrappedText(g2,narrationText,px+28,textStartY,NARRATION_W-56,26);
        g2.setStroke(new BasicStroke(1f)); g2.setColor(new Color(30,50,25,140)); g2.drawLine(px+20,py+NARRATION_H-48,px+NARRATION_W-20,py+NARRATION_H-48);
        int footerY=py+NARRATION_H-22; g2.setFont(getImFell(12f));
        g2.setColor(new Color(110,105,98)); g2.drawString((narPageIndex+1)+" / "+NARRATION_PAGES.length,px+24,footerY);
        String hint="Space · Enter · Click to advance"; int hintW=g2.getFontMetrics().stringWidth(hint);
        g2.setColor(new Color(90,86,80)); g2.drawString(hint,px+NARRATION_W/2-hintW/2,footerY);
        String fullPage=NARRATION_PAGES[narPageIndex];
        if (narCharIndex<fullPage.length()) {
            long blink=(System.currentTimeMillis()/400)%2;
            if (blink==0) {
                g2.setFont(getImFell(15f)); g2.setColor(new Color(160,155,145,180));
                FontMetrics fm=g2.getFontMetrics(); int maxWidth=NARRATION_W-56;
                String[] words=narrationText.split(" "); String cl=""; int lc=0;
                for (String w:words) { String t=cl.isEmpty()?w:cl+" "+w; if(fm.stringWidth(t)>maxWidth){lc++;cl=w;}else{cl=t;} }
                g2.fillRect(px+28+fm.stringWidth(cl)+2,textStartY+(lc*26)-14,2,16);
            }
        }
        int btnW=100,btnX=px+NARRATION_W-btnW-16,btnY=py+NARRATION_H-48; g2.setFont(getImFell(13f));
        String btnLabel=narCharIndex<fullPage.length()?"Skip >":(narPageIndex+1<NARRATION_PAGES.length?"Next >":"Begin");
        g2.setColor(new Color(195,188,174)); int lw=g2.getFontMetrics().stringWidth(btnLabel);
        g2.drawString(btnLabel,btnX+btnW/2-lw/2,btnY+28); g2.setStroke(new BasicStroke(1f)); g2.setComposite(orig);
    }

    private void drawWrappedText(Graphics2D g2,String text,int x,int y,int maxWidth,int lineHeight) {
        if (text==null||text.isEmpty()) return;
        String[] words=text.split(" "); StringBuilder line=new StringBuilder();
        for (String word:words) {
            String test=line.length()==0?word:line+" "+word;
            if (g2.getFontMetrics().stringWidth(test)>maxWidth) { g2.drawString(line.toString(),x,y); y+=lineHeight; line=new StringBuilder(word); }
            else { line=new StringBuilder(test); }
        }
        if (line.length()>0) g2.drawString(line.toString(),x,y);
    }

    private void drawPromptBox(Graphics2D g2,String text,int cx,int y,float fontSize) {
        g2.setFont(getImFell(fontSize)); int textW=g2.getFontMetrics().stringWidth(text),padX=18,padY=8;
        int boxW=textW+padX*2,boxH=(int)fontSize+padY*2,boxX=cx-boxW/2;
        g2.setColor(new Color(0,0,0,220)); g2.fillRect(boxX,y,boxW,boxH);
        g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,180)); g2.drawRect(boxX,y,boxW,boxH);
        g2.setColor(new Color(150,190,130)); g2.drawString(text,boxX+padX,y+padY+(int)fontSize-2); g2.setStroke(new BasicStroke(1f));
    }

    private void drawKeyPromptBox(Graphics2D g2,String key,String label,int cx,int y,float fontSize) {
        g2.setFont(getImFell(fontSize)); int kbs=(int)fontSize+6,lw2=g2.getFontMetrics().stringWidth(label),kw=g2.getFontMetrics().stringWidth(key);
        int gap=8,totalW=kbs+gap+lw2,padX=18,padY=8,boxW=totalW+padX*2,boxH=kbs+padY*2,boxX=cx-boxW/2;
        g2.setColor(new Color(0,0,0,220)); g2.fillRect(boxX,y,boxW,boxH);
        g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,180)); g2.drawRect(boxX,y,boxW,boxH);
        int keyX=boxX+padX,keyY=y+padY;
        g2.setColor(new Color(20,35,15,200)); g2.fillRect(keyX,keyY,kbs,kbs);
        g2.setStroke(new BasicStroke(1f)); g2.setColor(new Color(35,55,30,200)); g2.drawRect(keyX,keyY,kbs,kbs);
        g2.setColor(new Color(150,190,130)); g2.setFont(getImFell(fontSize)); g2.drawString(key,keyX+(kbs-kw)/2,keyY+kbs-4);
        g2.setColor(new Color(120,160,100)); g2.drawString(label,keyX+kbs+gap,keyY+kbs-4); g2.setStroke(new BasicStroke(1f));
    }

    private void drawHUD(Graphics2D g2) {
        if (dC.countdownActive) {
            int secs=(int)Math.ceil(dC.countdownTimer); String ct="0 : "+(secs<10?"0"+secs:secs);
            g2.setFont(getImFell(18f)); String lbl="Last 15 Seconds"; int lw2=g2.getFontMetrics().stringWidth(lbl);
            g2.setColor(new Color(180,0,0)); g2.drawString(lbl,screenWidth/2-lw2/2,50);
            g2.setFont(getImFell(42f)); int tw=g2.getFontMetrics().stringWidth(ct);
            g2.setColor(new Color(139,0,0,40)); g2.drawString(ct,screenWidth/2-tw/2+2,80);
            g2.setColor(new Color(139,0,0,80)); g2.drawString(ct,screenWidth/2-tw/2+1,79);
            g2.setColor(new Color(180,0,0)); g2.drawString(ct,screenWidth/2-tw/2,78);
        }
        int cx=screenWidth/2;
        if (tileM.currentMap==1) {
            if (interactionChecker.showDoorPrompt) {
                models.ObjHouse house=(models.ObjHouse)objectM.ObjHouse[0];
                if (house.isDoorOpen) { drawKeyPromptBox(g2,"E","Enter",cx-80,screenheight-70,15f); drawKeyPromptBox(g2,"F","Close Door",cx+60,screenheight-70,15f); }
                else { drawKeyPromptBox(g2,"E","Open Door",cx,screenheight-70,15f); }
            }
            if (interactionChecker.showPortalPrompt) drawKeyPromptBox(g2,"E","Enter",cx,screenheight-110,15f);
            if (interactionChecker.showRiddlePrompt) {
                int ri=interactionChecker.nearRiddleIndex;
                boolean solved=ri>=0&&riddleM.getRiddle(ri)!=null&&riddleM.getRiddle(ri).solved;
                if (!solved) drawKeyPromptBox(g2,"E","Read the Riddle  ("+riddleM.solvedCount+"/3 solved)",cx,screenheight-110,15f);
            }
            if (interactionChecker.showShelterPrompt) drawKeyPromptBox(g2,"E","Enter",cx,screenheight-70,15f);
            return;
        }
        if (interactionChecker.showExitPrompt) {
            models.ObjHouse house=(models.ObjHouse)objectM.ObjHouse[0];
            if (house.isDoorOpen) { drawKeyPromptBox(g2,"E","Exit",cx-80,screenheight-70,15f); drawKeyPromptBox(g2,"F","Close Door",cx+60,screenheight-70,15f); }
            else { drawKeyPromptBox(g2,"F","Open Door",cx,screenheight-70,15f); }
        }
        if (interactionChecker.showWindowPrompt) drawKeyPromptBox(g2,"E","Open / Close Window",cx,screenheight-70,15f);
        if (interactionChecker.showTorchPrompt) {
            models.ObjTorch torch=objectM.interior.torch;
            String st=torch.isLit?"Torch burning  —  "+torch.getSecondsLeft()+"s left":"Torch is OUT";
            Color sc=torch.isLit?new Color(180,140,60):new Color(120,80,80);
            g2.setFont(getImFell(13f)); int stW=g2.getFontMetrics().stringWidth(st),sPX=14,sPY=6;
            int sBW=stW+sPX*2,sBH=13+sPY*2,sBX=cx-sBW/2,sBY=screenheight-110;
            g2.setColor(new Color(8,8,6,200)); g2.fillRect(sBX,sBY,sBW,sBH);
            g2.setColor(new Color(70,60,45)); g2.setStroke(new BasicStroke(1f)); g2.drawRect(sBX,sBY,sBW,sBH);
            g2.setColor(sc); g2.drawString(st,sBX+sPX,sBY+sPY+11);
            drawKeyPromptBox(g2,"E","Fuel Torch  (costs 3 wood)",cx,screenheight-70,15f);
        }
        if (interactionChecker.hasTorchFeedback()) {
            drawPromptBox(g2,interactionChecker.torchFeedback,cx,screenheight-140,13f);
        }
        if (interactionChecker.showCabinetPrompt&&!objectM.interior.cabinet.isOpen) {
            drawKeyPromptBox(g2,"E","Open Cabinet",cx,screenheight-70,15f);
        }
        if (interactionChecker.hasCabinetFeedback()) {
            drawPromptBox(g2,interactionChecker.cabinetFeedback,cx,screenheight-140,13f);
        }
        if (interactionChecker.showAppleTablePrompt&&!objectM.interior.appleTable.isOpen) {
            drawKeyPromptBox(g2,"E","Open Table (Apple)",cx,screenheight-70,15f);
        }
        if (interactionChecker.hasAppleTableFeedback()) {
            drawPromptBox(g2,interactionChecker.appleTableFeedback,cx,screenheight-140,13f);
        }
        
        boolean isNight=dC.currentState==dayCounter.dayNightState.Night;
        if (isNight&&!objectM.interior.torch.isLit) {
            g2.setColor(new Color(0,0,8,130)); g2.fillRect(0,0,screenWidth,screenheight);
        }

        if (showMonsterDialogue) {
            
            int w=460,h=180,x=screenWidth/2-w/2,y=screenheight/2-h/2;
            
            g2.setColor(new Color(0,0,0,240)); g2.fillRect(x,y,w,h);
            g2.setStroke(new BasicStroke(10f)); g2.setColor(new Color(20,35,20,40)); g2.drawRect(x-5,y-5,w+10,h+10);
            g2.setStroke(new BasicStroke(7f)); g2.setColor(new Color(25,42,22,70)); g2.drawRect(x-3,y-3,w+6,h+6);
            g2.setStroke(new BasicStroke(4f)); g2.setColor(new Color(30,50,25,100)); g2.drawRect(x-1,y-1,w+2,h+2);
            g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,180)); g2.drawRect(x,y,w,h);
            
            int cs=14; g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,200));
            g2.drawLine(x,y,x+cs,y); g2.drawLine(x,y,x,y+cs); g2.drawLine(x+w-cs,y+h,x+w,y+h); g2.drawLine(x+w,y+h-cs,x+w,y+h);
            g2.setFont(getImFell(12f)); g2.setColor(new Color(100,130,80)); g2.drawString("Outside :",x+16,y+22);
            g2.setStroke(new BasicStroke(1f)); g2.setColor(new Color(30,50,25,140)); g2.drawLine(x+10,y+30,x+w-10,y+30);
            g2.setFont(getImFell(15f)); g2.setColor(new Color(195,182,155)); FontMetrics dlgFm=g2.getFontMetrics();
            
            int lh=22,tsx=x+16,tsy=y+60; String[] lines=dialogueText.split("\n",-1);
            for (int i=0;i<lines.length;i++) g2.drawString(lines[i],tsx,tsy+(i*lh));
            
            if (dialogueCharIndex<dialogueFullTextMonster.length()) {
                long blink=(System.currentTimeMillis()/400)%2;
                if (blink==0) { 
                    String ll=lines[lines.length-1]; 
                    g2.setColor(new Color(150,130,100)); 
                    g2.fillRect(tsx+dlgFm.stringWidth(ll)+2,tsy+((lines.length-1)*lh)-14,2,16);
                }
            }
            boolean td=dialogueCharIndex>=dialogueFullTextMonster.length(); int ba=td?220:80;
            g2.setFont(getImFell(14f)); g2.setColor(new Color(140,170,110,ba)); g2.drawString("Yes",x+103,y+166);
            g2.setColor(new Color(180,100,90,ba)); g2.drawString("No",x+336,y+166); g2.setStroke(new BasicStroke(1f));
        }

        if (showNPCDialogue) {
            int w=460,h=180,x=screenWidth/2-w/2,y=screenheight/2-h/2;
            g2.setColor(new Color(0,0,0,240)); g2.fillRect(x,y,w,h);
            g2.setStroke(new BasicStroke(10f)); g2.setColor(new Color(20,35,20,40)); g2.drawRect(x-5,y-5,w+10,h+10);
            g2.setStroke(new BasicStroke(7f)); g2.setColor(new Color(25,42,22,70)); g2.drawRect(x-3,y-3,w+6,h+6);
            g2.setStroke(new BasicStroke(4f)); g2.setColor(new Color(30,50,25,100)); g2.drawRect(x-1,y-1,w+2,h+2);
            g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,180)); g2.drawRect(x,y,w,h);
            int cs=14; g2.setStroke(new BasicStroke(1.5f)); g2.setColor(new Color(35,55,30,200));
            g2.drawLine(x,y,x+cs,y); g2.drawLine(x,y,x,y+cs); g2.drawLine(x+w-cs,y+h,x+w,y+h); g2.drawLine(x+w,y+h-cs,x+w,y+h);
            g2.setFont(getImFell(12f)); g2.setColor(new Color(100,130,80)); g2.drawString("Outside (stranger) :",x+16,y+22);
            g2.setStroke(new BasicStroke(1f)); g2.setColor(new Color(30,50,25,140)); g2.drawLine(x+10,y+30,x+w-10,y+30);
            g2.setFont(getImFell(15f)); g2.setColor(new Color(195,182,155)); FontMetrics dlgFm=g2.getFontMetrics();
            int lh=22,tsx=x+16,tsy=y+60; String[] lines=dialogueText.split("\n",-1);
            for (int i=0;i<lines.length;i++) g2.drawString(lines[i],tsx,tsy+(i*lh));
            if (dialogueCharIndex<dialogueFullTextNPC.length()) {
                long blink=(System.currentTimeMillis()/400)%2;
                if (blink==0) { String ll=lines[lines.length-1]; g2.setColor(new Color(150,130,100)); g2.fillRect(tsx+dlgFm.stringWidth(ll)+2,tsy+((lines.length-1)*lh)-14,2,16); }
            }
            boolean td=dialogueCharIndex>=dialogueFullTextNPC.length(); int ba=td?220:80;
            g2.setFont(getImFell(14f)); g2.setColor(new Color(140,170,110,ba)); g2.drawString("Yes",x+103,y+166);
            g2.setColor(new Color(180,100,90,ba)); g2.drawString("No",x+336,y+166); g2.setStroke(new BasicStroke(1f));
        }
    }

    private void advanceNarration() {
        narPageIndex++;
        if (narPageIndex>=NARRATION_PAGES.length) { narPageIndex=NARRATION_PAGES.length-1; narrationFadeOut=true; }
        else { narCharIndex=0; narTickCounter=0; narrationText=""; }
    }
}