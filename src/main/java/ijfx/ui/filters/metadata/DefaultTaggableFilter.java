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
package ijfx.ui.filters.metadata;

import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.ui.filters.metadata.TaggableFilter;
import ijfx.ui.filter.string.DefaultStringFilter;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 *
 * @author Cyril MONGIS
 */
public class DefaultTaggableFilter implements TaggableFilter {

    DefaultStringFilter filter = new DefaultStringFilter();

    ObservableValue<Predicate<Taggable>> predicateProperty = Bindings.createObjectBinding(this::getPredicate, filter.predicateProperty());

    @Override
    public void setName(String name) {

    }

    
    
    @Override
    public ObservableValue<Predicate<Taggable>> predicateProperty() {

        return predicateProperty;

    }

    public void reset() {

    }

    public Predicate<Taggable> getPredicate() {
        
        if(filter.predicateProperty().getValue() == null) {
            return null;
        }
        
        return taggable -> taggable
                .getTagList()
                .parallelStream()
                .map(Tag::getName)
                .filter(filter.predicateProperty().getValue())
                .count() > 0;
    }

    @Override
    public Node getContent() {
        return filter.getContent();
    }
    
    @Override
    public String getName() {
        return "By tags";
    }

    @Override
    public void setAllPossibleValues(Collection<? extends Taggable> values) {
        filter.setAllPossibleValues(values
                .stream()
                .flatMap(taggable->taggable.getTagList().stream())
                .map(tag -> tag.getName())
                .collect(Collectors.toSet()));
    }

}
