// Copyright (c) 2017, Danilo Ferreira, João de Oliveira and Lucas Alves.
// All rights reserved.
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

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.application.Preloader.ProgressNotification;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
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
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import mes.io.Document;
import mes.io.Document.CommandLineData;
import mes.io.File;
import mes.io.Preferences;
import mes.lang.ExceptionContent;
import mes.lang.FunctionLiteralSymbol;
import mes.lang.IdentifierLiteralSymbol;
import mes.lang.Interpreter;
import mes.lang.LiteralSymbol;
import mes.lang.MathUtils;
import mes.lang.Statement;
import mes.lang.Symbol.SymbolType;
import mes.lang.SymbolTable;

public class MainWindow extends javafx.application.Application {
    private final int width;
    private final int height;
    private final int minimumWidth;
    private final int minimumHeight;

    private final String fileExtensionName;
    private final String fileExtension;

    private final String[] icons;
    private final String confirmationIcon;
    private final String errorIcon;
    private final String exceptionIcon;
    private final String informationIcon;
    private final String variableIcon;
    private final String functionIcon;

    private final String autocompleteSeparators;

    private Interpreter interpreter;

    private Stage primaryStage;
    FXRobot robot;

    private File file;
    private SimpleBooleanProperty fileWasSavedProperty;

    private ObservableList<CommandLine> commandLines;

    private SimpleDoubleProperty scrollPositionProperty;

    private SimpleBooleanProperty disableMenuItemProperty;
    private SimpleBooleanProperty primaryStageBlockedProperty;
    private SimpleBooleanProperty messageVisibleProperty;
    private SimpleBooleanProperty enableTypeCheckingProperty;
    private SimpleBooleanProperty enableAutocompleteProperty;

    private SimpleIntegerProperty lineNumberProperty;
    private SimpleIntegerProperty columnNumberProperty;

    private static PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static PseudoClass TYPECHECK_PSEUDO_CLASS = PseudoClass.getPseudoClass("type-checked");
    private static PseudoClass FILLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("filled");

    private class CommandLine extends TextField {
        private boolean error;

        private javafx.scene.control.ContextMenu defaultContextMenu;
        private ContextMenu customContextMenu;

        TypeCheckPopup typeCheckPopup;
        AutocompletePopup autocompletePopup;

        public CommandLine() {
            this("", false);
        }

        public CommandLine(boolean error) {
            this("", error);
        }

        public CommandLine(String text, boolean error) {
            super(text);
            this.error = error;

            defaultContextMenu = getContextMenu();
            customContextMenu = new ContextMenu(this);

            typeCheckPopup = new TypeCheckPopup(this);
            autocompletePopup = new AutocompletePopup(this);

            getStyleClass().setAll("command-line");

            setOnKeyPressed(MainWindow.this::editCommandLineEvent);
            setOnKeyTyped(MainWindow.this::typedCharacterEvent);
            setOnMouseClicked(actionEvent -> focusCommandLine(this));
            setOnMouseEntered(this::showTypeCheckEvent);
            setOnMouseExited(this::hideTypeCheckEvent);

            lengthProperty().addListener(this::updateErrorPseudoClassListener);
            widthProperty().addListener(this::updateErrorPseudoClassListener);
            editableProperty().addListener(this::editableListener);
            caretPositionProperty().addListener(this::caretPositionListener);
            focusedProperty().addListener(this::focusedListener);
            selectionProperty().addListener(this::selectionListener);
            textProperty().addListener(this::textListener);

            if (error) {
                setEditable(false);
                pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            }
        }

        public TypeCheckPopup getTypeCheckPopup() {
            return typeCheckPopup;
        }

        public AutocompletePopup getAutocompletePopup() {
            return autocompletePopup;
        }

        public boolean isError() {
            return error;
        }

        private void updateErrorPseudoClassListener(ObservableValue<? extends Number> observable,
                Number previousValue, Number currentValue) {
            pseudoClassStateChanged(FILLED_PSEUDO_CLASS, getLength() * 7.9 > getWidth());
        }

        private void editableListener(ObservableValue<? extends Boolean> observable,
                Boolean previousValue, Boolean currentValue) {
            if (currentValue)
                setContextMenu(defaultContextMenu);
            else {
                setContextMenu(customContextMenu);

                typeCheckPopup.hide();
                pseudoClassStateChanged(TYPECHECK_PSEUDO_CLASS, false);
            }
        }

        private void caretPositionListener(ObservableValue<? extends Number> observable,
                Number previousValue, Number currentValue) {
            if (isEditable())
                columnNumberProperty.set(currentValue.intValue() + 1);
            else
                columnNumberProperty.set(1);
        }

