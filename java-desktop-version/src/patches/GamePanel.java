package patches;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GamePanel extends JPanel {

    public interface Listener {
        void onBack();
        void onWin(Level level, int seconds);
    }

    private final BoardPanel board = new BoardPanel();
    private final JLabel levelBadge = new JLabel();
    private final JLabel levelMeta = new JLabel();
    private final JLabel timerLabel = new JLabel("00:00");
    private final javax.swing.Timer tickTimer;
    private Level currentLevel;
    private Listener listener;

    public GamePanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 4, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        RoundedButton back = new RoundedButton("\u2190 Levels", false);
        back.addActionListener(e -> { if (listener != null) listener.onBack(); });
        levelBadge.setFont(Theme.displayBold(18f));
        levelBadge.setForeground(Theme.DENIM);
        levelBadge.setBorder(new EmptyBorder(0, 6, 0, 0));
        left.add(back);
        left.add(levelBadge);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        timerLabel.setFont(Theme.displayBold(15f));
        timerLabel.setForeground(Theme.INK_SOFT);
        timerLabel.setBorder(new EmptyBorder(0, 0, 0, 6));
        RoundedButton hint = new RoundedButton("\uD83D\uDCA1 Hint", false);
        hint.addActionListener(e -> board.hint());
        RoundedButton undo = new RoundedButton("\u21BA Undo", false);
        undo.addActionListener(e -> board.undo());
        RoundedButton reset = new RoundedButton("\u27F2 Reset", false);
        reset.addActionListener(e -> board.resetBoard());
        right.add(timerLabel);
        right.add(hint);
        right.add(undo);
        right.add(reset);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);

        levelMeta.setFont(Theme.body(12f));
        levelMeta.setForeground(Theme.INK_SOFT);
        levelMeta.setBorder(new EmptyBorder(0, 6, 12, 0));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        toolbar.setAlignmentX(LEFT_ALIGNMENT);
        levelMeta.setAlignmentX(LEFT_ALIGNMENT);
        top.add(toolbar);
        top.add(levelMeta);

        JPanel boardWrap = new JPanel(new GridBagLayout());
        boardWrap.setOpaque(false);
        boardWrap.add(board);

        add(top, BorderLayout.NORTH);
        add(boardWrap, BorderLayout.CENTER);

        board.setListener(new BoardPanel.Listener() {
            @Override public void onWin(int elapsedSeconds) {
                tickTimer.stop();
                if (listener != null && currentLevel != null) {
                    listener.onWin(currentLevel, elapsedSeconds);
                }
            }
            @Override public void onProgress() { /* no-op, board repaints itself */ }
        });

        tickTimer = new javax.swing.Timer(250, e -> updateTimerLabel());
    }

    public void setListener(Listener l) { this.listener = l; }

    public void loadLevel(Level level) {
        this.currentLevel = level;
        levelBadge.setText("Level " + level.id);
        levelMeta.setText(level.rows + "\u00d7" + level.cols + " grid \u00b7 " + level.clues.size() + " patches");
        board.loadLevel(level);
        tickTimer.start();
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        int s = board.getElapsedSeconds();
        timerLabel.setText(String.format("%02d:%02d", s / 60, s % 60));
    }

    public void stopTimer() { tickTimer.stop(); }
}
