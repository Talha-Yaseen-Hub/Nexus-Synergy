package patches;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.function.IntConsumer;

/** Grid of level tiles grouped by grid-size tier, plus daily puzzle & how-to entry points. */
public class LevelSelectPanel extends JPanel {

    private final List<Level> levels;
    private final Progress progress;
    private final IntConsumer onSelectLevel; // receives level index (0-based)
    private final JPanel tileGrid = new JPanel();

    public LevelSelectPanel(List<Level> levels, Progress progress, IntConsumer onSelectLevel,
                             Runnable onHowTo) {
        this.levels = levels;
        this.progress = progress;
        this.onSelectLevel = onSelectLevel;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel intro = new JLabel("<html><body style='width:520px'>" +
            "Fill every cell with a numbered rectangle patch. Each clue tells you how many " +
            "cells its patch must cover and whether it's square, tall, or wide. " +
            "Pick any of the 32 levels below, or jump into today's featured puzzle.</body></html>");
        intro.setFont(Theme.body(13.5f));
        intro.setForeground(Theme.INK_SOFT);
        intro.setBorder(new EmptyBorder(0, 0, 14, 0));

        RoundedButton daily = new RoundedButton("\u2605 Play today's puzzle", true);
        daily.addActionListener(e -> onSelectLevel.accept(dailyLevelIndex()));
        RoundedButton howTo = new RoundedButton("How to play", false);
        howTo.addActionListener(e -> onHowTo.run());

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionRow.setOpaque(false);
        actionRow.add(daily);
        actionRow.add(howTo);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        intro.setAlignmentX(LEFT_ALIGNMENT);
        actionRow.setAlignmentX(LEFT_ALIGNMENT);
        top.add(intro);
        top.add(actionRow);
        top.setBorder(new EmptyBorder(4, 0, 16, 0));

        tileGrid.setOpaque(false);
        tileGrid.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));

        JScrollPane scroll = new JScrollPane(wrapTop(top, tileGrid));
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(scroll, BorderLayout.CENTER);
        rebuild();
    }

    private JPanel wrapTop(JPanel top, JPanel grid) {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        top.setAlignmentX(LEFT_ALIGNMENT);

        wrap.add(top);
        int lastRows = -1, lastCols = -1;
        JPanel currentTier = null;
        for (int i = 0; i < levels.size(); i++) {
            Level lvl = levels.get(i);
            if (lvl.rows != lastRows || lvl.cols != lastCols) {
                JLabel heading = new JLabel(lvl.rows + " \u00d7 " + lvl.cols + " GRID");
                heading.setFont(Theme.displayBold(13f));
                heading.setForeground(Theme.INK_SOFT);
                heading.setAlignmentX(LEFT_ALIGNMENT);
                heading.setBorder(new EmptyBorder(lastRows == -1 ? 0 : 18, 0, 8, 0));
                wrap.add(heading);

                currentTier = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
                currentTier.setOpaque(false);
                currentTier.setAlignmentX(LEFT_ALIGNMENT);
                wrap.add(currentTier);
                lastRows = lvl.rows; lastCols = lvl.cols;
            }
            currentTier.add(makeTile(i, lvl));
        }
        return wrap;
    }

    private void rebuild() {
        // tileGrid unused directly (tiers built in wrapTop); kept for API symmetry
    }

    private int dailyLevelIndex() {
        int dayOfYear = LocalDate.now().getDayOfYear();
        int id = (dayOfYear % levels.size()) + 1;
        for (int i = 0; i < levels.size(); i++) if (levels.get(i).id == id) return i;
        return 0;
    }

    private JComponent makeTile(int index, Level lvl) {
        boolean solved = progress.isSolved(lvl.id);
        boolean isDaily = lvl.id == levels.get(dailyLevelIndex()).id;

        JPanel tile = new JPanel() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g.setColor(solved ? new Color(0xEA, 0xF2, 0xEA) : Theme.CANVAS_RAISED);
                g.fillRoundRect(1, 1, w - 2, h - 2, 14, 14);
                g.setColor(solved ? Theme.SUCCESS : Theme.THREAD);
                g.setStroke(new BasicStroke(solved ? 2f : 1.6f,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                    1f, solved ? null : new float[]{4f, 3f}, 0f));
                g.drawRoundRect(1, 1, w - 2, h - 2, 14, 14);

                g.setFont(Theme.displayBold(19f));
                g.setColor(Theme.DENIM);
                String num = String.valueOf(lvl.id);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(num, w / 2 - fm.stringWidth(num) / 2, h / 2 - 2);

                g.setFont(Theme.body(9.5f));
                g.setColor(Theme.INK_SOFT);
                String size = lvl.rows + "\u00d7" + lvl.cols;
                FontMetrics fm2 = g.getFontMetrics();
                g.drawString(size, w / 2 - fm2.stringWidth(size) / 2, h / 2 + 16);

                if (solved) {
                    g.setFont(Theme.bodyBold(12f));
                    g.setColor(Theme.SUCCESS);
                    g.drawString("\u2713", w - 16, 15);
                }
                if (isDaily) {
                    g.setFont(Theme.bodyBold(12f));
                    g.setColor(new Color(0xD9, 0xA4, 0x41));
                    g.drawString("\u2605", 6, 15);
                }
                g.dispose();
            }
        };
        tile.setPreferredSize(new Dimension(80, 80));
        tile.setOpaque(false);
        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tile.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onSelectLevel.accept(index); }
        });
        return tile;
    }

    /** Call after returning from a game to refresh solved checkmarks. */
    public void refresh(JFrame frame) {
        frame.revalidate();
        frame.repaint();
    }
}
