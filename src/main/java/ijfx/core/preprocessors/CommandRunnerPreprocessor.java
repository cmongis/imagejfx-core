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
package ijfx.core.preprocessors;

import ijfx.core.batch.CommandRunner;
import org.scijava.Priority;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
@Plugin(type = PreprocessorPlugin.class, priority = Priority.LOW_PRIORITY)
public class CommandRunnerPreprocessor extends AbstractPreprocessorPlugin{

    @Override
    public void process(Module module) {

        String findInput = findInput(module);
        if(findInput != null) {
            
            module.setInput(findInput, new CommandRunner(getContext()));
            
        }
        
    }
    
    String findInput(Module module) {
       for(ModuleItem item : module.getInfo().inputs()) {
           
           if(item.getType() == CommandRunner.class) {
               return item.getName();
           }
       }
       return null;
    }
    
}
