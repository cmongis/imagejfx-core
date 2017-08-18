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
import ijfx.explorer.views.IconExplorerView;
import ijfx.explorer.widgets.ExplorerIconCell;
import ijfx.ui.loading.LoadingScreenService;
import ijfx.ui.main.ImageJFX;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import mongis.utils.panecell.PaneCell;
import mongis.utils.panecell.PaneCellController;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author sapho
 */
public class CategorizedExplorableController extends Pane {

    @Parameter
    LoadingScreenService loadingScreenService;

    private final HashMap<String, List<? extends Explorable>> catMap = new HashMap();
    private final Pane pane = new Pane();
    private final VBox mainVBox = new VBox();
    private Consumer<DataClickEvent<Explorable>> onItemClicked;

    public CategorizedExplorableController() {
        pane.getChildren().add(mainVBox);
        this.getChildren().add(pane);

    }

    public CategorizedExplorableController addCategory(String name) { //inchangeable
        model(name, null);

        return this;
    }

    public CategorizedExplorableController setElements(String name, List<Explorable> list) { //inchangeable
        if (catMap.containsKey(name)) {
            catMap.replace(name, catMap.get(name), list);

        } else {
            ImageJFX.getLogger().info(String.format("This category doesn't exist. "));
        }

        return this;
    }

    public CategorizedExplorableController setMaxItemPerCategory(int max) { //inchangeable

        catMap.keySet()
                .stream()
                .filter((mapKey) -> catMap.get(mapKey).size() > max)
                .forEach((mapKey) -> {
                    catMap.replace(mapKey, catMap.get(mapKey), catMap.get(mapKey).subList(0, max));
                });

        return this;

    }

    public void update() { //inchangeable

        mainVBox.getChildren().clear();

        catMap.keySet().stream().forEach((mapKey) -> {
            mainVBox.getChildren().add(categoryDesign(mapKey));
        });

    }

    public Pane generate() { //inchangeable
        update();
        return this;
    }

    public List<? extends Explorable> getList(String name) {
        return catMap.get(name);
    }

    public HashMap<String, List<? extends Explorable>> getMap() {
        return catMap;
    }

    private Node categoryDesign(String name) { //design chaque category
        Label label = new Label(name);
        VBox vBox = new VBox();
        TilePane tilePane = new TilePane();

        PaneCellController<Explorable> icon = new PaneCellController<>(tilePane);
        icon.setCellFactory(this::createIcon);
        icon.setTaskDisplayer(loadingScreenService);
        icon.setSelected((List<Explorable>) catMap.get(name));
        icon.update(new ArrayList<>(catMap.get(name)));

        vBox.getChildren().addAll(label, tilePane);

        return vBox;

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

        cell.setOnDataClick(event -> {
            onItemClicked.accept(event);
        });

        return cell;
    }

    public void setOnItemClicked(Consumer<DataClickEvent<Explorable>> onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

}
