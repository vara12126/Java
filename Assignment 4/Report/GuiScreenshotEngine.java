package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * High-Resolution GUI Component & Window Screenshot Rendering Engine for Assignment 4.
 * Captures pixel-perfect genuine screenshots of AWT and Swing windows.
 */
public class GuiScreenshotEngine {

    public static void captureComponent(Component component, File outputFile) throws IOException {
        int width = component.getWidth();
        int height = component.getHeight();

        if (width <= 0) width = 600;
        if (height <= 0) height = 400;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fill background
        g2d.setColor(component.getBackground() != null ? component.getBackground() : Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height);

        // Print component hierarchy
        component.printAll(g2d);
        g2d.dispose();

        outputFile.getParentFile().mkdirs();
        ImageIO.write(image, "PNG", outputFile);
        System.out.println("[GUI CAPTURE ENGINE] Saved GUI screenshot: " + outputFile.getAbsolutePath());
    }
}
