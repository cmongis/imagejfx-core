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
package ijfx.ui.display.metadataowner;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataKeyPrioritizer;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetUtils;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import mongis.utils.properties.ListChangeListenerBuilder;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class MetaDataOwnerHelper<T extends MetaDataOwner> {

    final TableView<T> tableView;

    private Set<String> currentColumns = new HashSet<>();

    //LinkedHashSet<String> priority = new LinkedHashSet();
    private MetaDataKeyPrioritizer priority = new MetaDataKeyPrioritizer(new String[0]);

    private List<TableColumn<T, ?>> additionalColumns = new ArrayList<>();

    private List<WeakReference<RefreshableProperty<T>>> propertyList = new ArrayList<>();

    String currentColumn;

    public MetaDataOwnerHelper(TableView<T> tableView) {
        this.tableView = tableView;
        MenuItem item = new MenuItem("Edit");
        menu.getItems().add(item);

        item.setOnAction(event -> {

        });

        // adding a listener that
        // will get the current selected column
        // for further processes
        tableView
                .getSelectionModel()
                .getSelectedCells()
                .addListener(
                        ListChangeListenerBuilder
                                .<TablePosition>create()
                                .onChange(event -> {
                                    if (event.getList().get(0).getTableColumn() == null) {
                                        return;
                                    }
                                    Object userData = event.getList()
                                            .get(0)
                                            .getTableColumn()
                                            .getUserData();
                                    if (userData != null) {
                                        currentColumn = userData.toString();
                                    }
                                })
                                .build()
                );

        tableView.setContextMenu(menu);

    }

    public void addAdditionalColumn(TableColumn<T, ?> column) {
        this.additionalColumns.add(column);
        this.tableView.getColumns().add(column);
    }

    public void setItem(List<? extends T> mList) {
        tableView.getItems().clear();
        tableView.getItems().addAll(mList);

    }

    public Set<String> getCurrentColumns() {
        return currentColumns;
    }

    public void setColumns(String... columns) {

        updateColums(columns);
    }

    public void setColums(List<String> columnList) {
        updateColumns(columnList);
    }

    public void setColumnsFromItems(List<? extends T> items) {
        List<MetaDataSet> mList = items
                .stream()
                .map(i -> i.getMetaDataSet())
                .collect(Collectors.toList());

        updateColumns(MetaDataSetUtils.getAllPossibleKeys(mList).stream().filter(MetaData::canDisplay).sorted(priority).collect(Collectors.toList()));
    }
    
    protected void updateColumns() {
        setColumnsFromItems(tableView.getItems());
    }

    protected void updateColums(String... columnList) {
        updateColumns(Arrays.asList(columnList));
    }

    protected void updateColumns(List<String> columnList) {
        columnList.sort(priority);

        if (!columnList.equals(currentColumns)) {
            System.out.println("The columns are not the same, updating");
            setColumnNumber(columnList.size());
            currentColumns = new HashSet(columnList);
            IntStream.range(0, columnList.size()).forEach(n -> {
                tableView.getColumns().get(n).setUserData(columnList.get(n));
                tableView.getColumns().get(n).setText(columnList.get(n));
            });
        }
    }

    private void setColumnNumber(Integer number) {
        int actualSize = tableView.getColumns().size() - additionalColumns.size();
        System.out.println(String.format("Changing the number of column from %d to %d", actualSize, number));

        if (number == 0) {
            tableView.getColumns().clear();
            return;
        }

        if (actualSize == number) {
            return;
        }

        if (actualSize > number) {
            tableView.getColumns().removeAll(tableView.getColumns().subList(number - 1, actualSize - 1));
        } else {
            tableView.getColumns().addAll(0, IntStream
                    .range(0, number - actualSize)
                    .mapToObj(i -> generateColumn(""))
                    .collect(Collectors.toList()));
        }
    }

    ContextMenu menu = new ContextMenu();

    protected TableColumn<T, String> generateColumn(String key) {
        TableColumn<T, String> column = new TableColumn<>();
        column.setUserData(key);
        column.setCellValueFactory(this::observableWrapper);
        column.setEditable(true);

        return column;
    }

    public void onCellEdit(CellEditEvent<T, MetaData> event) {

    }

    public void setPriority(String... keyName) {
        // if(priority.isSame(keyName) == false) {
        priority = new MetaDataKeyPrioritizer(keyName);
        //}
    }

    public String[] getPriority() {
        return priority.getPriority();
    }

    public int priorityIndex(Set<String> set, String element) {
        int i = 0;
        for (String s : set) {
            if (s.equals(element)) {
                return 100 - i;
            }
            i++;
        }
        return 0;
    }

    /*
    private ObservableValue<MetaData> getCellValueFactory(TableColumn.CellDataFeatures<T, MetaData> cell) {
        String key = cell.getTableColumn().getUserData().toString();
        MetaData value = cell.getValue().getMetaDataSet().get(key);

        return new ReadOnlyObjectWrapper<>(value);
    }*/

    protected ObservableValue<String> observableWrapper(TableColumn.CellDataFeatures<T, String> cell) {

        String key = cell.getTableColumn().getUserData().toString();
        //MetaData value = cell.getValue().getMetaDataSet().get(key)

        int hash = key.hashCode() + cell.getValue().hashCode();
        
        WeakReference<RefreshableProperty<T>> property
                = propertyList
                        .stream()
                        .filter(ref -> ref.get() != null && ref.get().hashCode() == hash)
                        .findFirst()
                        .orElse(null);

        if (property == null) {
            property = new WeakReference<>(new RefreshableProperty<>(cell.getValue(), key));
            propertyList.add(property);
        }
        return property.get();

    }

    private double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);

        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private class MetaDataTableCell<T extends MetaDataOwner> extends TableCell<T, MetaData> {

        public MetaDataTableCell() {
            super();

        }

    }

    public synchronized void refresh() {
        
        
        updateColumns();
        
        
        propertyList = propertyList
                .stream()
                .filter(property -> property.get() != null)
                .collect(Collectors.toList());

        for (WeakReference<RefreshableProperty<T>> ref : propertyList) {
            ref.get().refresh();
        }

    }

    /*
        Property related function
     */
    protected class RefreshableProperty<T extends MetaDataOwner> extends ReadOnlyObjectPropertyBase<String> {

        final T owner;
        final String keyName;
        String strValue;

        public int hashCode() {
            return owner.hashCode() + keyName.hashCode();
        }

        public RefreshableProperty(T owner, String keyName) {
            this.owner = owner;
            this.keyName = keyName;
            MetaData m = owner.getMetaDataSet().get(keyName);
            strValue = m != null ? m.getStringValue() : null;
        }

        public void refresh() {
            String oldValue = strValue;
            String newValue = get();
            strValue = newValue;
            if (oldValue == null && newValue != null) {
                fireValueChangedEvent();
                return;
            }

            if (oldValue != null && oldValue.equals(newValue) == false) {
                fireValueChangedEvent();
            }
        }

        @Override
        public String get() {
            return owner.getMetaDataSet().get(keyName).getStringValue();
        }

        @Override
        public Object getBean() {
            return owner;
        }

        @Override
        public String getName() {
            return keyName;
        }
    }

}
