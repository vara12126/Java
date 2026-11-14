package task04_forensics;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 04 Complete the Code: Repaired DrawPad with Retained Mode Rendering.
 * 
 * Solutions Implemented:
 * - TODO 1: Corrected method signature to mouseClicked(MouseEvent e) with @Override annotation,
 *           and updated the status label text on every click.
 * - TODO 2: Stored clicked coordinates in a List<Point> and overrode paint(Graphics g)
 *           to guarantee dots survive repainting, minimizing, and restoring.
 * 
 * Student: Devi Vara Prasad | Reg: 192365032
 */
public class RepairedDrawPad extends Frame {

    private int count = 0;
    private final Label status = new Label("Clicks: 0", Label.CENTER);

    // TODO 2: Persistent point model to retain dots across repaint/minimize cycles
    private final List<Point> clickedPoints = new ArrayList<>();

    public RepairedDrawPad() {
        super("SIMATS DrawPad – Repaired Event-Driven Retained Graphics");
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        status.setBackground(new Color(230, 235, 245));
        status.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(status, BorderLayout.SOUTH);

        // TODO 1: Correct listener method signature with @Override and update status label
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Record coordinate in persistent model
                clickedPoints.add(new Point(e.getX(), e.getY()));
                count++;

                // Update status label
                status.setText("Clicks: " + count + " | Latest Point: (" + e.getX() + ", " + e.getY() + ")");

                // Request asynchronous repaint via AWT Event Dispatch Thread
                repaint();
            }
        });

        // Window Closing Handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(420, 320);
        setBackground(Color.WHITE);
    }

    /**
     * TODO 2: Retained-mode rendering overriding paint(Graphics g).
     * Automatically invoked by the AWT subsystem whenever the window is restored,
     * uncovered, or resized.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Antialiasing for smooth dots
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Render all persistent points
        g.setColor(new Color(220, 20, 60)); // Crimson Red dots
        for (Point p : clickedPoints) {
            g.fillOval(p.x - 4, p.y - 4, 8, 8);
        }

        // Draw header watermark
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        g.drawString("Click anywhere on canvas to draw persistent dots | Devi Vara Prasad (192365032)", 20, 50);
    }

    /**
     * Programmatic simulation helper for automated testing and screenshot capture.
     */
    public void simulateClick(int x, int y) {
        clickedPoints.add(new Point(x, y));
        count++;
        status.setText("Clicks: " + count + " | Latest Point: (" + x + ", " + y + ")");
        repaint();
    }

    public int getClickCount() { return count; }
    public List<Point> getClickedPoints() { return clickedPoints; }
}
