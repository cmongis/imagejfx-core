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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    Set<String> currentColumns = new HashSet<>();

    //LinkedHashSet<String> priority = new LinkedHashSet();
    MetaDataKeyPrioritizer priority = new MetaDataKeyPrioritizer(new String[0]);

    List<TableColumn<T, ?>> additionalColumns = new ArrayList<>();

    String currentColumn;
    
    public MetaDataOwnerHelper(TableView<T> tableView) {
        this.tableView = tableView;
        MenuItem item = new MenuItem("Edit");
        menu.getItems().add(item);
        
        item.setOnAction(event->{
            
           
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
                        .onChange(event->{
                            currentColumn = event
                                    .getList()
                                    .get(0)
                                    .getTableColumn()
                                    .getUserData()
                                    .toString();
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
    
    protected TableColumn<T, MetaData> generateColumn(String key) {
        TableColumn<T, MetaData> column = new TableColumn<>();
        column.setUserData(key);
        column.setCellValueFactory(this::getCellValueFactory);
        column.setEditable(true);
       
        return column;
    }

    public void onCellEdit(CellEditEvent<T,MetaData> event) {
        
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

    protected ObservableValue<MetaData> getCellValueFactory(TableColumn.CellDataFeatures<T, MetaData> cell) {
        String key = cell.getTableColumn().getUserData().toString();
        MetaData value = cell.getValue().getMetaDataSet().get(key);

        return new ReadOnlyObjectWrapper<>(value);
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

}
