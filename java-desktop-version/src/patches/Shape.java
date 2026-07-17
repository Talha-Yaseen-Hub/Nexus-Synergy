package patches;

/** The three rectangle shape categories a clue can require. */
public enum Shape {
    SQUARE, TALL, WIDE;

    static Shape fromCode(String code) {
        switch (code) {
            case "S": return SQUARE;
            case "T": return TALL;
            case "W": return WIDE;
            default: throw new IllegalArgumentException("Unknown shape code: " + code);
        }
    }
}
