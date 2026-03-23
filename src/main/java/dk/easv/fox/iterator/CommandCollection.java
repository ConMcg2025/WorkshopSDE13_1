package dk.easv.fox.iterator;

import dk.easv.fox.command.ICommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterator pattern — CommandCollection is Iterable so the invoker can
 * loop through commands without knowing about the underlying List.
 */
public class CommandCollection implements Iterable<ICommand> {

    private final List<ICommand> commands = new ArrayList<>();

    public void addCommand(ICommand command) {
        commands.add(command);
    }

    /** True if any queued command has the given command string (e.g. "wr freq"). */
    public boolean contains(String commandString) {
        return commands.stream()
                .filter(c -> c instanceof dk.easv.fox.command.FoxCommand)
                .map(c -> ((dk.easv.fox.command.FoxCommand) c).getCommand())
                .anyMatch(commandString::equals);
    }

    /** Remove any queued command with the given command string. */
    public void removeByCommand(String commandString) {
        commands.removeIf(c ->
                c instanceof dk.easv.fox.command.FoxCommand &&
                ((dk.easv.fox.command.FoxCommand) c).getCommand().equals(commandString));
    }

    public void clear() {
        commands.clear();
    }

    public int size() {
        return commands.size();
    }

    /**
     * Returns a fresh CommandIterator each time, allowing multiple passes.
     */
    @Override
    public Iterator<ICommand> iterator() {
        return new CommandIterator(commands);
    }

    // -------------------------------------------------------------------------
    // Inner Iterator implementation
    // -------------------------------------------------------------------------

    private static class CommandIterator implements Iterator<ICommand> {

        private final List<ICommand> commands;
        private int index = 0;

        CommandIterator(List<ICommand> commands) {
            this.commands = commands;
        }

        @Override
        public boolean hasNext() {
            return index < commands.size();
        }

        @Override
        public ICommand next() {
            return commands.get(index++);
        }
    }
}
