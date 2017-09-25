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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import ijfx.commands.explorable.ExplorableDisplayCommand;
import ijfx.core.icon.FXIconService;
import ijfx.core.timer.TimerService;
import ijfx.core.utils.SciJavaUtils;
import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Taggable;
import ijfx.explorer.views.ExplorerView;
import ijfx.explorer.views.ViewStateManager;
import ijfx.ui.bindings.SideMenuBinding;
import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import ijfx.ui.filters.metadata.TaggableFilterPanel;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.metadata.MetaDataBar;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = FXDisplayPanel.class)
public class ExplorableDisplayPanel extends AbstractFXDisplayPanel<ExplorableDisplay> {

    AnchorPane root;

    @Parameter
    PluginService pluginService;

    @Parameter
    CommandService commandService;

    @Parameter
    FXIconService fxIconService;

    TabPane tabPane;

    List<ExplorerView> viewList = new ArrayList<>();

    @Parameter
    EventService eventService;

    @Parameter
    Context context;

    @Parameter
    TimerService timerService;

    MetaDataBar metaDataBar;

    BooleanProperty inZone = new SimpleBooleanProperty(false);

    TaggableFilterPanel filterPanel;

    List<Explorable> displayed;

    SideMenuBinding sideMenuBinding;

    ViewStateManager viewStateManager;

    DataClickEventListener eventListener;

    int filterDataHash = -10;

    private final double leftBorder = 36;

    Logger logger = ImageJFX.getLogger();

    public ExplorableDisplayPanel() {
        super(ExplorableDisplay.class);
    }

    @Override
    public void pack() {

        if (root != null) {
            return;
        }

        root = new AnchorPane();

        tabPane = new TabPane();

        metaDataBar = new MetaDataBar(context);

        filterPanel = new TaggableFilterPanel();

        //root.getChildren().add(metaDataBar);
        AnchorPane.setLeftAnchor(metaDataBar, 0d);
        AnchorPane.setRightAnchor(metaDataBar, 0d);
        AnchorPane.setTopAnchor(metaDataBar, 0d);

        AnchorPane.setBottomAnchor(tabPane, 15d);
        AnchorPane.setLeftAnchor(tabPane, leftBorder);
        AnchorPane.setRightAnchor(tabPane, 0d);
        AnchorPane.setTopAnchor(tabPane, 30d);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty().add(-5));

        root.setClip(clip);

        //
        ScrollPane sideBox = new ScrollPane();
        sideBox.setFitToWidth(true);
        sideBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        Label label = new Label("Filter", new FontAwesomeIconView(FontAwesomeIcon.FILTER));
        label.setRotate(90);
        sideBox.getStyleClass().add("filter-pane-container");
        sideBox.setContent(filterPanel.getPane());
        sideBox.setPrefWidth(300);

        sideMenuBinding = new SideMenuBinding(sideBox)
                .setxWhenHidden(-leftBorder);
        sideMenuBinding.showProperty().bind(sideBox.hoverProperty());
        sideMenuBinding.refresh();
        AnchorPane.setLeftAnchor(sideBox, 0d);
        AnchorPane.setTopAnchor(sideBox, 80d);
        AnchorPane.setBottomAnchor(sideBox, 0d);

        // adding the elements
        root.getChildren().addAll(metaDataBar, tabPane, sideBox);

        context.inject(filterPanel);
        // adding style class to the filter pane
        filterPanel
                .getPane()
                .getStyleClass()
                .add("side-filter-pane");

        // if the current tab is changd, the new selected tab should be updated
        tabPane.getSelectionModel().selectedItemProperty().addListener(this::onTabChanged);

        // if the filters are changed, the view should also be changed
        filterPanel
                .predicateProperty()
                .addListener(this::onFilterChanged);

        root.setOnMouseMoved(this::onMouseMoved);

        viewStateManager = new ViewStateManager();

        viewStateManager.setTaggleFilterPanel(filterPanel);

        redoLayout();
        redraw();

    }

    private ExplorerView getCurrentView() {
        return (ExplorerView) tabPane.getSelectionModel().getSelectedItem().getUserData();
    }

    private void updateCurrentView() {

        ExplorableDisplay display = getDisplay();

        ExplorerView view = getCurrentView();

        if (view == null || display == null) {
            return;
        }

        viewStateManager.checkView(view, display);

    }

    private void onMouseMoved(MouseEvent event) {
        if (event.getSource() == root && event.getY() < 20) {
            inZone.setValue(Boolean.TRUE);
        } else {
            inZone.setValue(Boolean.FALSE);
        }

    }

    private void onTabChanged(Observable obs, Tab oldTabl, Tab newTag) {
        redraw();
    }

    private void onFilterChanged(Observable obs, final Predicate<Taggable> old, final Predicate<Taggable> newValue) {

        if (newValue == null) {
            getDisplay().setFilter(null);
        } else {
            getDisplay().setFilter(exp -> newValue.test(exp));
        }
        getDisplay().update();
    }

    private Tab createTab(ExplorerView view) {

        Tab tab = new Tab();
        tab.setContent(view.getUIComponent());
        tab.setGraphic(root);
        tab.setUserData(view);
        tab.setText(SciJavaUtils.getLabel(view));
        tab.setGraphic(fxIconService.getIconAsNode(view));

        if (eventListener == null) {
            eventListener = new DataClickEventListener(getDisplay());
        }

        view.setOnItemClicked(eventListener);

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

        metaDataBar
                .addCommands(commandService.getCommandsOfType(ExplorableDisplayCommand.class));
        metaDataBar
                .addUiCommands();

        metaDataBar.update();

    }

    @Override
    public void setLabel(String s) {
    }

    @Override
    public void redraw() {

        if (tabPane.getTabs().size() == 0) {
            return;
        }
        logger.info("Redrawing for " + getDisplay().getName());

        viewStateManager.updateState(getDisplay());

        Platform.runLater(this::redrawFX);
    }

    public int hash(List<? extends Explorable> list) {
        if (list == null) {
            return 0;
        }
        return list
                .stream()
                .mapToInt(exp -> exp == null ? -1 : exp.hashCode())
                .parallel()
                .sum();

    }

    private boolean same(List<Explorable> list1, List<Explorable> list2) {
        int hash1 = hash(list1);
        int hash2 = hash(list2);

        return hash(list2) == hash(list1);
    }

    public void redrawFX() {
        if (getCurrentView() == null) {
            return;
        }

        updateCurrentView();

    }

}
