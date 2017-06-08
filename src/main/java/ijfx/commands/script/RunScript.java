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
package ijfx.commands.script;

import ijfx.ui.display.code.ScriptDisplay;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptService;

/**
 *
 * @author florian
 */
@Plugin(type = ScriptCommand.class, menuPath = "File > Run")
public class RunScript extends ContextCommand implements ScriptCommand{    
    @Parameter
    private ScriptService scriptService;
    @Parameter
    private ScriptDisplay scriptDisplay;

    @Override
    public void run() {
        String path = scriptDisplay.get(0).getSourceFile();
        File scriptFile = new File(path);
        try {
            scriptService.run(scriptFile, true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RunScript.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(RunScript.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
