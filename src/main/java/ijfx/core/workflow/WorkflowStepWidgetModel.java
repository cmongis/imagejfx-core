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
import org.scijava.Context;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS
 */
public class WorkflowStepWidgetModel<T> extends SuppliedWidgetModel<T> {

    private final WorkflowStep step;

    private final String parameterName;

    @Parameter
    private ModuleService moduleService;

    private ModuleInfo moduleInfo;

    public WorkflowStepWidgetModel(Context context,WorkflowStep step, String key) {
        
        context.inject(this);
        
        this.step = step;

        parameterName = key;

        setType(step.getParameterTypes().get(key));
        setGetter(this::getValueFromStep);
        setSetter(this::setValueInStep);

        ModuleInfo moduleInfo = getModuleInfo();
        
        if (moduleInfo != null) {
            setType(moduleInfo.getInput(key).getType());
            ModuleItem<?> inputInfo = moduleInfo.getInput(key);
            this
                    .setWidgetStyle(inputInfo.getWidgetStyle())
                    .setLabel(key)
                    .setMin((Number) inputInfo.getMinimumValue())
                    .setMax((Number) inputInfo.getMaximumValue())
                    .setSoftMin((Number) inputInfo.getSoftMinimum())
                    .setSoftMax((Number) inputInfo.getSoftMaximum())
                    .setStepSize(inputInfo.getStepSize())
                    .setChoices(inputInfo.getChoices());

        }

    }

    public ModuleInfo getModuleInfo() {
        if (moduleInfo == null) {
            if (moduleService != null) {

                moduleInfo = moduleService.getIndex()
                        .getAll()
                        .stream()
                        .filter(i -> step.getModuleType().equals(i.getDelegateClassName()))
                        .findFirst()
                        .orElse(null);
            }
        }
        return moduleInfo;
    }

    public String getWidgetLabel() {
        return getItem().getLabel();
    }

    @Override
    public ModuleItem<?> getItem() {

        return getModuleInfo().getInput(parameterName);

    }

    private T getValueFromStep() {
        return (T) step.getParameters().get(parameterName);

    }

    private void setValueInStep(T t) {
        step.getParameters().put(parameterName, t);
    }

}
