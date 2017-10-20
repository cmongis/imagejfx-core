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

import ijfx.core.activity.ActivityService;
import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.ExplorerActivity;
import org.scijava.Priority;
import org.scijava.display.DisplayService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import ijfx.explorer.ExplorerViewService;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type=PreprocessorPlugin.class,priority = Priority.LOW_PRIORITY)
public class ExplorableListPreprocessor extends AbstractPreprocessorPlugin{

    @Parameter
    ActivityService activityService;
    
    @Parameter
    DisplayService displayService;
    
    @Parameter
    ModuleService moduleService;
    
    @Parameter
    ExplorerViewService explorerService;
    
    @Override
    public void process(Module module) {

        ModuleItem<ExplorableList> singleInput = moduleService.getSingleInput(module, ExplorableList.class);
        
        if(singleInput != null) {
            
            ExplorableList list = new ExplorableList();
            
            if(activityService.isCurrentActivity(ExplorerActivity.class)) {
                if(explorerService.getSelectedItems().size() == 0) {
                    list.addAll(explorerService.getDisplayedItems());
                }
                else {
                    list.addAll(explorerService.getSelectedItems());
                }
            }
            else {
                ExplorableDisplay activeDisplay = displayService.getActiveDisplay(ExplorableDisplay.class);
                if(activeDisplay != null) {
                    if(activeDisplay.getSelectedItems().size() == 0) {
                        list.addAll(activeDisplay.getDisplayedItems());
                    }
                    else {
                        list.addAll(activeDisplay.getSelectedItems());
                    }
                }
            }
            
            if(list.size() > 0) {
                
                singleInput.setValue(module, list);
                module.setInput(singleInput.getName(), list);
                //System.out.println(singleInput.getValue(module));
            }
            
        }
        
    }
    
}
