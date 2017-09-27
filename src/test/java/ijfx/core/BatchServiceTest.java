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
package ijfx.core;

import ijfx.commands.binary.Binarize;
import ijfx.core.batch.BatchService;
import ijfx.core.metadata.MetaData;
import ijfx.core.segmentation.SegmentationService;
import io.scif.services.DatasetIOService;
import mongis.utils.ProgressHandler;
import net.imagej.DatasetService;
import net.imagej.plugins.commands.assign.InvertDataValues;
import net.imagej.plugins.commands.imglib.GaussianBlur;
import org.junit.Assert;
import org.junit.Test;
import org.scijava.display.ActiveDisplayPreprocessor;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS
 */
public class BatchServiceTest extends IjfxTest{
    
    
    @Parameter
    BatchService batchService;
    
    @Parameter
    DatasetService datasetService;
    
    @Parameter
    DatasetIOService datasetIOService;
    
    @Parameter
    SegmentationService segmentationService;
    
    
    

    public void testBatchService() {
        Assert.assertTrue("Blacklisting",batchService
                .getPreProcessors(ActiveDisplayPreprocessor.class)
                .stream()
                .filter(p->p.getClass() == ActiveDisplayPreprocessor.class)
                .count() == 0
        );
    }
    
    
    public void testProcessing() {
        
        
        
        batchService
                .builder()
                .addFolder("/Users/cyril/img_test/")
                .addStep(GaussianBlur.class,"sigma",3)
                .saveIn("target/test/")
                .start(ProgressHandler.console(),true);
        
        
    }
    
    
    public void testSegmentation() {
        segmentationService
                .createSegmentation()
                .add("/Users/cyril/test_img/BBBC018_multichannel_small/",true)
                .addStep(Binarize.class)
                .addStep(InvertDataValues.class)
                .filterNumber(MetaData.CHANNEL, channel->channel==1.0)
                .measure()
                .saveDataset("~/datasetResults/")
                .execute(ProgressHandler.console());
    }
    
    
}
