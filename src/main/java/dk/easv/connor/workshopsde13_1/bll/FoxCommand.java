package dk.easv.connor.workshopsde13_1.bll;

import dk.easv.connor.workshopsde13_1.dal.IFoxRepository;

public class FoxCommand {

    private final IFoxRepository repository;
    private final int groupNumber;
    private final String command;
    private final String value;

    public FoxCommand(IFoxRepository repository, int groupNumber, String command, String value) {
        this.repository = repository;
        this.groupNumber = groupNumber;
        this.command = command;
        this.value = value;
    }

    public void execute() throws Exception {
        repository.sendCommand(groupNumber, command, value);
    }

    public String getDescription() {
        return command + " " + value;
    }
}
