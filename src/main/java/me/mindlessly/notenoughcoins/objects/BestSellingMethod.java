package me.mindlessly.notenoughcoins.objects;

public enum BestSellingMethod {
    NPC("NPC"),
    BAZAAR("Bazaar"),
    LBIN("Lowest-BIN"),
    NONE("None");
    private final String string;

    BestSellingMethod(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
