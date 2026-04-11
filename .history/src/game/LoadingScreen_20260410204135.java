package game;

import java.awt.*;
import java.awt.BasicStroke;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

public class LoadingScreen extends JPanel {

    private int progress = 0;
    private String statusText = "awakening...";
    private Runnable onComplete;
    private JFrame parentFrame;
    private Font imFellBase = null;
    private javax.swing.Timer flickerTimer;

    public LoadingScreen(JFrame frame, Runnable onComplete) {
        
        this.parentFrame = frame;
        this.onComplete = onComplete;
        
        setPreferredSize(new Dimension(960, 540));

        setBackground(new Color(8, 6, 5));

        flickerTimer = new javax.swing.Timer(42, e -> repaint());
        flickerTimer.start();
    }

    public void setProgress(int value, String text) {
        
        this.progress = value;
        this.statusText = text;
        repaint();
    }

    public void onLoadingComplete() {
        
        flickerTimer.stop();
        javax.swing.Timer delay = new javax.swing.Timer(2500, e -> onComplete.run());
        
        delay.setRepeats(false);
        delay.start();
    }

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

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // bg
        g2.setColor(new Color(8, 6, 5));
        g2.fillRect(0, 0, w, h);

        long now = System.currentTimeMillis();
        
        double flicker = 0.5 + 0.5 * Math.sin(now / 900.0) + 0.08 * Math.sin(now / 137.0) + 0.04 * Math.sin(now / 53.0);
        flicker = Math.max(0.0, Math.min(1.0, flicker)); 

        int titleR = 140 + (int)(flicker * 60); // 140–200
        int titleG = 132 + (int)(flicker * 55); // 132–187
        int titleB = 118 + (int)(flicker * 50); // 118–168

        g2.setFont(getImFell(58f));
        g2.setColor(new Color(titleR, titleG, titleB));
        
        String title = "No Sanctuary";
        int titleW = g2.getFontMetrics().stringWidth(title);
        
        g2.drawString(title, (w - titleW) / 2, h / 2 - 80);

        int barW = 420;
        int barH = 6;
        int barX = (w - barW) / 2;
        int barY = h / 2; 

        g2.setStroke(new BasicStroke(0.5f));
        g2.setColor(new Color(55, 50, 44));
        g2.drawLine(barX, barY - 22, barX + barW, barY - 22);

        // pb track
        g2.setColor(new Color(22, 18, 15));
        g2.fillRect(barX, barY, barW, barH);

        // Progress bar
        int fillW = (int)(barW * progress / 100.0);
        
        if (fillW > 0) {
            
            g2.setColor(new Color(220, 215, 205));
            g2.fillRect(barX, barY, fillW, barH);
        }

        //bar border
        g2.setColor(new Color(50, 45, 40));
        g2.setStroke(new BasicStroke(0.5f));
        g2.drawRect(barX, barY, barW, barH);

        // persintij
        g2.setFont(getImFell(13f));
        g2.setColor(new Color(110, 104, 94));
        
        String pct = progress + "%";
        int pctW = g2.getFontMetrics().stringWidth(pct);
        
        g2.drawString(pct, barX + barW - pctW, barY - 8);

        g2.setStroke(new BasicStroke(0.5f));
        g2.setColor(new Color(55, 50, 44));
        g2.drawLine(barX, barY + barH + 14, barX + barW, barY + barH + 14);

        g2.setFont(getImFell(14f));
        g2.setColor(new Color(80, 75, 68));
        
        int stW = g2.getFontMetrics().stringWidth(statusText);
        
        g2.drawString(statusText, (w - stW) / 2, barY + barH + 34);

        g2.dispose();
    }
}