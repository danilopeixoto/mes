// Copyright (c) 2017, Danilo Peixoto. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, this
//   list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright notice,
//   this list of conditions and the following disclaimer in the documentation
//   and/or other materials provided with the distribution.
//
// * Neither the name of the copyright holder nor the names of its
//   contributors may be used to endorse or promote products derived from
//   this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package mes.ui;

import mes.io.File;
import mes.io.FileContent;
import mes.io.FileContent.CommandLineData;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javafx.application.Platform;
import javafx.application.Preloader.ProgressNotification;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;

public class MainWindow extends javafx.application.Application {
    private final String applicationName;
    private final String fullApplicationName;
    private final String copyright;

    private final String extension;

    private final int width;
    private final int height;
    private final int minimumWidth;
    private final int minimumHeight;

    private final String stylesheet;
    private final String[] icons;
    private final String confirmationIcon;
    private final String errorIcon;

    private Stage primaryStage;
    FXRobot robot;

    private File file;
    private SimpleBooleanProperty saveProperty;

    private ObservableList<CommandLine> commandLines;
    private SimpleDoubleProperty scrollProperty;
    private SimpleBooleanProperty disableProperty;
    private SimpleBooleanProperty primaryStageBlockedProperty;

    private static PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static PseudoClass FILLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("filled");

    private class CommandLine extends TextField {
        private boolean error;

        private javafx.scene.control.ContextMenu defaultContextMenu;
        private ContextMenu customContextMenu;

        public CommandLine() {
            super();
            initialize(false);
        }

        public CommandLine(boolean error) {
            super();
            initialize(error);
        }

        public CommandLine(String text, boolean error) {
            super(text);
            initialize(error);
        }

        public boolean isError() {
            return error;
        }

        private void initialize(boolean error) {
            this.error = error;

            defaultContextMenu = getContextMenu();
            customContextMenu = new ContextMenu(this);

            getStyleClass().clear();
            getStyleClass().add("command-line");

            setOnKeyPressed(MainWindow.this::editCommandLineEvent);
            setOnKeyTyped(MainWindow.this::typedCharacterEvent);

            lengthProperty().addListener(this::updateErrorPseudoClassListener);
            widthProperty().addListener(this::updateErrorPseudoClassListener);
            editableProperty().addListener(this::editableListener);
            focusedProperty().addListener((observable, previousValue, currentValue)
                    -> focusCommandLineListener(observable, previousValue, currentValue, this));
            selectionProperty().addListener(this::selectionListener);
            textProperty().addListener(this::textListener);

            if (error) {
                setEditable(false);
                pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            }
        }

        private void updateErrorPseudoClassListener(ObservableValue<? extends Number> observable,
                Number previousValue, Number currentValue) {
            pseudoClassStateChanged(FILLED_PSEUDO_CLASS, getLength() * 8.5 > getWidth());
        }

        private void editableListener(ObservableValue<? extends Boolean> observable,
                Boolean previousValue, Boolean currentValue) {
            if (currentValue)
                setContextMenu(defaultContextMenu);
            else
                setContextMenu(customContextMenu);
        }

        private void selectionListener(ObservableValue<? extends IndexRange> observable,
                IndexRange previousValue, IndexRange currentValue) {
            if (isEditable())
                return;

            if (currentValue.getLength() != 0)
                setContextMenu(defaultContextMenu);
            else
                setContextMenu(customContextMenu);
        }

        private void textListener(ObservableValue<? extends String> observable,
                String previousValue, String currentValue) {
            if (!currentValue.equals(previousValue))
                saveProperty.set(false);
        }
    }

    private static final ButtonType SAVE_BUTTON = new ButtonType("Save",
            ButtonData.YES);
    private static final ButtonType DISCARD_BUTTON = new ButtonType("Discard",
            ButtonData.NO);
    private static final ButtonType CANCEL_BUTTON = new ButtonType("Cancel",
            ButtonData.CANCEL_CLOSE);

