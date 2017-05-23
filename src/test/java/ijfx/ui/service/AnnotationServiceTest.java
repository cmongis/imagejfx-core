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

import ijfx.core.IjfxTest;
import ijfx.core.metadata.GenericMetaData;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.explorer.datamodel.DefaultTag;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.explorer.views.GenerateDummyExplorables;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;

/**
 *
 * @author sapho
 */
public class AnnotationServiceTest extends IjfxTest{
    
    @Parameter
    AnnotationService annotationService;
    
    public AnnotationServiceTest() {
    }
    

    /**
     * Test of addTag method, of class AnnotationService.
     */
    @Test
    public void testAddTag() {
        System.out.println("addTag");
        Taggable taggable  = (Taggable) new GenerateDummyExplorables();
        Tag tag = new DefaultTag("prout");
        annotationService.addTag(taggable, tag);
        Assert.assertNotNull("Testing tag creation", tag);
    }

    /**
     * Test of removeTag method, of class AnnotationServic
     */
    @Test
    public void testRemoveTag() {
        System.out.println("removeTag");
        Taggable taggable  = (Taggable) new GenerateDummyExplorables();
        Tag tag = new DefaultTag("prout");
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
        String key = "key";
        Object value = "Value";
        Explorable owner = (Explorable) new GenericMetaData(key,value);
        MetaData m = new GenericMetaData();
        annotationService.addMetaData(owner, m);
        assertNotNull("Testing addMetaDataOwner", owner);
        assertNotNull("test Metadata",m);
    }

    /**
     * Test of removeMetaData method, of class AnnotationService.
     */
    @Test
    public void testRemoveMetaData_3args() {
        System.out.println("removeMetaData with boolean");
        String key = "key";
        Object value = "Value";
        Explorable owner = (Explorable) new GenericMetaData(key,value);
        MetaData m = new GenericMetaData();
        boolean matchValue = false;
        annotationService.addMetaData(owner, m);
        annotationService.removeMetaData(owner, m, matchValue);
        assertNull("Testing remove metadata with boolean owner", owner);
        assertNull("Testing remove metadata with boolean m",m);
    }

    /**
     * Test of addMetaData method, of class AnnotationService.
     */
    @Test
    public void testAddMetaData_List_MetaData() {
        System.out.println("addMetaData list");
        List<? extends MetaDataOwner> list = null;
        MetaData m = new GenericMetaData();
        /*
        AnnotationService instance = new AnnotationServiceImpl();
        instance.addMetaData(list, m);
        */
        // TODO review the generated test code and remove the default call to fail.
        Assert.assertNotNull("Testing preinjection", annotationService);
    }

    /**
     * Test of removeMetaData method, of class AnnotationService.
     */
    @Test
    public void testRemoveMetaData_List_MetaData() {
        System.out.println("removeMetaData");
        List<? extends MetaDataOwner> list = null;
        MetaData m = null;
        /*
        AnnotationService instance = new AnnotationServiceImpl();
        instance.removeMetaData(list, m);
*/
        // TODO review the generated test code and remove the default call to fail.
        Assert.assertNotNull("Testing preinjection", annotationService);
    }

    
    
}
