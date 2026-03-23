package dk.easv.fox.command;

/**
 * Command pattern interface.
 * Each fox configuration parameter gets its own concrete implementation.
 */
public interface ICommand {
    void execute();
    void undo();
    String getDescription();
}
