package patches;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HowToDialog extends JDialog {

    public HowToDialog(Frame owner) {
        super(owner, "How to play Patches", true);
        setUndecorated(true);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.CANVAS_RAISED);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.DENIM, 3, true),
            new EmptyBorder(26, 30, 24, 30)));

        JLabel title = new JLabel("How to play Patches");
        title.setFont(Theme.displayBold(22f));
        title.setForeground(Theme.DENIM);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        String html = "<html><body style='width:360px;font-family:sans-serif;font-size:11.5px;color:#2B2420;line-height:150%'>"
            + "<ul style='margin-left:14px;padding-left:0'>"
            + "<li>Every numbered cell is a <b>clue</b>. The number is the exact cell count its rectangle must cover.</li>"
            + "<li>The small icon under the number shows the required shape: square, tall, or wide.</li>"
            + "<li>Click a clue and drag to the opposite corner of the rectangle you want to place, then release.</li>"
            + "<li>Every rectangle must contain exactly one clue, and rectangles can never overlap.</li>"
            + "<li>You win once every cell on the board belongs to a patch, with no gaps left uncovered.</li>"
            + "<li>Click any placed patch to remove it. Use <b>Undo</b>, <b>Reset</b>, or <b>Hint</b> if you're stuck.</li>"
            + "</ul></body></html>";
        JLabel body = new JLabel(html);
        body.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.setBorder(new EmptyBorder(0, 0, 18, 0));

        RoundedButton close = new RoundedButton("Got it", true);
        close.addActionListener(e -> dispose());
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionRow.setOpaque(false);
        actionRow.add(close);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(body);
        card.add(actionRow);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(card);
        setContentPane(wrap);
        pack();
        setLocationRelativeTo(owner);
    }
}
