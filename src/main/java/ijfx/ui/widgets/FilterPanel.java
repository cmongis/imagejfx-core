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
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import mongis.utils.FXUtilities;
import mongis.utils.properties.ListChangeListenerBuilder;

/**
 *
 * @author cyril
 */
public class FilterPanel<T> {

    ObservableList<TitledPane> additionalPaneList = FXCollections.observableArrayList();

    Accordion filterVBox = new Accordion();

    ObservableList<TitledPaneFilterWrapper<T>> filterList = FXCollections.observableArrayList();

    Property<Predicate<T>> predicateProperty = new SimpleObjectProperty<>();

    
    ChangeListener<Predicate<T>> listener = this::onFilterChanged;
    
    public FilterPanel() {

        ListChangeListener<TitledPaneFilterWrapper<T>> listener
                = ListChangeListenerBuilder
                        .<TitledPaneFilterWrapper<T>>create()
                        .onAdd(this::onFilterAdded)
                        .onRemove(this::onFilterRemoved)
                        .build();
        
        filterList.addListener(listener);
    }

    public void addAdditionalPane(TitledPane pane) {
        additionalPaneList.add(pane);
    }

    public Property<Predicate<T>> predicateProperty() {
        return predicateProperty;
    }

    private void onFilterAdded(List<? extends TitledPaneFilterWrapper<T>> addedFilter) {

        addedFilter.forEach(this::listenFilter);

        FXUtilities.addLater(
                addedFilter
                        .stream()
                        .map(filter -> (TitledPane) filter.getContent())
                        .collect(Collectors.toList()),
                filterVBox.getPanes()
        );

    }

    private void onFilterRemoved(List<? extends TitledPaneFilterWrapper<T>> removedFilter) {

        removedFilter.forEach(this::stopListeningToFilter);

        FXUtilities.removeLater(
                removedFilter
                        .stream()
                        .map(filter -> (TitledPane) filter.getContent())
                        .collect(Collectors.toList()),
                filterVBox.getPanes()
        );

    }

    public void setFilters(List<DataFilter<T>> filters) {
        
        
        filterList.clear();
        filterList.addAll(
                filters
                        .stream()
                        .map(TitledPaneFilterWrapper::new)
                        .collect(Collectors.toList()));
    }

    private void listenFilter(DataFilter<T> filter) {
        filter.predicateProperty().addListener(listener);
    }

    private void stopListeningToFilter(DataFilter<T> filter) {
        
        if(filter.predicateProperty() == null) {
            throw new IllegalArgumentException(String.format("The filter %s[%s] returns no predicate",filter.getName(),filter.getClass().getName()));
        }
        
        filter.predicateProperty().removeListener(listener);
    }

    
    private void updatePredicate() {
         Predicate<T> predicate = e -> true;

        List<Predicate<T>> predicateList = filterList
                .stream()
                .map(f -> f.predicateProperty().getValue())
                .filter(v -> v != null)
                .collect(Collectors.toList());

        if (predicateList.isEmpty() == false) {

            for (Predicate<T> p : predicateList) {
                predicate = predicate.and(p);
            }
             predicateProperty.setValue(predicate);
        }
        else {
            predicateProperty.setValue(null);
        }
       

       
    }
    
    
    private void onFilterChanged(Observable obs, Predicate<T> oldValue, Predicate<T> newValue) {
        Platform.runLater(this::updatePredicate);
    }

    public Node getPane() {
        return filterVBox;
    }

}
