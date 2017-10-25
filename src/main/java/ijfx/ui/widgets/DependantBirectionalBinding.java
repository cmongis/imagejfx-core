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

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author cyril
 */
public class DependantBirectionalBinding<T> {
    
    
    final Property<T> source;
    
    final Property<T> protectedTarget;
    
    final ObservableValue<Boolean> obs;

    public DependantBirectionalBinding(Property<T> source, Property<T> protectedTarget, ObservableValue<Boolean> obs) {
        this.source = source;
        this.protectedTarget = protectedTarget;
        this.obs = obs;
        
        source.addListener(this::onChangeA);
        protectedTarget.addListener(this::onChangeB);
        
    }
    
    public boolean canChange() {
        return obs.getValue();
    }
    
    public void onChangeA(ObservableValue a, T oldValue, T newValue) {
        if(canChange()) {
            protectedTarget.setValue(newValue);
        }
    }
    
    public void onChangeB(ObservableValue b, T oldValue, T newValue) {
        //if(oldValue.equals(newValue)) return;
        source.setValue(newValue);
    }
    
    public void refresh() {
        source.setValue(protectedTarget.getValue());
    }
    
}
