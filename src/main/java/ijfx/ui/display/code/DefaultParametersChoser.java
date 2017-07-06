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
import ijfx.core.activity.ActivityService;
import ijfx.core.prefs.JsonPreferenceService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.viewer.DisplayPanel;
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
    ScriptEditorPreferencies preferenceService;
    @Parameter
    Stage stage;
    @Parameter
    WidgetService widgetService;
    @Parameter
    ActivityService activityService;
    
    private VBox mainBox;
    
    private TextEditorPreferencies preferencies;
    private String fileName = "ScriptEdtirorPreferences";

    public DefaultParametersChoser() {        
        
        mainBox = new VBox();
        mainBox.getChildren().add(new Label("Preferencies"));
        mainBox.setPadding(new Insets(20, 20, 20, 20));
        this.setPadding(Insets.EMPTY);
        this.setCenter(mainBox);
        Button saveButton = new Button("Save preferencies");
        saveButton.setAlignment(Pos.CENTER_RIGHT);
        saveButton.setText("Save preferencies");
        saveButton.setOnAction(this::savePreferencies);
        Button closeButton = new Button("close");
        closeButton.setAlignment(Pos.CENTER_RIGHT);
        closeButton.setText("Close");
        closeButton.setOnAction(this::close);
        
        this.setBottom(saveButton);
        this.setTop(closeButton);
        this.setPadding(new Insets(20, 20, 20, 20));
    }
   
    public void savePreferencies(ActionEvent event){
        preferenceService.savePreferencies();
        
    }

    public TextEditorPreferencies getParameters() {
        return this.preferencies;
    }

    @Override
    public Node getContent() {
        this.preferencies = (TextEditorPreferencies) preferenceService.getPreferencies();
        mainBox.getChildren().add(preferenceService.generatepreferenciesWidget());
        return this;
    }

    @Override
    public Task updateOnShow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void close(ActionEvent event){
        activityService.back();
    }
}
