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

import mongis.utils.uuidmap.DefaultUUIDMap;
import mongis.utils.uuidmap.UUIDMap;
import mongis.utils.uuidmap.UUIDWeakHashMap;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author cyril
 */
public class UUIDMapTest {

    @Test
    public void test() {
        testMap(new DefaultUUIDMap<>());

        // testing weak hash mapping
        String key = "Weak key";

        Integer meantToDisapear = new Integer(300);

        UUIDWeakHashMap<Object> weakValueHashMap = new UUIDWeakHashMap<Object>();

        // let's do the basic testing
        testMap(weakValueHashMap);

        weakValueHashMap.key(key).put(meantToDisapear);

        // making the value null
        meantToDisapear = null;
        System.out.println(weakValueHashMap.key(key).get());
        System.gc();
        System.out.println(weakValueHashMap.key(key).get());
        Assert.assertNull(weakValueHashMap.key(key).get());
        Assert.assertNotNull(weakValueHashMap.key(key).getOrPut(new Integer(400)));

    }

    public void testMap(UUIDMap<String> uuidMap) {

        final String str1 = "String 1";
        final String str2 = "String 2";
        final String str3 = "String 3";

        Assert.assertFalse(uuidMap.key(str1, str2, str3).has());

        uuidMap.key(str1, str2, str3).put(str3);

        Assert.assertTrue("has the value", uuidMap.key(str1, str2, str3).has());

        Assert.assertEquals("key = value", str3, uuidMap.key(str1, str2, str3).getOrPut(null));

        Assert.assertFalse("order matters", uuidMap.key(str2, str1, str3).has());

        Assert.assertEquals("default value works", "Hello", uuidMap.key(str2, str3).getOrPut("Hello"));
        Assert.assertEquals("default value works 2", "Hello", uuidMap.key(str1, str2).getOrPutFrom(() -> "Hello"));
        Assert.assertEquals("default value works 3", str3, uuidMap.key(str1, str2, str3).getOrPut("Hello"));
    }

}