    private class FileSaveDialog extends Alert {
        public FileSaveDialog() {
            super(AlertType.CONFIRMATION);

            setTitle("Save");
            setResizable(false);
            setHeaderText("Do you want to save changes to document?");

            Image image = new Image(confirmationIcon);
            ImageView imageView = new ImageView(image);

            setGraphic(imageView);

            ButtonType saveButton = SAVE_BUTTON;
            ButtonType discardButton = DISCARD_BUTTON;
            ButtonType cancelButton = CANCEL_BUTTON;

            getButtonTypes().setAll(saveButton, discardButton, cancelButton);

            initOwner(primaryStage);
            initModality(Modality.APPLICATION_MODAL);

            showingProperty().addListener(MainWindow.this::parentStageShowingListener);
        }
    }

    private class FileErrorDialog extends Alert {
        public FileErrorDialog(String message) {
            super(Alert.AlertType.ERROR);

            setTitle("File Error");
            setResizable(false);
            setHeaderText(message);

            Image image = new Image(errorIcon);
            ImageView imageView = new ImageView(image);

            setGraphic(imageView);

            getButtonTypes().setAll(ButtonType.OK);

            initOwner(primaryStage);
            initModality(Modality.APPLICATION_MODAL);

            showingProperty().addListener(MainWindow.this::parentStageShowingListener);
        }
    }

    private class AboutDialog extends Alert {
        public AboutDialog() {
            super(Alert.AlertType.INFORMATION);

            Image image = new Image(icons[2]);
            ImageView imageView = new ImageView(image);

            Label titleLabel = new Label(fullApplicationName);
            titleLabel.setStyle("-fx-font-weight: bold");

            Label copyrightLabel = new Label(copyright);

            VBox boxLayout = new VBox();
            boxLayout.setSpacing(5.0);
            boxLayout.getChildren().addAll(titleLabel, copyrightLabel);

            HBox rootLayout = new HBox();
            rootLayout.setSpacing(10.0);
            rootLayout.getStyleClass().add("header-panel");
            rootLayout.getChildren().addAll(imageView, boxLayout);

            DialogPane dialogPane = getDialogPane();
            dialogPane.setHeader(rootLayout);

            setTitle("About");
            setResizable(false);

            ButtonType closeButton = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
            getButtonTypes().setAll(closeButton);

            Button closeButtonLookup = (Button)dialogPane.lookupButton(closeButton);
            closeButtonLookup.setDefaultButton(true);

            initOwner(primaryStage);
            initModality(Modality.APPLICATION_MODAL);

            showingProperty().addListener(MainWindow.this::parentStageShowingListener);
        }
    }

    private class ContextMenu extends javafx.scene.control.ContextMenu {
        public ContextMenu() {
            super();
            createMenus(null);
        }

        public ContextMenu(CommandLine commandLine) {
            super();
            createMenus(commandLine);
        }

        private void createMenus(CommandLine commandLine) {
            MenuItem copyMenuItem = new MenuItem("Copy");
            copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C,
                    KeyCombination.CONTROL_DOWN));

            SeparatorMenuItem separator = new SeparatorMenuItem();

            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

            if (commandLine != null) {
                copyMenuItem.setOnAction((actionEvent) -> copyAction(actionEvent, commandLine));
                deleteMenuItem.setOnAction((actionEvent) -> deleteAction(actionEvent, commandLine));
            } else {
                copyMenuItem.setDisable(true);
                deleteMenuItem.setDisable(true);
            }

            Binding binding = Bindings.size(commandLines).lessThan(2);

            MenuItem deleteAllMenuItem = new MenuItem("Delete All");
            deleteAllMenuItem.setOnAction(this::deleteAllAction);
            deleteAllMenuItem.disableProperty().bind(binding);

