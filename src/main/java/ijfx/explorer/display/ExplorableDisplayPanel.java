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
package ijfx.explorer.display;

import ijfx.core.icon.FXIconService;
import ijfx.core.utils.SciJavaUtils;
import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.views.ExplorerView;
import ijfx.ui.bindings.SideMenuBinding;
import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import ijfx.ui.filters.metadata.TaggableFilterPanel;
import ijfx.ui.metadata.MetaDataBar;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import mongis.utils.ProgressHandler;
import org.scijava.Context;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;

/**
 *
 * @author cyril
 */
@Plugin(type = FXDisplayPanel.class)
public class ExplorableDisplayPanel extends AbstractFXDisplayPanel<ExplorableDisplay> {

    AnchorPane root;

    @Parameter
    PluginService pluginService;

    @Parameter
    FXIconService fxIconService;

    TabPane tabPane;

    List<ExplorerView> viewList = new ArrayList<>();

    @Parameter
    EventService eventService;

    @Parameter
    Context context;

    MetaDataBar metaDataBar;

    BooleanProperty inZone = new SimpleBooleanProperty(false);

    TaggableFilterPanel filterPanel;

    public ExplorableDisplayPanel() {
        super(ExplorableDisplay.class);
    }

    @Override
    public void pack() {

        root = new AnchorPane();

        tabPane = new TabPane();

        metaDataBar = new MetaDataBar(context);
        ToggleButton filterButton = new ToggleButton("Filter");
        metaDataBar.getChildren().add(filterButton);
        //root.getChildren().add(metaDataBar);
        AnchorPane.setLeftAnchor(metaDataBar, 0d);
        AnchorPane.setRightAnchor(metaDataBar, 0d);
        AnchorPane.setTopAnchor(metaDataBar, 0d);

        AnchorPane.setBottomAnchor(tabPane, 15d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 30d);

        filterPanel = new TaggableFilterPanel();

        AnchorPane.setLeftAnchor(filterPanel.getPane(), 0d);
        AnchorPane.setTopAnchor(filterPanel.getPane(), 40d);
        AnchorPane.setBottomAnchor(filterPanel.getPane(), 0d);

        SideMenuBinding binding = new SideMenuBinding(filterPanel.getPane());
        binding.showProperty().bind(filterButton.selectedProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty().add(-5));

        //getStyleClass().add("image-display-pane");
        root.setClip(clip);

        // adding the elements
        root.getChildren().addAll(metaDataBar, tabPane, filterPanel.getPane());
        redoLayout();
        redraw();

        root.setOnMouseMoved(this::onMouseMoved);
    }

    private void onMouseMoved(MouseEvent event) {
        if (event.getSource() == root && event.getY() < 20) {
            inZone.setValue(Boolean.TRUE);
        } else {
            inZone.setValue(Boolean.FALSE);
        }

    }

    private Tab createTab(ExplorerView view) {

        Tab tab = new Tab();
        tab.setContent(view.getUIComponent());
        tab.setGraphic(root);
        tab.setUserData(view);
        tab.setText(SciJavaUtils.getLabel(view));
        tab.setGraphic(fxIconService.getIconAsNode(view));
        return tab;

    }

    @Override
    public Pane getUIComponent() {
        return root;
    }

    @Override
    public void redoLayout() {

        tabPane.getTabs().clear();
        viewList = pluginService
                .createInstancesOfType(ExplorerView.class);
        tabPane.getTabs()
                .addAll(
                        viewList
                                .stream()
                                .map(this::createTab)
                                .collect(Collectors.toList()));

    }

    @Override
    public void setLabel(String s) {
    }

    @Override
    public void redraw() {

        Platform.runLater(() -> {

            viewList
                    .stream()
                    .forEach(view -> {
                        view.setItem(getDisplay().getDisplayedItems());
                        view.refresh();
                        filterPanel.generateFilters(ProgressHandler.check(null), getDisplay().getItems());
                    });

        });
    }

}
