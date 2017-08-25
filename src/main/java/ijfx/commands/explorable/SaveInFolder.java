package ijfx.commands.explorable;
import ijfx.core.batch.BatchService;
import ijfx.explorer.commands.ExplorerActivityCommand;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.loading.LoadingScreenService;
import java.io.File;
import java.util.List;
import mongis.utils.FXUtilities;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

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
/**
 *
 * @author cyril
 */
@Plugin(type = ExplorerActivityCommand.class,label = "Export to...",iconPath="fa:save")
public class SaveInFolder extends ExplorerActivityCommand{

    @Parameter
    BatchService batchService;
    
    @Parameter
    LoadingScreenService loadingScreenService;
    
    @Override
    protected void process(List<? extends Explorable> selected) {

        File file = FXUtilities.openFolder("Open folder to save in...", "~/");
        
        if(file == null) return;
        
        batchService
                .builder()
                .add(selected)
                .saveIn(file.getAbsolutePath())
                .startAsync(true)
                .submit(loadingScreenService);
                
        
        
        
    }
    
}
