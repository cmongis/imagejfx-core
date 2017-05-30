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

import ijfx.ui.service.AnnotationService;
import ijfx.ui.service.DefaultAnnotationService;

import ijfx.core.IjfxTest;
import ijfx.core.metadata.GenericMetaData;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.explorer.datamodel.DefaultTag;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.explorer.views.GenerateDummyExplorables;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.scijava.plugin.Parameter;
import org.scijava.Context;

/**
 *
 * @author sapho
 */
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
    public void setUp() throws Exception {
        annotationService = new DefaultAnnotationService();
        key = "key";
        value = "value";
        taggable = (Taggable) new GenerateDummyExplorables(); // doesn this work ??? If yes, why ?
        tag = new DefaultTag("bieber");
        owner = (MetaDataOwner) new GenerateDummyExplorables();
        m = new GenericMetaData(key, value);
	    
	// your list should contain at least 3 metadata 
        list = new ArrayList<MetaDataOwner>();

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
        annotationService.removeTag(taggable, tag);
        Assert.assertNull("Testing tag remove", tag);
    }

    /**
     * Test of addMetaData method, of class AnnotationService.
     */
    @Test
    public void testAddMetaData_MetaDataOwner_MetaData() {
        System.out.println("addMetaData");
        annotationService.addMetaData(owner, m);
        assertNotNull("metadata null", m);
        assertTrue("m is really to owner", owner.getMetaDataSet().containMetaData(m));
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
        annotationService.addMetaData(owner, m);
        annotationService.removeMetaData(owner, m, matchValue);
        assertNull("Metadata not null",m);
        assertFalse("m is not in owner", owner.getMetaDataSet().containMetaData(m));
        
        //--------------------------------------
        //In the case of the boolean is false
        
        matchValue = false;
        
        annotationService.addMetaData(owner, m);
        annotationService.removeMetaData(owner, m, matchValue);
        assertTrue("m is in owner", owner.getMetaDataSet().containMetaData(m));
        
        //--------------------------------------
        //In the case of the key match but not the value
        
        String key2 = "key2";
        Object value2 = value;
        matchValue = true;
        
        MetaData n = new GenericMetaData(key2, value2);
        annotationService.addMetaData(owner, n);
        annotationService.removeMetaData(owner, n, matchValue);
        assertTrue("n is in owner", owner.getMetaDataSet().containMetaData(n));
        
        //--------------------------------------
        //In the case of the value match but not the key
        
        key2 = key;
        value2 = "value2";
        matchValue = true;
        
        MetaData o = new GenericMetaData(key2, value2);
        annotationService.addMetaData(owner, o);
        annotationService.removeMetaData(owner, o, matchValue);
        assertTrue("o is in owner", owner.getMetaDataSet().containMetaData(o));
        
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
        
        assertNull("MetaData not null", m);
        assertEquals("MetaData is containing by several MetaDataOwners", expectedSize, numMetadata);
    }

    
    
}
