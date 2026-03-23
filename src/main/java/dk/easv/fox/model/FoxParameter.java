package dk.easv.fox.model;

/**
 * All configurable fox parameters with their terminal command strings.
 */
public enum FoxParameter {

    TEST_TIME  ("Test begin time",       "wr test",   "e.g. 1230"),
    START_TIME ("Hunt begin time",       "wr start",  "e.g. 1300"),
    STOP_TIME  ("Hunt stop time",        "wr stop",   "e.g. 1500"),
    FOX_ID     ("Fox ID",                "wr id",     "A, U, V, H or 5"),
    FREQUENCY  ("Nominal frequency (Hz)","wr freq",   "e.g. 434750"),
    PERIOD     ("Repeat period (min)",   "wr period", "e.g. 5"),
    FOXCALL    ("Fox call (max 15 chars)","wr call",  "e.g. OZ7FOX");

    private final String label;
    private final String command;
    private final String placeholder;

    FoxParameter(String label, String command, String placeholder) {
        this.label       = label;
        this.command     = command;
        this.placeholder = placeholder;
    }

    public String getLabel()       { return label; }
    public String getCommand()     { return command; }
    public String getPlaceholder() { return placeholder; }
}
