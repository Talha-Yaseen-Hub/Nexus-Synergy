package patches;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PatchesApp {

    private static final String CARD_SELECT = "select";
    private static final String CARD_GAME = "game";

    private final JFrame frame = new JFrame("Patches — a rectangle puzzle");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardHost = new JPanel(cardLayout);
    private final GamePanel gamePanel = new GamePanel();
    private final JLabel streakLabel = new JLabel();

    private final List<Level> levels;
    private final Progress progress = new Progress();
    private int currentLevelIndex = 0;

    public PatchesApp() {
        levels = LevelLoader.loadAll();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Theme.CANVAS);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(720, 640));
        frame.setSize(920, 760);
        frame.setLocationRelativeTo(null);

        frame.add(buildHeader(), BorderLayout.NORTH);
        frame.add(buildCenter(), BorderLayout.CENTER);
        frame.add(buildFooter(), BorderLayout.SOUTH);

        gamePanel.setListener(new GamePanel.Listener() {
            @Override public void onBack() { showSelect(); }
            @Override public void onWin(Level level, int seconds) { handleWin(level, seconds); }
        });

        showSelect();
    }

    public void show() {
        frame.setVisible(true);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 28, 14, 28));

        JLabel brand = new JLabel("Patches");
        brand.setFont(Theme.displayBold(30f));
        brand.setForeground(Theme.DENIM);

        JLabel tag = new JLabel("   a rectangle puzzle");
        tag.setFont(Theme.body(12f).deriveFont(Font.ITALIC));
        tag.setForeground(Theme.INK_SOFT);

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brandRow.setOpaque(false);
        brandRow.add(brand);
        brandRow.add(tag);

        streakLabel.setFont(Theme.bodyBold(12.5f));
        streakLabel.setForeground(Theme.INK_SOFT);
        streakLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.THREAD, 1, true),
            new EmptyBorder(7, 14, 7, 14)));
        streakLabel.setOpaque(true);
        streakLabel.setBackground(Theme.CANVAS_RAISED);
        updateStreakLabel();

        header.add(brandRow, BorderLayout.WEST);
        header.add(streakLabel, BorderLayout.EAST);
        return header;
    }

    private void updateStreakLabel() {
        streakLabel.setText("\uD83E\uDDF5  " + progress.streak() + " day streak");
    }

    private JComponent buildCenter() {
        cardHost.setOpaque(false);
        cardHost.setBorder(new EmptyBorder(0, 28, 0, 28));
        cardHost.add(makeSelectPanel(), CARD_SELECT);
        cardHost.add(gamePanel, CARD_GAME);
        return cardHost;
    }

    private JComponent buildFooter() {
        JLabel footer = new JLabel("<html><body style='width:600px;text-align:center'>" +
            "Patches is an original, independently-built puzzle inspired by LinkedIn's daily " +
            "rectangle game. Not affiliated with or endorsed by LinkedIn.<br/>" +
            "Progress and best times are saved locally on this computer.</body></html>");
        footer.setFont(Theme.body(10.5f));
        footer.setForeground(Theme.INK_SOFT);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(new EmptyBorder(14, 28, 20, 28));
        return footer;
    }

    private LevelSelectPanel makeSelectPanel() {
        return new LevelSelectPanel(levels, progress, this::startLevel,
            () -> new HowToDialog(frame).setVisible(true));
    }

    private void showSelect() {
        gamePanel.stopTimer();
        // rebuild so solved checkmarks / streak reflect latest progress
        cardHost.remove(0);
        cardHost.add(makeSelectPanel(), CARD_SELECT, 0);
        updateStreakLabel();
        cardLayout.show(cardHost, CARD_SELECT);
        cardHost.revalidate();
        cardHost.repaint();
    }

    private void startLevel(int index) {
        currentLevelIndex = index;
        gamePanel.loadLevel(levels.get(index));
        cardLayout.show(cardHost, CARD_GAME);
    }

    private void handleWin(Level level, int seconds) {
        progress.recordSolve(level.id, seconds);
        boolean hasNext = currentLevelIndex + 1 < levels.size();
        WinDialog dialog = new WinDialog(frame, level.id, seconds, progress.bestTimeSeconds(level.id),
            () -> startLevel(currentLevelIndex + 1),
            this::showSelect,
            hasNext);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new PatchesApp().show());
    }
}
