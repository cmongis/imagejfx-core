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
package ijfx.ui.display.metadataowner;

import ijfx.core.datamodel.MetaDataOwnerList;
import ijfx.explorer.datamodel.MetaDataOwnerDisplay;
import ijfx.ui.display.image.AbstractFXDisplayViewer;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;
import org.scijava.ui.viewer.DisplayViewer;

/**
 *
 * @author cyril
 */
@Plugin(type = DisplayViewer.class)
public class MetaDataOwnerDisplayViewer extends AbstractFXDisplayViewer<MetaDataOwnerList>{

    public MetaDataOwnerDisplayViewer() {
        super(MetaDataOwnerList.class);
    }

    

    @Override
    public boolean canView(Display<?> d) {
        return d instanceof MetaDataOwnerDisplay;
       }
    
}
