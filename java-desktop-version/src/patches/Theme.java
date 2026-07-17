package patches;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/** Shared colors and fonts, mirroring the web version's quilted-canvas theme. */
public final class Theme {
    private Theme() { }

    public static final Color CANVAS = new Color(0xED, 0xE6, 0xD6);
    public static final Color CANVAS_RAISED = new Color(0xF6, 0xF1, 0xE6);
    public static final Color INK = new Color(0x2B, 0x24, 0x20);
    public static final Color INK_SOFT = new Color(0x6B, 0x5F, 0x52);
    public static final Color DENIM = new Color(0x2F, 0x4B, 0x63);
    public static final Color DENIM_LIGHT = new Color(0x3A, 0x5A, 0x78);
    public static final Color THREAD = new Color(0xB9, 0xAC, 0x95);
    public static final Color SUCCESS = new Color(0x4C, 0x7A, 0x4C);
    public static final Color DANGER = new Color(0xB3, 0x43, 0x2F);
    public static final Color SELECT_OK = new Color(0xDC, 0xE8, 0xEA);
    public static final Color SELECT_BAD = new Color(0xF3, 0xD9, 0xD2);

    public static final Color[] PATCH_COLORS = new Color[] {
        new Color(0xC1, 0x50, 0x2E), // terracotta
        new Color(0xD9, 0xA4, 0x41), // mustard
        new Color(0x7A, 0x8B, 0x69), // sage
        new Color(0x6B, 0x4A, 0x6B), // plum
        new Color(0x3F, 0x7B, 0x7B), // teal
        new Color(0xB6, 0x5A, 0x6B), // rose
        new Color(0xC9, 0x8A, 0x3B), // ochre
        new Color(0x5B, 0x6C, 0x7A), // slate
    };

    private static Font displayBase;
    private static Font bodyBase;

    static {
        displayBase = loadFont("/fonts/Fredoka-SemiBold.ttf");
        bodyBase = loadFont("/fonts/Inter-Regular.ttf");
        if (displayBase == null) displayBase = new Font("SansSerif", Font.BOLD, 12);
        if (bodyBase == null) bodyBase = new Font("SansSerif", Font.PLAIN, 12);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(displayBase);
            ge.registerFont(bodyBase);
        } catch (Exception ignored) { }
    }

    private static Font loadFont(String resourcePath) {
        try (InputStream in = Theme.class.getResourceAsStream(resourcePath)) {
            if (in == null) return null;
            return Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (IOException | FontFormatException e) {
            return null;
        }
    }

    public static Font display(float size) {
        return displayBase.deriveFont(Font.PLAIN, size);
    }

    public static Font displayBold(float size) {
        return displayBase.deriveFont(Font.BOLD, size);
    }

    public static Font body(float size) {
        return bodyBase.deriveFont(Font.PLAIN, size);
    }

    public static Font bodyBold(float size) {
        return bodyBase.deriveFont(Font.BOLD, size);
    }
}
