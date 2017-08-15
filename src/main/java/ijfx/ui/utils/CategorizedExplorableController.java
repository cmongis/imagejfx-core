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
package ijfx.ui.utils;

import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.views.DataClickEvent;
import ijfx.explorer.widgets.ExplorerIconCell;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import mongis.utils.panecell.PaneCell;

/**
 *
 * @author sapho
 */
public class CategorizedExplorableController extends Pane{

    public CategorizedExplorableController() {
        Pane pane = new Pane();
        HBox hBox = new HBox();
        
    }
    
    public List<Explorable> addCategory(String name, List<Explorable> list){
        
        
        return list;
    }
    
    
    
    public List<Explorable> setElements (String name, List<Explorable> list){
        
        
        return list;
    }
    
    public void setMaxItemPerCategory (int max){
        
    }
    
    public void update (){
        
    }
    
    
    public Node generate(){
        
        return this;
    }
    
    private PaneCell<Explorable> createIcon() {
        ExplorerIconCell cell = new ExplorerIconCell();
        /*
        cell.setOnDataClick(event->{
            onItemClicked.accept(event);
        });
*/
       
        //context.inject(cell);
        return cell;
    }
    
    private void categoryDesign(String title, int max){
        Label label = new Label(title);
        TilePane tilePane = new TilePane();
        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, tilePane);
        for (int i = 1; i <= max; i++){
            
        }
        
        
    }
    /*
    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> onItemClicked) {
        this.onItemClicked = onItemClicked;
    }
*/
}
