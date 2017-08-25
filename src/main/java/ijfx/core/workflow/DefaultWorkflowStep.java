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
package ijfx.core.workflow;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import ijfx.core.datamodel.LongInterval;
import ijfx.core.image.ChannelSettings;
import ijfx.core.workflow.json.JsonFieldName;
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.imagej.ImageJ;
import net.imagej.axis.AxisType;
import net.imagej.threshold.ThresholdMethod;
import net.imglib2.display.ColorTable8;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.module.Module;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;

import org.scijava.Context;

/**
 *
 * @author Cyril MONGIS, 2015
 */
public class DefaultWorkflowStep implements WorkflowStep {

    protected String id;

    @JsonIgnore
    protected Module module;

    @JsonIgnore
    protected String className;

    @JsonIgnore
    protected Map<String, Object> parameters = new HashMap<>();

    @Parameter
    protected CommandService commandService;

    @Parameter
    protected ModuleService moduleSerivce;

    @Parameter
    protected WorkflowIOService workflowIOService;
    
    Logger logger = ImageJFX.getLogger();
    
   

    public DefaultWorkflowStep() {

    }

    private void checkInjection() {
        if(commandService == null) {
            
        }
    }
    
    public DefaultWorkflowStep(Context context) {
        context.inject(this);
    }
    
    public DefaultWorkflowStep(Context context,Module module) {
        this(context);
        setModule(module);
        setParameters(module.getInputs());
    }

    public DefaultWorkflowStep(Context context,String className) {
        this(context);
        setClassName(className);
        prefill();
    }

    
    
    public DefaultWorkflowStep prefill() {
        setParameters(getModule().getInputs());
        return this;
    }

    @Override
    public String getId() {
        if (id == null && module != null) {
            id = module.getInfo().getDelegateClassName();
        }
        return id;
    }

    public DefaultWorkflowStep createModule(ImageJ ij) {
        return createModule(ij.command(), ij.module());
    }

    protected DefaultWorkflowStep createModule(CommandService commandService, ModuleService moduleService) {
        CommandInfo infos = commandService.getCommand(getClassName());
        if (infos != null) {
            module = moduleService.createModule(infos);
            try {
                module.initialize();
            } catch (Exception ex) {
               logger.log(Level.WARNING,"Error when initializing module...",ex);
            }
        }
        
        
        return this;
    }

    @Override
    @JsonIgnore
    public Module getModule() {

        if (module == null) {
            createModule(commandService, moduleSerivce);
        }
         
        return module;
    }

    @Override
    @JsonGetter("parameters")
    public Map<String, Object> getParameters() {
        return parameters;
    }

   
    @Override
    @JsonSetter("id")
    public void setId(String id) {
        this.id = id;
    }

    public void setModule(Module module) {
        //this.module = module;
        className = module.getInfo().getDelegateClassName();
    }

    @JsonSetter("parameters")
    public void setParameters(Map<String, Object> parameters) {

        parameters.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            
            boolean canSave  = workflowIOService.canSave(value);
            if (canSave) {

                //if (value instanceof String && value.toString().startsWith(FileSerializer.FILE_PREFIX)) {
                //    this.parameters.put(key, FileDeserializer.deserialize(value.toString()));
                //} else {
                    this.parameters.put(key, value);
                //}

            }
        });
        //this.parameters = parameters;
    }

   
   
    @JsonGetter(value = JsonFieldName.CLASS)
    public String getClassName() {
        if (className == null && module != null) {
            className = module.getInfo().getDelegateClassName();
        }
        return className;
    }

    @JsonSetter(value = JsonFieldName.CLASS)
    public void setClassName(String className) {
        this.className = className;
    }

    @JsonSetter(value = "parameters")
    public void setParameter(String alpha, Object object) {
        getParameters().put(alpha, object);

    }

    @JsonGetter(value = "parameters")
    public Object getParameter(String alpha) {
        return getParameters().get(alpha);
    }

}
