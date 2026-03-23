package dk.easv.fox.repository;

import dk.easv.fox.model.FoxConfig;

/**
 * Stub repository that simulates a Telnet connection to a real fox terminal.
 * Replace the body of sendCommand with actual socket/telnet logic when ready.
 * Swap this in place of ApiRepository without changing anything else.
 */
public class TelnetRepository implements IFoxRepository {

    private final String host;
    private final int port;

    public TelnetRepository(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void sendCommand(int groupId, String command, String value) {
        // TODO: open a TCP socket to host:port and write the command string
        // e.g.  socket.getOutputStream().write((command + " " + value + "\r\n").getBytes());
        String fullCommand = command + " " + value;
        System.out.printf("[TELNET STUB] %s:%d → %s%n", host, port, fullCommand);
    }

    @Override
    public FoxConfig getConfig(int groupId) {
        // TODO: send a read command over telnet and parse the response
        System.out.printf("[TELNET STUB] %s:%d → read config for group %d%n", host, port, groupId);
        return new FoxConfig(); // returns empty config for now
    }
}
