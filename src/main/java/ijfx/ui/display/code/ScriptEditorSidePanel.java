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
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiPlugin;
import ijfx.ui.widgets.ModuleInfoPane;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "research function panel", localization = Localization.RIGHT, context="script-open")
public class ScriptEditorSidePanel extends TabPane implements UiPlugin{
    @Parameter
    UiContextService uiContextService;
    @Parameter
    CommandService commandService;
    
    @FXML
    TextField searchField;
    @FXML 
    ListView<CommandInfo> listView;
    @FXML
    TabPane tabPane;
    
    private List<CommandInfo> entriesList;
    
    public ScriptEditorSidePanel() throws IOException {
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ijfx/ui/display/code/DefaultSidePanel.fxml")); // c'est toujour galere de trouver le bon chemin, penser a mettre le fxml dans le bon dossier comme ici
        loader.setRoot(this);       //root est la base de la page, ici anchorPane
        loader.setController(this); //definition du controlleur
        loader.load(); // generation de la fenetre.
        
        listView.setCellFactory(this::createCell);
        
    }
    
    private ListCell<CommandInfo> createCell (ListView<CommandInfo> commandInfo){
        return new CommandInfoListCell();
    }
    
    public UiPlugin init() {
        
        this.entriesList = commandService.getCommands();
        
        this.listView.getItems().addAll(entriesList);
        this.listView.setOnMouseClicked(this::onAction);
        this.searchField.setOnKeyPressed(this::onSearch);
        this.listView.refresh();
        
	return this;
    }
    
    
    public void onAction( MouseEvent event){
        ModuleInfoPane moduleInfoPane = new ModuleInfoPane();
        CommandInfo item = this.listView.getSelectionModel().getSelectedItem();
        moduleInfoPane.setModuleInfo(item);
        String[] className = item.getClassName().split("\\.");
        Tab newTab = new Tab(className[className.length-1]);
        
        newTab.setContent(moduleInfoPane);
        this.getTabs().add(newTab);
               
    }
    
    public void onSearch(KeyEvent event){
        /*
        This function look only for the word typed before, the last letter typed is not compted
        I don't know how to do it 
        */
        String word = this.searchField.getText();
        List<CommandInfo> filteredEntries = this.entriesList
              .stream()
              .filter(e -> e.getClassName().toLowerCase().contains(word.toLowerCase()))
              .collect(Collectors.toList()); 
        
        this.listView.getItems().clear();
        this.listView.getItems().addAll(filteredEntries);
        this.listView.refresh();
    }

    @Override
    public Node getUiElement() {
        return this;
    }
    
    private class CommandInfoListCell extends ListCell<CommandInfo> {
        /*
        Ici on definis comment la vue doit afficher les tache, 
        */
        
        VBox box = new VBox();

        public CommandInfoListCell() {
            super();
            itemProperty().addListener(this::onItemChanged);
            
        }

        private void onItemChanged(ObservableValue obs, CommandInfo oldValue, CommandInfo newValue) {
            
            
            
            if(newValue == null){
                setGraphic(null);
            }
            else {
                setGraphic(box);
                String[] text = newValue.getClassName()
                        .split("\\.");
                box.getChildren().clear();
                box.getChildren().add(new Label(text[text.length-1]));
            }
        }
        
    }
}
