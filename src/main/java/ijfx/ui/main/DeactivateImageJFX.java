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

import net.imagej.app.QuitProgram;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.ui.swing.SwingUI;

/**
 *
 * @author Cyril MONGIS
 */
 @Plugin(type = Command.class, menuPath="Help > Switch back to ImageJ/Fiji")
public class DeactivateImageJFX extends ContextCommand{
   

    @Parameter
    UIService uiService;
    
    @Parameter
    CommandService commandService;
    
    @Override
    public void run() {
        
        uiService.getUI(SwingUI.NAME).dispose();
        
        uiService.setDefaultUI(uiService.getUI(SwingUI.NAME));
        
        uiService.showDialog("ImageJ-FX is shutting down to apply the new changes.");
        
        commandService.run(QuitProgram.class, true);
        
        //uiService.showUI(ImageJFX.UI_NAME);
        
        
        
    }
    
}
