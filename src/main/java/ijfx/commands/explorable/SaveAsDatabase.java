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
package ijfx.commands.explorable;

import ijfx.core.io.ExplorableIOService;
import ijfx.explorer.core.FolderManagerService;
import ijfx.explorer.datamodel.Explorable;
import java.io.File;
import java.util.List;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.widget.FileWidget;

/**
 *
 * @author cyril
 */
@Plugin(type = ExplorableDisplayCommand.class, label = "Save...",iconPath = "fa:save",description="Saves the data in a form of a database file that can be reopened by ImageJ-FX for further annotation. Images are not exported.")
public class SaveAsDatabase extends AbstractExplorableDisplayCommand{

    
    @Parameter(label = "Select output file",style = FileWidget.SAVE_STYLE + " " + ExplorableIOService.DB_EXTENSION)
    File outputFile;
    
    @Parameter
    ExplorableIOService explorableIOService;
    
    @Parameter
    FolderManagerService folderManagerService;
    
    @Override
    public void run(List<? extends Explorable> items) throws Exception {
        
        if(outputFile == null) return;

        explorableIOService.saveAll(items, outputFile);
        folderManagerService.addFolder(outputFile);
        uiService.showDialog("Database exported successfully", DialogPrompt.MessageType.INFORMATION_MESSAGE);
        
    }
    
}
