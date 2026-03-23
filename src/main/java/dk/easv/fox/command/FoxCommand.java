package dk.easv.fox.command;

import dk.easv.fox.repository.IFoxRepository;

/**
 * Generic concrete command for sending a single fox configuration parameter.
 * e.g. command="wr freq", value="434750"
 */
public class FoxCommand implements ICommand {

    private final String command;
    private final String value;
    private final String previousValue; // for undo
    private final String description;
    private final IFoxRepository repository;
    private final int groupId;

    public FoxCommand(String command, String value, String previousValue,
                      String description, IFoxRepository repository, int groupId) {
        this.command       = command;
        this.value         = value;
        this.previousValue = previousValue;
        this.description   = description;
        this.repository    = repository;
        this.groupId       = groupId;
    }

    @Override
    public void execute() {
        repository.sendCommand(groupId, command, value);
    }

    /**
     * Undo by re-sending the previous value.
     * If there was no previous value, sends an empty string to clear the field.
     */
    @Override
    public void undo() {
        String restoreValue = (previousValue != null && !previousValue.isBlank())
                ? previousValue : "";
        if (!restoreValue.isBlank()) {
            repository.sendCommand(groupId, command, restoreValue);
        }
    }

    @Override
    public String getDescription() {
        return description + ": " + command + " " + value;
    }

    public String getCommand()       { return command; }
    public String getValue()         { return value; }
    public String getPreviousValue() { return previousValue; }
}
