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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import net.imagej.ImageJService;
import org.scijava.Prioritized;
import org.scijava.Priority;
import org.scijava.log.LogService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.widget.InputPanel;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author florian
 *  Structure of preferencies : (String) key : (List) [type of variable, [possible value1, possible value2, ...], actual value]
 */
@Plugin(type = Service.class,priority = Priority.VERY_LOW_PRIORITY)
public class ScriptEditorPreferenciesService extends AbstractService implements ImageJService{
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    
    //private HashMap<String,List> preferencies;
    private String fileName = "ScriptEdtirorPreferences";
    private TextEditorPreferencies textEditorPreferencies;

    public ScriptEditorPreferenciesService() {
        
    }
    
    
    
    
    public void loadPreferencies(){
        //this.preferencies = (HashMap) jsonPreferenceService.loadMapFromJson(this.fileName, String.class, List.class);
        try {
            this.textEditorPreferencies = jsonPreferenceService.loadFromJson(fileName, textEditorPreferencies);
        } catch (Exception e) {
            this.textEditorPreferencies = new TextEditorPreferencies();
        }
        
        
    }
    
    public void savePreferencies(){
        jsonPreferenceService.savePreference(textEditorPreferencies, fileName);
        //jsonPreferenceService.savePreference(this.preferencies, this.fileName);
    }
/*
    public HashMap<String,List> getParameters() {
        return preferencies;
    }
    */
    public TextEditorPreferencies getPreferencies(){
        return this.textEditorPreferencies;
    }
    
    /*
    public void createPreferencies(){
        // setting autocompletion, by default actived
        List autocompletion = new ArrayList();
        autocompletion.add(0, "boolean");
        List values = new ArrayList();
        values.add("true");
        values.add("false");
        autocompletion.add(1, values);
        autocompletion.add(2, "true");
        this.preferencies.put("showAutocompletion", autocompletion);
        
        // setting css
        List css = new ArrayList();
        css.add(0, "filePath");
        List path = new ArrayList();
        path.add("darkTheme");
        path.add("lightTheme");
        css.add(1, path);
        css.add(2, "darkTheme");
        this.preferencies.put("styleSheet", css);
        
    }
    
    public void setPreference(String type, String value){
        if (!this.preferencies.containsKey(type)) return;
        List oldValue = this.preferencies.get(type);
        oldValue.add(2, value);
        this.preferencies.put(type, oldValue);
    }
    
    public void setPreference(String type, String value, String newPossibleValue){
        if (!this.preferencies.containsKey(type)) return;
        List oldValue = this.preferencies.get(type);
        oldValue.add(2, value);
        List possibleValues = (List<String>) oldValue.get(1);
        possibleValues.add(newPossibleValue);
        oldValue.add(1, possibleValues);
        this.preferencies.put(type, oldValue);
    }
*/
    @Override
    public void initialize(){
        loadPreferencies();
        
    }

}
