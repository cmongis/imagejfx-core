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
package ijfx.explorer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.core.activity.Activity;
import ijfx.core.hint.HintService;
import ijfx.core.icon.FXIconService;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataKeyPriority;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetUtils;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.uiplugin.FXUiCommandService;
import ijfx.core.utils.SciJavaUtils;
import ijfx.explorer.core.Folder;
import ijfx.explorer.core.FolderManagerService;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.events.DisplayedListChanged;
import ijfx.explorer.events.ExplorationModeChangeEvent;
import ijfx.explorer.events.ExploredListChanged;
import ijfx.explorer.events.ExplorerSelectionChangedEvent;
import ijfx.explorer.events.FolderAddedEvent;
import ijfx.explorer.events.FolderDeletedEvent;
import ijfx.explorer.events.FolderUpdatedEvent;
import ijfx.explorer.views.DataClickEvent;
import ijfx.ui.filters.metadata.TaggableFilterPanel;
import ijfx.explorer.views.ExplorerView;
import ijfx.explorer.views.FolderListCellCtrl;
import ijfx.ui.bindings.SideMenuBinding;

import ijfx.ui.loading.LoadingScreenService;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.utils.CollectionsUtils;
import ijfx.ui.utils.DragPanel;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import mongis.utils.CallableTask;
import mongis.utils.CallbackTask;
import mongis.utils.FXUtilities;
import mongis.utils.TextFileUtils;
import mongis.utils.properties.ListChangeListenerBuilder;
import org.reactfx.EventStreams;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = Activity.class, name = "explore")
public class ExplorerActivity extends AnchorPane implements Activity {

    @FXML
    private ListView<Folder> folderListView;

    @FXML
    private BorderPane contentBorderPane;

    @FXML
    private ToggleButton filterToggleButton;

    @FXML
    private TextField filterTextField;

    @FXML
    private ToggleButton fileModeToggleButton;

    @FXML
    private ToggleButton planeModeToggleButton;


    @FXML
    private Button selectAllButton;

    @FXML
    private MenuButton moreMenuButton;

    @FXML
    private ScrollPane filterScrollPane;

    private ToggleGroup explorationModeToggleGroup;

    private final ObjectProperty<ExplorerView> currentView = new SimpleObjectProperty<>();
    @Parameter
    private FolderManagerService folderManagerService;

    @Parameter
    private ExplorerService explorerService;

    @Parameter
    private LoadingScreenService loadingScreenService;

    @Parameter
    private PluginService pluginService;

    @Parameter
    private UiContextService uiContextService;

    @Parameter
    private UIService uiService;

    @Parameter
    private HintService hintService;

    @Parameter
    private FXIconService fxIconService;

    @FXML
    private TabPane tabPane;

    @Parameter
    private FXUiCommandService uiCommandService;

    private ExplorerView view;

    private List<Runnable> folderUpdateHandler = new ArrayList<>();

    private BooleanProperty folderListEmpty = new SimpleBooleanProperty(true);

    private BooleanProperty explorerListEmpty = new SimpleBooleanProperty(true);

    private DragPanel dragPanel;

    private final String NO_FOLDER_TEXT = "Click on \"Add folder\" or drop a\nfolder here to explorer it";
    private final FontAwesomeIcon NO_FOLDER_ICON = FontAwesomeIcon.DOWNLOAD;
    private final String EMPTY_FOLDER_TEXT = "Empty";
    private final FontAwesomeIcon EMPTY_FOLDER_ICON = FontAwesomeIcon.FROWN_ALT;

    Logger logger = ImageJFX.getLogger();

    List<Explorable> currentItems;

    TaggableFilterPanel filterPanel = new TaggableFilterPanel();

