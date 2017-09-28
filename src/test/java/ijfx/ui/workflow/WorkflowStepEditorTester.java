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

import ijfx.core.batch.BatchService;
import ijfx.core.workflow.Workflow;
import ijfx.ui.utils.IjfxUITester;
import javafx.scene.Node;
import net.imagej.plugins.commands.assign.InvertDataValues;
import net.imagej.plugins.commands.imglib.GaussianBlur;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class WorkflowStepEditorTester extends IjfxUITester{

    @Parameter
    BatchService batchService;
    
    @Override
    public Node initApp() {
        
        
        Workflow workflow = batchService
                .builder()
                .addStep(GaussianBlur.class,"sigma",4.0)
                .addStep(InvertDataValues.class)
                .getWorkflow();
                
        
        WorkflowPanel panel = new WorkflowPanel(getContext());
        
        panel.stepListProperty().addAll(workflow.getStepList());
        
        return panel;
        
        
    }
    
    public static void main(String... args) {
        launch(args);
    }
    
}
