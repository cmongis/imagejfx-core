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

import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.scijava.command.CommandService;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.prefs.PrefService;
import org.scijava.script.ScriptLanguage;
import org.scijava.script.ScriptService;
import org.scijava.ui.viewer.DisplayWindow;

/**
 * TODO : Change to ScriptDisplayPanelFX
 * @author florian
 */
@Plugin(type = FXDisplayPanel.class)
public class TextEditorDisplayPanel extends AbstractFXDisplayPanel<ScriptDisplay> {
    @Parameter
    Scene scene;
    @Parameter
    ScriptService scriptService;
    @Parameter
    CommandService commandService;
    @Parameter
    ScriptEditorPreferenciesService scriptEditorPreferenciesService;
    
    BorderPane root;
    ScriptDisplay display;
    DefaultTextArea textArea;
    MenuButton languageButton;
    Button runButton;
        
    public TextEditorDisplayPanel() {
        super(ScriptDisplay.class);
    }

    @Override
    public void pack() {
        
        this.root = new BorderPane();
        this.textArea = new DefaultTextArea();
        this.root.setCenter(this.textArea);
        textArea.setBottomAnchor(this.textArea.getCodeArea(), 15d);
        textArea.setTopAnchor(this.textArea.getCodeArea(), 0d);
        textArea.setLeftAnchor(this.textArea.getCodeArea(), 0d);
        textArea.setRightAnchor(this.textArea.getCodeArea(), 0d);
        
        this.root.setPadding(Insets.EMPTY);
        this.languageButton = createLanguageButton(display.getLanguage().toString());
        this.runButton = createRunButton();
        
        this.root.setBottom(new HBox(this.runButton,this.languageButton));
        this.languageButton.setFont(new Font(12));
                
        this.textArea.setAutocompletion(commandService.getCommands());
        DefaultParametersChoser parametersChoser = new DefaultParametersChoser();
        this.textArea.setPreferencies(scriptEditorPreferenciesService.getPreferencies());
        changeLanguage(display.getLanguage());
        initCode();        
        
    }
    
    public MenuButton createLanguageButton(String name){
        
        MenuButton mb = new MenuButton(name);
        Field[] languages = ScriptLanguage.class.getDeclaredFields();
        
        for (ScriptLanguage language : scriptService.getInstances()){
            MenuItem mi = new MenuItem(language.toString());
            mi.setOnAction((event) -> {
                changeLanguage(language);
                initCode();
                this.languageButton.setText(language.toString());
            });
            mb.getItems().add(mi);
        }
        return mb;
    }
    
    public Button createRunButton(){
        Button rb = new Button("Run script");
        rb.setOnAction((event) -> {
                display.runScript();
            });
        return rb;
    }
    
   
    public void initCode(){
        Platform.runLater( () ->{
            this.textArea.setText(display.get(0).getCode());
            display.textProperty().bind(this.textArea.textProperty());
            display.selectedTextProperty().bind(this.textArea.selectedTextProperty());
            display.selectionProperty().bind(this.textArea.selectionProperty());
        });
        
    }
    
    public void setCode(String code) {
        display.get(0).setCode(code);
    }

    public String getCode() {
        return display.get(0).getCode();
    } 
    
    public VBox createSideBar(){
        VBox vBox = new VBox();
        
        
        return vBox;
    }
    
    public void setPreferencies(){
        
    }
    
    @Override
    public void view(DisplayWindow window, ScriptDisplay display){
        this.display = display;

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
        initCode();
    }
    
    public void changeLanguage(ScriptLanguage language){
        this.textArea.initLanguage(language);
    }

    @EventHandler
    public void onUndoEvent(UndoEvent event){
        this.textArea.undo();

    }
    @EventHandler
    public void onRedoEvent(RedoEvent event){
        this.textArea.redo();

    }
    
}
