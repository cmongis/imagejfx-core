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
package ijfx.explorer.events;

import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.utils.SelectionChange;
import java.util.List;
import java.util.stream.Collectors;
import org.scijava.event.SciJavaEvent;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class ExplorerSelectionChangedEvent extends SciJavaEvent{

    
    public ExplorerSelectionChangedEvent() {
        
    }
    
    public ExplorerSelectionChangedEvent(List<SelectionChange<Explorable>> changes) {
    
        setSelectedSublist(changes
                .stream()
                .filter(SelectionChange::getNewState)
                .map(SelectionChange::getSelectable)
                .collect(Collectors.toList())
        );
        
        setUnselectedSublist(changes
                .stream()
                .filter(exp->exp.getNewState() == false)
                .map(SelectionChange::getSelectable)
                .collect(Collectors.toList())
        );
        
    
    }
     
    
    private List<? extends Explorable> selectedSublist;
    
    private List<? extends Explorable> unselectedSublist;

    public void setSelectedSublist(List<? extends Explorable> selectedSublist) {
        this.selectedSublist = selectedSublist;
    }

    public List<? extends Explorable> getSelectedSublist() {
        return selectedSublist;
    }

    public void setUnselectedSublist(List<? extends Explorable> unselectedSublist) {
        this.unselectedSublist = unselectedSublist;
    }

    public List<? extends Explorable> getUnselectedSublist() {
        return unselectedSublist;
    }
    
    
    
    
}
