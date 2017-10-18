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
import ijfx.core.batch.BatchService;
import ijfx.core.overlay.MeasurementService;
import ijfx.core.uicontext.UiContextProperty;
import ijfx.core.workflow.WorkflowStep;
import ijfx.segmentation.core.InteractiveSegmentation;
import ijfx.segmentation.core.InteractiveSegmentationUI;
import ijfx.segmentation.core.WorkflowSegmentation;
import ijfx.ui.UiContexts;
import ijfx.ui.loading.LoadingScreenService;
import ijfx.ui.widgets.PopoverToggleButton;
import ijfx.ui.widgets.PrettyStats;
import ijfx.ui.workflow.WorkflowPanel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import mongis.utils.bindings.OpacityTransitionBinding;
import net.imagej.display.ImageDisplay;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.controlsfx.control.PopOver;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = InteractiveSegmentationUI.class, label = "Process workflow", priority = 0.09)
public class WorkflowSegmentationUiPanel extends VBox implements InteractiveSegmentationUI<WorkflowSegmentation> {

    @Parameter
    private Context context;

    @Parameter
    private BatchService batchService;

    @Parameter
    UIService uiService;

    @Parameter
    LoadingScreenService loadingService;

    @Parameter
    MeasurementService measurementSrv;

    @FXML
    protected ToggleButton toggleButton;

    Property<Img<BitType>> maskProperty = new SimpleObjectProperty();

    protected WorkflowPanel workflowPanel;

    protected PrettyStats stepCount = new PrettyStats("Steps");

    protected Button testButton = new Button("Test", new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE_ALT));

    protected ImageDisplay imageDisplay;

    private final BooleanProperty activatedProperty = new SimpleBooleanProperty();

    protected UiContextProperty isExplorer;

    
    WorkflowSegmentation currentSegmentation; 
    
    public WorkflowSegmentationUiPanel() {

        getStyleClass().add("vbox");

        // creating the toggle button displaying the workflow panel
        toggleButton = new ToggleButton("Edit workflow");
        toggleButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.GEARS));
        toggleButton.setMaxWidth(Double.POSITIVE_INFINITY);

        getChildren().addAll(stepCount, toggleButton, testButton);

        testButton.setOnAction(this::onTestClicked);
        testButton.setMaxWidth(Double.POSITIVE_INFINITY);
        testButton.getStyleClass().add("success");

    }

   
   
    private void onWorkflowChange(WorkflowStep step) {
        currentSegmentation.getWorkflow().setStepList(workflowPanel.stepListProperty());
    }
    
    @Override
    public Node getContentNode() {

        if (workflowPanel == null) {

            workflowPanel = new WorkflowPanel(context);
            
            workflowPanel.setPrefHeight(500);
            workflowPanel.setPrefWidth(600);
            workflowPanel.addChangeListener(this::onWorkflowChange);
            isExplorer = new UiContextProperty(context, UiContexts.EXPLORE);
            
            new OpacityTransitionBinding(testButton, isExplorer.not());
            
            stepCount.valueProperty().bind(Bindings.createIntegerBinding(() -> workflowPanel.stepListProperty().size(), workflowPanel.stepListProperty()));

            // binding the toggle button to the workflow panel
            PopoverToggleButton.bind(toggleButton, workflowPanel, PopOver.ArrowLocation.RIGHT_CENTER);
        }

        return this;
    }

    
    private void onTestClicked(ActionEvent event) {
        
        getCurrentSegmentation().reprocess(workflowPanel.stepListProperty());
        
    }

    public WorkflowSegmentation getCurrentSegmentation() { 
        return currentSegmentation;
    }
  
    
    
    
    @Override
    public void bind(WorkflowSegmentation t) {
        currentSegmentation = t;
        workflowPanel.stepListProperty().setAll(t.getWorkflow().getStepList());
    }

    @Override
    public Class<? extends InteractiveSegmentation> getSupportedType() {
        return WorkflowSegmentation.class;
    }

   

}
