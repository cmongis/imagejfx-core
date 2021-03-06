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
package ijfx.ui.main;

import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

/**
 *
 * @author cyril
 */
public class AbstractUISwitchCommand extends ContextCommand{
    
    final private String UI_NAME;

    @Parameter
    protected UIService uiService;
    
    public AbstractUISwitchCommand(String UI_NAME) {
        this.UI_NAME = UI_NAME;
    }

    @Override
    public void run() {
        if(uiService.isDefaultUI(UI_NAME)) return;
        uiService.getDefaultUI().dispose();
        uiService.setDefaultUI(uiService.getUI(UI_NAME));
        System.setProperty(UIService.UI_PROPERTY, UI_NAME);
        
        uiService.showUI();
        
        
    }
    
    
    
}
