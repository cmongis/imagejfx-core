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
import ijfx.core.activity.ActivityService;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetDisplayService;
import ijfx.core.segmentation.DisplayedSegmentedObject;
import ijfx.core.segmentation.SegmentationService;
import ijfx.core.segmentation.SegmentedObject;
import ijfx.core.segmentation.SegmentedObjectExplorerWrapper;
import ijfx.core.uicontext.UiContextProperty;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.uiplugin.Localization;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.core.FolderManagerService;
import ijfx.explorer.datamodel.Explorable;
import ijfx.segmentation.commands.AnalyseParticles;
import ijfx.segmentation.commands.BatchCount;
import ijfx.segmentation.commands.BatchMeasurement;
import ijfx.segmentation.commands.CountObject;
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import mongis.utils.task.FluentTask;
import mongis.utils.FXUtilities;
import mongis.utils.task.ProgressHandler;
import net.imagej.display.ImageDisplayService;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import ijfx.explorer.ExplorerViewService;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "segmentation-panel", localization = Localization.RIGHT, context = "image-open+segment explore+segment -overlay-selected")
public class DefaultInteractiveSegmentationPanel extends BorderPane implements UiPlugin, InteractiveSegmentationPanel {

    /*
        FXML related objects
     */
    @FXML
    private Accordion accordion;

    @FXML
    private Button analyseParticlesButton;

    @FXML
    private Button countObjectsButton;

    @FXML
    private VBox actionVBox;

    @FXML
    private Button segmentMoreButton;

    /*
        SciJava services
     */
    @Parameter
    private SegmentationService segmentationService;

    @Parameter
    private ImageDisplayService imageDisplayService;

    @Parameter
    private UIService uiService;

    @Parameter
    private MetaDataSetDisplayService metadataDisplaySrv;

    @Parameter
    private CommandService commandService;

    @Parameter
    private Context context;

    @Parameter
    private LoadingScreenService loadingScreenService;

    @Parameter
    private InteractiveSegmentationService segUISrv;

    @Parameter
    private UiContextService uiContextSrv;

    @Parameter
    private ActivityService activityService;

    @Parameter
    private FolderManagerService folderManagerService;

    @Parameter
    ExplorerViewService explorerService;

    /*
        Attributes
     */
    private Map<Class<?>, TitledPane> paneMap = new HashMap<>();

    private UiContextProperty uiContextProperty;


    /*
        Constants
     */
    private static final String MEASURE_CURRENT_PLANE = "...only this plane using...";
    private static final String ALL_PLANE_ONE_MASK = "... for all planes using this mask";
    private static final String SEGMENT_AND_MEASURE = "... after segmenting each planes";

    public DefaultInteractiveSegmentationPanel() throws Exception {
        FXUtilities.injectFXML(this);
        setPrefWidth(200);

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

        uiContextProperty = new UiContextProperty(context, UiContexts.EXPLORE);

        segmentMoreButton.textProperty().bind(Bindings.createStringBinding(this::getSegmentMoreButtonText, uiContextProperty));
        
        
        
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
        item.setOnAction(event -> new FluentTask<Void, Void>()
                        .callback((progress, voiid) -> {
                            action.accept(progress);
                            return null;
                        })
                        .submit(loadingScreenService)
                        .start()
        );

        menuButton.getItems().add(item);

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
        if (isExplorer()) {
            commandService.run(BatchMeasurement.class, true, "workflow", segUISrv.getWorkflow());
        } else {
            commandService.run(AnalyseParticles.class, true);
        }

    }

    private void displayObject(List<List<? extends SegmentedObject>> result) {
        List<? extends Explorable> objectList
                = result.stream()
                        .flatMap(objects -> objects.stream())
                        .map(object -> new DisplayedSegmentedObject(imageDisplayService.getActiveImageDisplay(), object))
                        .map(object -> new SegmentedObjectExplorerWrapper(object))
                        .collect(Collectors.toList());

        uiService.show(new ExplorableList(objectList));
    }

    @FXML
    public void segmentMore() {
        if (!isExplorer()) {
            folderManagerService.openImageFolder(imageDisplayService.getActiveImageDisplay());
        } else {
            if (explorerService.getSelectedItems().size() == 0) {
                uiService.showDialog("Please select an item for test");
                return;
            }
            
            Explorable get = explorerService.getSelectedItems().get(0);
            explorerService.open(get);

        }
    }

    @FXML
    public void countObjects() {

        if (isExplorer()) {
            commandService.run(BatchCount.class, true, "workflow", segUISrv.getWorkflow());
        } else {
            commandService.run(CountObject.class, true);
        }

    }

    private String getSegmentMoreButtonText() {
        if (isExplorer()) {
            return "Test mode";
        } else {
            return "Batch mode";
        }
    }

    private void displayCount(List<MetaDataSet> metaDataSet) {
        metadataDisplaySrv.addMetaDataSetToDisplay("Object count", metaDataSet);
    }

    private boolean isExplorer() {
        return uiContextProperty.get();
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
