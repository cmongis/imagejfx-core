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
import ijfx.ui.utils.SelectableManager;
import ijfx.ui.utils.SelectionChange;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.display.event.DisplayUpdatedEvent;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
@Plugin(type = Display.class)
public class DefaultExplorableDisplay extends AbstractDisplay<ExplorableList> implements ExplorableDisplay {

   

    @Parameter
    private EventService eventService;

    private List<Explorable> displayedItems = new ArrayList<>();

    private List<Explorable> items = new ArrayList<>();
    private final SelectableManager<Explorable> selectableManager = new SelectableManager<>();

    
     public DefaultExplorableDisplay() {
        super(ExplorableList.class);

        selectableManager
                .getChangeBuffer()
                .subscribe(this::onItemSelectionChanged);
    }
    
    
    @Override
    public int size() {
        return items.size();
    }
    
    public List<Explorable> getItems() {
        return items;
    }
    
    
    @Override
    public boolean add(ExplorableList list) {

        displayedItems.addAll(list);
        selectableManager.setItem(list);
        return items.addAll(list);
    }

    @Override
    public List<Explorable> getDisplayedItems() {
        return displayedItems;
    }

    @Override
    public void setFilter(Predicate<Explorable> filter) {
        if (filter == null) {
            displayedItems.clear();
            displayedItems.addAll(items);
        } else {
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

    private void onItemSelectionChanged(List<? extends SelectionChange<Explorable>> list) {

        
        
        eventService.publishLater(new DisplayUpdatedEvent(this, DisplayUpdatedEvent.DisplayUpdateLevel.UPDATE));

    }

}
