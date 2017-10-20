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
package ijfx.explorer.core;

import ijfx.core.io.ExplorableIOService;
import ijfx.core.metadata.MetaData;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import mongis.utils.task.ProgressHandler;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class DatabaseFolder implements Folder{

    @Parameter
    private ExplorableIOService explorableIOService;
    
    private List<Explorable> list = null;
    
    private final File file;

    private final String NOT_LOADED = "Click to load";
    
    private String status = NOT_LOADED;
    
    private Property<Task> currentTaskProperty = new SimpleObjectProperty<>();
    
    public DatabaseFolder(Context context,File file) {
        context.inject(this);
        this.file = file;
    }
    
    
    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public void setName(String name) {
        ImageJFX.getLogger().warning("action impossible");
        
    }

    @Override
    public File getDirectory() {
        return file.getParentFile();
    }

    @Override
    public List<Explorable> getFileList(ProgressHandler handler) {
        if(list == null) {
            load(handler);
        }
        return list;
    }

    @Override
    public List<Explorable> getPlaneList(ProgressHandler handler) {
        return getFileList(handler);
    }

    @Override
    public List<Explorable> getObjectList(ProgressHandler handler) {
        return getFileList(handler);
    }

    @Override
    public Property<Task> currentTaskProperty() {
        return currentTaskProperty;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public boolean isFilePartOf(File f) {
       return  getFileList(ProgressHandler.NONE)
                .stream()
                .filter(exp->f.getAbsolutePath().equals(exp.getMetaData(MetaData.ABSOLUTE_PATH).getStringValue()))
                .count() > 0;
    }
    
    private void load(ProgressHandler handler) {
        handler.setStatus("Loading...");
        handler.setProgress(0.2);
        try {
        list  = new ArrayList<>(explorableIOService.loadAll(file));
        }
        catch(Exception e) {
            
            ImageJFX.getLogger().log(Level.SEVERE,null,e);
            
            status = "Error when loading database !";
            
        }
        
        handler.setProgress(1.0);
    }
    
}
