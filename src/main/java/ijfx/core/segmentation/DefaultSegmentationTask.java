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

import ijfx.core.batch.BatchSingleInput;
import ijfx.core.workflow.Workflow;
import java.util.function.BiConsumer;
import mongis.utils.ProgressHandler;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;

/**
 *
 * @author cyril
 */
public class DefaultSegmentationTask<T> implements SegmentationOp<T> {
    
    private BatchSingleInput input;
    
    private Workflow workflow;
    
    private SegmentationHandler<T> handler;

     public DefaultSegmentationTask() {
    }
    
    public DefaultSegmentationTask(BatchSingleInput input, Workflow workflow, SegmentationHandler<T> handler) {
        this.input = input;
        this.workflow = workflow;
        this.handler = handler;
    }

   

    public DefaultSegmentationTask(BatchSingleInput input, Workflow workflow) {
        this(input, workflow, null);
    }
    
    
    
    public SegmentationHandler<T> getHandler() {
        return handler;
    }

    public void setHandler(SegmentationHandler<T> handler) {
        this.handler = handler;
    }

    public BatchSingleInput getInput() {
        return input;
    }

    public void setInput(BatchSingleInput input) {
        this.input = input;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }
    
    
    
    
}
