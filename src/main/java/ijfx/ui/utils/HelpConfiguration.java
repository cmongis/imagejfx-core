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
package ijfx.ui.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import ijfx.core.hint.Hint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.Node;

/**
 *  Class describing JSON configuration files that contains hints and descriptions of JavaFX UI Elements
 * @author cyril
 */
public class HelpConfiguration {

    private HashMap<String,String> descriptions = new HashMap<>();
    
    private List<Hint> hintList = new ArrayList<>();
    
    @JsonCreator
    public HelpConfiguration() {
    }
    
    @JsonSetter("descriptions")
    public void setDescriptions(HashMap<String, String> descriptions) {
        this.descriptions = descriptions;
    }
    
    @JsonSetter("hints")
    public void setHintList(List<Hint> hintList) {
        this.hintList = hintList;
    }
    public HashMap<String, String> getDescriptions() {
        return descriptions;
    }

    public List<Hint> getHintList() {
        return hintList;
    }
}
