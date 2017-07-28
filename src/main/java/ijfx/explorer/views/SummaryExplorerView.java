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
import ijfx.ui.main.ImageJFX;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import net.imagej.Dataset;

import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */
@Plugin(type = ExplorerView.class, priority = 0.7, label = "Summary", iconPath = "fa:table")
public class SummaryExplorerView extends BorderPane implements ExplorerView {

    private final BorderPane borderPane = new BorderPane();
    private VBox vBox = new VBox(10);
    private HBox hBox1 = new HBox(10);
    private HBox hBox2 = new HBox(10);
    private Button last = new Button("last");
    private Button next = new Button("next");
    private Label label = new Label("Nothing selected");
    private TableView<Explorable> tableView = new TableView<>();
    private TilePane tilePane = new TilePane();

    private List<? extends Explorable> itemsList;

    private Consumer<DataClickEvent<Explorable>> onItemClicked;

    private Explorable currentItems;
    
    private static double WIDHT = 1070;
    private static double HEIGTH = 970;

    public SummaryExplorerView() {
        System.out.println("CA COMMENCE");
        borderPane.setCenter(vBox);
        borderPane.setMinSize(WIDHT, HEIGTH);
        vBox.setSpacing(10);
        vBox.getChildren().addAll(hBox1, hBox2);
        hBox1.getChildren().addAll(tilePane, tableView);
        hBox2.getChildren().addAll(last, label, next);
        this.getChildren().add(borderPane);
        
        TableColumn key = new TableColumn("Table");
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

    }

    @Override
    public List<? extends Explorable> getSelectedItems() {
        return itemsList;
    }

    @Override
    public void setSelectedItem(List<? extends Explorable> items) {
        
        List<Explorable> selected = tableView.getSelectionModel().getSelectedItems();
        selected.stream().forEach(this::tableDisplay);
        

    }

    @Override
    public SelectionModel getSelectionModel() {
        return tableView.selectionModelProperty().getValue();

    }

    @Override
    public void refresh() {

    }

    @Override
    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> eventHandler) {
    }
    
    public void onDisplayLastExplorable(ActionEvent event){
        
    }
    
    public void onDisplayNextExplorable(ActionEvent event){
        
    }
    
    public void tableDisplay(Explorable exp){
        Dataset set = exp.getDataset();
        label.setText(exp.getTitle());
        //tableView.setItems(set);
        
    }

}
