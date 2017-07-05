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

import ijfx.core.activity.Activity;
import ijfx.core.prefs.JsonPreferenceService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.WidgetService;

/**
 *
 * @author florian
 */

@Plugin(type = Activity.class, name = "display preferencies")
public class DefaultParametersChoser extends BorderPane implements Activity{
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    @Parameter
    ScriptEditorPreferenciesService preferenceService;
    @Parameter
    Stage stage;
    @Parameter
    WidgetService widgetService;
    
    private VBox mainBox;
    
    private TextEditorPreferencies preferencies;
    private String fileName = "ScriptEdtirorPreferences";

    public DefaultParametersChoser() {        
        
        mainBox = new VBox();
        mainBox.getChildren().add(new Label("Preferencies"));
        this.setPadding(Insets.EMPTY);
        this.setCenter(mainBox);
        Button saveButton = new Button("Save preferencies");
        saveButton.setText("Save preferencies");
        saveButton.setOnAction(this::savePreferencies);
        
        
    }
    /*
    public void createWidget (){
        InputWidget<?, Node> textWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(String.class)
                .setGetter(preferencies::getTheme)
                .setSetter(preferencies::setTheme)
                .setStyle(TextWidget.FIELD_STYLE)
        );
        InputWidget<?,Node> booleanWidget = (InputWidget<?, Node>) widgetService.create(
                new SuppliedWidgetModel<>(Boolean.class)
                .setGetter(preferencies::isAutocompletion)
                .setSetter(preferencies::setAutocompletion)
        );
        
        textWidget.refreshWidget();
        booleanWidget.refreshWidget();
        mainBox.getChildren().addAll(textWidget.getComponent(), booleanWidget.getComponent());
    }
    */
    /*
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
            node = createStyleChoice(key);
            
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
            menuButton.getItems().add(menuItem);
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
        
        CheckBox checkBox = new CheckBox("Enable");
        checkBox.setSelected(value);
        box.getChildren().add(1, checkBox);
        checkBox.setOnAction((event)->{
            String newValue;
            if (checkBox.isSelected()){
                newValue = "false";
            }
            else newValue = "true";
                setParameter(name, newValue);
            });
        
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
        //this.parameters = preferenceService.getParameters();
        
        
    }
    */
    public void savePreferencies(ActionEvent event){
        jsonPreferenceService.savePreference(this.preferencies, this.fileName);
    }

    public TextEditorPreferencies getParameters() {
        return this.preferencies;
    }

    @Override
    public Node getContent() {
        this.preferencies = preferenceService.getPreferencies();
        mainBox.getChildren().add(preferenceService.generatepreferenciesWidget());
        return this;
    }

    @Override
    public Task updateOnShow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
