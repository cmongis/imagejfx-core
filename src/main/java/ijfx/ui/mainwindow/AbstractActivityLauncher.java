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
package ijfx.ui.mainwindow;

import ijfx.core.activity.Activity;
import ijfx.core.activity.ActivityService;
import ijfx.core.uiplugin.AbstractUiCommand;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public abstract class AbstractActivityLauncher<T extends Activity> extends AbstractUiCommand<SideBar>{

    
    @Parameter
    private ActivityService activityService;
    
    private final Class<T> activityType;
    
    public AbstractActivityLauncher(Class<T> type) {
        super(SideBar.class);
        activityType = type;
    }

    
    
    
    
    @Override
    public void run(SideBar t) {
        
        activityService.open(activityType);
        
    }

    
    
    
}
