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
    

    private String key;
    private Object value;
    private MetaData m; 
    private HashMap <Object, Object> mapMapper = new HashMap();
    private Mapper mapper = new DefaultMapper();
    
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
        key = "key";
        value = "value";
        m = MetaData.create(key, value);
        
        mapMapper.put(5.0, "truc");
        mapMapper.put(0.0, "machin");
        mapMapper.put(1.0, "bidule");
        
    }
    
    @After
    public void tearDown() {
        key = null;
        value = null;
        m = null;
        mapMapper = null;
    }

    /**
     * Test of map method, of class DefaultMapper.
     */
    @Test
    public void testMap() {
        System.out.println("map");
        
        assertNotNull("n is null", mapper.map(m));
        assertNotSame("Metadata are the same", m, mapper.map(m));
    }

    /**
     * Test of getMapObject method, of class DefaultMapper.
     */
    @Test
    public void testGetMapObject() {
        System.out.println("getMapObject");
        DefaultMapper instance = new DefaultMapper();
        HashMap<Object, Object> result = instance.getMapObject();
        assertEquals("map is not null",mapMapper, result);
        
    }

   
    

    /**
     * Test of getNewKey method, of class DefaultMapper.
     */
    @Test
    public void testGetNewKey() {
        System.out.println("getNewKey");
        DefaultMapper instance = new DefaultMapper();
        String expResult = "newkey";
        String result = instance.getNewKey();
        assertEquals("keys not equals",expResult, result);
        
    }

    /**
     * Test of associatedValues method, of class DefaultMapper.
     */
    @Test
    public void testAssociatedValues() {
        System.out.println("associatedValues");
        DefaultMapper instance = new DefaultMapper();
        instance.associatedValues(key, value);
        mapMapper.clear();
        mapMapper.put(key, value);
        assertEquals("map are not the same", mapMapper, instance.getMapObject());
        assertEquals("values are not the same",mapMapper.get(key), instance.getMapObject().get(key));
        
    }

    /**
     * Test of setNewKey method, of class DefaultMapper.
     */
    @Test
    public void testSetNewKey() {
        System.out.println("setNewKey");
        String s = "newkeyyyy";
        DefaultMapper instance = new DefaultMapper();
        instance.setNewKey(s);
        assertEquals("keys are different", s, instance.getNewKey());
        
    }

    /**
     * Test of lookInsideMap method, of class DefaultMapper.
     */
    @Test
    public void testLookInsideMap() {
        System.out.println("lookInsideMap");
        DefaultMapper instance = new DefaultMapper();
        Object result = instance.lookInsideMap(5.0);
        Object truc = "truc";
        assertEquals("bad research", truc, result);
        
    }
    
}
