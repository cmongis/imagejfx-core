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
package ijfx.explorer.draganddrop;

import ijfx.core.activity.ActivityService;
import ijfx.explorer.ExplorerActivity;
import ijfx.explorer.core.Folder;
import ijfx.explorer.core.FolderManagerService;
import java.io.File;
import org.scijava.display.Display;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;
import org.scijava.ui.dnd.AbstractDragAndDropHandler;
import org.scijava.ui.dnd.DragAndDropHandler;

/**
 *
 * @author cyril
 */
@Plugin(type = DragAndDropHandler.class)
public class FolderDragAndDropHandler extends AbstractDragAndDropHandler<File>{

    
    @Parameter
    UIService uiService;
    
    
    @Parameter
    FolderManagerService folderManagerService;
    
    @Parameter
    ActivityService activityService;
    
    private final static String QUESTION = "Do you want to explore %s and its subfolder ?";
    
    @Override
    public boolean supports(File dataObject) {
        if(super.supports(dataObject)) {
            return dataObject.isDirectory();
        }
        else return false;
    }

    @Override
    public boolean drop(File dataObject, Display<?> display) {
        
        DialogPrompt.Result result = uiService.showDialog(String.format(QUESTION,dataObject.getName()), DialogPrompt.MessageType.QUESTION_MESSAGE,DialogPrompt.OptionType.YES_NO_OPTION);
        
        if(DialogPrompt.Result.YES_OPTION.equals(result)) {
            
            Folder folder = folderManagerService.addFolder(dataObject);
            
            activityService.open(ExplorerActivity.class);
            
            folderManagerService.setCurrentFolder(folder);
            
            return true;
        }
        
        else return false;
        
    }

    @Override
    public Class<File> getType() {
        return File.class;
    }
    
}
