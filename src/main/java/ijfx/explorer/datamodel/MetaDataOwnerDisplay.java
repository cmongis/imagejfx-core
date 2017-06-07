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
package ijfx.explorer.datamodel;

import ijfx.core.datamodel.MetaDataOwnerList;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.metadata.MetaDataSet;
import ijfx.explorer.wrappers.MetaDataSetExplorerWrapper;
import java.util.Collection;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
@Plugin(type = Display.class)
public class MetaDataOwnerDisplay extends AbstractDisplay<MetaDataOwnerList>{
    
    public MetaDataOwnerDisplay() {
        super(MetaDataOwnerList.class);
    }
    
    public boolean add(MetaDataOwner owner) {
        
        if(size() == 0) {
            add(new MetaDataOwnerList());
        }
        
        return get(0).add(owner);
    }
    
    public boolean add(MetaDataSet set) {
       return add(new MetaDataSetExplorerWrapper(set));
    }
    
    public boolean add(Collection< ? extends MetaDataOwner> list) {
       return get(0).addAll(list);
    }
    
    
    
}
