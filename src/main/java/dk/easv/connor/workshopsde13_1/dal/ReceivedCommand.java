package dk.easv.connor.workshopsde13_1.dal;

public class ReceivedCommand {
    private int id;
    private String command;
    private String value;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() { return command + " " + value; }
}
