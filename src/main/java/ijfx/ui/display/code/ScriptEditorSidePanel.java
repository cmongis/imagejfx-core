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
import ijfx.ui.widgets.PluginInfoPane;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.ToggleGroupValue;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.module.ModuleInfo;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;

/**
 *
 * @author florian
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "research function panel", localization = Localization.RIGHT, context="script-open")
public class ScriptEditorSidePanel extends BorderPane implements UiPlugin{
    @Parameter
    UiContextService uiContextService;
    @Parameter
    CommandService commandService;
    @Parameter
    ScriptEditorPreferencies scriptEditorPreferencies;
    @Parameter
    Context context;
    
    @FXML
    TextField searchField;
    @FXML 
    ListView<PluginInfo> listView;
    @FXML
    TabPane tabPane;
    @FXML
    ToggleButton methodsButton;
    @FXML
    ToggleButton servicesButton;
    
    ToggleGroupValue<Panel> currentPanel;
    
    private List<CommandInfo> methodsList;
    private List<PluginInfo<?>> servicesList;
    private Panel panelDisplayed = Panel.METHODS;
    
    
    
    public ScriptEditorSidePanel() throws IOException {
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ijfx/ui/display/code/DefaultSidePanel.fxml")); // c'est toujour galere de trouver le bon chemin, penser a mettre le fxml dans le bon dossier comme ici
        loader.setRoot(this);       //root est la base de la page, ici anchorPane
        loader.setController(this); //definition du controlleur
        loader.load(); // generation de la fenetre.
        
        
        currentPanel = new ToggleGroupValue<>();
        currentPanel.add(methodsButton, Panel.METHODS);
        currentPanel.add(servicesButton,Panel.SERVICES);
        
        //initButtons();
        currentPanel.valueProperty().addListener((obs,oldValue,newValue)->refresh(newValue));
        currentPanel.setValue(Panel.METHODS);
        listView.setCellFactory(this::createCell);        
        
    }
    
    private ListCell<PluginInfo> createCell (ListView<PluginInfo> commandInfo){
        return new CommandInfoListCell();
    }
    
    public UiPlugin init() {
        
        this.listView.getItems().clear();
        this.methodsList = commandService.getCommands();
        this.servicesList = context
                .getPluginIndex()
                .getAll()
                .stream()
                .filter(info->info.getPluginType().getSimpleName().contains("Service"))
                .collect(Collectors.toList());// List<PluginInfo<?>>
        
        this.listView.refresh();
        return this;
    }
    public void refresh(Panel panelDisplayed) {
        if (panelDisplayed.equals(Panel.METHODS)){
            this.listView.getItems().addAll(methodsList);
            this.listView.setOnMouseClicked(this::onActionMethod);
            this.searchField.setOnKeyPressed(this::onSearchMethod);
        }
        else if (panelDisplayed.equals(Panel.SERVICES)){
            this.listView.getItems().addAll(servicesList);
            this.listView.setOnMouseClicked(this::onActionService);
            this.searchField.setOnKeyPressed(this::onSearchService);
        }
        
        
       
        
    }
    
    /*
    public void initButtons(){
        methodsButton.setOnAction((event) -> {
                switchPanel(Panel.METHODS);
            });
        servicesButton.setOnAction((event) -> {
                switchPanel(Panel.SERVICES);
            });
    }
    */
    
    public void onActionMethod( MouseEvent event){
        ModuleInfoPane moduleInfoPane = new ModuleInfoPane();
        PluginInfo item = this.listView.getSelectionModel().getSelectedItem();
        moduleInfoPane.setModuleInfo((ModuleInfo) item);                           // pas le bon typemade
        String[] className = item.getClassName().split("\\.");
        Tab newTab = new Tab(className[className.length-1]);
        
        newTab.setContent(moduleInfoPane);
        this.tabPane.getTabs().add(newTab);
               
    }
    
    public void onActionService( MouseEvent event){
        PluginInfoPane moduleInfoPane = new PluginInfoPane();
        PluginInfo item = this.listView.getSelectionModel().getSelectedItem();
        moduleInfoPane.setModuleInfo(item);                           // pas le bon typemade
        String[] className = item.getClassName().split("\\.");
        Tab newTab = new Tab(className[className.length-1]);
        
        newTab.setContent(moduleInfoPane);
        this.tabPane.getTabs().add(newTab);
               
    }
    
    public void onSearchMethod(KeyEvent event){
        /*
        This function look only for the word typed before, the last letter typed is not compted
        I don't know how to do it 
        */
        String word = this.searchField.getText();
        List<PluginInfo<Command>> filteredEntries = this.methodsList
              .stream()
              .filter(e -> e.getClassName().toLowerCase().contains(word.toLowerCase()))
              .collect(Collectors.toList()); 
        
        this.listView.getItems().clear();
        this.listView.getItems().addAll(filteredEntries);
        this.listView.refresh();
    }
    
    public void onSearchService(KeyEvent event){
        /*
        This function look only for the word typed before, the last letter typed is not compted
        I don't know how to do it 
        */
        String word = this.searchField.getText();
        List<PluginInfo> filteredEntries = this.servicesList
              .stream()
              .filter(e -> e.getClassName().toLowerCase().contains(word.toLowerCase()))
              .collect(Collectors.toList()); 
        
        this.listView.getItems().clear();
        this.listView.getItems().addAll(filteredEntries);
        this.listView.refresh();
    }

    @Override
    public Node getUiElement() {
        TextEditorPreferencies preferencies = (TextEditorPreferencies) scriptEditorPreferencies.getPreferencies();
        if (preferencies.isSidePanel()){
            return this;
        }
        else return null;
    }
    
    public void switchPanel(Panel panel){
        this.panelDisplayed = panel;
        init();
    }
    
    private class CommandInfoListCell extends ListCell<PluginInfo> {
        /*
        Ici on definis comment la vue doit afficher les tache, 
        */
        
        VBox box = new VBox();

        public CommandInfoListCell() {
            super();
            itemProperty().addListener(this::onItemChanged);
            
        }

        private void onItemChanged(ObservableValue obs, PluginInfo oldValue, PluginInfo newValue) {
            
            
            
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
    
    
    
    public enum Panel {
        METHODS ("methods"),
        SERVICES ("services");
        
        private String name = "";

        Panel(String name){
            this.name = name;

        }

        public String toString(){
            return name;

        }
    }
}
