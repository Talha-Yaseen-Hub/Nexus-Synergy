package patches;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** Tracks which levels are solved, best times, and the daily play streak. */
public final class Progress {
    private static final File FILE =
        new File(System.getProperty("user.home"), ".patches-game/progress.properties");

    private final Map<Integer, Boolean> solved = new HashMap<>();
    private final Map<Integer, Integer> bestTime = new HashMap<>();
    private int streak = 0;
    private String lastPlayedDay = null;

    public Progress() {
        load();
    }

    public boolean isSolved(int levelId) {
        return Boolean.TRUE.equals(solved.get(levelId));
    }

    public Integer bestTimeSeconds(int levelId) {
        return bestTime.get(levelId);
    }

    public int streak() {
        return streak;
    }

    /** Records a solve, updates best time, and advances the daily streak. */
    public void recordSolve(int levelId, int seconds) {
        solved.put(levelId, true);
        Integer prevBest = bestTime.get(levelId);
        if (prevBest == null || seconds < prevBest) {
            bestTime.put(levelId, seconds);
        }
        String today = LocalDate.now().toString();
        if (!today.equals(lastPlayedDay)) {
            streak += 1;
            lastPlayedDay = today;
        }
        save();
    }

    private void load() {
        if (!FILE.exists()) return;
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(FILE)) {
            p.load(in);
        } catch (IOException e) {
            return;
        }
        for (String key : p.stringPropertyNames()) {
            if (key.startsWith("solved.")) {
                int id = Integer.parseInt(key.substring("solved.".length()));
                solved.put(id, Boolean.parseBoolean(p.getProperty(key)));
            } else if (key.startsWith("best.")) {
                int id = Integer.parseInt(key.substring("best.".length()));
                bestTime.put(id, Integer.parseInt(p.getProperty(key)));
            } else if (key.equals("streak")) {
                streak = Integer.parseInt(p.getProperty(key));
            } else if (key.equals("lastPlayedDay")) {
                lastPlayedDay = p.getProperty(key);
            }
        }
    }

    private void save() {
        Properties p = new Properties();
        for (Map.Entry<Integer, Boolean> e : solved.entrySet()) {
            p.setProperty("solved." + e.getKey(), String.valueOf(e.getValue()));
        }
        for (Map.Entry<Integer, Integer> e : bestTime.entrySet()) {
            p.setProperty("best." + e.getKey(), String.valueOf(e.getValue()));
        }
        p.setProperty("streak", String.valueOf(streak));
        if (lastPlayedDay != null) p.setProperty("lastPlayedDay", lastPlayedDay);

        try {
            File dir = FILE.getParentFile();
            if (!dir.exists()) dir.mkdirs();
            try (FileOutputStream out = new FileOutputStream(FILE)) {
                p.store(out, "Patches game progress");
            }
        } catch (IOException ignored) {
            // non-fatal: progress just won't persist this run
        }
    }
}
