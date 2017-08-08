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

import com.google.common.collect.Lists;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataSet;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.display.annotation.DefaultAnnotationDialog;
import ijfx.ui.main.ImageJFX;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */
@Plugin(type = ExplorerView.class, priority = 0.7, label = "Details", iconPath = "fa:table")
public class DetailsExplorerView extends BorderPane implements ExplorerView {

    @FXML
    private BorderPane borderPane;

    @FXML
    private HBox hBox;

    @FXML
    private Button last;

    @FXML
    private Button next;

    @FXML
    private Label label;

    @FXML
    private TableView<MetaData> tableView;

    @FXML
    private TableColumn<Explorable, String> keyColumn;

    @FXML
    private TableColumn<Explorable, String> valueColumn;

    private List<? extends Explorable> itemList;

    private Explorable currentItem;

    private Consumer<DataClickEvent<Explorable>> onItemClicked;

    private List<? extends Explorable> selectedItems = new ArrayList<>();

    private static final String FXMLWAY = "/ijfx/ui/display/image/DetailsDisplay.fxml";
    private Consumer<DataClickEvent<Explorable>> eventHandler;

    public DetailsExplorerView() {

        loadFXML();

        last.setOnAction(this::onDisplayPreviousExplorable);
        next.setOnAction(this::onDisplayNextExplorable);

        keyColumn.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        valueColumn.setCellValueFactory(
                new PropertyValueFactory<>("value"));

    }

    private void loadFXML() {
        System.out.println("loadFXML");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(FXMLWAY));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();

        } catch (IOException ex) {
            Logger.getLogger(DefaultAnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public Node getUIComponent() {
        System.out.println("getUIComponent");
        return this;
    }

    @Override
    public void setItems(List<? extends Explorable> items) {
        System.out.println("setItem");
        this.itemList = items;

        ImageJFX.getLogger().info(String.format("Items List   " + itemList));
        refresh();
    }

    private void setColumnsData(MetaDataSet map) {

        tableView.getItems().clear();

        map.entrySet().stream().forEach((entry) -> {
            tableView.getItems().add(entry.getValue());
        });

    }

    @Override
    public List<? extends Explorable> getSelectedItems() {
        if (currentItem == null) {
            return new ArrayList<>();
        } else {
            return Lists.newArrayList(currentItem);
        }
    }

    @Override
    public void setSelectedItem(List<? extends Explorable> itemList) {

        selectedItems = itemList;

        if (selectedItems.size() > 0) {
            setCurrentItem(itemList.get(0));
        }

        checkSelection();

        //ImageJFX.getLogger().info(String.format("Current item  " + currentItem));
    }

    @Override
    public SelectionModel getSelectionModel() {
        return null;

    }

    public void setCurrentItem(Explorable explorable) {

        currentItem = explorable;
        setColumnsData(currentItem.getMetaDataSet());

    }

    @Override
    public void refresh() {
        checkSelection();
    }

    @Override
    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> eventHandler) {

        this.eventHandler = eventHandler;

    }

    private void checkSelection() {

        if (selectedItems.size() == 0 && itemList.size() > 0) {
            eventHandler.accept(new DataClickEvent<Explorable>(itemList.get(0), null, false));
        }

        if (selectedItems.size() > 1) {
            eventHandler.accept(new DataClickEvent<Explorable>(currentItem, null, true));
        }
    }

    private void onDisplayPreviousExplorable(ActionEvent event) {
        int index = itemList.indexOf(currentItem) - 1;
        if (index >= 0 && index + 1 < itemList.size()) {
            eventHandler.accept(new DataClickEvent<Explorable>(itemList.get(index), null, false));
        }

    }

    private void onDisplayNextExplorable(ActionEvent event) {
        int index = itemList.indexOf(currentItem) + 1;
        if (index >= 0 && index + 1 < itemList.size()) {
            eventHandler.accept(new DataClickEvent<Explorable>(itemList.get(index), null, false));
        }

    }

    private void labelDisplay(Explorable exp) {
        label.setText(exp.getTitle());

    }

    @Override
    public List<? extends Explorable> getItems() {
        return itemList;
        /*
        System.out.println("getItem");

        if (currentItem != null && !currentItemList.contains(currentItem)) {
            currentItemList.add(currentItem);
            return currentItemList;
            
        } else {
            ImageJFX.getLogger().info(String.format("Current item is null : nothing selected"));
            return null;
        }*/
        //une erreur ici : nullpointerexception
    }

    public Explorable getDisplayedItem() {
        return currentItem;
    }

}
