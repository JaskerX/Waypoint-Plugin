package de.jaskerx.waypoints;

public enum Visibility {

    PUBLIC ("Public"),
    PRIVATE ("Private");

    private final String displayString;

    Visibility(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return this.displayString;
    }

}
