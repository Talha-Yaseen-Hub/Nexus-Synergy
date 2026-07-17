package patches;

/** A single numbered clue cell plus the rectangle it anchors. */
public final class Clue {
    public final int row, col;
    public final int value;
    public final Shape shape;
    // the generator's reference solution, used only for the Hint feature
    public final int solRow, solCol, solW, solH;

    public Clue(int row, int col, int value, Shape shape,
                int solRow, int solCol, int solW, int solH) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.shape = shape;
        this.solRow = solRow;
        this.solCol = solCol;
        this.solW = solW;
        this.solH = solH;
    }
}
