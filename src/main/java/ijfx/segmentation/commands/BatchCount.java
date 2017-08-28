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

import ijfx.core.segmentation.SegmentationService;
import ijfx.core.uiplugin.UiCommand;
import ijfx.core.workflow.Workflow;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.commands.AbstractExplorableListCommand;
import ijfx.ui.loading.LoadingScreenService;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.NumberWidget;

/**
 *
 * @author cyril
 */
@Plugin(type = UiCommand.class)
public class BatchCount extends AbstractExplorableListCommand{

    
    
    @Parameter
    private SegmentationService segmentationService;
       
    @Parameter
    private Workflow workflow;
    
    @Parameter
    private LoadingScreenService loadingScreenService;
   
    @Parameter(label = "Minimum object area",style=NumberWidget.SCROLL_BAR_STYLE,min = "1.0",max = "10")
    private double minSize = 4.0;
    
    
    @Override
    public void run(ExplorableList t) {
        
        segmentationService
                .createSegmentation()
                .add(t)
                .setWorkflow(workflow)
                .count()
                .display("Batch count")
                .executeAsync()
                .submit(loadingScreenService);
        
        
    }
    
}
