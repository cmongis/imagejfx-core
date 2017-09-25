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

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Service.class)
public class DefaultWorkflowService extends AbstractService implements WorkflowService {

    @Override
    public WorkflowStep createStep(Class<? extends Command> moduleClass, Object... params) {
        
        return createStep(moduleClass.getName(),params);
       
    }

    @Override
    public WorkflowStep createStep(String cmd, Object... params) {
          DefaultWorkflowStep step = new DefaultWorkflowStep(getContext(),cmd);
        for (int i = 0; i != params.length; i += 2) {
            step.setParameter(params[i].toString(), params[i + 1]);
        }
        return step;
    }
    
}
