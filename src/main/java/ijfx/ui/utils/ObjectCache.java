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
package ijfx.ui.utils;

import ijfx.ui.main.ImageJFX;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import mongis.utils.CallableTask;
import mongis.utils.CallbackTask;
import mongis.utils.ProgressHandler;

/**
 *
 * @author cyril
 */
public class ObjectCache<T> {

    final private List<T> cache = new ArrayList<>();

    private Callable<T> factory;

    Logger logger = ImageJFX.getLogger();
    
    private int position;
    
    
    
    
    public ObjectCache() {
        
    }

    public ObjectCache(Callable<T> factory) {
        this.factory = factory;
    }

    public ObjectCache<T> setFactory(Callable<T> factory) {
        this.factory = factory;
        return this;
    }

    public List<T> get(ProgressHandler handler, Integer size) {
        return get(handler,0,size);
    }
    
    public void reset() {
        position = 0;
    }
    
    
    public synchronized T getNext() {
        if(position >= cache.size()) {
            // safe way to make sure there will be no missing object in the cache
            // in cache the cache has been cleared
            cache.addAll(get(null, position,1));
        }
        T object = cache.get(position);
        position++;
        return object;
    }
    
    public List<T> get(final ProgressHandler handler, int start, Integer size) {
        int required = start + size - cache.size();
        
        if(required > 0) {
            if(handler != null) handler.setTotal(required);
            
            List<T> collect = IntStream
                    .range(0, size)
                    .parallel()
                    .mapToObj(i->{
                        if(handler != null) handler.increment(1);
                        try {
                            return factory.call();
                        }
                        catch(Exception e) {
                            logger.log(Level.SEVERE, "Error when filling cache",e);
                        }
                        return null;
                    })
                    .collect(Collectors.toList());
            
            cache.addAll(collect);
            
        }
        return cache.subList(0, size);
    }
    
    public CallbackTask<Integer,List<T>> getAsync(Integer size, Consumer<List<T>> onFinshed) {
        return new CallbackTask<Integer,List<T>>()
                .setInput(size)
                .run(this::get)
                .then(onFinshed);
    }
    
    public void getAsyncFragmented(ProgressHandler handler, Integer totalSize, int fragment, Consumer<List<T>> onFinshed) {
        
        // creating a thread that will hold the creation task
        ExecutorService thread = Executors.newFixedThreadPool(1);
        
        
        for(int i = 0; i < totalSize; i+= fragment) {
            
            // defining the number of object required for this fragment
            final int required = i+fragment < totalSize ? fragment : totalSize - i;
            
            // christalizing the start index
            final int start = i;
            
            
            CallableTask<List<T>> task = new CallableTask<List<T>>(()->get(handler,start,required))
                    .then(onFinshed);
            
            logger.info("Adding task to fragmentation thread");
            // add the
            thread.execute(task); 
        }
        
        thread.shutdown();
    }
    
    
}
