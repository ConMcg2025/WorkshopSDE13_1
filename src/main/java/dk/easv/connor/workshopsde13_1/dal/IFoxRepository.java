package dk.easv.connor.workshopsde13_1.dal;

import java.util.List;

public interface IFoxRepository {
    void sendCommand(int groupNumber, String command, String value) throws Exception;
    List<PredefinedCommand> getCommands() throws Exception;
    List<ReceivedCommand> getConfig(int groupNumber) throws Exception;
}