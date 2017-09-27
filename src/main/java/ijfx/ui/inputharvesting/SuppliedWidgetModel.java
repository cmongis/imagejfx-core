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
package ijfx.ui.inputharvesting;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 *
 * @author Cyril MONGIS
 */
public class SuppliedWidgetModel<T> extends AbstractWidgetModel {

    private Callable<T> getter;

    private Consumer<T> setter;

    public SuppliedWidgetModel() {
        
    }
    
   
    
    public SuppliedWidgetModel(Class<T> type) {
        super(type);
    }
    

    public SuppliedWidgetModel<T> setSetter(Consumer<T> setter) {
        this.setter = setter;
        return this;
    }

    public SuppliedWidgetModel<T> setGetter(Callable<T> getter) {
        this.getter = getter;
        return this;
    }

    @Override
    public Object getValue() {
        try {
            return getter.call();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setValue(Object value) {
        setter.accept((T) value);
    }

}
