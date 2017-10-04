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
package ijfx.ui.workflow;

import ijfx.core.history.HistoryService;
import ijfx.core.workflow.DefaultWorkflow;
import ijfx.core.workflow.DefaultWorkflowStep;
import ijfx.core.workflow.Workflow;
import ijfx.core.workflow.WorkflowStep;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.utils.ChoiceBoxMenuCreator;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import mongis.utils.FXUtilities;
import org.controlsfx.control.textfield.TextFields;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.menu.MenuService;
import org.scijava.menu.ShadowMenu;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class WorkflowPanel extends GridPane {

    @Parameter
    MenuService menuService;

    @Parameter
    CommandService commandService;

    @Parameter
    ModuleService moduleService;

    @Parameter
    Context context;

    @Parameter
    UIService uiService;

    @FXML
    private ListView<WorkflowStep> stepListView;

    @FXML
    private TextField moduleSearchTextField;

    @FXML
    private Button addButton;

    @FXML
    private MenuButton menuButton;

    @Parameter
    private HistoryService historyService;

    Logger logger = ImageJFX.getLogger();

    private List<WeakReference<Consumer<WorkflowStep>>> listeners = new ArrayList<>();

    public WorkflowPanel(Context context) {
        try {
            FXUtilities.injectFXML(this);

            context.inject(this);
            init();
            setId("workflow-panel");

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Couldn't initiate the WorkflowPanel", ex);
        }

    }

    /*
     Properties
     */
    private final ObservableList<WorkflowStep> stepList = FXCollections.observableArrayList();

    private final BooleanProperty isModuleNameValidProperty = new SimpleBooleanProperty(false);

    private HashMap<String, Runnable> modules = new HashMap<>();

    public void init() {

        // instanciating a menu creator
        ChoiceBoxMenuCreator menuCreator = new ChoiceBoxMenuCreator(this::onAddStepButtonClicked);

        // setting the cell factory for the list that will display the indiviual staps
        stepListView.setCellFactory(newCell -> new DraggableStepCell(context, this::delete));

        stepListView.setItems(stepList);

        stepListView.setEditable(true);

        // registering the actions from the menu so it can be used with the autocompletion text field.
        // it will fill the modules hash map with runnables.
        registerAction(menuService.getMenu());

        // binding the autocompletion to the hashmap
        TextFields.bindAutoCompletion(moduleSearchTextField, modules.keySet());

        // adding the event handler that will add the step
        moduleSearchTextField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (modules == null) {
                return;
            }
            if (event.getCode() == KeyCode.ENTER) {
                onAddButtonClicked();
            }
        });

        moduleSearchTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            isModuleNameValidProperty.setValue(modules.containsKey(newValue));
        });

        menuService.createMenus(menuCreator, menuButton);

        //addEventHandler(ModuleInputEvent.ALL, this::onModuleInputChanged);
    }

    public void addChangeListener(Consumer<WorkflowStep> listener) {

    }

    public void setItems(List<WorkflowStep> steps) {
        stepList.clear();
        stepList.addAll(steps);
    }

    @FXML
    private void onAddButtonClicked() {
        modules.get(moduleSearchTextField.getText()).run();
        moduleSearchTextField.setText("");
    }

    @FXML
    public void loadWorkflow() {
        Workflow workflow = new WorkflowSelectionDialog(context).showAndWait().orElse(null);
        if (workflow != null) {
            stepList.addAll(workflow.getStepList());
        }
    }

    @FXML
    public void saveWorkflow() {
        new SaveWorkflowDialog(context).save(new DefaultWorkflow(new ArrayList(stepList)));
    }

    @FXML
    public void importFromHistory() {

        DialogPrompt.Result result = uiService.showDialog(
                "This action will delete your current workflow.\nAre you sure"
                + "you want to continue ?",
                 DialogPrompt.MessageType.QUESTION_MESSAGE,
                 DialogPrompt.OptionType.YES_NO_OPTION);

        if (DialogPrompt.Result.YES_OPTION.equals(result)) {

            stepList.clear();
            stepList.addAll(historyService.getStepList().filtered(step -> step.getModule().getDelegateObject().getClass().getSimpleName().contains("OpenFile") == false));
        }
    }

    // handler used when clicked on a action in the menu list
    private void onAddStepButtonClicked(ShadowMenu shadowMenu) {
        addStep(shadowMenu.getModuleInfo());
    }

    public void delete(WorkflowStep step) {
        stepList.remove(step);
    }

    private void registerAction(ShadowMenu menu) {
        if (menu.isLeaf()) {
            modules.put(menu.getName(), () -> {
                addStep(menu.getModuleInfo());
            });
        } else {
            menu.getChildren().forEach(child -> registerAction(child));
        }
    }

    //public void onModuleInputChanged(ModuleInputEvent event) {
    //    notifyChange(null);
    //}
    public void addStep(ModuleInfo moduleInfo) {

        stepList.add(new DefaultWorkflowStep(context, moduleInfo.getDelegateClassName())
                .prefill());
    }

    public void addStep(Class<?> moduleClass) {
        try {
            stepList.add(
                    new DefaultWorkflowStep(context, moduleClass.getName())
                            .prefill()
            );
        } catch (Exception e) {
            ImageJFX.getLogger().log(Level.SEVERE, "Couldn't create plugin from class :" + moduleClass.getSimpleName(), e);
        }
    }

    public ObservableList<WorkflowStep> stepListProperty() {
        return stepList;
    }

    private void notifyChange(WorkflowStep step) {
        // notify and update the list of available listeners
        listeners = listeners
                .stream()
                .filter(ref -> ref.get() != null)
                .peek(ref -> ref.get().accept(step))
                .collect(Collectors.toList());

    }
}
