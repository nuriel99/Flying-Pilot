package enums;

public enum PREF_TYPES {
    PLAYER("PLAYER"),
    SELECTED("SELECTED"),
    PLAY_MUSIC("PLAY_MUSIC"),
    COINS("COINS"),
    SCORE_BOARD("SCORE_BOARD"),
    SCORE("SCORE_"),
    CHARACTERS("CHARACTERS");

    private final String name;

    private PREF_TYPES(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
