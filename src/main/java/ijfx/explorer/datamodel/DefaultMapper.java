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

import ijfx.core.metadata.GenericMetaData;
import ijfx.core.metadata.MetaData;
import java.util.HashMap;

/**
 *
 * @author sapho
 */
public class DefaultMapper implements Mapper {

    //final MetaData m;
    public HashMap <Object, Object> mapValue = new HashMap();
    public String newKey = "newkey";
    
    public DefaultMapper (){
        mapValue.put(5.0, "truc");
        mapValue.put(0.0, "machin");
        mapValue.put(1.0, "bidule");
        
    }

    /**
     * Create a new Metadata according to a first metadata.
     * 
     * @param m
     * @return 
     */

    @Override
    public MetaData map(MetaData m) {
        Object newValue = lookInsideMap(m.getValue());
        MetaData n = new GenericMetaData(newKey, newValue);
        return n;
        
    }
    
    

    public HashMap<Object, Object> getMapObject() {
        return mapValue;
    }
    
        
    public String getNewKey (){
        return newKey;
    }
    
    /**
     * Create the mapper associated Value:value for
     * the creation of new Metadata
     * @param base
     * @param associated 
     */
    public void associatedValues (Object base, Object associated){
        if (base != null && associated != null){
            mapValue.put(base, associated);
        }
    }
    
    public void setNewKey (String s){
        this.newKey = s;
    }
    
    /**
     * Looking for the conrresponding value on the mapper
     * @param base
     * @return 
     */
    public Object lookInsideMap (Object base){
        System.out.println("base " +base);
        if (mapValue.containsKey(base)){
            System.out.println(mapValue.get(base));
            return mapValue.get(base);
            
        }
        System.out.println("null !!!");
        return null;
    
        
         
    }
    

    
    
}
