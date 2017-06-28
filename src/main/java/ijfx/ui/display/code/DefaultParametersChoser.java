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

import ijfx.core.prefs.JsonPreferenceService;
import ijfx.core.uiplugin.UiCommand;
import ijfx.ui.activity.DisplayContainer;
import ijfx.ui.mainwindow.AbstractActivityLauncher;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author florian
 */
@Plugin(type = UiCommand.class,label = "Preferences", priority= 100,iconPath = "fa:picture_alt")

public class DefaultParametersChoser extends AbstractActivityLauncher{
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    @Parameter
    Stage stage;
    
    private VBox mainBox;
    
    private Hashtable<String,List> parameters = new Hashtable();
    private String fileName = "ScriptEdtirorPreferences";
    
    

    public DefaultParametersChoser() {
        super(DisplayContainer.class);
        
        
        mainBox = new VBox();
        for (String parameter : this.parameters.keySet()){
            createParameter(parameter);
        }
        loadPreferencies();
        
    }
    
    public Node createParameter(String key){
        Node node = null;
        
        if (this.parameters.get(key).get(0).equals("boolean")){
            boolean value = true;
            if (this.parameters.get(key).get(2).equals("true")){
                value = true;
            }
            else {
                value = false;
            }
            
            node = createBoolean(key, value);
        }
        
        if (key.equals("styleSheet")){
            
        }
        
        return node;
    }
    
    public VBox createStyleChoice(String value){
        VBox box = new VBox();
        HBox subBox = new HBox();
        
        // making a menu to chose in the existent stylesheets
        MenuButton menuButton = new MenuButton("Select theme");
        for (String style : (List<String>) this.parameters.get("styleSheet").get(1)){
            MenuItem menuItem = new MenuItem(style);
            menuItem.setOnAction((event)->{
                setParameter("styleSheet", style);
            });
        }
        subBox.getChildren().add(menuButton);
        Label label = new Label(" Or select a new css file");
        subBox.getChildren().add(label);
        
        // adding a button to select a new css file
        FileChooser fileChooser = new FileChooser();
        
        Button button = new Button("Select a new css");
        button.setOnAction((event)->{
                File file = fileChooser.showOpenDialog(stage);
                if (file != null && file.getName().matches(".*\\.css")){ 
                    setParameter("styleSheet", file.getAbsolutePath());
                }
            });
        subBox.getChildren().add(button);
        
        box.getChildren().add(subBox);
        
        return box;
    }
    
    public void setParameter(String key, String value){
        
    }
    
    public void setParameter(String key, String value, String newPossibleValue){
        
    }
    
    public VBox createBoolean(String name, boolean value){
        VBox box = new VBox();
        box.getChildren().add(0, new Label(name));
        
        
        box.getChildren().add(1, new CheckBox("Enable"));
        
        return  box;
    }
    
    public VBox createMultiChoiceBox (String name, List<String> choices, String selected){
        VBox box = new VBox();
        box.getChildren().add(0, new Label(name));
        
        HBox buttonBox = new HBox();
        ToggleGroup group = new ToggleGroup();
        for (String item : choices){
            RadioButton rb1 = new RadioButton(item);
            rb1.setToggleGroup(group);
            buttonBox.getChildren().add(rb1);
            if (item.equals(selected)) rb1.setSelected(true);
        }
        box.getChildren().add(1, buttonBox);
        
        return box;
    }
    
    public void loadPreferencies(){
        try {
            this.parameters = (Hashtable) jsonPreferenceService.loadMapFromJson(fileName, String.class, String.class);
        } catch (Exception e) {
        }
        
        
    }
    
    public void savePreferencies(){
        jsonPreferenceService.savePreference(this.parameters, this.fileName);
    }

    public Hashtable getParameters() {
        return parameters;
    }
    
    
}