            getItems().addAll(copyMenuItem, separator, deleteMenuItem, deleteAllMenuItem);
        }

        private void copyAction(ActionEvent actionEvent, CommandLine commandLine) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();

            clipboardContent.putString(commandLine.getText());
            clipboard.setContent(clipboardContent);

            actionEvent.consume();
        }

        private void deleteAction(ActionEvent actionEvent, CommandLine commandLine) {
            int index = commandLines.indexOf(commandLine);

            if (index % 2 != 0)
                index--;

            commandLines.remove(index);
            commandLines.remove(index);

            focusCommandLine(commandLines.get(index));

            actionEvent.consume();
        }

        private void deleteAllAction(ActionEvent actionEvent) {
            clearCommandLines();
            actionEvent.consume();
        }
    }

    public MainWindow() {
        applicationName = "MES";
        fullApplicationName = "Mathematical Expression Solver";
        copyright = "Copyright © 2017, Danilo Peixoto. All rights reserved.";

        extension = "*.mes";

        width = 600;
        height = 600;
        minimumWidth = 250;
        minimumHeight = 250;

        stylesheet = "styles/general.css";

        icons = new String[4];
        icons[0] = "images/icon_16.png";
        icons[1] = "images/icon_32.png";
        icons[2] = "images/icon_48.png";
        icons[3] = "images/icon_256.png";

        confirmationIcon = "images/confirmation_icon.png";
        errorIcon = "images/error_icon.png";

        Locale.setDefault(Locale.US);

        file = new File();
        saveProperty = new SimpleBooleanProperty(false);

        scrollProperty = new SimpleDoubleProperty(0);
        disableProperty = new SimpleBooleanProperty(true);
        primaryStageBlockedProperty = new SimpleBooleanProperty(false);
    }

    private void createMenuBar(VBox rootLayout) {
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setOnAction(this::newAction);
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N,
                KeyCombination.CONTROL_DOWN));

        SeparatorMenuItem separatorFileMenu1 = new SeparatorMenuItem();

        MenuItem openMenuItem = new MenuItem("Open...");
        openMenuItem.setOnAction(this::openAction);
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O,
                KeyCombination.CONTROL_DOWN));

        MenuItem closeMenuItem = new MenuItem("Close");
        closeMenuItem.setOnAction(this::closeAction);

        SeparatorMenuItem separatorFileMenu2 = new SeparatorMenuItem();

        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setOnAction(this::saveAction);
        saveMenuItem.disableProperty().bind(saveProperty);
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN));

        MenuItem saveAsMenuItem = new MenuItem("Save As...");
        saveAsMenuItem.setOnAction(this::saveAsAction);
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
                KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));

        SeparatorMenuItem separatorFileMenu3 = new SeparatorMenuItem();

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(this::exitAction);
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.CONTROL_DOWN));

        Menu fileMenu = new Menu("_File");
        fileMenu.getItems().addAll(newMenuItem, separatorFileMenu1,
                openMenuItem, closeMenuItem, separatorFileMenu2,
                saveMenuItem, saveAsMenuItem, separatorFileMenu3,
                exitMenuItem);

        Binding binding = Bindings.size(commandLines).lessThan(2);

        MenuItem copyMenuItem = new MenuItem("Copy");
        copyMenuItem.setOnAction(this::copyAction);
        copyMenuItem.disableProperty().bind(disableProperty);
        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C,
                KeyCombination.CONTROL_DOWN));

        SeparatorMenuItem separatorEditMenu = new SeparatorMenuItem();

        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(this::deleteAction);
        deleteMenuItem.disableProperty().bind(disableProperty);
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        MenuItem deleteAllMenuItem = new MenuItem("Delete All");
        deleteAllMenuItem.setOnAction(this::deleteAllAction);
        deleteAllMenuItem.disableProperty().bind(binding);

        Menu editMenu = new Menu("_Edit");
        editMenu.getItems().addAll(copyMenuItem, separatorEditMenu,
                deleteMenuItem, deleteAllMenuItem);

        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setOnAction(this::showAboutDialogAction);

        Menu helpMenu = new Menu("_Help");
        helpMenu.getItems().add(aboutMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        rootLayout.getChildren().add(menuBar);
    }

    private void centerWindowOnScreen(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
    }

    private void readFile() {
        FileContent content = file.read();

        if (content != null) {
            commandLines.clear();
            CommandLine commandLine = null;

            for (int i = 0; i < content.getCommandLineDataCount(); i++) {
                CommandLineData commandLineData = content.getCommandLineData(i);

                commandLine = createCommandLine(commandLineData.isError(),
                        commandLineData.getText());
                commandLine.setEditable(false);
            }

            commandLine = getCommandLineOnStack(0);
            commandLine.setEditable(true);

            saveProperty.set(true);
        } else {
            clearCommandLines();
            file.close();

            FileErrorDialog fileFormatErrorDialog = new FileErrorDialog(
                    "The file is corrupted or in an unsupported format.");
            fileFormatErrorDialog.showAndWait();
        }
    }

    private void writeFile() {
        FileContent content = new FileContent();

        for (int i = 0; i < commandLines.size(); i++) {
            CommandLine commandLine = commandLines.get(i);
            CommandLineData commandLineData = content.new CommandLineData(
                    commandLine.isError(), commandLine.getText());

            content.addCommandLineData(commandLineData);
        }

        file.write(content);
        saveProperty.set(true);
    }

    private boolean requestOpen() {
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                fullApplicationName + " (" + extension + ")", extension);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File temporaryFile = new File(fileChooser.showOpenDialog(primaryStage));

        if (!temporaryFile.isOpen()) {
            if (temporaryFile.hasExceptions()) {
                FileErrorDialog fileAccessErrorDialog = new FileErrorDialog(
                        "Cannot access file already in use or corrupted.");
                fileAccessErrorDialog.showAndWait();
            }

            return false;
        }

        file.close();
        file = temporaryFile;

        readFile();

        return true;
    }

    private boolean requestSave() {
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                fullApplicationName + " (" + extension + ")", extension);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File temporaryFile = new File(fileChooser.showSaveDialog(primaryStage));

        if (!temporaryFile.isOpen()) {
            if (temporaryFile.hasExceptions()) {
                FileErrorDialog fileAccessErrorDialog = new FileErrorDialog(
                        "Cannot access file already in use or corrupted.");
                fileAccessErrorDialog.showAndWait();
            }

            return false;
        }

        file.close();
        file = temporaryFile;

        writeFile();

        return true;
    }

    private void clearCommandLines() {
        commandLines.subList(0, commandLines.size() - 1).clear();

        CommandLine commandLine = commandLines.get(0);
        commandLine.clear();

        focusCommandLine(commandLine);
    }

    private void closeApplication() {
        file.close();

        Platform.exit();
        System.exit(0);
    }

    private void newAction(ActionEvent actionEvent) {
        if (saveProperty.get()) {
            clearCommandLines();
            file.close();
        } else {
            FileSaveDialog fileSaveDialog = new FileSaveDialog();
            Optional<ButtonType> option = fileSaveDialog.showAndWait();

            if (option.isPresent()) {
                if (option.get() == SAVE_BUTTON)
                    if (file.isOpen())
                        writeFile();
                    else if (!requestSave()) {
                        actionEvent.consume();
                        return;
                    }

                if (option.get() != CANCEL_BUTTON) {
                    clearCommandLines();
                    file.close();
                }
            }
        }

        actionEvent.consume();
    }

    private void openAction(ActionEvent actionEvent) {
        if (saveProperty.get())
            requestOpen();
        else {
            FileSaveDialog fileSaveDialog = new FileSaveDialog();
            Optional<ButtonType> option = fileSaveDialog.showAndWait();

            if (option.isPresent()) {
                if (option.get() == SAVE_BUTTON)
                    if (file.isOpen())
                        writeFile();
                    else if (!requestSave()) {
                        actionEvent.consume();
                        return;
                    }

                if (option.get() != CANCEL_BUTTON)
                    requestOpen();
            }
        }

        actionEvent.consume();
    }

    private void closeAction(ActionEvent actionEvent) {
        if (saveProperty.get()) {
            clearCommandLines();
            file.close();
        } else {
            FileSaveDialog fileSaveDialog = new FileSaveDialog();
            Optional<ButtonType> option = fileSaveDialog.showAndWait();

            if (option.isPresent()) {
                if (option.get() == SAVE_BUTTON)
                    if (file.isOpen())
                        writeFile();
                    else if (!requestSave()) {
                        actionEvent.consume();
                        return;
                    }

                if (option.get() != CANCEL_BUTTON) {
                    clearCommandLines();
                    file.close();
                }
            }
        }

        actionEvent.consume();
    }

    private void saveAction(ActionEvent actionEvent) {
        if (!file.isOpen())
            requestSave();
        else
            writeFile();

        actionEvent.consume();
    }

    private void saveAsAction(ActionEvent actionEvent) {
        requestSave();
        actionEvent.consume();
    }

    private void exitAction(Event event) {
        if (!saveProperty.get()) {
            FileSaveDialog fileSaveDialog = new FileSaveDialog();
            Optional<ButtonType> option = fileSaveDialog.showAndWait();

            if (option.isPresent())
                if (option.get() == SAVE_BUTTON) {
                    if (file.isOpen())
                        writeFile();
                    else if (requestSave())
                        closeApplication();
                } else if (option.get() == DISCARD_BUTTON)
                    closeApplication();
        } else
            closeApplication();

        event.consume();
    }

    private void copyAction(ActionEvent actionEvent) {
        Scene scene = primaryStage.getScene();
        CommandLine commandLine = (CommandLine)scene.focusOwnerProperty().get();

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();

        clipboardContent.putString(commandLine.getText());
        clipboard.setContent(clipboardContent);

        actionEvent.consume();
    }

    private void deleteAction(ActionEvent actionEvent) {
        Scene scene = primaryStage.getScene();
        CommandLine commandLine = (CommandLine)scene.focusOwnerProperty().get();

        int index = commandLines.indexOf(commandLine);

        if (index % 2 != 0)
            index--;

        commandLines.remove(index);
        commandLines.remove(index);

        focusCommandLine(commandLines.get(index));

        actionEvent.consume();
    }

    private void deleteAllAction(ActionEvent actionEvent) {
        clearCommandLines();
        actionEvent.consume();
    }

    private void showAboutDialogAction(ActionEvent actionEvent) {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.showAndWait();
    }

    private void focusCommandLine(CommandLine commandLine) {
        Platform.runLater(() -> commandLine.requestFocus());
    }

    private void focusCommandLineListener(ObservableValue<? extends Boolean> observable,
            Boolean previousValue, Boolean currentValue, CommandLine commandLine) {
        if (currentValue) {
            int position = commandLine.lengthProperty().get();

            Platform.runLater(() -> commandLine.positionCaret(position));
        }
    }

    private void focusScrollPanelListener(ObservableValue<? extends Boolean> observable,
            Boolean previousValue, Boolean currentValue) {
        if (currentValue)
            focusCommandLine(getCommandLineOnStack(0));
    }

    private CommandLine createCommandLine(boolean error, String text) {
        CommandLine commandLine = new CommandLine(error);

        if (text != null)
            commandLine.setText(text);

        commandLines.add(commandLine);

        if (!error)
            focusCommandLine(commandLine);

        return commandLine;
    }

    private CommandLine createCommandLine(boolean error) {
        return createCommandLine(error, null);
    }

    private CommandLine getCommandLineOnStack(int index) {
        return commandLines.get(commandLines.size() - index - 1);
    }

    private void typedCharacterEvent(KeyEvent keyEvent) {
        CommandLine sourceCommandLine = (CommandLine)keyEvent.getSource();

        if (sourceCommandLine.isEditable())
            return;

        char character = 0;

        try {
            byte[] unicodeBytes = keyEvent.getCharacter().getBytes("UTF8");
            String text = new String(unicodeBytes, "UTF8");

            if (text.length() == 1)
                character = text.charAt(0);
        } catch (UnsupportedEncodingException exception) {
        }

        if (character > 32 && character < 127 && !keyEvent.isShortcutDown()) {
            CommandLine currentCommandLine = getCommandLineOnStack(0);

            currentCommandLine.appendText(String.valueOf(character));
            focusCommandLine(currentCommandLine);

            scrollProperty.set(1.0);

            keyEvent.consume();
        }
    }

    private void editCommandLineEvent(KeyEvent keyEvent) {
        CommandLine currentCommandLine = getCommandLineOnStack(0);
        CommandLine sourceCommandLine = (CommandLine)keyEvent.getSource();

        KeyCode code = keyEvent.getCode();

        KeyCombination copyShortcut = new KeyCodeCombination(KeyCode.C,
                KeyCombination.CONTROL_DOWN);

        if (sourceCommandLine != currentCommandLine && code == KeyCode.ENTER) {
            focusCommandLine(currentCommandLine);
            scrollProperty.set(1.0);

            keyEvent.consume();
        } else if (code == KeyCode.DOWN) {
            robot.keyPress(KeyCode.TAB);
            robot.keyRelease(KeyCode.TAB);

            keyEvent.consume();
        } else if (code == KeyCode.UP) {
            robot.keyPress(KeyCode.SHIFT);
            robot.keyPress(KeyCode.TAB);
            robot.keyRelease(KeyCode.TAB);
            robot.keyRelease(KeyCode.SHIFT);

            keyEvent.consume();
        } else if (code == KeyCode.END || code == KeyCode.PAGE_DOWN) {
            focusCommandLine(currentCommandLine);
            scrollProperty.set(1.0);

            keyEvent.consume();
        } else if (code == KeyCode.HOME || code == KeyCode.PAGE_UP) {
            focusCommandLine(commandLines.get(0));
            scrollProperty.set(0);

            keyEvent.consume();
        } else if (sourceCommandLine != currentCommandLine
                && sourceCommandLine.getSelection().getLength() == 0
                && copyShortcut.match(keyEvent)) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();

            clipboardContent.putString(sourceCommandLine.getText());
            clipboard.setContent(clipboardContent);

            keyEvent.consume();
        } else if (code == KeyCode.ENTER) {
            if (currentCommandLine.lengthProperty().get() != 0) {
                currentCommandLine.setEditable(false);

                CommandLine commandLine = createCommandLine(false, "Error: this is a test.");
                commandLine.setEditable(false);

                createCommandLine(false);
            } else
                focusCommandLine(currentCommandLine);

            scrollProperty.set(1.0);

            keyEvent.consume();
        } else if (commandLines.size() > 1)
            if (sourceCommandLine != currentCommandLine) {
                if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
                    int index = commandLines.indexOf(sourceCommandLine);

                    if (keyEvent.getCode() == KeyCode.BACK_SPACE)
                        index -= 2;

                    if (index % 2 != 0)
                        index--;

                    if (index >= 0) {
                        commandLines.remove(index);
                        commandLines.remove(index);

                        focusCommandLine(commandLines.get(index));
                    }

                    keyEvent.consume();
                }
            } else if (code == KeyCode.BACK_SPACE
                    && currentCommandLine.caretPositionProperty().get() == 0) {
                commandLines.remove(getCommandLineOnStack(1));
                commandLines.remove(getCommandLineOnStack(1));

                focusCommandLine(currentCommandLine);

                keyEvent.consume();
            }
    }

    private void commandLinesSizeListener(ListChangeListener.Change<? extends CommandLine> change) {
        if (change.next() && (change.getAddedSize() != 0 || change.getRemovedSize() != 0))
            saveProperty.set(false);
    }

    private void sceneCommandLineListener(ObservableValue<? extends Node> observable,
            Node previousValue, Node currentValue) {
        if (currentValue instanceof CommandLine)
            disableProperty.set(currentValue.equals(getCommandLineOnStack(0)));
        else
            disableProperty.set(true);
    }

    private void parentStageShowingListener(ObservableValue<? extends Boolean> observable,
            Boolean previousValue, Boolean currentValue) {
        primaryStageBlockedProperty.set(currentValue);
    }

    private void validateDraggedFileEvent(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();

        if (dragEvent.getGestureSource() != dragEvent.getTarget()
                && !primaryStageBlockedProperty.get() && dragboard.hasFiles()) {
            List<java.io.File> files = dragboard.getFiles();
            String filepath = files.get(0).getPath();

            if (files.size() == 1 && filepath.endsWith(extension.substring(1)))
                dragEvent.acceptTransferModes(TransferMode.COPY);
        }

        dragEvent.consume();
    }

    private void openDroppedFileEvent(DragEvent dragEvent) {
        File temporaryFile = new File();

        if (dragEvent.isAccepted()) {
            Dragboard dragboard = dragEvent.getDragboard();
            temporaryFile.open(dragboard.getFiles().get(0));

            dragEvent.setDropCompleted(true);
        } else
            dragEvent.setDropCompleted(false);

        Platform.runLater(() -> {
            if (temporaryFile.isOpen())
                if (saveProperty.get()) {
                    file.close();
                    file = temporaryFile;

                    readFile();
                } else {
                    FileSaveDialog fileSaveDialog = new FileSaveDialog();
                    Optional<ButtonType> option = fileSaveDialog.showAndWait();

                    if (option.isPresent()) {
                        if (option.get() == SAVE_BUTTON)
                            if (file.isOpen())
                                writeFile();
                            else if (!requestSave()) {
                                dragEvent.consume();
                                return;
                            }

                        if (option.get() != CANCEL_BUTTON) {
                            file.close();
                            file = temporaryFile;

                            readFile();
                        }
                    }
                }
            else {
                FileErrorDialog fileAccessErrorDialog = new FileErrorDialog(
                        "Cannot access file already in use or corrupted.");
                fileAccessErrorDialog.showAndWait();
            }
        });

        dragEvent.consume();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        VBox boxLayout = new VBox();

        commandLines = (ObservableList<CommandLine>)(ObservableList<?>)boxLayout.getChildren();
        commandLines.addListener(this::commandLinesSizeListener);

        ReadOnlyDoubleProperty heightProperty = stage.heightProperty();
        Binding binding = Bindings.createObjectBinding(()
                -> new Insets(0, 0, heightProperty.doubleValue() * 0.25, 0), heightProperty);

        boxLayout.paddingProperty().bind(binding);

        ScrollPane scrollPanel = new ScrollPane();
        scrollPanel.setContent(boxLayout);
        scrollPanel.setContextMenu(new ContextMenu());
        scrollPanel.setFitToWidth(true);
        scrollPanel.setOnDragOver(this::validateDraggedFileEvent);
        scrollPanel.setOnDragDropped(this::openDroppedFileEvent);
        scrollPanel.vvalueProperty().bindBidirectional(scrollProperty);
        scrollPanel.focusedProperty().addListener(this::focusScrollPanelListener);

        VBox rootLayout = new VBox();

        createMenuBar(rootLayout);

        rootLayout.getChildren().add(scrollPanel);
        VBox.setVgrow(scrollPanel, Priority.ALWAYS);

        Scene scene = new Scene(rootLayout, width, height);
        scene.getStylesheets().add(stylesheet);
        scene.focusOwnerProperty().addListener(this::sceneCommandLineListener);

        robot = FXRobotFactory.createRobot(scene);

        stage.setTitle(applicationName);
        stage.setMinWidth(minimumWidth);
        stage.setMinHeight(minimumHeight);
        stage.setScene(scene);
        stage.setOnCloseRequest(this::exitAction);

        Image[] images = new Image[4];

        for (int i = 0; i < 4; i++) {
            images[i] = new Image(icons[i]);
            stage.getIcons().add(images[i]);
        }

        createCommandLine(false);
        stage.show();

        centerWindowOnScreen(stage);
        notifyPreloader(new ProgressNotification(100.0));
    }
}