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
package ijfx.core.uiplugin;

import ijfx.core.IjfxService;
import ijfx.core.icon.FXIconService;
import ijfx.core.utils.SciJavaUtils;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

/**
 *
 * @author cyril
 */
@Plugin(type = Service.class,priority=Priority.NORMAL_PRIORITY)
public class FXUiCommandService extends DefaultUiCommandService implements IjfxService{
    
    @Parameter
    FXIconService fxIconService;
    
    
    
    public <T> MenuItem createMenuItem(UiCommand<T> action, T object) {
        MenuItem item = new MenuItem(SciJavaUtils.getLabel(action),fxIconService.getIconAsNode(action));
        
        item.setOnAction(event->{
            action.run(object);
        });
        
        return item;
        
    }
    
    public <T> Button createButton(UiCommand<T> action, T object) {
        Button button = new Button(SciJavaUtils.getLabel(action),fxIconService.getIconAsNode(action));
        
        button.setTooltip(new Tooltip(SciJavaUtils.getDescription(action)));
        
        button.setOnAction(event->{
           action.run(object);
        });
        
        return button;
    }
    
    
    
}
