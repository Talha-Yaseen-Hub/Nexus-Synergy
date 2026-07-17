package patches;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A pill-shaped button with a soft hover lift, matching the web app's .btn style. */
public class RoundedButton extends JButton {
    private boolean primary;
    private boolean hovered = false;

    public RoundedButton(String text, boolean primary) {
        super(text);
        this.primary = primary;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(primary ? Color.WHITE : Theme.INK);
        setFont(Theme.bodyBold(13f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int lift = hovered ? -1 : 0;

        Color fill = primary
            ? (hovered ? Theme.DENIM_LIGHT : Theme.DENIM)
            : Theme.CANVAS_RAISED;
        Color border = primary ? fill : (hovered ? Theme.DENIM_LIGHT : Theme.THREAD);

        g2.setColor(fill);
        g2.fillRoundRect(0, lift, w, h - 1, h, h);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(0, lift, w - 1, h - 2, h, h);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 36));
    }
}
