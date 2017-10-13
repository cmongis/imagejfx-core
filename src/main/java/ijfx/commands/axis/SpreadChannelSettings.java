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
package ijfx.commands.axis;

import ijfx.core.batch.BatchService;
import ijfx.core.image.ChannelSettings;
import net.imagej.Dataset;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.lut.LUTService;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = Command.class
        ,menuPath="Image > Color > Spread channel setting"
        ,description = "The LUT settings of the input image will be applied to all other"
                + "open images (no data is modified, only the visualization)."
)
public class SpreadChannelSettings extends ContextCommand{

    //@Parameter(type=ItemIO.INPUT)
    //private ImageDisplay imageDisplay;
    
    @Parameter(type = ItemIO.BOTH)
    Dataset dataset;
    
    @Parameter
    private ImageDisplayService imageDisplayService;
    
    @Parameter
    private ChannelSettings channelSettings;
    
    @Parameter
    private LUTService lutService;
    
    @Parameter
    private BatchService batchService;
    
  
    
    @Override
    public void run() {
        
        channelSettings.apply(dataset);;
        
        if(batchService.isRunning()) {
            return;
        }
        
        imageDisplayService
                .getImageDisplays()
                .stream()
                .forEach(this::apply);
        
    }
    
    private ImageDisplay associatedDisplay(Dataset dataset) {
        
        return imageDisplayService
                .getImageDisplays()
                .stream()
                .filter(display->imageDisplayService.getActiveDataset(display) == dataset)
                .findAny()
                .orElse(null);
        
    }
    
    private void apply(ImageDisplay view) {
        channelSettings.apply(view);
        DatasetView datasetView = imageDisplayService.getActiveDatasetView(view);
        channelSettings.apply(datasetView);
        channelSettings.apply(datasetView.getData());
        datasetView.getProjector().map();
        view.update();
        
    }
}
