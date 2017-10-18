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
package mongis.utils.uuidmap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cyril MONGIS
 */
public class DefaultUUIDMap<T>  implements UUIDMap{
    
    
    protected Map<UUID,T> map;

    public DefaultUUIDMap() {
        
        map = new HashMap<UUID,T>();
        
    }
    
    
    
    
    private UUID transform(Object... object) {
        
        ByteBuffer allocate = ByteBuffer
                .allocate(object.length*8);
        for(Object o : object) {
            allocate.putInt(o.hashCode());
        }
        Arrays.toString(allocate.array());
        return UUID.nameUUIDFromBytes(allocate.array());
        
    }
    
    public Accessor<T> key(Object... object) {
       UUID uuid = transform(object);
       return new DefaultPutter(this,uuid);
    }
    
    public UUID getId(Object... object) {
        return transform(object);
    }
    
    
    protected class DefaultPutter implements UUIDMap.Accessor<T> {
        final UUID uuid;
        final UUIDMap<T> parent;
        
        public DefaultPutter(DefaultUUIDMap<T> parent, UUID uuid) {
            this.uuid = uuid;
            this.parent = parent;
        }

        public T get(){
            return map.get(id());
        }
        
        public T getOrPut(T t) {          
            if(map.containsKey(uuid) == false || map.get(uuid) == null)  {
                map.put(uuid,t);
            }
            
            return map.get(uuid);
        }
        
        public T getOrPutFrom(Callable<T> getter){
            
            if(map.containsKey(uuid) == false || map.get(uuid) == null) {
                try {
                    put(getter.call());
                   
                }
                catch(Exception e) {
                    Logger.getLogger(DefaultUUIDMap.class.getName()).log(Level.SEVERE,"Error when getting value",e);
                }
                
            }
            
            return map.get(uuid);
        }
        
        @Override
        public UUIDMap<T> put(T t) {
           map.put(uuid,t);
           return parent;
        }

        @Override
        public boolean has() {
           return map.containsKey(uuid) && map.get(uuid) != null;
        }
        
        public UUID id() {
            return uuid;
        }
        
    }
    
   
  
    
}
