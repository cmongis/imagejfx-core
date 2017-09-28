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
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Taggable;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Cyril MONGIS
 */
public interface ExplorableIOService extends IjfxService{
    
    
    public final String DB_EXTENSION = "jdb.gz";
    
    
    /**
     * Loads one explorable from a JSON file that should contain only one Explorable
     * @param file
     * @return a single explorable
     * @throws IOException 
     */
    Explorable loadOne(File file) throws IOException;
    
    /**
     * Loads a list of explorable contained in a single JSON file
     * @param file 
     * @return 
     * @throws IOException 
     */
    List<? extends Explorable> loadAll(File file) throws IOException;
    
    /**
     * Saves a list of explorable into a single JSON file
     * @param explorableList
     * @param file
     * @throws IOException 
     */
    void saveAll(List<? extends Explorable> explorableList, File file) throws IOException;
    
    /**
     * Saves a single Explorable into a single JSON file
     * @param explorable
     * @param target destination file
     * @throws IOException 
     */
    void saveOne(Explorable explorable, File target) throws IOException;
    
     /**
      * Returns the ObjectMapper used for generating JSON files.
      * The ObjectMapper can be used on Taggables and Overlays.
      * @return Json Jackson Object Mapper
      */
     ObjectMapper getJsonMapper();
    
    
    
}
