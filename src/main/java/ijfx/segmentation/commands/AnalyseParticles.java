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

import ijfx.core.metadata.MetaData;
import ijfx.core.segmentation.SegmentationService;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.Overlay;
import org.scijava.command.ContextCommand;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import ijfx.core.overlay.OverlayUtilsService;
import mongis.utils.ProgressHandler;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.widget.NumberWidget;

/**
 *
 * @author cyril
 */
@Plugin(type = Command.class, menuPath = "Analyse > Analyse particles")
public class AnalyseParticles extends ContextCommand{
    
    @Parameter
    SegmentationService segmentationService;
    
    @Parameter
    ImageDisplay imageDisplay;
    
    @Parameter
    OverlayUtilsService overlayUtilsSrv;
    
    @Parameter
    DisplayService displayService;

    @Parameter
    UIService uiService;
    
    @Parameter(label = "Minimum area (pixel)",style = NumberWidget.SLIDER_STYLE,min = "1.0",max="50")
    Integer minimumArea = 10;
    
    @Override
    public void run() {
        
        Img<BitType> mask = overlayUtilsSrv.extractBinaryMask(imageDisplay);
        
        if(mask == null) {
            cancel("You must first have a Binary mask");
            return;
        }
        
        segmentationService
                .createSegmentation()
                .addImageDisplay(imageDisplay)
                .measure()
                .then((progres,task)->uiService.show(imageDisplay.getName(), task.getAsExplorable().filterGreaterThan(MetaData.LBL_AREA, minimumArea)))
                .execute(ProgressHandler.console());
        
        

    }
    
    
    
    
    
}
