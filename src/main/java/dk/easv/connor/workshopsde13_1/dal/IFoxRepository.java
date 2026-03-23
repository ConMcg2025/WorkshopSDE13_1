package dk.easv.connor.workshopsde13_1.dal;

public interface IFoxRepository {
    void sendCommand(int groupNumber, String command, String value) throws Exception;
}
