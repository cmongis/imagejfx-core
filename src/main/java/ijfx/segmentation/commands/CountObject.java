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
package ijfx.segmentation.commands;

import ijfx.core.metadata.MetaDataSetDisplayService;
import ijfx.core.segmentation.CountObjectSegmentationTask;
import ijfx.core.segmentation.SegmentationService;
import ijfx.explorer.datamodel.MetaDataOwnerDisplay;
import mongis.utils.ProgressHandler;
import net.imagej.display.ImageDisplay;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.NumberWidget;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Command.class, menuPath = "Analyze > Count objects")
public class CountObject extends ContextCommand {

    @Parameter
    ImageDisplay imageDisplay;
    
    @Parameter
    SegmentationService segmentationService;
    
    @Parameter
    MetaDataSetDisplayService metadataSetDisplaySrv;
    
    @Parameter(label = "Minimum area",style=NumberWidget.SLIDER_STYLE,min="1.0",max="50")
    Integer minimumSize = 5;
    
    @Override
    public void run() {
        
        segmentationService
                .createSegmentation()
                .addImageDisplay(imageDisplay)
                .count()
                .setMinimumSize(minimumSize)
                .then(this::onFinished)
                .execute(ProgressHandler.console());
                
        
        
    }
    
    private void onFinished(ProgressHandler handler, CountObjectSegmentationTask result) {
        
        MetaDataOwnerDisplay display = metadataSetDisplaySrv.findDisplay(String.format("Count (min size = %d)",minimumSize));
        display.add(result.getResults());
        display.update();
    }
    
}
