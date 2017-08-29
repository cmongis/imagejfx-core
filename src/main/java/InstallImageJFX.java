
import java.io.File;
import net.imagej.updater.FilesCollection;
import net.imagej.updater.UpdateService;
import net.imagej.updater.UpdateSite;
import org.scijava.app.AppService;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Menu;
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
@Plugin(type = Command.class,menu = {@Menu(label = "Help"),
	@Menu(label = "Install ImageJ-FX") },label="Install ImageJ-FX")
public class InstallImageJFX extends ContextCommand{

    @Parameter
    UpdateService updateService;
    
    @Parameter
    AppService appService;
    
    
    
    @Override
    public void run() {

        FilesCollection filesCollection = new FilesCollection(appService.getApp().getBaseDirectory());
        
        UpdateSite addUpdateSite = filesCollection.addUpdateSite("ImageJ-FX", "http://localhost:8080", null, null, 0);
        try {
            filesCollection.activateUpdateSite(addUpdateSite, null);
        }
        catch(Exception e) {
            
        }
       
    }
    
}
