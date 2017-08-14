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
import ijfx.core.workflow.Workflow;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.commands.AbstractExplorableListCommand;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class BatchSegment extends AbstractExplorableListCommand{

    @Parameter
    SegmentationService segmentationService;
    
    @Parameter
    Workflow workflow;
    
   
    
    @Override
    public void run(ExplorableList t) {

        
        segmentationService
                .createSegmentation()
                .setWorkflow(workflow)
                .add(t)
                .measure()
                .executeAsync()
                .then(segmentationService::show);
                
        
        
        

    }
    
    
    
    
}
