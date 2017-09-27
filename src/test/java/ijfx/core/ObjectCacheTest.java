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

import ijfx.ui.utils.ObjectCache;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Cyril MONGIS
 */
public class ObjectCacheTest {

    Integer next = new Integer(-1);

    public Integer getNext() {
        next = next + 1;
        return next;
    }

    @Test
    public void testCaching() throws Exception {

        ObjectCache<Integer> cache = new ObjectCache<>(this::getNext);

        
        cache.reset();
        // creating object one after the other
        for (int i = 0; i != 10; i++) {
            cache.getNext();
        }

        // recycling object
        cache.reset();

        // after recycling, the cache should return the first object which is 0
        Assert.assertEquals("first in cache", 0, cache.getNext(), 0);

        Assert.assertEquals("cache size", 10, cache.size());

        cache.reset();

        cache.get(null, 0, 20);

        Assert.assertEquals("cache size", 20, cache.size());

        cache.get(null,10,20);
        Assert.assertEquals("cache size", 30,cache.size());
        
        // testing async creationg
        System.out.println("Starting the async job");
        //cache.getAsyncFragmented(null, 60, 7, this::onFinished);

        Thread.sleep(1000);
        
        //Assert.assertEquals("cache size", 60, cache.size());

    }

    private void onFinished(List<Integer> list) {
        System.out.println("1 pool finished");
        list.forEach(System.out::println);
    }

}
