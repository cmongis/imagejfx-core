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
import java.util.Hashtable;
import java.util.List;
import org.scijava.plugin.Parameter;

/**
 *
 * @author florian
 *  Structure of preferencies : (String) key : (List) [type of variable, [possible value1, possible value2, ...], actual value]
 */
public class ScriptEditorPreferenciesService {
    @Parameter
    JsonPreferenceService jsonPreferenceService;
    
    private Hashtable<String,List> preferencies;
    private String fileName = "ScriptEdtirorPreferences";

    public ScriptEditorPreferenciesService() {
        loadPreferencies();
        if (this.preferencies.isEmpty()) {
            createPreferencies();
        }
    }
    
    
    
    
    public void loadPreferencies(){
        this.preferencies = (Hashtable) jsonPreferenceService.loadMapFromJson(this.fileName, String.class, List.class);
        
    }
    
    public void savePreferencies(){
        jsonPreferenceService.savePreference(this.preferencies, this.fileName);
    }

    public Hashtable getParameters() {
        return preferencies;
    }
    
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
        autocompletion.add(0, "filePath");
        List path = new ArrayList();
        path.add("darkTheme");
        path.add("lightTheme");
        autocompletion.add(1, path);
        autocompletion.add(2, "darkTheme");
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
}
