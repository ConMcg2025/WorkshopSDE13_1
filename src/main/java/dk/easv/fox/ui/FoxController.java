package dk.easv.fox.ui;

import dk.easv.fox.command.CommandInvoker;
import dk.easv.fox.command.FoxCommand;
import dk.easv.fox.command.ICommand;
import dk.easv.fox.model.FoxConfig;
import dk.easv.fox.model.FoxParameter;
import dk.easv.fox.repository.ApiRepository;
import dk.easv.fox.repository.IFoxRepository;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FoxController implements Initializable {

    // ── FXML fields ──────────────────────────────────────────────────────────
    @FXML private Spinner<Integer> groupIdSpinner;
    @FXML private VBox             fieldsBox;
    @FXML private Label            statusLabel;
    @FXML private Label            queueLabel;
    @FXML private TextArea         logArea;
    @FXML private Button           sendButton;
    @FXML private Button           clearButton;
    @FXML private Button           undoButton;
    @FXML private Button           redoButton;
    @FXML private Button           readButton;

    // ── Design pattern objects ────────────────────────────────────────────────
    private IFoxRepository repository;
    private CommandInvoker  invoker;

    // ── UI state ─────────────────────────────────────────────────────────────
    private final Map<FoxParameter, TextField> fieldMap = new EnumMap<>(FoxParameter.class);

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        repository = new ApiRepository();   // swap to TelnetRepository here
        invoker    = new CommandInvoker();

        groupIdSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));

        buildParameterFields();
        refreshQueueLabel();
        refreshUndoRedo();
    }

    // ── Field construction ────────────────────────────────────────────────────

    /** Dynamically build one labelled TextField per FoxParameter. */
    private void buildParameterFields() {
        for (FoxParameter param : FoxParameter.values()) {
            Label label = new Label(param.getLabel());
            label.getStyleClass().add("field-label");

            TextField field = new TextField();
            field.setPromptText(param.getPlaceholder());
            field.getStyleClass().add("fox-field");

            // Queue a command when the user leaves the field with a value
            field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (!isFocused && !field.getText().isBlank()) {
                    queueCommand(param, field.getText().trim());
                }
            });

            fieldMap.put(param, field);
            fieldsBox.getChildren().addAll(label, field);
        }
    }

    // ── Command helpers ───────────────────────────────────────────────────────

    /**
     * Build a FoxCommand with the current field value as "new" and the
     * field's existing text as "previous" (for undo), then queue it.
     */
    private void queueCommand(FoxParameter param, String newValue) {
        String previousValue = fieldMap.get(param).getText().trim();
        ICommand cmd = new FoxCommand(
                param.getCommand(), newValue, previousValue,
                param.getLabel(), repository, groupIdSpinner.getValue());
        invoker.addOrReplace(cmd, param.getCommand());
        log("Queued: " + cmd.getDescription());
        refreshQueueLabel();
    }

    // ── Button handlers ───────────────────────────────────────────────────────

    @FXML
    private void onSendConfig() {
        // Flush any field that currently has focus (focus-lost won't have fired for it)
        for (FoxParameter param : FoxParameter.values()) {
            TextField field = fieldMap.get(param);
            String text = field.getText().trim();
            // Only queue if the field has a value AND no command for this param is already queued
            if (!text.isBlank() && !invoker.isQueued(param.getCommand())) {
                queueCommand(param, text);
            }
        }

        if (invoker.size() == 0) {
            setStatus("Nothing to send — fill in at least one field.", "warn");
            return;
        }

        sendButton.setDisable(true);
        readButton.setDisable(true);
        log("─── Sending " + invoker.size() + " command(s) ───");
        setStatus("Sending…", "info");

        new Thread(() -> {
            try {
                invoker.executeAll();   // Iterator loops inside here
                Platform.runLater(() -> {
                    log("─── All commands sent successfully ───");
                    setStatus("Done! " + invoker.size() + " command(s) sent.", "ok");
                    invoker.clear();
                    refreshQueueLabel();
                    refreshUndoRedo();
                    sendButton.setDisable(false);
                    readButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    log("ERROR: " + e.getMessage());
                    setStatus("Error: " + e.getMessage(), "error");
                    sendButton.setDisable(false);
                    readButton.setDisable(false);
                });
            }
        }, "fox-sender").start();
    }

    @FXML
    private void onReadConfig() {
        int groupId = groupIdSpinner.getValue();
        readButton.setDisable(true);
        setStatus("Reading config from fox #" + groupId + "…", "info");
        log("─── Reading config for group " + groupId + " ───");

        new Thread(() -> {
            try {
                FoxConfig config = repository.getConfig(groupId);
                Platform.runLater(() -> {
                    applyConfigToFields(config);
                    log("─── Config loaded ───");
                    setStatus("Config loaded from fox #" + groupId + ".", "ok");
                    readButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    log("ERROR: " + e.getMessage());
                    setStatus("Error reading config: " + e.getMessage(), "error");
                    readButton.setDisable(false);
                });
            }
        }, "fox-reader").start();
    }

    @FXML
    private void onUndo() {
        if (!invoker.canUndo()) return;
        ICommand cmd = invoker.peekUndo();
        log("Undo: " + cmd.getDescription());

        new Thread(() -> {
            try {
                invoker.undo();
                Platform.runLater(() -> {
                    setStatus("Undone.", "ok");
                    refreshUndoRedo();
                    // Refresh fields from the API to reflect the restored state
                    onReadConfig();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    log("Undo ERROR: " + e.getMessage());
                    setStatus("Undo failed: " + e.getMessage(), "error");
                });
            }
        }, "fox-undo").start();
    }

    @FXML
    private void onRedo() {
        if (!invoker.canRedo()) return;
        ICommand cmd = invoker.peekRedo();
        log("Redo: " + cmd.getDescription());

        new Thread(() -> {
            try {
                invoker.redo();
                Platform.runLater(() -> {
                    setStatus("Redone.", "ok");
                    refreshUndoRedo();
                    onReadConfig();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    log("Redo ERROR: " + e.getMessage());
                    setStatus("Redo failed: " + e.getMessage(), "error");
                });
            }
        }, "fox-redo").start();
    }

    @FXML
    private void onClearQueue() {
        invoker.clear();
        invoker.clearHistory();
        fieldMap.values().forEach(TextField::clear);
        log("Queue and history cleared.");
        refreshQueueLabel();
        refreshUndoRedo();
        setStatus("Ready", "info");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyConfigToFields(FoxConfig config) {
        for (FoxParameter param : FoxParameter.values()) {
            String value = config.get(param);
            TextField field = fieldMap.get(param);
            field.setText(value);
            if (!value.isBlank()) {
                log("  " + param.getLabel() + " = " + value);
            }
        }
    }

    private void log(String message) {
        logArea.appendText(message + "\n");
    }

    private void refreshQueueLabel() {
        queueLabel.setText("Queued: " + invoker.size());
    }

    private void refreshUndoRedo() {
        undoButton.setDisable(!invoker.canUndo());
        redoButton.setDisable(!invoker.canRedo());

        undoButton.setText(invoker.canUndo()
                ? "↩ Undo" : "↩ Undo");
        redoButton.setText(invoker.canRedo()
                ? "↪ Redo" : "↪ Redo");
    }

    private void setStatus(String text, String styleClass) {
        statusLabel.setText(text);
        statusLabel.getStyleClass().removeAll("ok", "error", "warn", "info");
        statusLabel.getStyleClass().add(styleClass);
    }
}
