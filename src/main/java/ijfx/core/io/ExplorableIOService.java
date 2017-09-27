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
package ijfx.core.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import ijfx.core.IjfxService;
import ijfx.explorer.datamodel.Taggable;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Cyril MONGIS
 */
public interface ExplorableIOService extends IjfxService{
    
    
    Taggable loadTaggable(File file) throws IOException;
    
    List<? extends Taggable> load(File file) throws IOException;
    
    void save(List<? extends Taggable> explorableList, File file) throws IOException;
    
    void saveDatasets(List<? extends Taggable> taggable, String folder, String suffix) throws IOException;
    
    void save(Taggable taggable, File target) throws IOException;
    
     ObjectMapper getJsonMapper();
    
   // List<? extends Taggable> loadTaggables(File file) throws IOException;
    
    //void saveTaggables(List<? extends Taggable> taggables);
    
}
