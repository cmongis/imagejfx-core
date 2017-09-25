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
package ijfx.ui.plugin.history;

import ijfx.core.workflow.WorkflowStep;
import ijfx.core.workflow.WorkflowStepWidgetModel;
import ijfx.ui.inputharvesting.InputPanelFX;
import ijfx.ui.inputharvesting.SuppliedWidgetModel;
import ijfx.ui.main.ImageJFX;
import java.util.logging.Level;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;
import org.scijava.widget.WidgetService;

/**
 *
 * @author Cyril MONGIS
 */
public class WorkflowStepEditPane extends BorderPane {

    InputPanelFX panel;

    WorkflowStep step;

    @Parameter
    Context context;

    @Parameter
    WidgetService widgetService;

    public WorkflowStepEditPane(Context context) {

        context.inject(this);

    }

    public void clear() {
        if (panel != null) {
            panel = null;
        }
    }

    public void edit(WorkflowStep step) {

        if (panel == null) {
            panel = new InputPanelFX();
            setCenter(panel.getComponent());
        }

        step
                .getParameters()
                .keySet()
                .stream()
                .map(key -> createModel(step, key))
                .filter(model -> model != null)
                .map(this::createWidget)
                .forEach(panel::addWidget);

        panel.refresh();
    }

    public WidgetModel createModel(WorkflowStep step, String key) {
        try {

            WorkflowStepWidgetModel workflowStepWidgetModel = new WorkflowStepWidgetModel(step, step.getParameters().get(key).getClass(), key);
            context.inject(workflowStepWidgetModel);
            return workflowStepWidgetModel;

        } catch (Exception e) {
            ImageJFX
                    .getLogger()
                    .log(Level.SEVERE, "Error when creating WidgetModel for " + key, e);
            return null;
        }

    }

    public InputWidget<?, Node> createWidget(WidgetModel model) {
        InputWidget<?, Node> widget = (InputWidget<?, Node>) widgetService.find(model);

        widget.set(model);

        return widget;

    }

}
