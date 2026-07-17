package patches;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The game board. Click a clue cell and drag to the opposite corner of the
 * rectangle you want to place; release to commit it. Click any placed patch
 * to remove it. Mirrors the validation rules of the web version exactly.
 */
public class BoardPanel extends JPanel {

    public interface Listener {
        void onWin(int elapsedSeconds);
        void onProgress(); // fired on any successful placement/removal, for UI refresh
    }

    private static final int MIN_CELL = 34;
    private static final int MAX_CELL = 62;

    private Level level;
    private int[][] owner;
    private final List<int[]> patches = new ArrayList<>(); // {clueIndex, r1, c1, r2, c2}
    private int cellSize = 56;

    private boolean dragging = false;
    private int anchorR, anchorC, anchorClue;
    private int currentR, currentC;

    private final Set<Long> shakeCells = new HashSet<>();
    private final Set<Long> hintGlowCells = new HashSet<>();
    private final javax.swing.Timer flashTimer;
    private final javax.swing.Timer glowTimer;

    private long startTimeMs;
    private Listener listener;

    public BoardPanel() {
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { handlePress(e); }
            @Override public void mouseReleased(MouseEvent e) { handleRelease(); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) { handleDrag(e); }
        });

        flashTimer = new javax.swing.Timer(360, e -> {
            shakeCells.clear();
            repaint();
        });
        flashTimer.setRepeats(false);

        glowTimer = new javax.swing.Timer(1400, e -> {
            hintGlowCells.clear();
            repaint();
        });
        glowTimer.setRepeats(false);
    }

    public void setListener(Listener l) { this.listener = l; }

    public void loadLevel(Level level) {
        this.level = level;
        this.owner = new int[level.rows][level.cols];
        for (int[] row : owner) java.util.Arrays.fill(row, -1);
        patches.clear();
        dragging = false;
        shakeCells.clear();
        hintGlowCells.clear();
        computeCellSize();
        startTimeMs = System.currentTimeMillis();
        revalidate();
        repaint();
    }

    public void resetBoard() {
        for (int[] row : owner) java.util.Arrays.fill(row, -1);
        patches.clear();
        startTimeMs = System.currentTimeMillis();
        repaint();
        if (listener != null) listener.onProgress();
    }

    public int getElapsedSeconds() {
        return (int) ((System.currentTimeMillis() - startTimeMs) / 1000);
    }

    private void computeCellSize() {
        int longest = Math.max(level.rows, level.cols);
        int size = 620 / longest;
        cellSize = Math.max(MIN_CELL, Math.min(MAX_CELL, size));
    }

    @Override
    public Dimension getPreferredSize() {
        if (level == null) return new Dimension(400, 400);
        return new Dimension(level.cols * cellSize + 6, level.rows * cellSize + 6);
    }

    // ---------- interaction ----------

    private int[] pointToCell(Point p) {
        int c = Math.floorDiv(p.x - 3, cellSize);
        int r = Math.floorDiv(p.y - 3, cellSize);
        r = Math.max(0, Math.min(level.rows - 1, r));
        c = Math.max(0, Math.min(level.cols - 1, c));
        return new int[]{r, c};
    }

    private void handlePress(MouseEvent e) {
        if (level == null) return;
        int[] rc = pointToCell(e.getPoint());
        int r = rc[0], c = rc[1];

        int own = owner[r][c];
        if (own >= 0) {
            removePatchByClue(own);
            repaint();
            if (listener != null) listener.onProgress();
            return;
        }
        int clueIdx = level.clueIndexAt(r, c);
        if (clueIdx < 0) return;

        dragging = true;
        anchorR = r; anchorC = c; anchorClue = clueIdx;
        currentR = r; currentC = c;
        repaint();
    }

    private void handleDrag(MouseEvent e) {
        if (!dragging) return;
        int[] rc = pointToCell(e.getPoint());
        if (rc[0] == currentR && rc[1] == currentC) return;
        currentR = rc[0]; currentC = rc[1];
        repaint();
    }

    private void handleRelease() {
        if (!dragging) return;
        dragging = false;
        int r1 = Math.min(anchorR, currentR), r2 = Math.max(anchorR, currentR);
        int c1 = Math.min(anchorC, currentC), c2 = Math.max(anchorC, currentC);

        boolean ok = validate(anchorClue, r1, c1, r2, c2, false);
        if (ok) {
            commitPatch(anchorClue, r1, c1, r2, c2);
            if (listener != null) listener.onProgress();
            if (isSolved() && listener != null) {
                listener.onWin(getElapsedSeconds());
            }
        } else {
            flashInvalid(r1, c1, r2, c2);
        }
        repaint();
    }

    // ---------- validation & mutation ----------

    private boolean validate(int clueIndex, int r1, int c1, int r2, int c2, boolean partial) {
        Clue clue = level.clues.get(clueIndex);
        int w = c2 - c1 + 1, h = r2 - r1 + 1;
        int area = w * h;

        switch (clue.shape) {
            case SQUARE: if (w != h) return false; break;
            case TALL: if (h <= w) return false; break;
            case WIDE: if (w <= h) return false; break;
        }

        if (!partial && area != clue.value) return false;
        if (partial && area > clue.value) return false;

        for (int r = r1; r <= r2; r++) {
            for (int c = c1; c <= c2; c++) {
                if (owner[r][c] >= 0) return false;
            }
        }

        for (int i = 0; i < level.clues.size(); i++) {
            if (i == clueIndex) continue;
            Clue other = level.clues.get(i);
            if (other.row >= r1 && other.row <= r2 && other.col >= c1 && other.col <= c2) {
                return false;
            }
        }
        return true;
    }

    private void commitPatch(int clueIndex, int r1, int c1, int r2, int c2) {
        patches.add(new int[]{clueIndex, r1, c1, r2, c2});
        for (int r = r1; r <= r2; r++) {
            for (int c = c1; c <= c2; c++) {
                owner[r][c] = clueIndex;
            }
        }
    }

    private void removePatchByClue(int clueIndex) {
        for (int i = patches.size() - 1; i >= 0; i--) {
            int[] p = patches.get(i);
            if (p[0] == clueIndex) {
                for (int r = p[1]; r <= p[3]; r++) {
                    for (int c = p[2]; c <= p[4]; c++) {
                        owner[r][c] = -1;
                    }
                }
                patches.remove(i);
                return;
            }
        }
    }

    public void undo() {
        if (patches.isEmpty()) return;
        int[] last = patches.get(patches.size() - 1);
        removePatchByClue(last[0]);
        repaint();
        if (listener != null) listener.onProgress();
    }

    public void hint() {
        if (level == null) return;
        List<Integer> unsolved = new ArrayList<>();
        for (int i = 0; i < level.clues.size(); i++) {
            boolean placed = false;
            for (int[] p : patches) if (p[0] == i) { placed = true; break; }
            if (!placed) unsolved.add(i);
        }
        if (unsolved.isEmpty()) return;
        int idx = unsolved.get((int) (Math.random() * unsolved.size()));
        Clue clue = level.clues.get(idx);
        int r1 = clue.solRow, c1 = clue.solCol;
        int r2 = r1 + clue.solH - 1, c2 = c1 + clue.solW - 1;

        if (validate(idx, r1, c1, r2, c2, false)) {
            commitPatch(idx, r1, c1, r2, c2);
            glow(r1, c1, r2, c2);
            if (listener != null) listener.onProgress();
            if (isSolved() && listener != null) listener.onWin(getElapsedSeconds());
        } else {
            glow(clue.row, clue.col, clue.row, clue.col);
        }
        repaint();
    }

    private void flashInvalid(int r1, int c1, int r2, int c2) {
        for (int r = r1; r <= r2; r++) {
            for (int c = c1; c <= c2; c++) {
                shakeCells.add(key(r, c));
            }
        }
        flashTimer.restart();
    }

    private void glow(int r1, int c1, int r2, int c2) {
        for (int r = r1; r <= r2; r++) {
            for (int c = c1; c <= c2; c++) {
                hintGlowCells.add(key(r, c));
            }
        }
        glowTimer.restart();
    }

    private long key(int r, int c) { return ((long) r << 32) | (c & 0xffffffffL); }

    public boolean isSolved() {
        if (level == null) return false;
        for (int r = 0; r < level.rows; r++) {
            for (int c = 0; c < level.cols; c++) {
                if (owner[r][c] < 0) return false;
            }
        }
        return true;
    }

    // ---------- painting ----------

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (level == null) return;
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ox = 3, oy = 3;

        // outer frame
        g.setColor(Theme.DENIM);
        g.setStroke(new BasicStroke(3f));
        g.drawRect(ox - 2, oy - 2, level.cols * cellSize + 3, level.rows * cellSize + 3);

        for (int r = 0; r < level.rows; r++) {
            for (int c = 0; c < level.cols; c++) {
                int x = ox + c * cellSize, y = oy + r * cellSize;
                int own = owner[r][c];

                Color fill = Theme.CANVAS_RAISED;
                if (own >= 0) fill = Theme.PATCH_COLORS[own % Theme.PATCH_COLORS.length];
                g.setColor(fill);
                g.fillRect(x, y, cellSize, cellSize);

                // thin grid line
                g.setColor(Theme.THREAD);
                g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1f, new float[]{3f, 3f}, 0f));
                g.drawRect(x, y, cellSize, cellSize);

                // thicker border where patch boundary meets a different owner (or grid edge)
                if (own >= 0) {
                    g.setColor(Theme.INK);
                    g.setStroke(new BasicStroke(2f));
                    if (r == 0 || owner[r - 1][c] != own) g.drawLine(x, y, x + cellSize, y);
                    if (r == level.rows - 1 || owner[r + 1][c] != own) g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
                    if (c == 0 || owner[r][c - 1] != own) g.drawLine(x, y, x, y + cellSize);
                    if (c == level.cols - 1 || owner[r][c + 1] != own) g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
                }

                if (shakeCells.contains(key(r, c))) {
                    g.setColor(Theme.DANGER);
                    g.setStroke(new BasicStroke(2.5f));
                    g.drawRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
                }
                if (hintGlowCells.contains(key(r, c))) {
                    g.setColor(new Color(0xD9, 0xA4, 0x41, 200));
                    g.setStroke(new BasicStroke(3f));
                    g.drawRect(x + 2, y + 2, cellSize - 4, cellSize - 4);
                }
            }
        }

        // drag selection preview overlay
        if (dragging) {
            int r1 = Math.min(anchorR, currentR), r2 = Math.max(anchorR, currentR);
            int c1 = Math.min(anchorC, currentC), c2 = Math.max(anchorC, currentC);
            boolean ok = validate(anchorClue, r1, c1, r2, c2, true);
            Color overlay = ok ? new Color(0x8F, 0xB7, 0xBD, 130) : new Color(0xC9, 0x6B, 0x54, 130);
            g.setColor(overlay);
            g.fillRect(ox + c1 * cellSize, oy + r1 * cellSize,
                (c2 - c1 + 1) * cellSize, (r2 - r1 + 1) * cellSize);
            g.setColor(ok ? Theme.DENIM_LIGHT : Theme.DANGER);
            g.setStroke(new BasicStroke(2f));
            g.drawRect(ox + c1 * cellSize, oy + r1 * cellSize,
                (c2 - c1 + 1) * cellSize, (r2 - r1 + 1) * cellSize);
        }

        // clue badges drawn last so they sit above fills/overlays
        for (Clue clue : level.clues) {
            drawClueBadge(g, clue, ox, oy);
        }

        g.dispose();
    }

    private void drawClueBadge(Graphics2D g, Clue clue, int ox, int oy) {
        int x = ox + clue.col * cellSize, y = oy + clue.row * cellSize;
        int pad = (int) (cellSize * 0.13);
        int size = cellSize - pad * 2;

        boolean filled = owner[clue.row][clue.col] >= 0;
        g.setColor(filled ? new Color(255, 255, 255, 210) : Theme.CANVAS);
        g.fillRoundRect(x + pad, y + pad, size, size, 8, 8);
        g.setColor(Theme.DENIM);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x + pad, y + pad, size, size, 8, 8);

        g.setFont(Theme.displayBold(cellSize * 0.30f));
        FontMetrics fm = g.getFontMetrics();
        String text = String.valueOf(clue.value);
        int tx = x + cellSize / 2 - fm.stringWidth(text) / 2;
        int ty = y + (int) (cellSize * 0.48);
        g.drawString(text, tx, ty);

        // shape icon
        g.setColor(new Color(Theme.DENIM.getRed(), Theme.DENIM.getGreen(), Theme.DENIM.getBlue(), 190));
        g.setStroke(new BasicStroke(1.6f));
        int iw, ih;
        switch (clue.shape) {
            case SQUARE: iw = ih = (int) (cellSize * 0.16); break;
            case TALL: iw = (int) (cellSize * 0.12); ih = (int) (cellSize * 0.22); break;
            default: iw = (int) (cellSize * 0.22); ih = (int) (cellSize * 0.12); break;
        }
        int ix = x + cellSize / 2 - iw / 2;
        int iy = y + (int) (cellSize * 0.62);
        g.drawRoundRect(ix, iy, iw, ih, 2, 2);
    }
}
