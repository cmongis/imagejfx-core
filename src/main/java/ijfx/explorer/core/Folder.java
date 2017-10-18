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

import ijfx.core.segmentation.SegmentedObject;
import ijfx.explorer.datamodel.Explorable;
import java.io.File;
import java.util.List;
import javafx.beans.property.Property;
import javafx.concurrent.Task;
import mongis.utils.task.ProgressHandler;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public interface Folder {
    
    
    public String getName();
    
    public void setName(String name);
    
    public File getDirectory();
    
    public List<Explorable> getFileList(ProgressHandler handler);
    
    public List<Explorable> getPlaneList(ProgressHandler handler);
    
    public List<Explorable> getObjectList(ProgressHandler handler);
    
    public Property<Task> currentTaskProperty();
    
    public String getStatus();
    
    //public void addObjects(List<SegmentedObject> objects);
    
    public boolean isFilePartOf(File f);
    
}
