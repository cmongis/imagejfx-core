/*
 * /*
 *     This file is part of ImageJ FX.
 *
 *     ImageJ FX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ImageJ FX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * 	Copyright 2015,2016 Cyril MONGIS, Michael Knop
 *
 */
package ijfx.ui.plugin.history;

import ijfx.core.history.HistoryService;
import ijfx.core.uiplugin.Localization;
import ijfx.core.workflow.DefaultWorkflow;
import ijfx.core.workflow.WorkflowIOService;
import ijfx.core.workflow.WorkflowStep;
import ijfx.core.workflow.WorkflowStepModifiedEvent;
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;
import ijfx.ui.UiPlugin;
import ijfx.ui.UiConfiguration;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.ListCell;
import mongis.utils.FXUtilities;
import org.scijava.event.EventHandler;

/**
 *
 * @author Cyril MONGIS, 2015
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "historyPanel", localization = Localization.RIGHT, context = "imagej+visualize -overlay-selected", order = 2)
public class HistoryPanel extends TitledPane implements UiPlugin {

    @FXML
    ListView<WorkflowStep> listView;

    @Parameter
    Context context;

    @Parameter
    HistoryService historyService;

    @Parameter
    WorkflowIOService workflowIOService;
    
    @Parameter
    UIService uiService;

    @FXML
    StackPane stackPane;

    
    
    List<HistoryStep> cellList = new ArrayList<>();
    
    private final static String SAVE_WORKFLOW = "Save workflow";
    private final static String LOAD_WORKFLOW = "Load workflow";
    private final static String ERROR_MESSAGE = "Error when reading the workflow.";

    public HistoryPanel() throws IOException {
        super();

        FXUtilities.injectFXML(this);

    }

    @Override
    public Node getUiElement() {
        return this;
    }

    @Override
    public UiPlugin init() {


        listView.setItems(historyService.getStepList());

        

        listView.setCellFactory(this::createCell);

        return this;

    }
    
    
    
    public ListCell<WorkflowStep> createCell(ListView<WorkflowStep> param) {
        HistoryStep cell = new HistoryStep();
        context.inject(cell);
        cellList.add(cell);
        return cell;
    }
    

    FileChooser chooser;

    private FileChooser getChooser(String title) {
        chooser = null;
        if (chooser == null) {
            chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Workflow JSON File", "*.json"));
        }

        return chooser;

    }

    @FXML
    public void saveWorkflow(ActionEvent event) {

        File file = getChooser(SAVE_WORKFLOW).showSaveDialog(null);

        if (file != null) {

            workflowIOService.saveWorkflow(new DefaultWorkflow(historyService.getStepList()),file);
        }

    }

    @FXML
    public void loadWorkflow(ActionEvent event) {
        File file = getChooser(LOAD_WORKFLOW).showOpenDialog(null);
        if (file != null) {

            Task<Void> task = new Task() {
                public Void call() {
                    try {

                        updateMessage("Loading workflow...");
                        updateProgress(0,3);
                        Thread.sleep(500);
                        //editService.loadWorkflow(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
                        historyService.setCurrentWorkflow(workflowIOService.loadWorkflow(file));
                        updateProgress(3,3);
                    } catch (Exception ex) {
                        ImageJFX.getLogger().log(Level.SEVERE,null,ex);;
                        uiService.showDialog(ERROR_MESSAGE, DialogPrompt.MessageType.ERROR_MESSAGE);
                    }
                    return null;
                }
            };
            //LoadingScreen.getInstance().submitTask(task, false, stackPane);
            ImageJFX.getThreadQueue().submit(task);

        }

    }

    @FXML
    public void repeatAll() {
        historyService.repeatAll();
    }
    
    @FXML
    public void useWorkflow() throws IOException {
        historyService.useWorkflow();
    }

    @FXML
    void deleteAll() {
        historyService.getStepList().clear();
    }
    
    @EventHandler
    public void onWorkflowStepModified(WorkflowStepModifiedEvent event) {
        if(listView.getItems().contains(event.getObject())) {
            cellList
                    .stream()
                    .filter(cell->cell.getItem() == event.getObject())
                    .forEach(cell-> Platform.runLater(cell::refresh));
        }
    }

}
