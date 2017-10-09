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

import ij.IJ;
import org.scijava.app.AppService;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.command.ContextCommand;
import org.scijava.console.ConsoleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Command.class, menuPath="Help > Switch to ImageJ-FX")
public class ActivateImageJFX extends ContextCommand {

    @Parameter
    UIService uiService;
    
    @Parameter
    CommandService commandService;
    
    @Parameter
    AppService appService;
    
    @Parameter
    ConsoleService consoleService;
  
    
    @Override
    public void run() {
        
        ImageJFX.disposeSwingUI(consoleService,uiService);
        uiService.setDefaultUI(uiService.getUI(ImageJFX.UI_NAME));
        
       
        
        //uiService.showDialog("ImageJ-FX activated.");
        try {
            IJ.run("Switch to Modern Mode");
        }
        catch(Exception e) {
            
        }
        finally {
             uiService.showUI(ImageJFX.UI_NAME);
        }
        
       
        
        
    } 
}
