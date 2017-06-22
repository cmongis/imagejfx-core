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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.scijava.command.CommandInfo;

/**
 *
 * @author florian
 */
public class DefaultSidePanel extends VBox{
    @FXML
    TextField searchField;
    @FXML 
    ListView<String> listView;
    List<CommandInfo> entriesList;
    ObservableList<String> observableEntries;
    
    public DefaultSidePanel(List<CommandInfo> entriesList) throws IOException {
        this.entriesList = entriesList;
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ijfx/ui/display/code/DefaultSidePanel.fxml")); // c'est toujour galere de trouver le bon chemin, penser a mettre le fxml dans le bon dossier comme ici
        loader.setRoot(this);       //root est la base de la page, ici anchorPane
        loader.setController(this); //definition du controlleur
        loader.load(); // generation de la fenetre.
        
        this.observableEntries = FXCollections.observableArrayList();
        fillObservableList();
        //listViewFiller();
        this.listView.setItems(observableEntries);
        this.searchField.setOnKeyPressed(this::onSearch);
        //searchField.setOnKeyTyped(this::onSearch);
        this.listView.refresh();
    }
    
    public void fillObservableList(){
        for (CommandInfo command : this.entriesList){
            this.observableEntries.add(command.getClassName());
            
        }
    }
    /*
    public void listViewFiller(){
        for (CommandInfo command : this.entriesList){
            if (listView.getItems().size() > 100) break;
            ListCell listCell = new ListCell();
            listCell.setItem(command.getClassName());
            this.listView.getItems().add(listCell);
        }
            //this.listView.setOnMouseClicked(this:: onAction);
        
    }
    */
    
    public void onAction( MouseEvent event){
        
        String item = this.listView.getSelectionModel().getSelectedItem();                    
                    
    }
    
    public void onSearch(KeyEvent event){
        String word = this.searchField.getText() + event.getText();
        final List<String> filteredEntries = this.entriesList
              .stream()
              .filter(e -> e.getClassName().toLowerCase().contains(word.toLowerCase()))
              .map(e -> e.getClassName())
              .collect(Collectors.toList()); 
        this.observableEntries.clear();
        if (!word.equals("/b")){
            
            this.observableEntries.addAll(filteredEntries);
        }
        else {
            this.observableEntries.addAll(this.observableEntries);
        }
        
        this.listView.refresh();
    }
    
}
