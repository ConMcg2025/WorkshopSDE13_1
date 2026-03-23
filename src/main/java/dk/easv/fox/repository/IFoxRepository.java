package dk.easv.fox.repository;

import dk.easv.fox.model.FoxConfig;

/**
 * Repository pattern interface.
 * Swap ApiRepository for TelnetRepository (or any other backend)
 * without touching command or UI code.
 */
public interface IFoxRepository {

    /**
     * Send a single configuration command to the fox machine.
     *
     * @param groupId the group's virtual fox machine number
     * @param command the command string, e.g. "wr freq"
     * @param value   the value string, e.g. "434750"
     */
    void sendCommand(int groupId, String command, String value);

    /**
     * Read the current configuration from the fox machine.
     *
     * @param groupId the group's virtual fox machine number
     * @return a FoxConfig populated with the current values, or empty strings if not set
     */
    FoxConfig getConfig(int groupId);
}
