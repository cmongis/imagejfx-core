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
package ijfx.core.segmentation;

import com.google.common.collect.Lists;
import ijfx.commands.binary.BinaryToOverlay;
import ijfx.core.batch.item.BatchItemBuilder;
import ijfx.core.batch.item.DatasetPlaneWrapper;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.overlay.MeasurementService;
import ijfx.core.utils.DimensionUtils;
import ijfx.core.workflow.Workflow;
import ijfx.ui.main.ImageJFX;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mongis.utils.ProgressHandler;
import net.imagej.Dataset;
import net.imagej.overlay.Overlay;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class SegmentationTaskBuilder {

    boolean separatePlane = true;

    final BatchItemBuilder itemBuilder;

    @Parameter
    Context context;

    @Parameter
    MeasurementService measurementService;
    
   // @Parameter
    //        Seg segmentationService;
    
    Workflow workflow;
    
    Logger logger = ImageJFX.getLogger();
    
    BiConsumer<ProgressHandler, Img<BitType>> handler = (progress,img)->{
        logger.info("Nothing is done with");
    };
    
    
    private SegmentationTaskBuilder(Context context) {
        context.inject(this);
        itemBuilder = new BatchItemBuilder(context);

    }

    public SegmentationTaskBuilder setWorkflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    
    private List<SegmentedObject> measure(ProgressHandler handler, MetaDataSet set, Dataset original, Img<BitType> mask) {
        
        
        
        Overlay[] transform = BinaryToOverlay.transform(context, mask, true);
        
        //measurementService.measureOverlays(overlays, mask)
        
        return null;
    }
    
    
    
    private  SegmentationTaskBuilder measure(Consumer<List<SegmentedObject>> onFinished) {
        
        
return this;
    }

    
    
    private SegmentationTaskBuilder useMask(Consumer<Img<BitType>> onFinished) {
        
        
        
        return this;
    }
    /*
    private List<? extends SegmentationTask> build() {

        List<DefaultSegmentationTask> inputList;
        
        // if separatePlane
        if (separatePlane) {

            Dataset dataset = itemBuilder.getInput().getDataset();

            inputList = Stream
                    .of(DimensionUtils.allPossibilities(dataset))
                    .map(position -> new DatasetPlaneWrapper(context, dataset, position))
                    .map(input->new DefaultSegmentationTask(input,workflow))
                    
                    .collect(Collectors.toList());
            
            
            
            
        }
        else {
            inputList = Lists.newArrayList(new DefaultSegmentationTask(itemBuilder.getInput(),workflow));
        }
        
        inputList
                .forEach(task->task.setHandler(getHandler()));
        // take input dataset

        // separate into individual batch input
        // and map it into individual SegmentationTask with each plane as a task
        // just take the input as input 
    }*/

}
