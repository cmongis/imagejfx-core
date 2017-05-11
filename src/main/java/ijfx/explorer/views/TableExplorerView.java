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
package ijfx.explorer.views;

import ijfx.core.hint.HintService;
import ijfx.core.metadata.MetaDataKeyPriority;
import ijfx.core.metadata.MetaDataSetUtils;
import ijfx.explorer.ExplorerService;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.events.ExplorerSelectionChangedEvent;
import ijfx.ui.display.metadataowner.MetaDataOwnerHelper;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.utils.SelectableManager;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = ExplorerView.class, priority = 0.9, label = "Data Table", iconPath = "fa:table")
public class TableExplorerView implements ExplorerView {

    TableView<Explorable> tableView = new TableView<>();

    MetaDataOwnerHelper<Explorable> helper = new MetaDataOwnerHelper(tableView);

    @Parameter
    EventService eventService;

    @Parameter
    private ExplorerService explorerService;

    @Parameter
    HintService hintService;

    SelectableManager<Explorable> selectableManager = new SelectableManager<>(this::onItemSelectionChanged);

    private static final String TABLE_VIEW_ID = "tableViewView";

    List<? extends Explorable> currentItems;

    Logger logger = ImageJFX.getLogger();

    public TableExplorerView() {

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().getSelectedItems().addListener(this::onListChange);
        tableView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
        tableView.setRowFactory(this::createRow);
        tableView.setId(TABLE_VIEW_ID);
    }

    @Override
    public Node getUIComponent() {
        return tableView;
    }

    String[] priority = new String[0];

    @Override
    public void setItem(List<? extends Explorable> items) {

        ImageJFX.getLogger().info(String.format("Setting %d items", items.size()));

        boolean columnsHasChanged = MetaDataSetUtils.getKeys(items).size() != MetaDataSetUtils.getKeys(currentItems).size();

        // if checking the priority first
        if (items.size() > 0) {
            priority = MetaDataKeyPriority.getPriority(items.get(0).getMetaDataSet());
        }
        // reseting the priorirty
        helper.setPriority(priority);

        helper.setColumnsFromItems(items);

        // if the item number has changed, let's refresh it to
        if (items != currentItems || items.size() != tableView.getItems().size()) {
            helper.setItem(items);
        }

        selectableManager.setItem(items);

        currentItems = items;

        List<? extends Explorable> selected = items
                .stream()
                .filter(item -> item.selectedProperty().getValue())
                .collect(Collectors.toList());

        setSelectedItem(selected);
        displayHints();

    }

    @Override
    public List<? extends Explorable> getSelectedItems() {
        return tableView
                .getSelectionModel()
                .getSelectedItems()
                .stream()
                .map(i -> (Explorable) i)
                .collect(Collectors.toList());
    }

    @Override
    public void setSelectedItem(List<? extends Explorable> items) {
        items.forEach(tableView.getSelectionModel()::select);

    }

    private void onListChange(ListChangeListener.Change<? extends Explorable> changes) {

        while (changes.next()) {

            logger.info(String.format("Selection changed : %d newly selected, %d unselected", changes.getAddedSize(), changes.getRemovedSize()));
            changes.getAddedSubList()
                    .stream()
                    .map(owner -> (Explorable) owner)
                    .forEach(explo -> explo.selectedProperty().setValue(true));

            changes.getRemoved()
                    .stream()
                    .map(owner -> (Explorable) owner)
                    .forEach(explo -> explo.selectedProperty().setValue(false));

        }

    }

    private void displayHints() {

        //hintService.displayHint(new DefaultHint(String.format("#%s",TABLE_VIEW_ID),"Double click on an element to open it."), false);
    }

    private void onSelectedItemChanged(Observable obs, Explorable oldValue, Explorable newValue) {
        currentItems.forEach(item -> {
            item.selectedProperty().setValue(tableView.getSelectionModel().getSelectedItems().contains(item));
        });

        if (eventService != null) {
            eventService.publish(new ExplorerSelectionChangedEvent().setObject(explorerService.getSelectedItems()));
        }

    }

    private TableRow<Explorable> createRow(TableView<Explorable> explorable) {

        TableRow<Explorable> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && row.isEmpty() == false) {
                Explorable e = row.getItem();
                explorerService.open(e);
            }
        });

        return row;
    }

    private void onItemSelectionChanged(Explorable explorable, Boolean selected) {

        if (selected) {
            tableView.getSelectionModel().select(explorable);
        }

    }

}
