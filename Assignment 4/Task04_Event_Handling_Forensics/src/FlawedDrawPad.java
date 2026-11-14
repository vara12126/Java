package task04_forensics;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Original Flawed Drawing Pad provided in Assignment 4.
 * 
 * Flaws:
 * 1. Method typo: 'mouseclicked' (lowercase 'c') instead of 'mouseClicked'.
 * 2. Status label is never updated with new click count.
 * 3. Ephemeral graphics: Uses getGraphics() instead of persistent paint(Graphics g).
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class FlawedDrawPad extends Frame {

    int count = 0;
    Label status = new Label("Clicks: 0");

    public FlawedDrawPad() {
        super("Flawed Drawing Pad (Original Code)");
        add(status, BorderLayout.SOUTH);

        // Flaw 1: Typo in method name (never called by AWT event dispatch)
        addMouseListener(new MouseAdapter() {
            public void mouseclicked(MouseEvent e) {
                count++;
                Graphics g = getGraphics(); // Flaw 3: Transient drawing context
                g.fillOval(e.getX(), e.getY(), 6, 6);
                // Flaw 2: status.setText() missing!
            }
        });

        setSize(400, 300);
    }
}
