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

import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.workflow.Workflow;
import net.imagej.Dataset;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;

/**
 *
 * @author Cyril MONGIS
 */
public interface SegmentationOp extends MetaDataOwner {
    
    void load();
    
    
    Dataset getMeasuredDataset();
    
    Dataset getInput();
    
    Img<BitType> getOutput();
    
    void setOutput(Img<BitType> mask);
    
    Workflow getWorkflow();
    
    public void dispose();
    
}
