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
import ijfx.ui.main.ImageJFX;
import java.util.ArrayList;
import java.util.HashMap;
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
public class CategorizedExplorableController extends Pane {

    private final HashMap<String, List<? extends Explorable>> catMap = new HashMap();
    private final Pane pane = new Pane();
    private final VBox mainVBox = new VBox();

    public CategorizedExplorableController() {
        pane.getChildren().add(mainVBox);

    }

    public Node addCategory(String name) { //inchangeable
        model(name, null);

        return this;
    }

    public Node setElements(String name, List<Explorable> list) { //inchangeable
        if (catMap.containsKey(name)) {
            catMap.replace(name, catMap.get(list), list);

        } else {
            //model(name, list);
            ImageJFX.getLogger().info(String.format("This category doesn't exist. "));
        }

        return this;
    }

    public void setMaxItemPerCategory(int max) { //inchangeable
        catMap.keySet().stream().forEach((mapKey)->{
            catMap.replace(mapKey, catMap.get(mapKey), catMap.get(mapKey).subList(0, max));
        });
        

    }

    public void update() { //inchangeable
        
        catMap.keySet().stream().forEach((mapKey) -> {
            mainVBox.getChildren().add(categoryDesign(mapKey));
        });
        
        
    }

    public Node generate() { //inchangeable
        update();

        return this;
    }

    public Node categoryDesign(String name) { //design chaque category
        Label label = new Label(name);
        TilePane tilePane = new TilePane();
        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, tilePane);
        
        
        return vBox;

    }
    
    

    public List<? extends Explorable> getList(String name) {
        return catMap.get(name);
    }

    public HashMap<String, List<? extends Explorable>> getMap() {
        return catMap;
    }

    private void model(String name, List<Explorable> list) {
        if (!catMap.containsKey(name) && !catMap.containsValue(list)) {
            catMap.put(name, list);
        } else {
            ImageJFX.getLogger().info(String.format("This category already exist. "));
        }

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

    /*
    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> onItemClicked) {
        this.onItemClicked = onItemClicked;
    }
     */
}
