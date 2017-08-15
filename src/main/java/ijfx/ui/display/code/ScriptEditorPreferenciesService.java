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
import ijfx.ui.inputharvesting.AbstractWidgetModel;
import ijfx.ui.inputharvesting.SuppliedWidgetModel;
import java.io.File;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.imagej.ImageJService;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.widget.ChoiceWidget;
import org.scijava.widget.FileWidget;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetService;

/**
 *
 * @author florian
 *  
 */
@Plugin(type = Service.class,priority = Priority.VERY_LOW_PRIORITY)
public class ScriptEditorPreferenciesService extends AbstractService implements ImageJService, ScriptEditorPreferencies{
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    @Parameter
    WidgetService widgetService;
    
    private String fileName = "ScriptEdtirorPreferences";
    private TextEditorPreferencies preferencies= new TextEditorPreferencies();

    public ScriptEditorPreferenciesService() {
        
    }
    
    @Override
    public void loadPreferencies(){
        this.preferencies = jsonPreferenceService.loadFromJson(fileName, preferencies);
                
    }
    
    @Override
    public void savePreferencies(){
        jsonPreferenceService.savePreference(preferencies, fileName);
    }

    @Override
    public Preferencies getPreferencies(){
        return this.preferencies;
    }
    
   
    @Override
    public void initialize(){
        loadPreferencies();
        
    }

}
