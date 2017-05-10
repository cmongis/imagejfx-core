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
package ijfx.explorer.display;

import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.datamodel.Explorable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
@Plugin(type = Display.class)
public class DefaultExplorableDisplay extends AbstractDisplay<ExplorableList> implements ExplorableDisplay {

    public DefaultExplorableDisplay() {
        super(ExplorableList.class);
    }
    
    List<Explorable> displayedItems = new ArrayList<>();

    List<Explorable> items = new ArrayList<>();
    
    @Override
    public boolean add(ExplorableList list) {
        
        displayedItems.addAll(list);
        
        return items.addAll(list);
    }
  

    @Override
    public List<Explorable> getDisplayedItems() {
        return displayedItems;
    }

    @Override
    public void setFilter(Predicate<Explorable> filter) {
        if(filter == null) {
            displayedItems.clear();
            displayedItems.addAll(items);
        }
        else {
            displayedItems.clear();
            displayedItems = displayedItems
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<Explorable> getSelected() {
        return items
                .stream()
                .filter(Explorable::isSelected)
                .collect(Collectors.toList());
    }

    @Override
    public void setSelected(List<Explorable> explorable) {
        
        clearSelection();
        
    }
    
    private void clearSelection() {
        
    }

}
