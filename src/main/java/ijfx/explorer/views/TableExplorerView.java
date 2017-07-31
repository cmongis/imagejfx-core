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
import ijfx.explorer.datamodel.Tag;
import ijfx.ui.display.metadataowner.MetaDataOwnerHelper;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.utils.CollectionsUtils;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = ExplorerView.class, priority = 0.9, label = "Data Table", iconPath = "fa:table")
public class TableExplorerView implements ExplorerView {

    private final TableView<Explorable> tableView = new TableView<>();

    private final MetaDataOwnerHelper<Explorable> helper = new MetaDataOwnerHelper(tableView);

    @Parameter
    private EventService eventService;

    @Parameter
    private ExplorerService explorerService;

    @Parameter
    private HintService hintService;

    //private final SelectableManager<Explorable> selectableManager = new SelectableManager<>(this::onItemSelectionChanged);
    private static final String TABLE_VIEW_ID = "tableViewView";

    private List<? extends Explorable> currentItems;

    private final static Logger logger = ImageJFX.getLogger();

    private final TableColumn<Explorable, String> tagColumn = new TableColumn();

    private final WeakHashMap<Explorable, ReadOnlyTagWrapper> wrapperList = new WeakHashMap<>();

    private Consumer<DataClickEvent<Explorable>> onItemClicked;

    public TableExplorerView() {

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //tableView.getSelectionModel().getSelectedItems().addListener(this::onListChange);
        //tableView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
        tableView.setRowFactory(this::createRow);
        tableView.setId(TABLE_VIEW_ID);

        //tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
        tagColumn.setCellValueFactory(this::getTagListProperty);
        tagColumn.setText("Tags");
        helper.addAdditionalColumn(tagColumn);

    }

    @Override
    public MultipleSelectionModel getSelectionModel() {
        return tableView.selectionModelProperty().getValue();
    }

    @Override
    public Node getUIComponent() {
        return tableView;
    }

    String[] priority = new String[0];

    public List<? extends Explorable> getItems() {
        return currentItems;
    }

    
    
    @Override
    public void setItems(List<? extends Explorable> items) {

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

        //selectableManager.setItem(items);
        currentItems = items;

        /*
        List<? extends Explorable> selected = items
                .stream()
                .filter(item -> item.selectedProperty().getValue())
                .collect(Collectors.toList());
        
        setSelectedItem(selected);*/
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
        
        List<Explorable> selected = tableView.getSelectionModel().getSelectedItems();
        
        
        
        List<Explorable> toAdd = CollectionsUtils.toAdd(items, selected);
        List<Explorable> toRemove = CollectionsUtils.toRemove(items, selected);
        toAdd.forEach(tableView.getSelectionModel()::select);
        toRemove
                .stream()
                .mapToInt(tableView.getItems()::indexOf)
                .forEach(tableView.getSelectionModel()::clearSelection);
    }

    /*
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

    }*/
    private void displayHints() {

        //hintService.displayHint(new DefaultHint(String.format("#%s",TABLE_VIEW_ID),"Double click on an element to open it."), false);
    }

    private TableRow<Explorable> createRow(TableView<Explorable> explorable) {

        TableRow<Explorable> row = new TableRow<>();
        row.addEventFilter(MouseEvent.MOUSE_PRESSED,Event::consume);
        row.addEventFilter(MouseEvent.MOUSE_CLICKED,new RowListener(row));

        return row;
    }

    private class RowListener implements EventHandler<MouseEvent> {

        private final TableRow<Explorable> row;

        public RowListener(TableRow<Explorable> row) {
            this.row = row;
        }

        @Override
        public void handle(MouseEvent event) {
            onItemClicked.accept(new DataClickEvent(row.getItem(), event, event.getClickCount() == 2));
            event.consume();
        }

    }

    private void onItemSelectionChanged(Explorable explorable, Boolean selected) {

        if (selected) {
            tableView.getSelectionModel().select(explorable);
        }

    }

    private ObservableValue<String> getTagListProperty(TableColumn.CellDataFeatures<Explorable, String> cell) {
        wrapperList.put(cell.getValue(), new ReadOnlyTagWrapper(cell.getValue()));

        return wrapperList.get(cell.getValue());
    }

    public void refresh() {

        //setItem(currentItems);
        //setSelectedItem(currentItems.stream().filter(Explorable::isSelected).collect(Collectors.toList()));
        wrapperList.values().forEach(ReadOnlyTagWrapper::refresh);
        
        helper.refresh();

    }

    private class ReadOnlyTagWrapper extends ReadOnlyObjectWrapper<String> {

        final Explorable explorable;

        private String lastValue;

        private String value;

        public ReadOnlyTagWrapper(Explorable explorable) {
            this.explorable = explorable;
        }

        public void computeValue() {
            value = explorable.getTagList()
                    .stream()
                    .map(Tag::toString)
                    .collect(Collectors.joining(", "));
        }

        @Override
        public String getValue() {
            if (value == null) {
                computeValue();
            }
            return value;
        }

        public void refresh() {
            value = getValue();
            if (value.equals(lastValue) == false) {
                lastValue = value;
                fireValueChangedEvent();
            }

        }
    }

    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

}
