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
 * @author cyril
 */
public class ObjectCacheTest {
    
    Double next = new Double(-1);
    public Double getNext() {
        next = next + 1;
        return  next;
    }
    
    @Test
    public void testCaching() throws Exception {
        
        ObjectCache<Double> cache =  new ObjectCache<>(this::getNext);
        
        for(int i = 0; i!= 10;i++) {
            cache.getNext();
        }
        cache.reset();
        Assert.assertEquals("first in cache",0,cache.getNext(),0);
        
        Assert.assertEquals("cache = 20",20,cache.get(null, 20).size());
        System.out.println("Starting the async job");
        cache.getAsyncFragmented(null, 100, 20,this::onFinished);
        
        
        Thread.sleep(1000);
        
    }
    
    private void onFinished(List<Double> list) {
        System.out.println("1 pool finished");
        list.forEach(System.out::println);
    }
    
}
