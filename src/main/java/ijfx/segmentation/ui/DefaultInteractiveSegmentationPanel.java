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
package ijfx.segmentation.ui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.uiplugin.Localization;
import ijfx.segmentation.core.InteractiveSegmentation;
import ijfx.segmentation.core.InteractiveSegmentationPanel;
import ijfx.segmentation.core.InteractiveSegmentationService;
import ijfx.segmentation.core.InteractiveSegmentationUI;
import ijfx.segmentation.core.NoInteractiveSegmentation;
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiContexts;
import ijfx.ui.UiPlugin;
import ijfx.ui.loading.LoadingScreenService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import mongis.utils.CallbackTask;
import mongis.utils.FXUtilities;
import mongis.utils.ProgressHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;


/**
 *
 * @author cyril
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "segmentation-panel-2", localization = Localization.RIGHT, context = "any-display-open+segment explore+segment -overlay-selected")
public class DefaultInteractiveSegmentationPanel extends BorderPane implements UiPlugin, InteractiveSegmentationPanel {

    /*
        FXML related objects
     */
    @FXML
    private Accordion accordion;

    @FXML
    private SplitMenuButton analyseParticlesButton;

    @FXML
    private SplitMenuButton countObjectsButton;

    @FXML
    private VBox actionVBox;

    @FXML
    private Button segmentMoreButton;

    private Runnable onRefresh;

    /*
        SciJava services
     */
    @Parameter
    LoadingScreenService loadingScreenService;

    @Parameter
    InteractiveSegmentationService segUISrv;

    @Parameter
    UiContextService uiContextSrv;

    Map<Class<?>, TitledPane> paneMap = new HashMap<>();

    /*
        Constants
     */
    private static final String MEASURE_CURRENT_PLANE = "...only this plane using...";
    private static final String ALL_PLANE_ONE_MASK = "... for all planes using this mask";
    private static final String SEGMENT_AND_MEASURE = "... after segmenting each planes";

    public DefaultInteractiveSegmentationPanel() throws Exception {
        FXUtilities.injectFXML(this);
        setPrefWidth(200);

        addAction(FontAwesomeIcon.COPY, ALL_PLANE_ONE_MASK, this::analyseParticlesFromAllPlanes, analyseParticlesButton);
        addAction(FontAwesomeIcon.TASKS, SEGMENT_AND_MEASURE, this::segmentAndAnalyseEachPlane, analyseParticlesButton);
    }

    @Override
    public UiPlugin init() {

        Collection<TitledPane> collect = segUISrv
                .getUiWidgets()
                .stream()
                .map(this::wrap)
                .collect(Collectors.toList());

        accordion
                .getPanes()
                .addAll(
                        collect
                );

        segUISrv.addPanel(this);

        accordion.expandedPaneProperty().addListener(this::onExpanded);

        refresh();
        return this;
    }

    private TitledPane wrap(InteractiveSegmentationUI plugin) {
        TitledPane pane = new TitledPane();
        pane.setText(plugin.getName());
        pane.setContent(plugin.getContentNode());
        pane.setUserData(plugin.getSupportedType());
        return pane;
    }

    /*
        Event handlers
     */
    private void onExpanded(ObservableValue value, TitledPane oldValue, TitledPane pane) {

        Platform.runLater(() -> {

            if (pane == null) {
                segUISrv.setSegmentation(NoInteractiveSegmentation.class);
            } else {
                if (pane.getUserData() == segUISrv.getCurrentSegmentationType()) {
                    return;
                }
                Class<? extends InteractiveSegmentation> segmentationType = (Class<? extends InteractiveSegmentation>) pane.getUserData();
                segUISrv.setSegmentation(segmentationType);
            }
        });
    }

    /*
        Helpers
     */
    private void addAction(FontAwesomeIcon icon, String label, Consumer<ProgressHandler> action, SplitMenuButton menuButton) {

        MenuItem item = new MenuItem(label, new FontAwesomeIconView(icon));
        item.setOnAction(
                event -> new CallbackTask<Void, Void>()
                        .callback((progress, voiid) -> {
                            action.accept(progress);
                            return null;
                        })
                        .submit(loadingScreenService)
                        .start()
        );

        menuButton.getItems().add(item);

    }

    private void analyseParticlesFromAllPlanes(ProgressHandler event) {

    }

    private void segmentAndAnalyseEachPlane(ProgressHandler event) {

    }

    @Override
    public Node getUiElement() {
        return this;
    }

    @Override
    public void refresh() {

        Platform.runLater(() -> {

            Class<? extends InteractiveSegmentation> currentSegmentationType = segUISrv.getCurrentSegmentationType();
            if (currentSegmentationType == NoInteractiveSegmentation.class) {
                accordion.setExpandedPane(null);
            } else {
                accordion
                        .expandedPaneProperty()
                        .setValue(accordion
                                .getPanes()
                                .stream()
                                .filter(pane -> pane.getUserData() == currentSegmentationType)
                                .findFirst()
                                .orElse(null));

            }
        });
    }

    @FXML
    public void analyseParticles() {

    }

    @FXML
    public void segmentMore() {

    }

    @FXML
    public void countObjects() {

    }

    private boolean isExplorer() {
        return false;
    }

    @FXML
    private void close() {
        uiContextSrv
                .leave(UiContexts.SEGMENT);
        if (!isExplorer()) {
            uiContextSrv.enter(UiContexts.VISUALIZE);
        }
        uiContextSrv.update();
    }

}
