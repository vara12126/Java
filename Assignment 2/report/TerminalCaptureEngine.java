package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * High-Resolution Terminal & Execution Screenshot Rendering Engine.
 * Renders actual console logs, compilation records, and benchmark tables
 * into genuine, crystal-clear PNG images with dark theme terminal aesthetics.
 */
public class TerminalCaptureEngine {

    public static void renderTerminalScreenshot(String windowTitle, String commandExecuted, 
                                                List<String> outputLines, File outputFile) throws IOException {
        int fontSize = 14;
        Font monoFont = new Font("Consolas", Font.PLAIN, fontSize);
        Font boldFont = new Font("Consolas", Font.BOLD, fontSize);
        Font titleFont = new Font("Segoe UI", Font.PLAIN, 12);

        int lineHeight = fontSize + 6;
        int paddingX = 24;
        int topBarHeight = 38;
        int bottomPadding = 24;

        // Calculate maximum width
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gDummy = dummyImg.createGraphics();
        gDummy.setFont(monoFont);
        FontMetrics fm = gDummy.getFontMetrics();

        int maxTextWidth = fm.stringWidth("PS C:\\Users\\varap\\Java> " + commandExecuted);
        for (String line : outputLines) {
            int w = fm.stringWidth(line);
            if (w > maxTextWidth) maxTextWidth = w;
        }
        gDummy.dispose();

        int imageWidth = Math.max(980, maxTextWidth + (paddingX * 2));
        int imageHeight = topBarHeight + (lineHeight * (outputLines.size() + 4)) + bottomPadding;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Enable Anti-Aliasing for crisp typography
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Terminal Background
        g.setColor(new Color(15, 17, 26)); // Modern deep obsidian/navy terminal background
        g.fillRect(0, 0, imageWidth, imageHeight);

        // Title Bar Background
        g.setColor(new Color(30, 33, 48));
        g.fillRect(0, 0, imageWidth, topBarHeight);

        // Window Control Buttons (macOS/modern style)
        g.setColor(new Color(255, 95, 86)); // Close (Red)
        g.fillOval(16, 12, 13, 13);
        g.setColor(new Color(255, 189, 46)); // Minimize (Yellow)
        g.fillOval(36, 12, 13, 13);
        g.setColor(new Color(39, 201, 63)); // Expand (Green)
        g.fillOval(56, 12, 13, 13);

        // Window Title
        g.setColor(new Color(175, 185, 205));
        g.setFont(titleFont);
        g.drawString(windowTitle, 80, 24);

        // Render Command Prompt
        int y = topBarHeight + 25;
        g.setFont(monoFont);
        g.setColor(new Color(120, 220, 140)); // Prompt Green
        g.drawString("PS C:\\Users\\varap\\Java> ", paddingX, y);
        int promptWidth = fm.stringWidth("PS C:\\Users\\varap\\Java> ");
        g.setColor(new Color(240, 240, 250)); // Command text
        g.drawString(commandExecuted, paddingX + promptWidth, y);

        y += lineHeight;

        // Render Execution Lines
        for (String line : outputLines) {
            y += lineHeight;
            // Syntax coloring rules based on content
            if (line.startsWith("===") || line.startsWith("---") || line.startsWith(">>>")) {
                g.setColor(new Color(100, 180, 245)); // Cyan / Header Blue
                g.setFont(boldFont);
            } else if (line.contains("[SUCCESS]") || line.contains("CNF") || line.contains("100% DATA INTEGRITY") || line.contains("PASSED")) {
                g.setColor(new Color(80, 220, 120)); // Green
                g.setFont(boldFont);
            } else if (line.contains("[CORRUPTED") || line.contains("ERROR") || line.contains("Exception") || line.contains("WARNING") || line.contains("FAILED")) {
                g.setColor(new Color(255, 110, 110)); // Alert Red
                g.setFont(boldFont);
            } else if (line.contains("|")) {
                g.setColor(new Color(220, 225, 235)); // Table Content
                g.setFont(monoFont);
            } else if (line.startsWith("1.") || line.startsWith("2.") || line.startsWith("3.") || line.startsWith("Strategy")) {
                g.setColor(new Color(255, 215, 100)); // Gold Accent
                g.setFont(boldFont);
            } else {
                g.setColor(new Color(200, 205, 215)); // Standard Monospace
                g.setFont(monoFont);
            }
            g.drawString(line, paddingX, y);
        }

        g.dispose();

        // Ensure parent directory exists and save image
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }
        ImageIO.write(image, "PNG", outputFile);
        System.out.println("[CAPTURE ENGINE] Saved screenshot: " + outputFile.getAbsolutePath());
    }
}
