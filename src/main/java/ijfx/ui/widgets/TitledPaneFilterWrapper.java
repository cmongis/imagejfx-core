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
package ijfx.ui.widgets;

import ijfx.ui.filter.DataFilter;
import java.util.Collection;
import java.util.function.Predicate;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TitledPane;

/**
 *
 * @author cyril
 */
class TitledPaneFilterWrapper<T> implements DataFilter<T> {
    
    private final DataFilter<T> filter;
    private final TitledPane pane;

    public TitledPaneFilterWrapper(DataFilter<T> filter) {
       
        
        
        
        this.filter = filter;
        if (filter != null) {
            pane = new TitledPane(filter.getName(), filter.getContent());
            pane.setExpanded(false);
            pane.getStyleClass().add("explorer-filter");
            pane.getContent().getStyleClass().add("content");
        } else {
            pane = null;
        }
    }

    @Override
    public TitledPane getContent() {
        return pane;
    }

    @Override
    public ObservableValue<Predicate<T>> predicateProperty() {
        return filter.predicateProperty();
    }

    @Override
    public void setAllPossibleValues(Collection<? extends T> values) {
        filter.setAllPossibleValues(values);
    }
    
    public DataFilter<T> getFilter() {
        return filter;
    }
    
}
