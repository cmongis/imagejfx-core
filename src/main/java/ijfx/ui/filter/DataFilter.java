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
package ijfx.ui.filter;

import java.util.Collection;
import java.util.function.Predicate;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 *
 * @author Cyril MONGIS
 */
public interface DataFilter<T> {

    public static String DEFAULT_NAME = "Filter";
    
    void setAllPossibleValues(Collection<? extends T> values);

    void setName(String name);
    
    String getName();
    
    public default Predicate<T> getPredicate() {
        return predicateProperty().getValue();
    }

    ObservableValue<Predicate<T>> predicateProperty();

    Node getContent();
}
