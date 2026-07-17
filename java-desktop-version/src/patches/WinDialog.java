package patches;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WinDialog extends JDialog {

    public WinDialog(Frame owner, int levelId, int seconds, Integer bestSeconds,
                      Runnable onNext, Runnable onAllLevels, boolean hasNext) {
        super(owner, "Patch complete!", true);
        setUndecorated(true);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.CANVAS_RAISED);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.DENIM, 3, true),
            new EmptyBorder(30, 34, 26, 34)));

        JLabel stitches = new JLabel("\u2702\uFE0E\u2508\u2508\u2508\u2508\u2508\u2508\u2508\u2508");
        stitches.setFont(Theme.body(18f));
        stitches.setForeground(new Color(0xC1, 0x50, 0x2E));
        stitches.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Patch complete!");
        title.setFont(Theme.displayBold(24f));
        title.setForeground(Theme.DENIM);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(6, 0, 6, 0));

        String bestText = bestSeconds == null ? "--:--" :
            String.format("%02d:%02d", bestSeconds / 60, bestSeconds % 60);
        JLabel stats = new JLabel(String.format("Solved in %02d:%02d \u00b7 Best: %s",
            seconds / 60, seconds % 60, bestText));
        stats.setFont(Theme.body(13.5f));
        stats.setForeground(Theme.INK_SOFT);
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actions.setOpaque(false);
        if (hasNext) {
            RoundedButton next = new RoundedButton("Next level \u2192", true);
            next.addActionListener(e -> { dispose(); onNext.run(); });
            actions.add(next);
        }
        RoundedButton all = new RoundedButton("All levels", false);
        all.addActionListener(e -> { dispose(); onAllLevels.run(); });
        actions.add(all);

        card.add(stitches);
        card.add(title);
        card.add(stats);
        card.add(actions);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(card);
        setContentPane(wrap);
        getContentPane().setBackground(new Color(0, 0, 0, 0));
        pack();
        setLocationRelativeTo(owner);
    }
}
