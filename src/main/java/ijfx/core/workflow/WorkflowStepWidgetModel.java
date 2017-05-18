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
package ijfx.core.workflow;

import java.util.ArrayList;
import org.scijava.Context;
import org.scijava.widget.DefaultWidgetModel;
import org.scijava.widget.InputPanel;

/**
 *
 * @author cyril
 */
public class WorkflowStepWidgetModel extends DefaultWidgetModel {

    final WorkflowStep step;

    final String parameterName;

    public WorkflowStepWidgetModel(Context context, WorkflowStep step, String parameter, InputPanel<?, ?> panel) {
        super(context, panel, step.getModule(), step.getModule().getInfo().getInput(parameter), new ArrayList<>());
        this.step = step;
        this.parameterName = parameter;
        
    }

    @Override
    public Object getValue() {

        if (step.getParameters().get(parameterName) == null) {
            return super.getValue();
        }
        return step.getParameters().get(parameterName);
    }

    @Override
    public void setValue(Object object) {
        if (step != null) {
            super.setValue(object);

            step.getParameters().put(parameterName, object);
        }
    }

}
