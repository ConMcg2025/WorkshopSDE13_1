package dk.easv.connor.workshopsde13_1.dal;

public class ReceivedCommandDto {
    private String command;
    private String value;

    public ReceivedCommandDto(String command, String value) {
        this.command = command;
        this.value = value;
    }

    public String getCommand() { return command; }
    public String getValue() { return value; }
}