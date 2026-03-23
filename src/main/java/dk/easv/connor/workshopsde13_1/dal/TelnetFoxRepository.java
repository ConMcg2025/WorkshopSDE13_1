package dk.easv.connor.workshopsde13_1.dal;

public class TelnetFoxRepository implements IFoxRepository {

    @Override
    public void sendCommand(int groupNumber, String command, String value) throws Exception {
        throw new UnsupportedOperationException("Telnet not yet implemented");
    }
}
