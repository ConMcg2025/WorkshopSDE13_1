package dk.easv.fox.command;

import dk.easv.fox.iterator.CommandCollection;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command pattern invoker.
 * Collects ICommand objects and executes them all via the iterator.
 * Maintains an undo stack and a redo stack for full undo/redo support.
 */
public class CommandInvoker {

    private final CommandCollection commandCollection = new CommandCollection();

    // Stacks for undo/redo
    private final Deque<ICommand> undoStack = new ArrayDeque<>();
    private final Deque<ICommand> redoStack = new ArrayDeque<>();

    public void addCommand(ICommand command) {
        commandCollection.addCommand(command);
    }

    /**
     * Returns true if a command with the given command string is already in the queue.
     * Used to prevent double-queuing when onSendConfig flushes focused fields.
     */
    public boolean isQueued(String commandString) {
        return commandCollection.contains(commandString);
    }

    /**
     * If a command for this command string is already queued, replace it.
     * Otherwise add it. This prevents stale values when a user edits a field twice.
     */
    public void addOrReplace(ICommand command, String commandString) {
        commandCollection.removeByCommand(commandString);
        commandCollection.addCommand(command);
    }

    /**
     * Execute all queued commands via the iterator.
     * Each successfully executed command is pushed onto the undo stack,
     * and the redo stack is cleared (a new action breaks the redo chain).
     */
    public void executeAll() {
        for (ICommand command : commandCollection) {
            command.execute();
            undoStack.push(command);
        }
        redoStack.clear();
    }

    /**
     * Undo the most recently executed command.
     * Moves it from the undo stack onto the redo stack.
     */
    public void undo() {
        if (undoStack.isEmpty()) return;
        ICommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    /**
     * Redo the most recently undone command.
     * Moves it back from the redo stack onto the undo stack.
     */
    public void redo() {
        if (redoStack.isEmpty()) return;
        ICommand command = redoStack.pop();
        command.execute();
        undoStack.push(command);
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public ICommand peekUndo() { return undoStack.peek(); }
    public ICommand peekRedo() { return redoStack.peek(); }

    public void clear() {
        commandCollection.clear();
    }

    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    public int size() {
        return commandCollection.size();
    }

    public CommandCollection getCommandCollection() {
        return commandCollection;
    }
}