        private void focusedListener(ObservableValue<? extends Boolean> observable,
                Boolean previousValue, Boolean currentValue) {
            if (currentValue) {
                int position = lengthProperty().get();
                Platform.runLater(() -> positionCaret(position));
            }
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
                fileWasSavedProperty.set(false);

            typeCheckPopup.hide();

            if (enableTypeCheckingProperty.get()) {
                Statement statement = interpreter.run(currentValue, true);

                if (statement.hasException()) {
                    ExceptionContent exception = statement.getException();

                    typeCheckPopup.setErrorMessage(exception.getMessage());
                    pseudoClassStateChanged(TYPECHECK_PSEUDO_CLASS, true);
                } else {
                    typeCheckPopup.setErrorMessage(null);
                    pseudoClassStateChanged(TYPECHECK_PSEUDO_CLASS, false);
                }
            }

            autocompletePopup.hide();

            if (enableAutocompleteProperty.get()) {
                if (currentValue.isEmpty()) {
                    autocompletePopup.setForced(false);
                    return;
                }

                int offset = currentValue.length() > previousValue.length() ? 1 : -1;
                int endIndex = getCaretPosition() + offset;

                Optional<Integer> maxIndex = autocompleteSeparators.chars().mapToObj(
                        c -> currentValue.lastIndexOf(c, endIndex - 1)).max(Comparator.naturalOrder());

                int beginIndex;

                try {
                    beginIndex = maxIndex.get() + 1;
                } catch (Exception exception) {
                    beginIndex = 0;
                }

                if (beginIndex >= endIndex) {
                    autocompletePopup.setForced(false);
                    return;
                }

                if (autocompletePopup.isForced())
                    beginIndex = autocompletePopup.getAnchor();

                if (beginIndex >= endIndex) {
                    autocompletePopup.setForced(false);
                    return;
                }

                try {
                    String word = currentValue.substring(beginIndex, endIndex).toLowerCase();

                    if (!word.isEmpty()) {
                        autocompletePopup.computeList(word, interpreter.getSymbolTable());
                        autocompletePopup.show(primaryStage);
                    }
                } catch (Exception exception) {
                    Application.logInformation("cannot evaluate text autocomplete.");
                }
            }
        }

        private void showTypeCheckEvent(MouseEvent mouseEvent) {
            if (isEditable() && enableTypeCheckingProperty.get()
                    && !autocompletePopup.isShowing()) {
                typeCheckPopup.show(primaryStage);
                mouseEvent.consume();
            }
        }

        private void hideTypeCheckEvent(MouseEvent mouseEvent) {
            if (isEditable()) {
                typeCheckPopup.hide();
                mouseEvent.consume();
            }
        }
    }

    private class TypeCheckPopup extends Popup {
        private CommandLine commandLine;
        private Label errorMessageLabel;

        public TypeCheckPopup(CommandLine commandLine) {
            super();

            this.commandLine = commandLine;

            errorMessageLabel = new Label();
            errorMessageLabel.setMaxWidth(minimumWidth);
            errorMessageLabel.setWrapText(true);

            Image image = new Image(exceptionIcon);
            ImageView imageView = new ImageView(image);

            HBox rootLayout = new HBox();
            rootLayout.setSpacing(10.0);
            rootLayout.setAlignment(Pos.CENTER_LEFT);
            rootLayout.getChildren().addAll(imageView, errorMessageLabel);
            rootLayout.getStyleClass().setAll("type-check-popup");

            setAutoFix(true);
            setAutoHide(true);
            setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
            getScene().setRoot(rootLayout);
        }

        public void setErrorMessage(String errorMessage) {
            errorMessageLabel.setText(errorMessage != null ? errorMessage : "");
        }

        public String getErrorMessage() {
            return errorMessageLabel.getText();
        }

        public CommandLine getCommandLine() {
            return commandLine;
        }

        public boolean hasErrorMessage() {
            return !errorMessageLabel.getText().isEmpty();
        }

        @Override
        public void show(Window window) {
            if (hasErrorMessage()) {
                Point2D offset = commandLine.localToScene(Point2D.ZERO);

                if (offset.getY() < primaryStage.getScene().getHeight() - 90)
                    setY(primaryStage.getY() + offset.getY() + 65);
                else
                    setY(primaryStage.getY() + offset.getY() - 2);

                super.show(window);

                double popupWidth = getWidth();
                double commandLineWidth = commandLine.getWidth();

                setX(primaryStage.getX() + offset.getX() + commandLineWidth - popupWidth);
            }
        }
    }

    public class AutocompleteData implements Comparable<AutocompleteData> {
        private IdentifierLiteralSymbol identifierSymbol;

        public AutocompleteData() {
            this(null);
        }

        public AutocompleteData(IdentifierLiteralSymbol identifierSymbol) {
            this.identifierSymbol = identifierSymbol;
        }

        public void setIdentifierSymbol(IdentifierLiteralSymbol identifierSymbol) {
            this.identifierSymbol = identifierSymbol;
        }

        public IdentifierLiteralSymbol getIdentifierSymbol() {
            return identifierSymbol;
        }

        public String getLabelText() {
            return identifierSymbol == null ? "No results" : getSymbolPrototype();
        }

        public String getAutocompleteText() {
            String prototype = getSymbolPrototype();
            return prototype.substring(0, prototype.indexOf(':'));
        }

        public String getSymbolName() {
            return identifierSymbol.getName();
        }

        public String getSymbolPrototype() {
            return identifierSymbol.getPrototype();
        }

        public int getArgumentCount() {
            if (isVariable())
                return 0;

            FunctionLiteralSymbol functionSymbol = (FunctionLiteralSymbol)identifierSymbol;
            return functionSymbol.getArguments().size();
        }

        public boolean isVariable() {
            return identifierSymbol.getType() == SymbolType.Variable;
        }

        @Override
        public int compareTo(AutocompleteData other) {
            return getLabelText().compareTo(other.getLabelText());
        }
    }

    private class AutocompleteItem extends ListCell<AutocompleteData> {
        public AutocompleteItem() {
            super();
            setGraphicTextGap(10.0);
            setPrefWidth(minimumWidth * 0.5);
        }

