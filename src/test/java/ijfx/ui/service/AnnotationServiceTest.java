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
package ijfx.ui.service;

import de.saxsys.javafx.test.JfxRunner;
import de.saxsys.javafx.test.TestInJfxThread;


import ijfx.core.IjfxTest;
import ijfx.core.metadata.GenericMetaData;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.explorer.wrappers.MetaDataSetExplorerWrapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.scijava.plugin.Parameter;

/**
 *
 * @author sapho
 */

@RunWith(JfxRunner.class)
public class AnnotationServiceTest extends IjfxTest{
    
    @Parameter
    AnnotationService annotationService;
    
    private Taggable taggable;
    private Tag tag;
    private MetaDataOwner owner;
    private String key;
    private Object value;
    private MetaData m; 
    private List<? extends MetaDataOwner> list;
    
    
    
    public AnnotationServiceTest() {
    }
    
    @Before
    @TestInJfxThread
    public void setUp() throws Exception {
        
        key = "key";
        value = "value";
        tag = Tag.create(key);
        m = MetaData.create(key, value);
        list = new ArrayList<MetaDataOwner>();
        taggable = new MetaDataSetExplorerWrapper();
        owner = new MetaDataSetExplorerWrapper();
        
        
    }
    
    @After
    public void tearDown() throws Exception {
        key = null;
        value = null;
        taggable = null;
        tag = null;
        owner = null;
        m = null;
        list = null;
        
    }

    /**
     * Test of addTag method, of class AnnotationService.
     */
    @Test
    public void testAddTag() {
        System.out.println("addTag");
        annotationService.addTag(taggable, tag);
        Assert.assertNotNull("Testing tag creation", tag);
        
    }

    /**
     * Test of removeTag method, of class AnnotationServic
     */
    @Test
    public void testRemoveTag() {
        System.out.println("removeTag");
        annotationService.addTag(taggable, tag);
        long size = taggable.getTagList().size();
        annotationService.removeTag(taggable, tag);
        long finalSize = taggable.getTagList().size();
        
        
        long expected = size -1;
        assertEquals("Wrong size", expected, finalSize);
        
    }

    /**
     * Test of addMetaData method, of class AnnotationService.
     */
    @Test
    public void testAddMetaData_MetaDataOwner_MetaData() {
        System.out.println("addMetaData");
        annotationService.addMetaData(owner, m);
        assertNotNull("metadata null", m);
        assertTrue("m is not on owner", owner.getMetaDataSet().containMetaData(m));
    }

    /**
     * Test of removeMetaData method, of class AnnotationService.
     */
    @Test
    public void testRemoveMetaData_3args() {
        System.out.println("removeMetaData with boolean");
        boolean matchValue = true;
	    
	//TODO: create an other test with matchValue = False
	//Make sure that it would fail removing if the metadata used
	//as input parameters only matches the key but not the value
	//of the metadata owned by the MetaDataOwner
        owner.getMetaDataSet().clear();
        annotationService.addMetaData(owner, m);
        long size = owner.getMetaDataSet().size();
        annotationService.removeMetaData(owner, m, matchValue);
        long finalSize = owner.getMetaDataSet().size();
        long expected = size -1;
        assertFalse("m is on owner", owner.getMetaDataSet().containMetaData(m));
        assertEquals ("Wrong size", expected, finalSize );

        
        //--------------------------------------
        //In the case of the boolean is false
        
        matchValue = false;
        
        annotationService.addMetaData(owner, m);
        annotationService.removeMetaData(owner, m, matchValue);
        assertTrue("m is not in owner", owner.getMetaDataSet().containMetaData(m));
        
        //--------------------------------------
        //In the case of the key match but not the value
        //m still in owner
        
        String key2 = "key2";
        Object value2 = value;
        matchValue = true;
        
        MetaData n = new GenericMetaData(key2, value2);
        annotationService.addMetaData(owner, m);
        annotationService.removeMetaData(owner, n, matchValue);
        assertTrue("m is not in owner", owner.getMetaDataSet().containMetaData(m));
        assertFalse("n is in owner", owner.getMetaDataSet().containMetaData(n));
        
        //--------------------------------------
        //In the case of the value match but not the key
        //m still in owner
        
        key2 = key;
        value2 = "value2";
        matchValue = true;
        
        MetaData o = new GenericMetaData(key2, value2);
        annotationService.addMetaData(owner, m);
        annotationService.removeMetaData(owner, o, matchValue);
        assertTrue("m is not in owner", owner.getMetaDataSet().containMetaData(m));
        assertFalse("o is in owner", owner.getMetaDataSet().containMetaData(o));
        
    }

    /**
     * Test of addMetaData method, of class AnnotationService.
     */
    @Test
    public void testAddMetaData_List_MetaData() {
	//TODO: make a test assert that all the 
	// metadataOwner contains the MetaData
	// Use the Stream API to do count the
	// MetaDataOwners that contains the
	// the MetaData
        
        
        System.out.println("addMetaData list");
        annotationService.addMetaData(list, m);
        
        long expectedSize = new Long(list.size());
        long numMetadata = list.stream().filter(c->c.getMetaDataSet().containMetaData(m))
                .count();
        
        assertNotNull("Metadata null", m);
        assertEquals("MetaData is not containing by all MetaDataOwners", expectedSize, numMetadata);
        
    }

    /**
     * Test of removeMetaData method, of class AnnotationService.
     */
    @Test
    public void testRemoveMetaData_List_MetaData() {
        System.out.println("removeMetaData list");
        annotationService.addMetaData(list, m);
        annotationService.removeMetaData(list, m);
        
        long expectedSize = 0;
        long numMetadata = list.stream().filter(c->c.getMetaDataSet().containMetaData(m))
                .count();
        assertFalse("m is in owner", owner.getMetaDataSet().containMetaData(m));
        assertEquals("MetaData is containing by several MetaDataOwners", expectedSize, numMetadata);
    }

    
    
}
