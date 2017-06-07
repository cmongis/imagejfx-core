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
package ijfx.core.metadata;

import ijfx.core.IjfxService;
import ijfx.core.datamodel.MetaDataOwnerList;
import ijfx.explorer.datamodel.MetaDataOwnerDisplay;
import ijfx.ui.main.ImageJFX;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.scijava.display.DisplayService;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = Service.class)
public class MetaDataSetDisplayService extends AbstractService implements IjfxService {

    @Parameter
    DisplayService displayService;

    @Parameter
    EventService eventService;

    Logger logger = ImageJFX.getLogger();

    public MetaDataOwnerDisplay createDisplay(String name) {

        MetaDataOwnerDisplay display = (MetaDataOwnerDisplay) displayService.createDisplay(new MetaDataOwnerList());
        
        displayService.getDisplays().add(display);
        
        
        
        return display;

    }

    public MetaDataOwnerDisplay findDisplay(String name) {
        return displayService.getDisplaysOfType(MetaDataOwnerDisplay.class)
                .stream()
                .filter(display -> name.equals(display.getName()))
                .findFirst()
                .orElseGet(() -> createDisplay(name));
    }

    public void addMetaDataset(MetaDataSet metaDataSet) {
        MetaDataOwnerDisplay activeDisplay = displayService.getActiveDisplay(MetaDataOwnerDisplay.class);

        if (activeDisplay == null) {
            displayService.createDisplay("Measures", metaDataSet);
            
        }
        activeDisplay = displayService.getActiveDisplay(MetaDataOwnerDisplay.class);
        activeDisplay.add(metaDataSet);

        activeDisplay.update();
    }

    public void addMetaDataSetToDisplay(MetaDataSet metaDataSet, String displayName) {
        MetaDataOwnerDisplay display = findDisplay(displayName);

        display.add(metaDataSet);
        display.update();
    }

    public void addMetaDataSetToDisplay(List<? extends MetaDataOwner> owners, String displayName) {
        
        MetaDataOwnerDisplay display = findDisplay(displayName);
       
        display.add(owners);
        
        display.update();
        
    }
    
}
