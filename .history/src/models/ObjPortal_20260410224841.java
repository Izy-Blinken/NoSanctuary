package models;

import java.awt.*;
import java.awt.geom.*;

public class ObjPortal {

    public int worldX, worldY;
    public static final int INTERACT_RANGE = 110;

    private int tick = 0;

    private static final int OUTER_R = 38;
    private static final int INNER_R = 24;
    private static final int CORE_R  = 12;

    public ObjPortal(int worldX, int worldY) {
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public void update() {
        tick++;
    }

    public void draw(Graphics2D g2, int screenX, int screenY) {

        int cx = screenX + OUTER_R;
        int cy = screenY + OUTER_R;

        Object aaHint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float phase = (tick % 120) / 120f; // 0→1 every 2 s
        float pulse = (float)(0.7 + 0.3 * Math.sin(phase * 2 * Math.PI)); // 0.7–1.0

        // Outer glow rings
        for (int r = OUTER_R + 10; r >= OUTER_R - 4; r -= 2) {
            
            float t = (OUTER_R + 10 - r) / 14f; // 0=outermost, 1=ring edge
            int alpha = (int)(40 * (1 - t) * pulse);
            
            g2.setColor(new Color(80, 30, 180, alpha));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        }

        // Outer ring (dark rim)
        g2.setColor(new Color(20, 5, 60));
        g2.fillOval(cx - OUTER_R, cy - OUTER_R, OUTER_R * 2, OUTER_R * 2);

        // Spinning arc segments 
        float spin = tick * 2.4f;  // degrees per tick × tick
        
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int[][] arcs = {{0,60},{90,50},{170,70},{260,45}};
        
        Color[] arcCols = {
            
            new Color(140, 80, 255, 220),
            new Color(80, 200, 255, 180),
            new Color(200, 100, 255, 200),
            new Color(60, 160, 255, 160)
        };
        
        for (int i = 0; i < arcs.length; i++) {
            
            int start = (int)(arcs[i][0] + spin) % 360;
            g2.setColor(arcCols[i]);
            
            g2.drawArc(cx - OUTER_R + 3, cy - OUTER_R + 3,
                       (OUTER_R - 3) * 2, (OUTER_R - 3) * 2,
                       start, arcs[i][1]);
        }

        // Counter-spin inner arcs
        float spin2 = -tick * 1.8f;
        
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int[][] arcs2 = {{20,40},{120,50},{230,35}};
        
        for (int i = 0; i < arcs2.length; i++) {
            
            int start = (int)(arcs2[i][0] + spin2) % 360;
            g2.setColor(new Color(180, 120, 255, 140));
            g2.drawArc(cx - INNER_R - 4, cy - INNER_R - 4,
                       (INNER_R + 4) * 2, (INNER_R + 4) * 2,
                       start, arcs2[i][1]);
        }

        g2.setStroke(oldStroke);

        // Inner void
        RadialGradientPaint innerGrad = new RadialGradientPaint(
            cx, cy, INNER_R,
            new float[]{0f, 0.6f, 1f},
            new Color[]{
                new Color(10, 0, 40, 240),
                new Color(40, 10, 100, 200),
                new Color(80, 30, 180, 120)
            }
        );
        
        g2.setPaint(innerGrad);
        g2.fillOval(cx - INNER_R, cy - INNER_R, INNER_R * 2, INNER_R * 2);

        // Core pulse 
        int coreR = (int)(CORE_R * pulse);
        
        RadialGradientPaint coreGrad = new RadialGradientPaint(
                
            cx, cy, coreR + 1,
            new float[]{0f, 0.5f, 1f},
                
            new Color[]{
                new Color(255, 220, 255, 240),
                new Color(180, 100, 255, 200),
                new Color(100, 40, 200, 0)
            }
        );
        g2.setPaint(coreGrad);
        g2.fillOval(cx - coreR, cy - coreR, coreR * 2, coreR * 2);

        g2.setPaint(null);

        // Floating particles
        drawParticles(g2, cx, cy, pulse);

        // Label above portal
        g2.setFont(new Font("Serif", Font.ITALIC, 11));
        float labelAlpha = 0.5f + 0.5f * pulse;
        
        g2.setColor(new Color(1f, 1f, 1f, labelAlpha));
        String lbl = "[ Portal ]";
        int lw = g2.getFontMetrics().stringWidth(lbl);
        
        g2.drawString(lbl, cx - lw / 2, cy - OUTER_R - 8);

        // Restore AA hint
        if (aaHint != null) {
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aaHint);
        }
    }

    private void drawParticles(Graphics2D g2, int cx, int cy, float pulse) {
        
        int count = 8;
        
        for (int i = 0; i < count; i++) {
            
            double angle = Math.toRadians(i * (360.0 / count) + tick * 1.5);
            float dist = OUTER_R + 6 + (float)(4 * Math.sin(tick * 0.05 + i));
            int px = (int)(cx + dist * Math.cos(angle));
            int py = (int)(cy + dist * Math.sin(angle));
            int pr = (int)(2 * pulse);
            
            if (pr < 1){
                pr = 1;
            }
            
            g2.setColor(new Color(180, 140, 255, (int)(160 * pulse)));
            g2.fillOval(px - pr, py - pr, pr * 2, pr * 2);
        }
    }
}
