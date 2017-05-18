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
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.explorer.datamodel.Taggable;
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
        Taggable taggable = null;
        /*
        AnnotationService instance = new AnnotationService();
        instance.addTag(taggable);
        */
        Assert.assertNotNull("Testing preinjection", annotationService);
        //fail("The test case is a prototype.");
    }

    /**
     * Test of removeTag method, of class AnnotationService.
     */
    @Test
    public void testRemoveTag() {
        System.out.println("removeTag");
        Taggable taggable = null;
        /*
        AnnotationService instance = new AnnotationServiceImpl();
        instance.removeTag(taggable);
        */
        // TODO review the generated test code and remove the default call to fail.
        Assert.assertNotNull("Testing preinjection", annotationService);
    }

    /**
     * Test of addMetaData method, of class AnnotationService.
     */
    @Test
    public void testAddMetaData_MetaDataOwner_MetaData() {
        System.out.println("addMetaData");
        MetaDataOwner owner = null;
        MetaData m = null;
        /*
        AnnotationService instance = new AnnotationServiceImpl();
        instance.addMetaData(owner, m);
        */
        // TODO review the generated test code and remove the default call to fail.
        Assert.assertNotNull("Testing preinjection", annotationService);
    }

    /**
     * Test of removeMetaData method, of class AnnotationService.
     */
    @Test
    public void testRemoveMetaData_3args() {
        System.out.println("removeMetaData");
        MetaDataOwner owner = null;
        MetaData m = null;
        boolean matchValue = false;
        /*
        AnnotationService instance = new AnnotationServiceImpl();
        instance.removeMetaData(owner, m, matchValue);
        */
        // TODO review the generated test code and remove the default call to fail.
        Assert.assertNotNull("Testing preinjection", annotationService);
    }

    /**
     * Test of addMetaData method, of class AnnotationService.
     */
    @Test
    public void testAddMetaData_List_MetaData() {
        System.out.println("addMetaData");
        List<? extends MetaDataOwner> list = null;
        MetaData m = null;
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
