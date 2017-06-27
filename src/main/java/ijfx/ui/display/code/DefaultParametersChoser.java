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
import java.util.Hashtable;
import java.util.List;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import org.scijava.plugin.Parameter;

/**
 *
 * @author florian
 */
public class DefaultParametersChoser {
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    
    private VBox mainBox;
    
    private Hashtable<String,String> parameters = new Hashtable();
    private String fileName = "ScriptEdtirorPreferences";
    
    

    public DefaultParametersChoser() {
        mainBox = new VBox();
        for (String parameter : this.parameters.keySet()){
            mainBox.getChildren().add(new Label(parameter));
        }
        loadPreferencies();
    }
    
    public VBox createBoolean(String name){
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
