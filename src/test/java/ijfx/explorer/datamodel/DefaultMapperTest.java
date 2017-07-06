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
package ijfx.explorer.datamodel;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sapho
 */
public class DefaultMapperTest {
    

    private String oldKey;
    private String newKey;
    private Object gfp = "gfp";
    private Object value;
    private MetaData m; 
    private MetaData n;
    DefaultMapper instance = new DefaultMapper();
    public HashMap <Object, Object> mapValue = new HashMap();
    
    public DefaultMapperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    @Before
    public void setUp() {
        
        mapValue.put(5.0, gfp);
        mapValue.put(0.0, "ncherry");
        mapValue.put(1.0, "bright");
        
        oldKey = "key";
        value = 5.0;
        newKey = "newKey";
        m = MetaData.create(oldKey, value);
        n = MetaData.create(newKey, gfp);
        
        instance.associatedValues(5.0, gfp);
        instance.associatedValues(0.0, "ncherry");
        instance.associatedValues(1.0, "bright");
        instance.setNewKey(newKey);
        instance.setOldKey(oldKey);
        
        
        
        
        
    }
    
    @After
    public void tearDown() {
        oldKey = null;
        value = null;
        m = null;
    }

    /**
     * Test of map method, of class DefaultMapper.
     */
    @Test
    public void testMap() {
        System.out.println("map");
        
        assertNotNull("n is null", instance.map(m));
        assertNotSame("Metadata are the same", m, instance.map(m));
        assertEquals("n et map(m) different",n , instance.map(m));
    }

    /**
     * Test of getMapObject method, of class DefaultMapper.
     */
    @Test
    public void testGetMapObject() {
        System.out.println("getMapObject");
        
        HashMap<Object, Object> result = instance.getMapObject();
        assertEquals("map is not null",mapValue, result);
        
    }

   
    

    /**
     * Test of getNewKey method, of class DefaultMapper.
     */
    @Test
    public void testGetNewKey() {
        System.out.println("getNewKey");
        String expResult = newKey;
        String result = instance.getNewKey();
        assertEquals("keys not equals",expResult, result);
        
    }

    /**
     * Test of associatedValues method, of class DefaultMapper.
     */
    @Test
    public void testAssociatedValues() {
        System.out.println("associatedValues");
        Object basisValue = 6.0;
        Object newValue = "light";
        instance.associatedValues(basisValue, newValue);
        mapValue.put(basisValue, newValue);
        assertEquals("map are not the same", mapValue, instance.getMapObject());
        assertEquals("values are not the same",mapValue.get(oldKey), instance.getMapObject().get(oldKey));
        
    }

    /**
     * Test of setNewKey method, of class DefaultMapper.
     */
    @Test
    public void testSetNewKey() {
        System.out.println("setNewKey");
        String s = "newkeyyyy";
        instance.setNewKey(s);
        assertEquals("keys are different", s, instance.getNewKey());
        instance.setNewKey(newKey);
        
    }

    /**
     * Test of lookInsideMap method, of class DefaultMapper.
     */
    @Test
    public void testLookInsideMap() {
        System.out.println("lookInsideMap");
        Object result = instance.lookInsideMap(5.0);
        Object expectResult = gfp;
        assertEquals("bad research", expectResult, result);
        
    }
    
}
