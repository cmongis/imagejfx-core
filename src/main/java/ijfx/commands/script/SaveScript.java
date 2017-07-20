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
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.util.logging.Level;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;
import org.scijava.util.FileUtils;
import org.scijava.widget.FileWidget;

/**
 *
 * @author florian
 */
@Plugin(type = ScriptCommand.class, menuPath = "File > Save", initializer = "init")
public class SaveScript extends ContextCommand implements ScriptCommand {

    @Parameter
    ScriptDisplay scriptDisplay;

    @Parameter(style = FileWidget.SAVE_STYLE)
    File outputFile;

    @Parameter
    UIService uiService;

    @Override
    public void run() {

        try {
            FileUtils.writeFile(outputFile, scriptDisplay.get(0).getCode().getBytes());
        } catch (Exception e) {
            uiService.showDialog("Error when saving file", DialogPrompt.MessageType.ERROR_MESSAGE);
            ImageJFX.getLogger().log(Level.SEVERE, "Error when saving " + outputFile.getPath(), e);
        }
    }

    public void init() {

        String sourceFile = scriptDisplay.get(0).getSourceFile();

        if (sourceFile != null && new File(sourceFile).exists()) {
            outputFile = new File(sourceFile);
        }

    }

}
