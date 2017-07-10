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

import ijfx.core.metadata.MetaData;
import ijfx.explorer.datamodel.Taggable;
import ijfx.ui.filter.DataFilter;
import ijfx.ui.widgets.FilterPanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mongis.utils.ProgressHandler;

/**
 *
 * @author cyril
 */
public class TaggableFilterPanel extends FilterPanel<Taggable>{
    
   
   MetaDataFilterFactory<Taggable> factory = new DefaultMetaDataFilterFactory<>();
    
   TaggableFilter taggableFilter = new DefaultTaggableFilter();
    
   public Void generateFilters(ProgressHandler handler, List<? extends Taggable> items) {

        handler.setProgress(0.1);

        Set<String> keySet = new HashSet();
        
        // first we get all the possible keys
        items
                .stream()
                .filter(owner -> owner != null)
                .map(owner -> owner.getMetaDataSet().keySet())
                .forEach(keys -> keySet.addAll(keys));

        handler.setTotal(1);

       // for each key, a filter is generated using the FilterFactory
       List<DataFilter<Taggable>> metadataFilters = keySet
               .stream()
               .filter(MetaData::canDisplay)
               .sorted((k1, k2) -> k1.compareTo(k2))
               .map(key->(DataFilter<Taggable>)factory.generateFilter(items, key))
               .filter(filter->filter != null)
               .collect(Collectors.toList());
       
       List<DataFilter<Taggable>> filters = new ArrayList<>();
       
       filters.addAll(metadataFilters);
       
       filters.add(taggableFilter);
       taggableFilter.setAllPossibleValues(items);
       setFilters(filters);
       
       
       return null;
    }
    
    
}
