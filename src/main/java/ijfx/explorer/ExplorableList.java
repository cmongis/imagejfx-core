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
package ijfx.explorer;

import ijfx.explorer.datamodel.Explorable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author cyril
 */
public class ExplorableList extends ArrayList<Explorable>{
    
    public ExplorableList() {
        super();
    }
    
   
    public ExplorableList(List<? extends Explorable> list) {
        super(list.size());
        addAll(list);
    }
    
    
   
    
    
    /**
     * Return a hash that only takes account the which elements are
     * in the list
     * @return hash
     */
    public static int listHash(Collection<? extends Explorable> list) {
     if(list == null) return 0;
        
        return list
                .stream()
                .mapToInt(exp->exp.hashCode())
                
                .parallel()
                .sum();
    
    }
    
   
    
    public static int listHashWithOrder(List<? extends Explorable> list) {
        
        if(list == null) return 0;
        
        return IntStream
                
                .range(0, list.size())
                .map(i->list.get(i) == null ? (i+1) * -1 : list.get(i).hashCode() * (i+1))
                .sum();
        
    }
    
     public static int contentHash(Collection<? extends Explorable> list) {
     if(list == null) return 0;
        
        return list
                .stream()
                .mapToInt(exp->exp.dataHashCode())
                .parallel()
                .sum();
    
    }
    
    
    public static int contentHashWidthOrder(List<? extends Explorable> list) {
        
        if(list == null) return 0;
        
        return IntStream
                
                .range(0, list.size())
                .map(i->list.get(i) == null ? (i+1) * -1 : list.get(i).hashCode() * (i+1))
                .sum();  
    }
    
    
}
