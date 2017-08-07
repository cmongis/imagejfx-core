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
import ijfx.explorer.widgets.ExplorerIconCell;
import ijfx.ui.loading.LoadingScreenService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import mongis.utils.panecell.PaneCell;
import mongis.utils.panecell.PaneCellController;
import mongis.utils.panecell.ScrollBinder;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = ExplorerView.class,priority = 0.8,label="Preview",iconPath="fa:picture_alt")
public class IconExplorerView extends ScrollPane implements ExplorerView {

    private final TilePane tilePane = new TilePane();

    private ScrollBinder binder;

    private final PaneCellController<Explorable> cellPaneCtrl = new PaneCellController<>(tilePane);

    private List<? extends Explorable> itemsList;
    
    private Consumer<DataClickEvent<Explorable>> onItemClicked;
    
    @Parameter
    LoadingScreenService loadingScreenService;
    
   
    @Parameter
    private Context context;
   
    
    public IconExplorerView() {
        setContent(tilePane);
        setPrefWidth(400);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tilePane.prefWidthProperty().bind(widthProperty());
        tilePane.setPrefTileWidth(170);
        tilePane.setPrefTileHeight(270);
        //tilePane.setPrefTileHeight(Control.USE_PREF_SIZE);
        tilePane.setVgap(5);
        tilePane.setHgap(5);
        binder = new ScrollBinder(this);
        cellPaneCtrl.setCellFactory(this::createIcon);
        
        addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
         tilePane.getChildren().addListener(this::onDisplayedNodesChanged);
    }
    
    public void setTileDimension(double width, double height, double vgap, double hgap) {
        tilePane.setPrefTileWidth(width);
        tilePane.setPrefTileHeight(height);
        tilePane.setVgap(vgap);
        tilePane.setHgap(hgap);
    }

    public void setCellFactory(Callable<PaneCell<Explorable>> callable) {
        cellPaneCtrl.setCellFactory(callable);
    }
    
    @Override
    public Node getUIComponent() {

        return this;
    }

    @Override
    public void setItems(List<? extends Explorable> items) {
        //loadingScreenService.frontEndTask(cellPaneCtrl.update(new ArrayList<Iconazable>(items)),false);
        
        this.itemsList = items;
        cellPaneCtrl.setTaskDisplayer(loadingScreenService);
        cellPaneCtrl.update(new ArrayList<Explorable>(items));
       
        
    }

    public List<? extends Explorable> getItems() {
        return itemsList;
    }

    
    
    private PaneCell<Explorable> createIcon() {
        ExplorerIconCell cell = new ExplorerIconCell();
        cell.setOnDataClick(event->{
            onItemClicked.accept(event);
        });
       
        context.inject(cell);
        return cell;
    }
    
    

    @Override
    public List<? extends Explorable> getSelectedItems() {
        return new ArrayList<>(cellPaneCtrl.getSelectedItems());
    }

    
    private void onMouseClick(MouseEvent event){
        
        if(event.getTarget() == tilePane) {
            cellPaneCtrl.setSelected(new ArrayList<>());
        }
        
        
    }
    
    public void onMouseDrag(DragEvent event) {
        
        
    }

    @Override
    public void setSelectedItem(List<? extends Explorable> items) {
        
        cellPaneCtrl.setSelected(new ArrayList<>(items));
                
    }
    
    private void onDisplayedNodesChanged(ListChangeListener.Change<? extends Node> change) {
            
        while(change.next());
        
       Platform.runLater(binder::update);
        
    }
    
    public void refresh() {
         cellPaneCtrl.updateSelection();
    }

    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

 
    
    

    @Override
    public SelectionModel getSelectionModel() {
        return null;
    }
    
}
