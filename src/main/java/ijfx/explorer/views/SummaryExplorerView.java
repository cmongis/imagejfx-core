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
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */
@Plugin(type = ExplorerView.class, priority = 0.7, label = "Summary")
public class SummaryExplorerView implements ExplorerView {
    
    private final BorderPane borderPane = new BorderPane();
    private  VBox vBox = new VBox();
    private HBox hBox1 = new HBox();
    private HBox hBox2 = new HBox();
    private Button last = new Button("last");
    private Button next = new Button("next");
    private Label label = new Label();
    private TableView<Explorable> tableView = new TableView<>();
    private TilePane tilePane = new TilePane();

    public SummaryExplorerView() {
        borderPane.setCenter(vBox);
        vBox.setSpacing(10);
        vBox.getChildren().addAll(hBox1, hBox2);
        hBox1.getChildren().addAll(tilePane, tableView);
        hBox2.getChildren().addAll(last, label, next);
        
    }

    @Override
    public Node getUIComponent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setItems(List<? extends Explorable> items) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<? extends Explorable> getSelectedItems() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSelectedItem(List<? extends Explorable> items) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOnItemClicked(Consumer<DataClickEvent> eventHandler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SelectionModel getSelectionModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
