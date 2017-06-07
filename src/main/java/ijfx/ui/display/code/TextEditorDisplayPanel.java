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

import ijfx.core.formats.Script;
import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.fxmisc.richtext.CodeArea;
import org.scijava.display.Display;
import org.scijava.display.TextDisplay;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptLanguage;
import org.scijava.ui.viewer.DisplayWindow;

/**
 * TODO : Change to ScriptDisplayPanelFX
 * @author florian
 */
@Plugin(type = FXDisplayPanel.class)
public class TextEditorDisplayPanel extends AbstractFXDisplayPanel<ScriptDisplay> {
    @Parameter
    Scene scene;
    TextArea root;
    //BorderPane borderPane;
    ScriptDisplay display;
    //static TextArea textArea;
    
    //static CodeArea codeArea;
    JavaBeanStringProperty codeProperty; 
    
    public TextEditorDisplayPanel() {
        super(ScriptDisplay.class);
    }

    @Override
    public void pack() {
        
        //this.root = new AnchorPane();
        //root.getStylesheets().add(getClass().getResource("/ijfx/ui/display/code/JavaRichtext.css").toExternalForm());
        if (root!=null) return;
        this.root = new TextArea();
        //this.codeArea = textArea.getCodeArea();
        
        root.setBottomAnchor(this.root.getCodeArea(), 15d);
        root.setTopAnchor(this.root.getCodeArea(), 0d);
        root.setLeftAnchor(this.root.getCodeArea(), 0d);
        root.setRightAnchor(this.root.getCodeArea(), 0d);


        //root.getChildren().add(this.textArea.getCodeArea());
        
        initCode();
       
        
    }
    
    public void initCode(){
        changeLanguage(display.getLanguage());
        this.root.setText(display.get(0).getCode());
         display.textProperty().bind(this.root.textProperty());
        display.selectedTextProperty().bind(this.root.selectedTextProperty());
        display.selectionProperty().bind(this.root.selectionProperty());
        
    }
    
    public void setCode(String code) {
        
        display.get(0).setCode(code);

    }

    public String getCode() {
        return display.get(0).getCode();
    } 
    
    @Override
    public void view(DisplayWindow window, ScriptDisplay display){
        this.display = display;
        //System.out.println("affichage : " + display.get(0));
        //this.root.initText(display);
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
        //root.setText(display.get(0).getCode());
    }
    public void changeLanguage(ScriptLanguage language){
        String path = findFileLanguage(language);
        this.root.initLanguage(path);
    }
    
    public static String findFileLanguage(ScriptLanguage language) {
       return String.format("/ijfx/ui/display/code/%s.nanorc",language.getLanguageName().toLowerCase().replace(" ", ""));
    }
    @EventHandler
    public void onUndoEvent(UndoEvent event){
        this.root.undo();

    }
    @EventHandler
    public void onRedoEvent(RedoEvent event){
        this.root.redo();

    }
}
