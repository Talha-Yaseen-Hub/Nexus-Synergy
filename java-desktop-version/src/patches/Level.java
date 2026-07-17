package patches;

import java.util.List;

public final class Level {
    public final int id;
    public final int rows, cols;
    public final List<Clue> clues;

    public Level(int id, int rows, int cols, List<Clue> clues) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.clues = clues;
    }

    public int clueIndexAt(int r, int c) {
        for (int i = 0; i < clues.size(); i++) {
            Clue cl = clues.get(i);
            if (cl.row == r && cl.col == c) return i;
        }
        return -1;
    }
}
