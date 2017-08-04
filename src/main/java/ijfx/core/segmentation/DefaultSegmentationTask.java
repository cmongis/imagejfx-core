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
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.workflow.Workflow;
import static jdk.nashorn.tools.ShellFunctions.input;
import net.imagej.Dataset;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;

/**
 *
 * @author cyril
 */
public class DefaultSegmentationTask implements SegmentationOp {
    
    private Dataset input;
    
    private Dataset measuredDataset;
    
    private Img<BitType> output;
    
    private Workflow workflow;
    
    MetaDataSet set;
    
    public DefaultSegmentationTask() {
         
    }
    
    public void load() {
       
    }
    
    
     
    public DefaultSegmentationTask(Dataset input, Workflow workflow, MetaDataSet set) {
        this.measuredDataset = input;
        this.input = input;
        this.workflow = workflow;
        this.set = set;
    }
    
    
    public DefaultSegmentationTask(Dataset input, Dataset measured, Workflow workflow, MetaDataSet set) {
        this.measuredDataset = measured;
        this.input = input;
        this.workflow = workflow;
        this.set = set;
    }

    public DefaultSegmentationTask(Dataset measured, Img<BitType> mask, Workflow workflow, MetaDataSet set) {
        this.measuredDataset = measured;
        this.output = mask;
        this.workflow = workflow;
        this.set = set;
    }
    
    public Dataset getMeasuredDataset() {
        return measuredDataset;
    }

    public Img<BitType> getOutput() {
        return output;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public Dataset getInput() {
        return measuredDataset;
    }

    @Override
    public void setOutput(Img<BitType> mask) {
        this.output = mask;
    }

    @Override
    public MetaDataSet getMetaDataSet() {
        if(set == null) {
            set = new MetaDataSet();
        }
        return set;
    }
}
