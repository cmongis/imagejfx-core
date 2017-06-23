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

import ijfx.core.uicontext.UiContextService;
import ijfx.core.uiplugin.Localization;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author florian
 */
@Plugin(type = FxWidgetPlugin.class)
@Widget(id = "research function panel", localization = Localization.LEFT, context="always")
public class ScriptEditorSidePanel extends VBox{
    @Parameter
    UiContextService uiContextService;
    @Parameter
    CommandService commandService;
    
    @FXML
    TextField searchField;
    @FXML 
    ListView<String> listView;
    List<CommandInfo> entriesList;
    ObservableList<String> observableEntries;
    
    public ScriptEditorSidePanel() throws IOException {
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ijfx/ui/display/code/DefaultSidePanel.fxml")); // c'est toujour galere de trouver le bon chemin, penser a mettre le fxml dans le bon dossier comme ici
        loader.setRoot(this);       //root est la base de la page, ici anchorPane
        loader.setController(this); //definition du controlleur
        loader.load(); // generation de la fenetre.
        
        
    }
    
    public FxWidgetPlugin init() {
        
        this.entriesList = commandService.getCommands();
	this.observableEntries = FXCollections.observableArrayList();
        fillObservableList();
        //listViewFiller();
        this.listView.setItems(observableEntries);
        this.searchField.setOnKeyPressed(this::onSearch);
        //searchField.setOnKeyTyped(this::onSearch);
        this.listView.refresh();
        
	return this;
    }
    
    
    public Node getUiComponent() {
	return this;
    }
    
    public void fillObservableList(){
        for (CommandInfo command : this.entriesList){
            this.observableEntries.add(command.getClassName());
            
        }
    }
    
    public void onAction( MouseEvent event){
        
        String item = this.listView.getSelectionModel().getSelectedItem();                    
                    
    }
    
    public void onSearch(KeyEvent event){
        /*
        This function look only for the word typed before, the last letter typed is not compted
        I don't know how to do it 
        */
        String word = this.searchField.getText();
        List<String> filteredEntries = this.entriesList
              .stream()
              .filter(e -> e.getClassName().toLowerCase().contains(word.toLowerCase()))
              .map(e -> e.getClassName())
              .collect(Collectors.toList()); 
        this.observableEntries.clear();
       
        this.observableEntries.addAll(filteredEntries);
        
        this.listView.refresh();
    }
    
}
