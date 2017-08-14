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
import ijfx.core.timer.Timer;
import ijfx.core.timer.TimerService;
import ijfx.explorer.datamodel.Taggable;
import ijfx.ui.filter.DataFilter;
import ijfx.ui.loading.LoadingScreenService;
import ijfx.ui.widgets.FilterPanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mongis.utils.CallbackTask;
import mongis.utils.ProgressHandler;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class TaggableFilterPanel extends FilterPanel<Taggable> {

    MetaDataFilterFactory<Taggable> factory = new DefaultMetaDataFilterFactory<>();

    TaggableFilter taggableFilter = new DefaultTaggableFilter();

    @Parameter
    TimerService timerService;

    @Parameter
    LoadingScreenService loadingScreenService;
    
    Timer timer;

    public void updateFilters(ProgressHandler handler, List<? extends Taggable> items) {

        new CallbackTask<List<? extends Taggable>, List<DataFilter<Taggable>>>()
                .setInput(items)
                .callback(this::generateFilters)
                .then(this::setFilters)
                .start()
                .submit(loadingScreenService);
    }

    /**
     * This function generates filters from a list of taggables. The filters are
     * not set inside the panes though.
     *
     * @param handler
     * @param items
     * @return The list of DataFilters to add to the pane (usually done)
     */
    private List<DataFilter<Taggable>> generateFilters(ProgressHandler handler, List<? extends Taggable> items) {

        factory.recycleCache();
        handler.setProgress(0.1);

        Set<String> keySet = new HashSet();

        logTimer("time since last update");
        // first we get all the possible keys
        items
                .parallelStream()
                .filter(owner -> owner != null)
                .map(owner -> owner.getMetaDataSet().keySet())
                .forEach(keys -> keySet.addAll(keys));
        logTimer("getting all possible keys");
        handler.setTotal(1);

        // for each key, a filter is generated using the FilterFactory
        List<DataFilter<Taggable>> metadataFilters = keySet
                .parallelStream()
                .filter(MetaData::canDisplay)
                .parallel()
                .map(key -> (DataFilter<Taggable>) factory.generateFilter(items, key))
                .filter(filter -> filter != null)
                .sorted((k1, k2) -> k1.getName().compareTo(k2.getName()))
                .collect(Collectors.toList());

        logTimer("genereting filters");
        List<DataFilter<Taggable>> filters = new ArrayList<>();

        filters.addAll(metadataFilters);

        filters.add(taggableFilter);

        taggableFilter.setAllPossibleValues(items);
        logTimer("updating taggable filter");

        return filters;

    }

    private void logTimer(String message) {

        if (timerService != null) {

            if (timer == null) {
                timer = timerService.getTimer("TaggableFilterPanel");
                timer.start();
            }

            timer.elapsed(message);

        }
    }

}
