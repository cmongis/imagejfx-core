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
package ijfx.commands.explorable;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataSet;
import ijfx.explorer.datamodel.Explorable;
import java.util.List;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
@Plugin(type = ExplorableDisplayCommand.class, label="Set to value...",iconPath="fa:edit",initializer = "init")
public class SetValue extends AbstractExplorableDisplayCommand{

    @Parameter(label = "Key to set")
    String key;
    
    @Parameter(label = "Value")
    String value;
    
    
    @Override
    public void run(List<? extends Explorable> items) {
        
        
       items
               .stream()
               .forEach(this::setValue);
       
       
       display.update();

    }
    
    public void setValue(Explorable exp) {
        MetaDataSet set = exp.getMetaDataSet();
        if(set.containsKey(key) == false) {
            set.put(MetaData.create(key, value));
        }
        else {
            set.get(key).setValue(value);
        }
    }
    
    public void init() {
        initWithPossibleKeys("key");
    }
    
}