        @Override
        public void updateItem(AutocompleteData autocompleteData, boolean empty) {
            super.updateItem(autocompleteData, empty);

            if (autocompleteData == null || empty) {
                setText(null);
                setGraphic(null);
            } else if (autocompleteData.getIdentifierSymbol() == null) {
                setText(autocompleteData.getLabelText());
                setGraphic(null);
            } else {
                setText(autocompleteData.getLabelText());

                Image image = new Image(autocompleteData.isVariable()
                        ? variableIcon : functionIcon);
                ImageView imageView = new ImageView(image);

                setGraphic(imageView);
            }
        }
    }

    private class AutocompletePopup extends Popup {
        private final double rowHeight;

        private CommandLine commandLine;
        private boolean forced;
        private int anchor;

        private VBox rootLayout;
        private ListView<AutocompleteData> listView;

        public AutocompletePopup(CommandLine commandLine) {
            super();

            rowHeight = 24.0;

            this.commandLine = commandLine;
            this.forced = false;
            this.anchor = 0;

            listView = new ListView<>();
            listView.setFixedCellSize(rowHeight);
            listView.setCellFactory(item -> new AutocompleteItem());

            listView.setOnKeyPressed(this::insertTextEvent);
            listView.setOnMouseClicked(this::insertTextEvent);

            listView.getSelectionModel().selectedIndexProperty().addListener(this::selectionListener);
            listView.prefWidthProperty().bind(commandLine.widthProperty());

            rootLayout = new VBox();
            rootLayout.getChildren().add(listView);
            rootLayout.getStyleClass().setAll("auto-complete-popup");

            setAutoFix(false);
            setAutoHide(true);
            setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
            getScene().setRoot(rootLayout);
        }

        private void selectionListener(ObservableValue<? extends Number> observable,
                Number previousValue, Number currentValue) {
            if (currentValue.intValue() == -1)
                listView.getSelectionModel().select(previousValue.intValue());
        }

        private void insertTextEvent(InputEvent inputEvent) {
            if (inputEvent instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent)inputEvent;

                KeyCombination autocompleteShortcut = new KeyCodeCombination(KeyCode.SPACE,
                        KeyCombination.CONTROL_DOWN);

                KeyCombination selectAllShortcut = new KeyCodeCombination(KeyCode.A,
                        KeyCombination.CONTROL_DOWN);

                if (autocompleteShortcut.match(keyEvent)) {
                    showAutocompletePopup(commandLine);

                    keyEvent.consume();
                    return;
                } else if (selectAllShortcut.match(keyEvent)) {
                    hide();
                    commandLine.selectAll();

                    keyEvent.consume();
                    return;
                } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    hide();

                    keyEvent.consume();
                    return;
                } else if (keyEvent.getCode() != KeyCode.ENTER)
                    return;
            } else if (inputEvent instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent)inputEvent;

