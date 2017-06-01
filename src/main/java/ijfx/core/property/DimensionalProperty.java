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
package ijfx.core.property;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import static javafx.scene.input.KeyCode.T;

/**
 *
 * @author cyril
 */
public class DimensionalProperty<T> {

    public static <T> JavaBeanObjectProperty<T> createProperty(Object bean,Class<? extends T> type, String property, int dim) {
       final DimensionalHelper dimensionalHelper = new DimensionalHelper(bean,type, property, dim);
        try {
            return new JavaBeanObjectPropertyBuilder<>()
                    .bean(dimensionalHelper)
                    .name(property)
                    .setter("set")
                    .getter("get")
                    .build();
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static class DimensionalHelper<T> {

        private final DimensionalGetter<T> getter;
        private final DimensionalSetter<T> setter;

        private final Class<? extends T> type;
        
        public DimensionalHelper(Object bean, Class<? extends T> type, String property, int dim) {
            this.type = type;
            getter = new DimensionalGetter<T>(bean,type, "get" + capitalize(property), dim);
            setter = new DimensionalSetter<T>(bean,type, "set" + capitalize(property), dim);

        }

        public void set(T t) {
            setter.accept(t);
        }

        public T get() {
            return getter.call();
        }

    }

}
