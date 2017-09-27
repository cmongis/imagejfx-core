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
package ijfx.core.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import ijfx.core.IjfxTest;
import ijfx.core.metadata.MetaData;
import ijfx.core.overlay.OverlayStatService;
import ijfx.core.overlay.io.PolygonOverlayBuilder;
import ijfx.core.segmentation.DefaultSegmentedObject;
import ijfx.core.segmentation.SegmentedObjectExplorerWrapper;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.ExplorerService;
import ijfx.explorer.core.FolderManagerService;
import ijfx.explorer.datamodel.DefaultTag;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.explorer.datamodel.wrappers.PlaneMetaDataSetWrapper;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import mongis.utils.ProgressHandler;
import net.imagej.overlay.PolygonOverlay;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class ExplorableIOTest extends IjfxTest{
    
    
    @Parameter
    ExplorableIOService explorableIOService;
    
    @Parameter
    OverlayStatService overlayStatsService;
    
    @Parameter
    ExplorerService explorerService;
    
    @Parameter
    FolderManagerService folderManagerService;
    
    static final File TEST_DIRECTORY = new File("src/test/resources/dataset");
    
    
    @Test
    public void contextNotNull() {
        Assert.assertNotNull(getContext());
    }
    
    @Test
    public void saveTag() throws IOException{
        File tmpFile = File.createTempFile("tag", ".json");
        Tag tag = new DefaultTag("hello");
        explorableIOService.getJsonMapper().writeValue(tmpFile, tag);
        
        Tag loaded = explorableIOService.getJsonMapper().readValue(tmpFile, Tag.class);
        displayFile(tmpFile);
        Assert.assertEquals(tag, loaded);
        
    }
    
    
    @Test
    public void saveSegmentedObject() throws IOException{
        
        System.out.println("### Creating tmp file");
        
        File tmpFile = File.createTempFile("saveSegmentedObject", ".json");
        
        
        
        DefaultSegmentedObject original = new DefaultSegmentedObject();
        PolygonOverlay polygon = new PolygonOverlayBuilder(getContext())
                .addVertex(0,0)
                .addVertex(10,10)
                .addVertex(5,3)
                .addVertex(1,5)
                
                .build();
                
        original.setOverlay(polygon);
        original.addTag(new DefaultTag("hello"));
        original.getMetaDataSet().merge(overlayStatsService.getShapeStatisticsAsMap(polygon));
        
        SegmentedObjectExplorerWrapper wrapper = new SegmentedObjectExplorerWrapper(original);
        
        explorableIOService.saveOne(wrapper, tmpFile);
        
        System.out.println(FileUtils.readFileToString(tmpFile));
        
        Explorable loaded =  explorableIOService.loadOne(tmpFile);
        
        Assert.assertNotNull("taggable not null",loaded);
        
        Assert.assertEquals("type",original.getMetaDataSet().getType(),loaded.getMetaDataSet().getType());
        
        
        assertTrue("taggable instanceof DefaultSegmentedObject",loaded instanceof  ijfx.core.segmentation.SegmentedObjectExplorerWrapper);
        
        
    }
    
    
    public void displayFile(File file) throws IOException{
        System.out.println(file.toString());
         System.out.println(FileUtils.readFileToString(file));
    }
    
    @Test
    public void saveFolder() throws Exception{
        
        
        
        
    }
    
    private int contentHash(Collection<? extends Taggable> set) {
        
        return set
                .stream()
                .map(Taggable::getMetaDataSet)
                .mapToInt(ExplorableList::contentHash)
                .sum();
        
    }
    
    @Test
    public void saveFileMetaData() throws IOException {
         List<Explorable> initial = explorerService
                .indexDirectory(ProgressHandler.NONE, TEST_DIRECTORY)
                .collect(Collectors.toList());
        
        File tmpFile = File.createTempFile("saveSegmentedObject", ".json");
        
        
        explorableIOService.saveAll(initial, tmpFile);
        
        displayFile(tmpFile);
        
        List<? extends Explorable> loaded = explorableIOService.loadAll(tmpFile);
        
        assertNotNull("List not null",loaded );
        
        assertTrue("List of the same size",initial.size() == loaded.size());
        
       
        
        System.out.println(initial.get(0).getMetaDataSet());
        System.out.println(loaded.get(0).getMetaDataSet());
        assertEquals("Same metadata"
                ,initial.get(0).getMetaDataSet().get(MetaData.ABSOLUTE_PATH)
                ,loaded.get(0).getMetaDataSet().get(MetaData.ABSOLUTE_PATH)
        );
        
        
        Assert.assertNotNull("dataset",loaded.get(0).getDataset());
        
    }
    
    
    protected ObjectMapper getMapper() {
        return explorableIOService.getJsonMapper();
    }
   
    @Test
    public void savePlaneMetaData() throws IOException{
        
        File tmpFile = File.createTempFile("planes", ".json");
        
        List<Explorable> planes = explorerService
                .indexDirectory(ProgressHandler.NONE, TEST_DIRECTORY)
                .map(exp->folderManagerService.extractPlanes(exp))
                .flatMap(exp->exp)
                .collect(Collectors.toList());
                ;
                
        
        explorableIOService.saveAll(planes, tmpFile);
        
        displayFile(tmpFile);
        
        List<? extends Explorable> loaded = explorableIOService.loadAll(tmpFile);
        
        
        Assert.assertTrue("Loading success",loaded != null && loaded.size() == planes.size());
        Assert.assertTrue("Loads the right type",loaded.get(0) instanceof PlaneMetaDataSetWrapper);
        Assert.assertNotNull("Loads with context injection",loaded.get(0).getDataset());
    }
    
}