                if (mouseEvent.getClickCount() != 2)
                    return;
            }

            AutocompleteData autocompleteData = listView.getSelectionModel().getSelectedItem();

            if (autocompleteData.getIdentifierSymbol() != null) {
                String autocompleteText = autocompleteData.getAutocompleteText();
                String text = commandLine.getText();

                int endIndex, beginIndex;
                IndexRange selectionRange = commandLine.getSelection();

                if (selectionRange.getLength() != 0) {
                    beginIndex = selectionRange.getStart();
                    endIndex = selectionRange.getEnd();
                } else if (forced) {
                    beginIndex = anchor;
                    endIndex = commandLine.getCaretPosition();
                } else {
                    endIndex = commandLine.getCaretPosition();

                    Optional<Integer> maxIndex = autocompleteSeparators.chars().mapToObj(
                            c -> text.lastIndexOf(c, endIndex - 1)).max(Comparator.naturalOrder());

                    try {
                        beginIndex = maxIndex.get() + 1;
                    } catch (Exception exception) {
                        beginIndex = 0;
                    }
                }

                String newText = text.substring(0, beginIndex) + autocompleteText
                        + text.substring(endIndex, text.length());

                commandLine.setText(newText);

                int argumentCount = autocompleteData.getArgumentCount();

                if (!autocompleteData.isVariable() && argumentCount != 0) {
                    int selectionBegin = beginIndex + autocompleteText.indexOf('(') + 1;
                    int selectionEnd = beginIndex;

                    if (argumentCount == 1)
                        selectionEnd += autocompleteText.indexOf(')');
                    else
                        selectionEnd += autocompleteText.indexOf(',');

                    commandLine.selectRange(selectionBegin, selectionEnd);
                } else
                    commandLine.positionCaret(beginIndex + autocompleteText.length());
            }

            this.forced = false;

            hide();
            inputEvent.consume();
        }

        private boolean filterSymbols(String word, LiteralSymbol symbol) {
            IdentifierLiteralSymbol identifierSymbol = (IdentifierLiteralSymbol)symbol;
            return identifierSymbol.getName().toLowerCase().startsWith(word);
        }

        public void setForced(boolean forced) {
            this.forced = forced;
        }

        public CommandLine getCommandLine() {
            return commandLine;
        }

        public boolean isForced() {
            return forced;
        }

        public int getAnchor() {
            return anchor;
        }

        public void setList(SymbolTable symbolTable) {
            ObservableList<AutocompleteData> items = listView.getItems();
            items.clear();

            if (symbolTable.isEmpty())
                items.add(new AutocompleteData());
            else {
                symbolTable.forEach(symbol -> items.add(
                        new AutocompleteData((IdentifierLiteralSymbol)symbol)));
                items.sort(null);
            }

            listView.setPrefHeight(MathUtils.min(minimumHeight * 0.5,
                    items.size() * rowHeight + 5));

            listView.getSelectionModel().selectFirst();
            listView.scrollTo(0);
        }

        public void computeList(String word, SymbolTable symbolTable) {
            Stream<LiteralSymbol> stream = symbolTable.stream().filter(
                    symbol -> filterSymbols(word, symbol));

            setList(stream.collect(Collectors.toCollection(SymbolTable::new)));
        }

        public ObservableList<AutocompleteData> getList() {
            return listView.getItems();
        }

        public void show(Window window, boolean forced) {
            if (!this.forced)
                this.forced = forced;

            if (forced)
                anchor = commandLine.getCaretPosition();

            ObservableList<AutocompleteData> items = listView.getItems();

            if (!items.isEmpty() && items.get(0).getIdentifierSymbol() == null && !this.forced)
                return;

            Point2D offset = commandLine.localToScene(Point2D.ZERO);
            Point2D positionOnScreen = commandLine.localToScreen(Point2D.ZERO);

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

            double listHeight = listView.getPrefHeight();
            double sceneHeight = primaryStage.getScene().getHeight() - 50;

            setX(primaryStage.getX() + offset.getX());

            if ((offset.getY() > sceneHeight * 0.5 && listHeight > sceneHeight - offset.getY())
                    || listHeight > bounds.getHeight() - positionOnScreen.getY() - 30)
                setY(primaryStage.getY() + offset.getY() - listHeight + 25);
            else
                setY(primaryStage.getY() + offset.getY() + 50);

            super.show(window);
        }

        @Override
        public void show(Window window) {
            show(window, false);
        }

        @Override
        public void hide() {
            super.hide();
        }
    }

    private class MenuBar extends javafx.scene.control.MenuBar {
        private SimpleBooleanProperty statusBarVisible;

        public MenuBar() {
            super();
            statusBarVisible = new SimpleBooleanProperty(true);

            MenuItem newMenuItem = new MenuItem("New");
            newMenuItem.setOnAction(MainWindow.this::newAction);
            newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N,
                    KeyCombination.CONTROL_DOWN));

            SeparatorMenuItem separatorFileMenu1 = new SeparatorMenuItem();

            MenuItem openMenuItem = new MenuItem("Open...");
            openMenuItem.setOnAction(MainWindow.this::openAction);
            openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O,
                    KeyCombination.CONTROL_DOWN));

            MenuItem closeMenuItem = new MenuItem("Close");
            closeMenuItem.setOnAction(MainWindow.this::closeAction);

            SeparatorMenuItem separatorFileMenu2 = new SeparatorMenuItem();

            MenuItem saveMenuItem = new MenuItem("Save");
            saveMenuItem.setOnAction(MainWindow.this::saveAction);
            saveMenuItem.disableProperty().bind(fileWasSavedProperty);
            saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
                    KeyCombination.CONTROL_DOWN));

            MenuItem saveAsMenuItem = new MenuItem("Save As...");
            saveAsMenuItem.setOnAction(MainWindow.this::saveAsAction);
            saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
                    KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));

            SeparatorMenuItem separatorFileMenu3 = new SeparatorMenuItem();

            MenuItem exitMenuItem = new MenuItem("Exit");
            exitMenuItem.setOnAction(MainWindow.this::exitAction);
            exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                    KeyCombination.CONTROL_DOWN));

            Menu fileMenu = new Menu("_File");
            fileMenu.getItems().addAll(newMenuItem, separatorFileMenu1,
                    openMenuItem, closeMenuItem, separatorFileMenu2,
                    saveMenuItem, saveAsMenuItem, separatorFileMenu3,
                    exitMenuItem);

            Binding binding = Bindings.size(commandLines).lessThan(2);

            MenuItem copyMenuItem = new MenuItem("Copy");
            copyMenuItem.setOnAction(MainWindow.this::copyAction);
            copyMenuItem.disableProperty().bind(disableMenuItemProperty);
            copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C,
                    KeyCombination.CONTROL_DOWN));

            SeparatorMenuItem separatorEditMenu1 = new SeparatorMenuItem();

            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setOnAction(MainWindow.this::deleteAction);
            deleteMenuItem.disableProperty().bind(disableMenuItemProperty);
            deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

            MenuItem deleteAllMenuItem = new MenuItem("Delete All");
            deleteAllMenuItem.setOnAction(MainWindow.this::deleteAllAction);
            deleteAllMenuItem.disableProperty().bind(binding);

            SeparatorMenuItem separatorEditMenu2 = new SeparatorMenuItem();

            CheckMenuItem typeCheckingMenuItem = new CheckMenuItem("Type Checking");
            typeCheckingMenuItem.setSelected(true);
            enableTypeCheckingProperty.bindBidirectional(typeCheckingMenuItem.selectedProperty());

            CheckMenuItem autocompleteMenuItem = new CheckMenuItem("Autocomplete");
            autocompleteMenuItem.setSelected(true);
            enableAutocompleteProperty.bindBidirectional(autocompleteMenuItem.selectedProperty());

            Menu editMenu = new Menu("_Edit");
            editMenu.getItems().addAll(copyMenuItem, separatorEditMenu1,
                    deleteMenuItem, deleteAllMenuItem, separatorEditMenu2,
                    typeCheckingMenuItem, autocompleteMenuItem);

            CheckMenuItem statusBarMenuItem = new CheckMenuItem("Status Bar");
            statusBarVisible.bindBidirectional(statusBarMenuItem.selectedProperty());

            Menu viewMenu = new Menu("_View");
            viewMenu.getItems().add(statusBarMenuItem);

            MenuItem aboutMenuItem = new MenuItem("About");
            aboutMenuItem.setOnAction(MainWindow.this::showAboutDialogAction);

            Menu helpMenu = new Menu("_Help");
            helpMenu.getItems().add(aboutMenuItem);

            getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
        }

        public SimpleBooleanProperty statusBarVisibleProperty() {
            return statusBarVisible;
        }

        public void setStatusBarVisible(boolean visible) {
            statusBarVisible.set(visible);
        }

        public boolean isStatusBarVisible() {
            return statusBarVisible.get();
        }
    }

    private class StatusBar extends HBox {
        private SimpleBooleanProperty messageVisible;
        private SimpleIntegerProperty lineNumber;
        private SimpleIntegerProperty columnNumber;

        private Label messageLabel;

        public StatusBar() {
            this(null);
        }

        public StatusBar(String defaultMessage) {
            super();

            messageVisible = new SimpleBooleanProperty(true);
            lineNumber = new SimpleIntegerProperty(1);
            columnNumber = new SimpleIntegerProperty(1);

            Binding lineNumberBinding = Bindings.createStringBinding(
                    () -> "Line " + lineNumber.get(), lineNumber);
            Binding columnNumberBinding = Bindings.createStringBinding(
                    () -> "Column " + columnNumber.get(), columnNumber);

            Image image = new Image(informationIcon);
            ImageView imageView = new ImageView(image);

            messageLabel = defaultMessage != null ? new Label(defaultMessage) : new Label();

            HBox messageLayout = new HBox();
            messageLayout.setSpacing(10.0);
            messageLayout.setAlignment(Pos.CENTER_LEFT);
            messageLayout.getChildren().addAll(imageView, messageLabel);
            messageLayout.visibleProperty().bind(messageVisible);

            Label lineLabel = new Label();
            lineLabel.setMinWidth(100);
            lineLabel.textProperty().bind(lineNumberBinding);

            Label columnLabel = new Label();
            columnLabel.setMinWidth(100);
            columnLabel.textProperty().bind(columnNumberBinding);

            HBox informationLayout = new HBox();
            informationLayout.setSpacing(10.0);
            informationLayout.setAlignment(Pos.CENTER_RIGHT);
            informationLayout.getChildren().addAll(lineLabel, columnLabel);

            setSpacing(10.0);
            HBox.setHgrow(messageLayout, Priority.ALWAYS);
            getChildren().addAll(messageLayout, informationLayout);
            getStyleClass().setAll("status-bar");
        }

        public SimpleBooleanProperty messageVisibleProperty() {
            return messageVisible;
        }

        public SimpleIntegerProperty lineNumberProperty() {
            return lineNumber;
        }

        public SimpleIntegerProperty columnNumberProperty() {
            return columnNumber;
        }

        public void setMessage(String message) {
            messageLabel.setText(message);
        }

        public void setMessageVisible(boolean visible) {
            messageVisible.set(visible);
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber.set(lineNumber);
        }

        public void setColumnNumber(int columnNumber) {
            this.columnNumber.set(columnNumber);
        }

        public String getMessage() {
            return messageLabel.getText();
        }

        public boolean isMessageVisible() {
            return messageVisible.get();
        }

        public int getLineNumber() {
            return lineNumber.get();
        }

        public int getColumnNumber() {
            return columnNumber.get();
        }
    }

    private static final ButtonType SAVE_BUTTON = new ButtonType("Save",
            ButtonData.YES);
    private static final ButtonType DISCARD_BUTTON = new ButtonType("Discard",
            ButtonData.NO);
    private static final ButtonType CANCEL_BUTTON = new ButtonType("Cancel",
            ButtonData.CANCEL_CLOSE);

    private class Dialog extends Alert {
        public Dialog(AlertType type) {
            super(type);

            setResizable(false);
            initOwner(primaryStage);
            initModality(Modality.APPLICATION_MODAL);

            showingProperty().addListener(MainWindow.this::parentStageShowingListener);
        }
    }

    private class FileSaveDialog extends Dialog {
        public FileSaveDialog() {
            super(AlertType.CONFIRMATION);

            setTitle("Save");
            setHeaderText("Do you want to save changes to document?");

            Image image = new Image(confirmationIcon);
            ImageView imageView = new ImageView(image);

            setGraphic(imageView);

            ButtonType saveButton = SAVE_BUTTON;
            ButtonType discardButton = DISCARD_BUTTON;
            ButtonType cancelButton = CANCEL_BUTTON;

            getButtonTypes().setAll(saveButton, discardButton, cancelButton);
        }
    }

    private class FileErrorDialog extends Dialog {
        public FileErrorDialog(String message) {
            super(Alert.AlertType.ERROR);

            setTitle("File Error");
            setHeaderText(message);

            Image image = new Image(errorIcon);
            ImageView imageView = new ImageView(image);

            setGraphic(imageView);
            getButtonTypes().setAll(ButtonType.OK);
        }
    }

    private class AboutDialog extends Dialog {
        public AboutDialog() {
            super(Alert.AlertType.INFORMATION);

            Image image = new Image(icons[2]);
            ImageView imageView = new ImageView(image);

            Text titleText = new Text(Application.fullName + ' ');
            titleText.getStyleClass().setAll("title-text");

            Text versionText = new Text(Application.version);
            Text licenseText = new Text(Application.name + " has been licensed under the ");

            Hyperlink licenseLink = new Hyperlink(Application.license);
            licenseLink.setOnAction(actionEvent
                    -> getHostServices().showDocument(Application.licenseLink));

            Text licenseEndLineText = new Text('.' + System.lineSeparator());
            Text copyrightText = new Text(Application.copyright);

            TextFlow titleTextFlow = new TextFlow(titleText, versionText);
            TextFlow descriptionTextFlow = new TextFlow(licenseText, licenseLink,
                    licenseEndLineText, copyrightText);

            VBox boxLayout = new VBox();
            boxLayout.setSpacing(5.0);
            boxLayout.setAlignment(Pos.CENTER_LEFT);
            boxLayout.getChildren().addAll(titleTextFlow, descriptionTextFlow);

            HBox rootLayout = new HBox();
            rootLayout.setSpacing(10.0);
            rootLayout.setAlignment(Pos.CENTER_LEFT);
            rootLayout.getStyleClass().add("header-panel");
            rootLayout.getChildren().addAll(imageView, boxLayout);

            DialogPane dialogPanel = getDialogPane();
            dialogPanel.setHeader(rootLayout);

            setTitle("About");

            ButtonType closeButton = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
            getButtonTypes().setAll(closeButton);

            Button closeButtonLookup = (Button)dialogPanel.lookupButton(closeButton);
            closeButtonLookup.setDefaultButton(true);
        }
    }

    private class ContextMenu extends javafx.scene.control.ContextMenu {
        public ContextMenu() {
            this(null);
        }

        public ContextMenu(CommandLine commandLine) {
            super();

            MenuItem copyMenuItem = new MenuItem("Copy");
            copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C,
                    KeyCombination.CONTROL_DOWN));

            SeparatorMenuItem separator = new SeparatorMenuItem();

            MenuItem deleteMenuItem = new MenuItem("Delete");
            deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

            if (commandLine != null) {
                copyMenuItem.setOnAction(actionEvent -> copyAction(actionEvent, commandLine));
                deleteMenuItem.setOnAction(actionEvent -> deleteAction(actionEvent, commandLine));
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

            if (MathUtils.isodd(index))
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
        width = 600;
        height = 600;
        minimumWidth = 250;
        minimumHeight = 250;

        fileExtensionName = Application.name + " document";
        fileExtension = '.' + Application.name.toLowerCase();

        icons = new String[5];
        icons[0] = "images/icon_16.png";
        icons[1] = "images/icon_32.png";
        icons[2] = "images/icon_48.png";
        icons[3] = "images/icon_96.png";
        icons[4] = "images/icon_256.png";

        confirmationIcon = "images/confirmation.png";
        errorIcon = "images/error.png";
        exceptionIcon = "images/exception.png";
        informationIcon = "images/information.png";
        variableIcon = "images/variable.png";
        functionIcon = "images/function.png";

        autocompleteSeparators = " ,()";

        Locale.setDefault(Locale.US);

        interpreter = new Interpreter();

        if (!interpreter.hasDefaultSymbols())
            Application.logInformation("cannot import default symbols.");

        file = new File();
        fileWasSavedProperty = new SimpleBooleanProperty(false);

        scrollPositionProperty = new SimpleDoubleProperty(0);

        disableMenuItemProperty = new SimpleBooleanProperty(true);
        primaryStageBlockedProperty = new SimpleBooleanProperty(false);
        messageVisibleProperty = new SimpleBooleanProperty(true);
        enableTypeCheckingProperty = new SimpleBooleanProperty(true);
        enableAutocompleteProperty = new SimpleBooleanProperty(true);

        lineNumberProperty = new SimpleIntegerProperty(1);
        columnNumberProperty = new SimpleIntegerProperty(1);
    }

    private void statusBarVisibleListener(ObservableValue<? extends Boolean> observable,
            Boolean previousValue, Boolean currentValue) {
        VBox rootLayout = (VBox)primaryStage.getScene().getRoot();

        if (currentValue) {
            StatusBar statusBar = new StatusBar("Type Enter to evaluate expression.");

            statusBar.messageVisibleProperty().bind(messageVisibleProperty);
            statusBar.lineNumberProperty().bind(lineNumberProperty);
            statusBar.columnNumberProperty().bind(columnNumberProperty);

            rootLayout.getChildren().add(statusBar);
        } else
            rootLayout.getChildren().remove(rootLayout.getChildren().size() - 1);
    }

    private void centerWindowOnScreen(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX((bounds.getWidth() - stage.getWidth()) * 0.5);
        stage.setY((bounds.getHeight() - stage.getHeight()) * 0.5);
    }

    private void readFile() {
        Document document = (Document)file.read();

        if (document != null) {
            commandLines.clear();
            CommandLine commandLine = null;

            for (int i = 0; i < document.getCommandLineDataCount(); i++) {
                CommandLineData commandLineData = document.getCommandLineData(i);

                commandLine = createCommandLine(commandLineData.isError(),
                        commandLineData.getText());
                commandLine.setEditable(false);
            }

            commandLine = getCommandLineOnStack(0);
            commandLine.setEditable(true);

            fileWasSavedProperty.set(true);
        } else {
            clearCommandLines();
            file.close();

            FileErrorDialog fileFormatErrorDialog = new FileErrorDialog(
                    "The file is corrupted or in an unsupported format.");
            fileFormatErrorDialog.showAndWait();
        }
    }

    private void writeFile() {
        Document document = new Document();

        for (int i = 0; i < commandLines.size(); i++) {
            CommandLine commandLine = commandLines.get(i);
            CommandLineData commandLineData = document.new CommandLineData(
                    commandLine.isError(), commandLine.getText());

            document.addCommandLineData(commandLineData);
        }

        file.write(document);
        fileWasSavedProperty.set(true);
    }

    private boolean requestOpen() {
        String extensionPattern = '*' + fileExtension;

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                fileExtensionName + " (" + extensionPattern + ")", extensionPattern);

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
        String extensionPattern = '*' + fileExtension;

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                fileExtensionName + " (" + extensionPattern + ")", extensionPattern);

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

    private void exitApplication() {
        file.close();

        VBox rootLayout = (VBox)primaryStage.getScene().getRoot();
        MenuBar menuBar = (MenuBar)rootLayout.getChildren().get(0);

        Preferences preferences = new Preferences(enableTypeCheckingProperty.get(),
                enableAutocompleteProperty.get(), menuBar.isStatusBarVisible());

        Application.savePreferences(preferences);
        Application.exit();
    }

    private void newAction(ActionEvent actionEvent) {
        if (fileWasSavedProperty.get()) {
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
        if (fileWasSavedProperty.get())
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
        if (fileWasSavedProperty.get()) {
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
        if (!fileWasSavedProperty.get()) {
            FileSaveDialog fileSaveDialog = new FileSaveDialog();
            Optional<ButtonType> option = fileSaveDialog.showAndWait();

            if (option.isPresent())
                if (option.get() == SAVE_BUTTON) {
                    if (file.isOpen())
                        writeFile();
                    else if (requestSave())
                        exitApplication();
                } else if (option.get() == DISCARD_BUTTON)
                    exitApplication();
        } else
            exitApplication();

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

        if (MathUtils.isodd(index))
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

    private void focusScrollPanelListener(ObservableValue<? extends Boolean> observable,
            Boolean previousValue, Boolean currentValue) {
        if (currentValue)
            focusCommandLine(getCommandLineOnStack(0));
    }

    private void updateTypeCheckingListener(ObservableValue<? extends Boolean> observable,
            Boolean previousValue, Boolean currentValue) {
        CommandLine commandLine = getCommandLineOnStack(0);
        TypeCheckPopup typeCheckPopup = commandLine.getTypeCheckPopup();

        if (currentValue) {
            Statement statement = interpreter.run(commandLine.getText(), true);

            if (statement.hasException()) {
                ExceptionContent exception = statement.getException();

                typeCheckPopup.setErrorMessage(exception.getMessage());
                commandLine.pseudoClassStateChanged(TYPECHECK_PSEUDO_CLASS, true);

                return;
            }
        }

        commandLine.pseudoClassStateChanged(TYPECHECK_PSEUDO_CLASS, false);
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
        } catch (Exception exception) {
            Application.logInformation("cannot decode pressed key.");
        }

        if (character > 32 && character < 127 && !keyEvent.isShortcutDown()) {
            CommandLine currentCommandLine = getCommandLineOnStack(0);

            currentCommandLine.appendText(String.valueOf(character));
            focusCommandLine(currentCommandLine);

            scrollPositionProperty.set(1.0);

            keyEvent.consume();
        }
    }

    private void showAutocompletePopup(CommandLine commandLine) {
        if (commandLine.isEditable() && enableAutocompleteProperty.get()) {
            AutocompletePopup autocompletePopup = commandLine.getAutocompletePopup();
            autocompletePopup.hide();

            autocompletePopup.setList(interpreter.getSymbolTable());
            autocompletePopup.show(primaryStage, true);
        }
    }

    private void editCommandLineEvent(KeyEvent keyEvent) {
        CommandLine currentCommandLine = getCommandLineOnStack(0);
        CommandLine sourceCommandLine = (CommandLine)keyEvent.getSource();

        KeyCode code = keyEvent.getCode();

        KeyCombination copyShortcut = new KeyCodeCombination(KeyCode.C,
                KeyCombination.CONTROL_DOWN);
        KeyCombination autocompleteShortcut = new KeyCodeCombination(KeyCode.SPACE,
                KeyCombination.CONTROL_DOWN);

        if (sourceCommandLine != currentCommandLine && code == KeyCode.ENTER) {
            focusCommandLine(currentCommandLine);
            scrollPositionProperty.set(1.0);

            keyEvent.consume();
        } else if (autocompleteShortcut.match(keyEvent)) {
            showAutocompletePopup(sourceCommandLine);
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
            scrollPositionProperty.set(1.0);

            keyEvent.consume();
        } else if (code == KeyCode.HOME || code == KeyCode.PAGE_UP) {
            focusCommandLine(commandLines.get(0));
            scrollPositionProperty.set(0);

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

                Statement statement = interpreter.run(currentCommandLine.getText());
                CommandLine commandLine;

                if (statement.hasException()) {
                    ExceptionContent exception = statement.getException();
                    commandLine = createCommandLine(true, ">> " + exception.getMessage());
                } else {
                    LiteralSymbol result = statement.getResult();
                    String output;

                    if (result.getType() == SymbolType.Number)
                        output = String.valueOf(result.getDoubleValue());
                    else {
                        IdentifierLiteralSymbol identifierResult = (IdentifierLiteralSymbol)result;
                        output = identifierResult.getPrototype();
                    }

                    commandLine = createCommandLine(false, ">> " + output);
                }

                commandLine.setEditable(false);

                createCommandLine(false);
            } else
                focusCommandLine(currentCommandLine);

            scrollPositionProperty.set(1.0);

            keyEvent.consume();
        } else if (commandLines.size() > 1)
            if (sourceCommandLine != currentCommandLine) {
                if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
                    int index = commandLines.indexOf(sourceCommandLine);

                    if (keyEvent.getCode() == KeyCode.BACK_SPACE)
                        index -= 2;

                    if (MathUtils.isodd(index))
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
        if (change.next() && (change.getAddedSize() != 0 || change.getRemovedSize() != 0)) {
            CommandLine commandLine = (CommandLine)primaryStage.getScene().getFocusOwner();

            lineNumberProperty.set(commandLines.indexOf(commandLine) + 1);
            fileWasSavedProperty.set(false);
        }
    }

    private void sceneCommandLineListener(ObservableValue<? extends Node> observable,
            Node previousValue, Node currentValue) {
        if (currentValue instanceof CommandLine) {
            boolean lastCommandLine = currentValue.equals(getCommandLineOnStack(0));

            messageVisibleProperty.set(lastCommandLine);
            lineNumberProperty.set(commandLines.indexOf(currentValue) + 1);
            disableMenuItemProperty.set(lastCommandLine);
        } else
            disableMenuItemProperty.set(true);
    }

    private void parentStageShowingListener(ObservableValue<? extends Boolean> observable,
            Boolean previousValue, Boolean currentValue) {
        primaryStageBlockedProperty.set(currentValue);
    }

    private void openFile(File newFile) {
        if (newFile.isOpen())
            if (fileWasSavedProperty.get()) {
                file.close();
                file = newFile;

                readFile();
            } else {
                FileSaveDialog fileSaveDialog = new FileSaveDialog();
                Optional<ButtonType> option = fileSaveDialog.showAndWait();

                if (option.isPresent()) {
                    if (option.get() == SAVE_BUTTON)
                        if (file.isOpen())
                            writeFile();
                        else if (!requestSave())
                            return;

                    if (option.get() != CANCEL_BUTTON) {
                        file.close();
                        file = newFile;

                        readFile();
                    }
                }
            }
        else {
            FileErrorDialog fileAccessErrorDialog = new FileErrorDialog(
                    "Cannot access file already in use or corrupted.");
            fileAccessErrorDialog.showAndWait();
        }
    }

    private void validateDraggedFileEvent(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();

        if (dragEvent.getGestureSource() != dragEvent.getTarget()
                && !primaryStageBlockedProperty.get() && dragboard.hasFiles()) {
            List<java.io.File> files = dragboard.getFiles();
            String filepath = files.get(0).getPath();

            if (files.size() == 1 && filepath.endsWith(fileExtension))
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

        Platform.runLater(() -> openFile(temporaryFile));

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
                -> new Insets(0, 0, heightProperty.doubleValue() * 0.3, 0), heightProperty);

        boxLayout.paddingProperty().bind(binding);

        MenuBar menuBar = new MenuBar();

        ScrollPane scrollPanel = new ScrollPane();
        scrollPanel.setContent(boxLayout);
        scrollPanel.setContextMenu(new ContextMenu());
        scrollPanel.setFitToWidth(true);
        scrollPanel.setOnDragOver(this::validateDraggedFileEvent);
        scrollPanel.setOnDragDropped(this::openDroppedFileEvent);
        scrollPanel.vvalueProperty().bindBidirectional(scrollPositionProperty);
        scrollPanel.focusedProperty().addListener(this::focusScrollPanelListener);

        VBox rootLayout = new VBox();
        VBox.setVgrow(scrollPanel, Priority.ALWAYS);
        rootLayout.getChildren().addAll(menuBar, scrollPanel);

        Scene scene = new Scene(rootLayout, width, height);
        scene.getStylesheets().add(Application.styleSheet);
        scene.focusOwnerProperty().addListener(this::sceneCommandLineListener);

        robot = FXRobotFactory.createRobot(scene);

        stage.setTitle(Application.name);
        stage.setMinWidth(minimumWidth);
        stage.setMinHeight(minimumHeight);
        stage.setScene(scene);
        stage.setOnCloseRequest(this::exitAction);

        Image[] images = new Image[icons.length];

        for (int i = 0; i < icons.length; i++) {
            images[i] = new Image(icons[i]);
            stage.getIcons().add(images[i]);
        }

        menuBar.statusBarVisibleProperty().addListener(this::statusBarVisibleListener);
        menuBar.setStatusBarVisible(true);

        createCommandLine(false);

        Parameters parameters = getParameters();

        if (!parameters.getRaw().isEmpty()) {
            File temporaryFile = new File(parameters.getRaw().get(0));
            openFile(temporaryFile);
        }

        Preferences preferences = Application.loadPreferences();

        if (preferences != null) {
            enableTypeCheckingProperty.set(preferences.isEnableTypeChecking());
            enableAutocompleteProperty.set(preferences.isEnableAutocomplete());
            menuBar.setStatusBarVisible(preferences.isStatusBarVisible());
        }

        enableTypeCheckingProperty.addListener(this::updateTypeCheckingListener);
        stage.show();

        centerWindowOnScreen(stage);
        notifyPreloader(new ProgressNotification(100.0));
    }
}