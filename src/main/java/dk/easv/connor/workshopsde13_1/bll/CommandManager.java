package dk.easv.connor.workshopsde13_1.bll;

import dk.easv.connor.workshopsde13_1.dal.IFoxRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandManager {

    private final List<FoxCommand> pendingCommands = new ArrayList<>();
    private final IFoxRepository repository;
    private final int groupNumber;

    public CommandManager(IFoxRepository repository, int groupNumber) {
        this.repository = repository;
        this.groupNumber = groupNumber;
    }

    public void addCommand(String command, String value) {
        pendingCommands.add(new FoxCommand(repository, groupNumber, command, value));
    }

    public void executeAll() throws Exception {
        Iterator<FoxCommand> iterator = pendingCommands.iterator();
        while (iterator.hasNext()) {
            FoxCommand cmd = iterator.next();
            System.out.println("Executing: " + cmd.getDescription());
            cmd.execute();
        }
        pendingCommands.clear();
    }

    public List<FoxCommand> getPendingCommands() {
        return pendingCommands;
    }
}