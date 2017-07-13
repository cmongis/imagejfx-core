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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import mongis.utils.TextFileUtils;
import org.scijava.ItemIO;
import org.scijava.command.CommandService;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptService;
import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;

/**
 *
 * @author florian
 */
@Plugin(type = ScriptCommand.class, menuPath = "File > Run")
public class RunScript extends ContextCommand implements ScriptCommand {

    @Parameter(type = ItemIO.INPUT)
    private ScriptDisplay scriptDisplay;

    @Parameter
    UIService uiService;

    @Parameter
    CommandService commandService;

    @Parameter
    ScriptService scriptService;
    
    @Override
    public void run() {
        try {
            String path = scriptDisplay.get(0).getSourceFile();
            
            File scriptFile;
            
            if (path == null) {
                File chooseFile = uiService.chooseFile("Save your file first", null, FileWidget.SAVE_STYLE);
                
                if (chooseFile == null) {
                    uiService.showDialog("Aborting.");
                    return;
                }
                
                scriptDisplay.get(0).setSourceFile(chooseFile.getAbsolutePath());
                scriptDisplay.update();
                scriptFile = chooseFile;
                
            } else {
                scriptFile = new File(path);
            }
            
            if (scriptFile != null) {
                try {
                    TextFileUtils.writeTextFile(scriptFile, scriptDisplay.get(0).getCode());
                } catch (IOException ex) {
                    
                    Logger.getLogger(RunScript.class.getName()).log(Level.SEVERE, null, ex);
                    
                    return;
                }
            }
            scriptService.run(scriptFile, true);
            // commandService.run(org.scijava.plugins.commands.script.RunScript.class, true, "script", scriptFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RunScript.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(RunScript.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