    public ExplorerActivity() throws Exception {
       
            FXUtilities.injectFXML(this);

            // contentBorderPane.setCenter(view.getNode());
            folderListView.setCellFactory(this::createFolderListCell);
            folderListView.getSelectionModel().selectedItemProperty().addListener(this::onFolderSelectionChanged);

            SideMenuBinding binding = new SideMenuBinding(filterScrollPane);

            binding.showProperty().bind(filterToggleButton.selectedProperty());
            filterScrollPane.setTranslateX(-300);

            explorationModeToggleGroup = new ToggleGroup();
            explorationModeToggleGroup.getToggles().addAll(fileModeToggleButton, planeModeToggleButton);
            explorationModeToggleGroup.selectToggle(fileModeToggleButton);
            fileModeToggleButton.setUserData(ExplorationMode.FILE);
            planeModeToggleButton.setUserData(ExplorationMode.PLANE);
            //objectModeToggleButton.setUserData(ExplorationMode.OBJECT);

            explorationModeToggleGroup.selectedToggleProperty().addListener(this::onToggleSelectionChanged);

            tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                currentView.setValue((ExplorerView) newTab.getUserData());
            });

            currentView.addListener(this::onViewModeChanged);

            dragPanel = new DragPanel("No folder open", FontAwesomeIcon.DASHCUBE);

            folderListEmpty.addListener(this::onFolderListEmptyPropertyChange);
            explorerListEmpty.addListener(this::onExplorerListEmptyPropertyChange);

            filterScrollPane.setContent(filterPanel.getPane());

            //fluentIconBinding(fileModeToggleButton,planeModeToggleButton,objectModeToggleButton);
            EventStreams.valuesOf(filterTextField.textProperty()).successionEnds(Duration.ofSeconds(1))
                    .subscribe(this::updateTextFilter);

       
    }

    private void init() {
        if (view == null) {
            List<ExplorerView> views = pluginService.createInstancesOfType(ExplorerView.class);

            List<Tab> buttons = views.stream().map(this::createTab).collect(Collectors.toList());
            //Map<ExplorerView, ToggleButton> viewButtons = views.stream().collect(Collectors.toMap(v -> v, this::createViewToggle));

            tabPane.getTabs().addAll(buttons);

            currentView.setValue(views.get(0));

            // add the items to the menu in the background
            new CallableTask<List<MenuItem>>()
                    .setCallable(this::initMoreActionButton)
                    .then(
                            items -> moreMenuButton.getItems().addAll(0, items))
                    .start();
        }
    }

    public List<MenuItem> initMoreActionButton() {

        return uiCommandService
                .getAssociatedAction(ExplorerActivity.class)
                .stream()
                .map(uiCommand -> uiCommandService.createMenuItem(uiCommand, this))
                .collect(Collectors.toList());
    }

    @Override
    public Node getContent() {
        return this;
    }

    @Override
    public Task updateOnShow() {

        init();

        explorationModeToggleGroup.selectToggle(getToggleButton(folderManagerService.getCurrentExplorationMode()));
        return new CallbackTask<Void, List<Explorable>>()
                .callback(this::update)
                .then(this::updateUi)
                .start();
    }

    // get the list of items to show from the explorer service
    public List<Explorable> update(Void v) {
        if (folderManagerService.getCurrentFolder() == null) {
            return new ArrayList<Explorable>();
        } else {
            return explorerService.getDisplayedItems();
        }
    }

    private void onViewClickEvent(DataClickEvent<Explorable> event) {

        if (event.isDoubleClick()) {
            new CallbackTask<>()
                    .tryRun(event.getData()::open)
                    .start();
        } else {

            if (event.getEvent().isShiftDown()) {
                explorerService.selectUntil(event.getData());
            } else {
                explorerService.toggleSelection(event.getData());
            }

        }

    }

    public void onFolderSelectionChanged(Observable obs, Folder oldValue, Folder newValue) {
        folderManagerService.setCurrentFolder(newValue);
        filterTextField.setText("");
    }

    public void updateFolderList() {

        CollectionsUtils.syncronizeContent(folderManagerService.getFolderList(), folderListView.getItems());

    }

    private void updateExplorerView(ExplorerView view) {
        this.view = view;
        //contentBorderPane.setCenter(view.getNode());

        view.setItems(explorerService.getDisplayedItems());
    }

    public synchronized void updateUi(List<? extends Explorable> explorable) {

        updateFolderList();
        init();
        folderListEmpty.setValue(folderListView.getItems().isEmpty());
        explorerListEmpty.setValue(explorable == null || explorable.isEmpty());

        if (view == null) {
            return;
        }

        if (explorable != null) {
            view.setItems(explorable);
            view.setSelectedItem(explorerService.getSelectedItems());
        }

        if (folderListEmpty.getValue()) {
            hintService.displayHints("/ijfx/ui/explorer/ExplorerActivity-tutorial-1.hints.json", false);
        }
    }

    // returns true if the folder is not displayed yet
    private boolean isNotDisplayed(Folder folder) {
        return !folderListView.getItems().contains(folder);
    }

    @FXML
    public void addFolder() {
        File f = FXUtilities.openFolder("Open a folder", null);

        if (f != null) {
            folderManagerService.addFolder(f);

        }
    }

    @FXML
    public void removeFolder() {
        folderManagerService.removeFolder(folderListView.getSelectionModel().getSelectedItem());
    }

    @EventHandler
    public void onFolderAdded(FolderAddedEvent event) {
        Platform.runLater(this::updateFolderList);
    }

    @EventHandler
    public void onFolderDeleted(FolderDeletedEvent event) {
        Platform.runLater(this::updateFolderList);
    }

    @EventHandler
    public void onExploredItemListChanged(ExploredListChanged event) {
        Platform.runLater(this::updateFilters);
        hintService.displayHints("/ijfx/ui/explorer/ExplorerActivity-tutorial-2.hints.json", false);
    }

    @EventHandler
    public void onDisplayedItemListChanged(DisplayedListChanged event) {
        Platform.runLater(() -> updateUi(event.getObject()));

    }

    @EventHandler
    public void onFolderUpdated(FolderUpdatedEvent event) {
        logger.info("Folder updated !");
        folderUpdateHandler.forEach(handler -> handler.run());
    }

    @EventHandler
    protected void onExplorationModeChanged(ExplorationModeChangeEvent event) {
        explorationModeToggleGroup.selectToggle(getToggleButton(event.getObject()));
    }

    @EventHandler
    protected void onExplorerServiceSelectionChanged(ExplorerSelectionChangedEvent event) {
        view.setSelectedItem(explorerService.getSelectedItems());
        Platform.runLater(this::updateButton);
    }

    private class FolderListCell extends ListCell<Folder> {

        FolderListCellCtrl ctrl = new FolderListCellCtrl();

        public FolderListCell() {
            super();
            getStyleClass().add("selectable");
            itemProperty().addListener(this::onItemChanged);
        }

        public void onItemChanged(Observable obs, Folder oldValue, Folder newValue) {
            if (newValue == null) {
                setGraphic(null);
            } else {

                setGraphic(ctrl);

                ctrl.setItem(newValue);
            }
        }

        public void update() {

            Platform.runLater(ctrl::forceUpdate);
        }
    }

    private ListCell<Folder> createFolderListCell(ListView<Folder> listView) {
        FolderListCell cell = new FolderListCell();
        folderUpdateHandler.add(cell::update);
        return cell;
    }

    public void updateFilters() {

        Task task = new CallbackTask<List<? extends Explorable>, Void>()
                .setInput(explorerService.getItems())
                .setName("Updating filters...")
                .callback(filterPanel::generateFilters)
                .start();

        loadingScreenService.frontEndTask(task, true);

    }

    /*
        View related functions
     */
    private Tab createTab(ExplorerView view) {

        Tab tab = new Tab(view.toString(), view.getUIComponent());
        tab.closableProperty().setValue(false);
        tab.setGraphic(fxIconService.getIconAsNode(view));
        tab.setUserData(view);

        view.setOnItemClicked(this::onViewClickEvent);

        tab.setText(SciJavaUtils.getLabel(view));

        return tab;

    }

    private void onViewModeChanged(Observable obs, ExplorerView oldValue, ExplorerView newValue) {
        updateExplorerView(newValue);
    }


    /*
        FXML Action
     */
    @FXML
    public void selectAll() {
        explorerService.selectAll();
    }

    @FXML
    public void unselectAll() {
        explorerService.selectItems(new ArrayList<>());
    }

    @FXML
    public void onSegmentButtonPressed() {

        uiContextService.toggleContext("segment", !uiContextService.isCurrent("segment"));
        uiContextService.toggleContext("segmentation", !uiContextService.isCurrent("segmentation"));
        uiContextService.update();

    }

    @FXML
    public void onProcessButtonPressed() {
        uiContextService.toggleContext("batch", true);
        uiContextService.update();
    }

    @FXML
    public void openSelection() {

        explorerService.openSelection();

    }

    @FXML
    public void explainMe() {
        uiService.showDialog("Function not implemented yet.");
    }

    @FXML
    public void tellMeMore() {
        hintService.displayHints("/ijfx/ui/explorer/ExplorerActivity-tutorial-3.hints.json", true);
    }

    @FXML
    public void exportToCSV() {

        List<MetaDataSet> mList = explorerService.getItems().stream()
                .map(e -> e.getMetaDataSet())
                .collect(Collectors.toList());

        String csvFile = MetaDataSetUtils.exportToCSV(mList, ",", true, MetaDataKeyPriority.getPriority(mList.get(0)));

        File saveFile = FXUtilities.saveFileSync("Export to CSV", null, "CSV", ".csv");
        if (saveFile != null) {
            try {
                TextFileUtils.writeTextFile(saveFile, csvFile);

                uiService.showDialog("CSV File successfully saved");
            } catch (IOException ex) {
                ImageJFX.getLogger().log(Level.SEVERE, null, ex);
                uiService.showDialog("Error when saving CSV File", DialogPrompt.MessageType.ERROR_MESSAGE);
            }
        }

    }

    private void onFolderListEmptyPropertyChange(Observable obs, Boolean oldV, Boolean isEmpty) {
        if (isEmpty) {
            dragPanel.setLabel(NO_FOLDER_TEXT)
                    .setIcon(NO_FOLDER_ICON);

        }

    }

    public void onExplorerListEmptyPropertyChange(Observable obs, Boolean oldV, Boolean isEmpty) {
        if (!isEmpty) {
            contentBorderPane.setCenter(tabPane);
            // getChildren().remove(dragPanel);
        } else {
            contentBorderPane.setCenter(dragPanel);

            dragPanel.setLabel(EMPTY_FOLDER_TEXT)
                    .setIcon(EMPTY_FOLDER_ICON);

        }
    }

    public boolean isEverythingSelected() {
        if (explorerService == null) {
            return false;
        }
        return explorerService.getDisplayedItems().size() == explorerService.getSelectedItems().size();
    }

    protected void updateTextFilter(final String query) {
        if (filterTextField.getText() != null && !filterTextField.getText().equals("")) {
            explorerService.setOptionalFilter(m -> m.getMetaDataSet().get(MetaData.FILE_NAME).getStringValue().toLowerCase().contains(query.toLowerCase()));
        } else {
            explorerService.setOptionalFilter(null);
        }
    }

    /*
        Exploration Mode Toggle related methods
    
     */
    protected ExplorationMode getExplorationMode(Toggle toggle) {
        return (ExplorationMode) toggle.getUserData();
    }

    protected Toggle getToggleButton(ExplorationMode mode) {
        return explorationModeToggleGroup.getToggles().stream().filter(toggle -> toggle.getUserData() == mode).findFirst().orElse(fileModeToggleButton);
    }

    protected void onToggleSelectionChanged(Observable obs, Toggle oldValue, Toggle newValue) {
        if (newValue == null) {
            return;
        }
        folderManagerService.setExplorationMode(getExplorationMode(newValue));
    }

    private static void fluentIconBinding(ButtonBase... buttons) {
        for (ButtonBase b : buttons) {
            fluenIconBinding(b);
        }
    }

    private static void fluenIconBinding(ButtonBase button) {
        fluentIconBinding(button.textProperty(), button);
    }

    private static void fluentIconBinding(StringProperty property, ButtonBase node) {

        final String initialString = property.getValue();

        property.bind(Bindings.createStringBinding(() -> {
            if (shouldHideText(node)) {
                return "";
            } else {
                return initialString;
            }
        }, node.widthProperty(), node.layoutXProperty()));

    }

    private static boolean shouldHideText(ButtonBase node) {

        return node.getWidth() < 40;
    }

    /*
        Select button related functions
     */
    private String getSelectAllText() {
        int selectedCount = explorerService.getSelectedItems().size();
        int total = explorerService.getDisplayedItems().size();
        if (selectedCount > 0) {
            return String.format("Select all (%d/%d)", selectedCount, total);
        } else {
            return "Select all";
        }
    }

    private void updateButton() {
        selectAllButton.setText(getSelectAllText());
    }

    /*
        Action Menu
     */
 /*
    public List<MenuItem> initActionMenu(PluginService pluginService) {

        return pluginService
                .getPluginsOfClass(ExplorerAction.class, ExplorerAction.class)
                .stream()
                .map(this::createMenuItem)
                .collect(Collectors.toList());

    }*/
 /*
    public <T> MenuItem createMenuItem(PluginInfo<ExplorerAction> infos) {
        String label = infos.getLabel();
        String icon = infos.getIconPath();
        String description = infos.getDescription();

        try {
            
            
            MenuItem item = new MenuItem();
            item.setOnAction(event->{
                runAction(infos);
            });
            item.setText(label);
            item.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.valueOf(icon)));
            return item;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error when loading " + label, e);
            return null;
        }

    }*/
 /*
    private void runAction(PluginInfo<ExplorerAction> infos){
        try {
        ExplorerAction action = infos.createInstance();
        runAction(action);
        }
        catch(Exception e) {
            logger.log(Level.SEVERE,"Error when creating action "+infos.getLabel());
            uiService.showDialog("Error when creating action "+infos.getLabel());
        }
    }
    
    private <T> void runAction(ExplorerAction<T> action) {
        
       new CallbackTask<Void, T>()
               
                .runLongCallable(action::call)
               .submit(loadingScreenService)
                .then(action::onFinished)
                .start();
        

    }*/
}
