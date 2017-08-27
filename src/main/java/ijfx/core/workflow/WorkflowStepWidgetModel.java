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

import ijfx.ui.inputharvesting.SuppliedWidgetModel;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class WorkflowStepWidgetModel<T> extends SuppliedWidgetModel<T> {

    final WorkflowStep step;

    final String parameterName;

    @Parameter
    ModuleService moduleService;
    
    public WorkflowStepWidgetModel(WorkflowStep step, Class<T> type, String key) {
        super(type);
        this.step = step;
                
        parameterName = key;
        
        setGetter(this::getValueFromStep);
        setSetter(this::setValueInStep);
        
        
    }
    
    @Override
    public ModuleItem<?> getItem() {
        if(moduleService != null) {
            return moduleService.getModuleById(step.getModuleType()).getInput(parameterName);
        }
        else {
            return null;
        }
    }
    
    private T getValueFromStep() {
        return (T) step.getParameters().get(parameterName);
        
    }
    
    private void setValueInStep(T t) {
        step.getParameters().put(parameterName, t);
    }

 

}
