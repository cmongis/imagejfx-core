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
import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    
    public ExplorableDisplayPanel() {
        super(ExplorableDisplay.class);
    }

    @Override
    public void pack() {

        root = new AnchorPane();

        tabPane = new TabPane();

        AnchorPane.setBottomAnchor(tabPane, 15d);
        AnchorPane.setLeftAnchor(tabPane, 0d);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 0d);

        root.getChildren().add(tabPane);
        redoLayout();
        redraw();
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
                    });
            
        });
    }

}
