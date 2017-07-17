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

import com.google.common.collect.Lists;
import ijfx.explorer.datamodel.Explorable;
import java.util.List;
import java.util.function.Predicate;
import org.scijava.display.Display;

/**
 *
 * @author cyril
 */
public interface ExplorableDisplay extends Display<ExplorableList>{
    
    
    public List<Explorable> getDisplayedItems();
    
    public void setFilter(Predicate<Explorable> filter);
    
    public List<Explorable> getSelected();
    
    public void select(Explorable explorable);
    
    default void selectUntil(Explorable explorable) {
        
        select(explorable);
        if(getSelected().size() == 0) {
            
            return;
        }
        int begin = getItems().indexOf(getSelected().get(0));
        int end =  getItems().indexOf(getSelected().get(getSelected().size()-1))+1;
        
        setSelected(getItems().subList(begin, end));
        
        
    }
    
    default void selectOnly(Explorable explorable) {
        setSelected(Lists.newArrayList(explorable));
    }
    
    public void setSelected(List<Explorable> explorable);
    
    public List<Explorable> getItems();
    
    
}
