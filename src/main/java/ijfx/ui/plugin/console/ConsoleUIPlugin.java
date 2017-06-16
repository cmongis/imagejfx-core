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
package ijfx.ui.plugin.console;

import ijfx.core.uiplugin.Localization;
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiPlugin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PopOver;
import org.scijava.console.OutputEvent;
import org.scijava.plugin.Plugin;
import org.scijava.ui.console.ConsolePane;

/**
 *
 * @author cyril
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id="console-plugin",context="always",localization = Localization.TOP_RIGHT)
public class ConsoleUIPlugin implements UiPlugin, ConsolePane<Node>{

    @FXML
    TextArea textArea;
    
    @FXML
    Pane root;
    
    ToggleButton toggleButton;
    
    PopOver popOver;
    
    
    
    @Override
    public Node getUiElement() {
        return toggleButton;
    }

    @Override
    public UiPlugin init() throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("ConsoleUIPlugin.fxml"));
        
        loader.setController(this);
        
        loader.load();
        
        
        popOver = new PopOver(root);
        toggleButton = new ToggleButton("Console");
        toggleButton.selectedProperty().bind(popOver.showingProperty());
        //toggleButton.addEventFilter(MouseEvent.MOUSE_CLICKED,this::onMousePressed);
        toggleButton.addEventFilter(MouseEvent.MOUSE_PRESSED,this::onMousePressed);
        
        return this;
        
    }
    
    public void onMousePressed(MouseEvent event) {
                    popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_BOTTOM);

            
                    
            if(popOver.isShowing()) {
                popOver.hide();
            }
            else {
                popOver.show(toggleButton);
            }
        
            event.consume();
    }

    @Override
    public void append(OutputEvent event) {
        textArea.appendText("\n");
        textArea.appendText(event.getOutput());
    }

    @Override
    public void show() {

        popOver.show(toggleButton);
    }

    @Override
    public Node getComponent() {
        return textArea;
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
