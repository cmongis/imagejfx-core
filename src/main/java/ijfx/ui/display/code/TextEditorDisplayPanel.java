 /*
    This file is part of ImageJ FX.

    ImageJ FX is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ImageJ FX is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
    
     Copyright 2015,2016 Cyril MONGIS, Michael Knop
	
 */
package ijfx.ui.display.code;

import ijfx.core.uiplugin.UiCommandService;
import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import ijfx.ui.main.ImageJFX;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.scijava.command.CommandService;
import org.scijava.console.ConsoleService;
import org.scijava.console.OutputEvent;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.ui.console.ConsolePane;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * TODO : Change to ScriptDisplayPanelFX
 *
 * @author Florian Lassale
 * @author Cyril MONGIS (contribution)
 */
@Plugin(type = FXDisplayPanel.class)
public class TextEditorDisplayPanel extends AbstractFXDisplayPanel<ScriptDisplay> implements ConsolePane<Node>{

    @Parameter
    private ScriptService scriptService;

    @Parameter
    private CommandService commandService;

    @Parameter
    private ScriptEditorPreferencies scriptEditorPreferenciesService;

    @Parameter
    private UiCommandService uiCommandSrv;
    
    @Parameter
    ConsoleService consoleService;

    private DefaultTextArea textArea;

    private BorderPane root;

    @FXML
    private ComboBox<ScriptLanguage> languageComboBox;

    @FXML
    private Button runButton;
    
    @FXML
    private SplitPane splitPane;
    
    @FXML
    private ToggleButton showConsoleButton;
    
    @FXML
    private ToggleButton hideConsoleButton;
    
    @FXML
    private ToggleButton vButton;
    
    @FXML
    private ToggleButton hButton;
    
    @FXML
    private ToggleGroup console;
    
    @FXML
    private ToggleGroup position;
    
    PublishSubject<String> consoleDispatcher = PublishSubject.create();
    
    private TextArea consoleArea;

    public TextEditorDisplayPanel() {
        super(ScriptDisplay.class);
    }

    @Override
    public void pack() {
        try {

            if (root != null) {
                return;
            }

            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("TextEditorDisplayPanel.fxml"));

            loader.setController(this);

            loader.load();

            root = loader.getRoot();

            DefaultTextArea textArea = getTextArea();

            splitPane.getItems().add(0, textArea);
            
            this.consoleArea = new TextArea();
            consoleService.addOutputListener(this);
            
            consoleDispatcher
                .observeOn(Schedulers.from(ImageJFX.getThreadPool()))
                .buffer(1000, TimeUnit.MILLISECONDS)
                
                .filter(strList->!strList.isEmpty())
                .map(strList->strList.stream().collect(Collectors.joining()))
                .subscribe(str->Platform.runLater(()->consoleArea.appendText(str)));
            
            //this.consoleArea.getStylesheets().add(this.getClass().getResource("bijou.min.css").toExternalForm());
        
            textArea.setBottomAnchor(textArea.getCodeArea(), 15d);
            textArea.setTopAnchor(textArea.getCodeArea(), 0d);
            textArea.setLeftAnchor(textArea.getCodeArea(), 0d);
            textArea.setRightAnchor(textArea.getCodeArea(), 0d);

            initLanguageComboBox();

            initCode();
        } catch (Exception e) {
            ImageJFX.getLogger().log(Level.SEVERE, "Error when creating Code panel", e);
            throw new IllegalArgumentException("Damn it Ted !");
        }
    }
    
    protected DefaultTextArea getTextArea() {
        
        if(textArea == null) {
            textArea = new DefaultTextArea(commandService.getCommands(), getDisplay().getLanguage(), (TextEditorPreferencies) scriptEditorPreferenciesService.getPreferencies());
            
        }
        return textArea;
        
    }

    protected void initLanguageComboBox() {
        languageComboBox.getItems().addAll(scriptService.getLanguages());
        languageComboBox.valueProperty().addListener(this::onLanguageChanged);
        languageComboBox.setValue(getDisplay().getLanguage());
    }

    protected void onLanguageChanged(Observable obs, ScriptLanguage oldValue, ScriptLanguage newValue) {

        getDisplay().setLanguage(newValue);
        getTextArea().initLanguage(newValue);

    }

    @FXML
    public void runScript() {
        this.consoleArea.clear();
        getDisplay().runScript();
    }
    
    
    @FXML
    public void flipConsoleVertical() {
        this.splitPane.setOrientation(Orientation.HORIZONTAL);
    }
    
    @FXML
    public void flipConsoleHorizontal() {
        this.splitPane.setOrientation(Orientation.VERTICAL);
    }
    
     @FXML
    public void showConsole() {
        if (this.splitPane.getItems().size() < 2){
            this.splitPane.getItems().add(this.consoleArea);
            initConsole();
        }
        
    }
    
     @FXML
    public void hideConsole() {
        if (this.splitPane.getItems().size() >= 2){
            this.splitPane.getItems().remove(1);
        }
        
    }
    
    public void initConsole(){
        this.consoleArea.clear();
        
    }
    
    public void initCode() {
        Platform.runLater(() -> {
            getTextArea().setText(getDisplay().get(0).getCode());
            getDisplay().textProperty().bind(this.textArea.textProperty());
            getDisplay().selectedTextProperty().bind(this.textArea.selectedTextProperty());
            getDisplay().selectionProperty().bind(this.textArea.selectionProperty());
        });

    }

    public void setCode(String code) {
        getDisplay().get(0).setCode(code);
    }

    public String getCode() {
        return getDisplay().get(0).getCode();
    }
    
    public void initPreferencies (){
        
    }

    @Override
    public void view(DisplayWindow window, ScriptDisplay display) {
        super.view(window, display);
    }

    @Override
    public Pane getUIComponent() {
        return this.root;
    }

    @Override
    public void redoLayout() {
    }

    @Override
    public void setLabel(String string) {
    }

    @Override
    public void redraw() {
        //initCode();
        this.textArea.setPreferencies((TextEditorPreferencies) scriptEditorPreferenciesService.getPreferencies());
    }

    public void changeLanguage(ScriptLanguage language) {
        this.textArea.initLanguage(language);
    }

    @EventHandler
    public void onUndoEvent(UndoEvent event) {
        this.textArea.undo();

    }

    @EventHandler
    public void onRedoEvent(RedoEvent event) {
        this.textArea.redo();

    }

    @Override
    public void append(OutputEvent event) {
        consoleDispatcher.onNext(event.getOutput()); 
    }

    @Override
    public void show() {
    }

    @Override
    public Node getComponent() {
        return consoleArea;
    }

    @Override
    public Class<Node> getComponentType() {
        return Node.class;
    }

    @Override
    public void outputOccurred(OutputEvent event) {
        append(event);
    }

}
