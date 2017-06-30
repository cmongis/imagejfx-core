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
package ijfx.explorer.commands;

import ijfx.core.uiplugin.AbstractUiCommand;
import ijfx.core.uiplugin.UiCommand;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.ExplorerActivity;
import ijfx.explorer.ExplorerService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

/**
 *
 * @author cyril
 */
@Plugin(type = UiCommand.class,label = "Annotate...",iconPath = "fa:edit")
public class OpenInWindow extends AbstractUiCommand<ExplorerActivity>{

    @Parameter
    ExplorerService explorerService;
    
    @Parameter
    UIService displayService;
    
    public OpenInWindow() {
        super(ExplorerActivity.class);
    }

    
    
    @Override
    public void run(ExplorerActivity t) {
        
        ExplorableList list = new ExplorableList(explorerService.getItems());
        displayService.show(list);
        
    }
    
}
