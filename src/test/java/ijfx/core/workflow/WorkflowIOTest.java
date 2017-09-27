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
package ijfx.core.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import ijfx.core.IjfxTest;
import ijfx.core.batch.BatchService;
import ijfx.core.datamodel.DefaultInterval;
import ijfx.core.datamodel.LongInterval;
import ijfx.core.image.ChannelSettings;
import ijfx.core.image.DefaultChannelSettings;
import java.io.File;
import java.io.IOException;
import net.imagej.lut.LUTService;
import net.imagej.plugins.commands.imglib.GaussianBlur;
import net.imglib2.display.ColorTable;
import net.imglib2.display.ColorTable16;
import net.imglib2.display.ColorTable8;
import org.junit.Assert;
import org.junit.Test;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class WorkflowIOTest extends IjfxTest{
    
    @Parameter
    BatchService batchService;
    
    @Parameter
    WorkflowIOService workflowIOService;
    
    @Parameter
    LUTService lutService;
    
    @Test
    public void channelSettings() throws IOException {
        
       
        
        
        ChannelSettings channelSettings = new DefaultChannelSettings()
                .addSetting("GFP", 200, 400, new ColorTable8())
                .addSetting("mCherry",100,300,new ColorTable16());
        
        
        File tmpFile = File.createTempFile("channelsettings", ".json");
        
        ObjectMapper mapper = workflowIOService.getObjectMapper();
        
        mapper.writeValue(tmpFile, channelSettings);
        
        displayFile(tmpFile);
        
        ChannelSettings loaded = mapper.readValue(tmpFile, ChannelSettings.class);
        
        Assert.assertNotNull(loaded);
        
        
        
        
        Assert.assertEquals("same number of channels",channelSettings.getChannelCount(),loaded.getChannelCount());
        Assert.assertEquals("same channels",channelSettings.get(0).getChannelMin(),loaded.get(0).getChannelMin(),0.0);
        
        System.out.println("Testing color tables...");
        
        for(int i = 0;i!=channelSettings.getChannelCount();i++) {
            
            
            
            ColorTable c1 = channelSettings.get(i).getColorTable();
            ColorTable c2 = loaded.get(i).getColorTable();
            
            Assert.assertEquals("Channel length "+(i+1), c1.getLength(),c2.getLength());
            for(int j = 0;j!= c1.getLength();j++) {
                Assert.assertEquals(String.format("Byte from channel %d / %d",i,j),c1.get(0, j),c2.get(0,j));
            }
        }
        
        //Assert.assertEquals("same colot table byes", channelSettings.get(0).getColorTable().);
        
    }
    
    @Test
    public void longInterval() throws IOException{
        
        LongInterval interval = new DefaultInterval(2, 10,0,20);
        
        
        File tmpFile = File.createTempFile("longinterval", ".json");
        
        
        ObjectMapper mapper = workflowIOService.getObjectMapper();
        
        mapper.writeValue(tmpFile, interval);
        
        displayFile(tmpFile);
        
        
        
        LongInterval loaded = mapper.readValue(tmpFile, LongInterval.class);
        
        Assert.assertNotNull("loaded interval not null",loaded);
        
        Assert.assertEquals("interval equals",loaded, interval);
        
    }
    
    
    @Test
    public void testWorkflowSaving() throws Exception {
        
        
        
        Workflow workflow = batchService
                .builder()
                .addStep(GaussianBlur.class, "data", new File("hello.txt"))
                //.addStep(GaussianBlur.class, "sigma",3.0)
                .addStep(GaussianBlur.class, "sigma",3.0)
                .getWorkflow();
        
        File tmpFile = File.createTempFile("testIjfx", ".json");
        
        workflowIOService.saveWorkflow(workflow, tmpFile);
        
        System.out.println(org.apache.commons.io.FileUtils.readFileToString(tmpFile));
        
        Workflow loadedWorkflow = workflowIOService.loadWorkflow(tmpFile);
        
        Assert.assertEquals("workflow size", workflow.getStepList().size(), loadedWorkflow.getStepList().size());
        Assert.assertEquals(
                "workflow params"
                ,workflow.getStepList().get(1).getParameters().get("sigma")
                ,loadedWorkflow.getStepList().get(1).getParameters().get("sigma")
        );
        
    }
    
    
  
    
}
