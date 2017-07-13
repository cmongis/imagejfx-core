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
package mongis.utils.properties;

import java.util.List;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;

/**
 *
 * @author cyril
 */
public class DefaultListChangeListenerBuilder<T> implements ListChangeListener<T>,ListChangeListenerBuilder<T>{

    
    private Consumer<List<? extends T>> onAdd = list->{};
    private Consumer<List<? extends T>> onRemove = list->{};
    private Consumer<ListChangeListener.Change<? extends T>> onChange = change->{};
    
    
    @Override
    public ListChangeListenerBuilder<T> onAdd(Consumer<List<? extends T>> list) {
        onAdd = list;
        return this;
    }

    @Override
    public ListChangeListenerBuilder<T> onRemove(Consumer<List<? extends T>> list) {
        onRemove = list;
        return this;
    }

    @Override
    public ListChangeListenerBuilder<T> onChange(Consumer<ListChangeListener.Change<? extends T>> change) {
        onChange = change;
        return this;
    }

    @Override
    public ListChangeListener<T> build() {
        return this;
    }

    @Override
    public void onChanged(Change<? extends T> c) {

        while(c.next()) {
            
            onChange.accept(c);
            onAdd.accept(c.getAddedSubList());
            onRemove.accept(c.getRemoved());
            
            
        }
    }
    
}
