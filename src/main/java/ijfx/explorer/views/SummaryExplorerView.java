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

import ijfx.explorer.datamodel.Explorable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import mongis.utils.panecell.PaneCellController;
import net.imagej.Dataset;

import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */
@Plugin(type = ExplorerView.class, priority = 0.7, label = "Summary", iconPath = "fa:table")
public class SummaryExplorerView extends BorderPane implements ExplorerView {

    private final BorderPane borderPane = new BorderPane();
    private final HBox hBox = new HBox(10);
    private final Button last = new Button("Last");
    private final Button next = new Button("Next");
    private final Label label = new Label("Nothing selected");
    private final TableView<Explorable> tableView = new TableView<>();

    private List<? extends Explorable> itemsList;

    private Consumer<DataClickEvent<Explorable>> onItemClicked;

    private Explorable currentItems;

    private static double WIDHT = 570;
    private static double HEIGTH = 370;

    private final PaneCellController<Explorable> cellPaneCtrl = new PaneCellController<>(borderPane);

    public SummaryExplorerView() {
        System.out.println("CA COMMENCE");
        borderPane.setRight(tableView);
        borderPane.setBottom(hBox);
        borderPane.setMinSize(WIDHT, HEIGTH);
        hBox.getChildren().addAll(last, label, next);
        this.getChildren().add(borderPane);

        TableColumn key = new TableColumn("Key");
        TableColumn value = new TableColumn("Value");

        tableView.getColumns().addAll(key, value);

        last.setOnAction(this::onDisplayLastExplorable);
        next.setOnAction(this::onDisplayNextExplorable);

    }

    @Override
    public Node getUIComponent() {
        return this;
    }

    @Override
    public void setItems(List<? extends Explorable> items) {
        this.itemsList = items;
        cellPaneCtrl.update(new ArrayList<Explorable>(items));

    }

    @Override
    public List<? extends Explorable> getSelectedItems() {
        List<Explorable> selected = tableView.getSelectionModel().getSelectedItems();
        

        return new ArrayList<>(cellPaneCtrl.getSelectedItems());
    }

    @Override
    public void setSelectedItem(List<? extends Explorable> items) {
        cellPaneCtrl.setSelected(new ArrayList<>(items));
        cellPaneCtrl.getSelectedItems().stream().forEach(this::labelDisplay);
        System.out.println("Allo ? ici setselectedItem");

    }

    @Override
    public SelectionModel getSelectionModel() {
        return tableView.selectionModelProperty().getValue();

    }

    @Override
    public void refresh() {
        cellPaneCtrl.updateSelection();

    }

    @Override
    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> eventHandler) {
    }

    public void onDisplayLastExplorable(ActionEvent event) {

    }

    public void onDisplayNextExplorable(ActionEvent event) {

    }

    public void labelDisplay(Explorable exp) {
        Platform.runLater(() -> {
            System.out.println("TableDisplay ? ");
            Dataset set = exp.getDataset();
            label.setText(exp.getTitle());
            tableView.setItems((ObservableList<Explorable>) cellPaneCtrl.getSelectedItems());
            //tableView.setItems(set);
        });

    }

    @Override
    public List<? extends Explorable> getItems() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
