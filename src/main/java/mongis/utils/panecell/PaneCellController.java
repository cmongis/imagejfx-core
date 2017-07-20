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
package mongis.utils.panecell;

import ijfx.ui.main.ImageJFX;
import ijfx.ui.utils.CollectionsUtils;
import ijfx.ui.utils.ObjectCache;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import mongis.utils.CallbackTask;
import mongis.utils.ProgressHandler;
import mongis.utils.properties.ListChangeListenerBuilder;
import mongis.utils.properties.ServiceProperty;

/**
 * The PaneController takes care of filling a Pane with PaneCells. Like a
 * ListView, PaneCells are just container that update themselves when changes of
 * the model occurs. The PaneController cache used PaneCell and create new when
 * necessary. When the list of items change, the PaneCellController update the
 * children nodes of the associated Pane.
 *
 * @author Cyril MONGIS
 */
public class PaneCellController<T extends Object> {

    private List<T> currentItems;
    private List<PaneCell<T>> cellList;
    private List<Node> nodeList;
    //private LinkedList<PaneCell<T>> cachedControllerList = new LinkedList<>();

    private Callable<PaneCell<T>> cellFactory;

    private Logger logger = ImageJFX.getLogger();

    private Pane pane;

    private ObservableList<T> selectedItems = FXCollections.observableArrayList();

    // private PaneCellControllerFX<T> updater;
    ObjectCache<PaneCell<T>> cache;

    public PaneCellController(Pane pane) {
        setPane(pane);
    }

    /**
     * Set the pane that should be updated by the controller
     *
     * @param pane
     */
    public void setPane(Pane pane) {
        this.pane = pane;
        nodeList = pane.getChildren();
    }

    public void setCellFactory(Callable<PaneCell<T>> cellFactory) {
        this.cellFactory = cellFactory;
        cache = new ObjectCache<>(cellFactory);
    }

    // PaneCellUpdateProcess<T> updateProcess;
    Task updateProcess;

    /**
     * Give it a list of items coming from the model and the controller will
     * update the pane. If necessary, new PanelCell will be created. Unnecessary
     * PaneCells will be cached.
     *
     * @param items List of items coming from the model
     */
    public CallbackTask update(List<T> items) {
        currentItems = items;
        
        return new CallbackTask<List<T>, List<PaneCell<T>>>()
                .setInput(items)
                .callback(this::retrieveCells)
                .then(this::onCellRetrieved)
                .start();
    }

    private List<PaneCell<T>> retrieveCells(ProgressHandler handler, List<T> items) {
        return cache
                .getFragmented(handler, items.size(), 10, this::onFragmentRetrieved);
    }

    /**
     * Updates the cells when they are retrieved
     *
     * @param list
     */
    private synchronized void onFragmentRetrieved(List<PaneCell<T>> list) {
                    List<Node> cells = list
                    .stream()
                    .map(PaneCell<T>::getContent)
                    .collect(Collectors.toList());

            List<Node> toAdd = CollectionsUtils.toAdd(cells, nodeList);
            
            
            
            nodeList.addAll(toAdd);
            final int start = cache.indexOf(list);
            for (int i = 0; i != list.size(); i++) {
                list.get(i).setItem(currentItems.get(start + i));
                updateSelection(list.get(i));
            }
        
    }

    private void onCellRetrieved(List<PaneCell<T>> allCells) {
        cellList = allCells;
        CollectionsUtils.synchronize(getContent(cellList), pane.getChildren());
    }

    // get the list of cells
    protected Collection<Node> getContent(Collection<PaneCell<T>> cellList) {
        return cellList.stream().map(PaneCell::getContent).collect(Collectors.toList());
    }

    public Boolean isSelected(T item) {
        return selectedItems.contains(item);
    }

    public void setSelected(T item, Boolean selection) {
        if (selection) {
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }
        Platform.runLater(this::updateSelection);
    }

    public void setSelected(List<T> items) {
        
        CollectionsUtils.synchronize(items, selectedItems);
        Platform.runLater(this::updateSelection);
    }

    public void unselected(List<T> items) {
        Platform.runLater(this::updateSelection);
    }

    public Property<Boolean> getSelectedProperty(T item) {
        return new ServiceProperty<>(item, this::setSelected, this::isSelected);
    }

    public void updateSelection() {
        if(cellList != null)
        cellList.forEach(this::updateSelection);
    }

    public void updateSelection(PaneCell<T> cell) {
        boolean isSelected = isSelected(cell.getItem());
        cell.selectedProperty().setValue(isSelected);
    }

    public List<T> getItems() {
        return currentItems;
    }

    public List<PaneCell<T>> getCells() {
        return cellList;
    }

    public List<? extends T> getSelectedItems() {
        return selectedItems;
    }

   
}
