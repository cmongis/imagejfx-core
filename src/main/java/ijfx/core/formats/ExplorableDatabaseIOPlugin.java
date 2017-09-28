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
package ijfx.core.formats;

import ijfx.core.io.ExplorableIOService;
import ijfx.explorer.ExplorableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.scijava.Priority;
import org.scijava.io.AbstractIOPlugin;
import org.scijava.io.IOPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.FileUtils;

/**
 *
 * @author cyril
 */
@Plugin(type = IOPlugin.class,priority=Priority.HIGH_PRIORITY)
public class ExplorableDatabaseIOPlugin extends AbstractIOPlugin<ExplorableList> {

    @Parameter
    ExplorableIOService explorabelIOService;

    @Override
    public Class<ExplorableList> getDataType() {
        return ExplorableList.class;
    }

    @Override
    public ExplorableList open(String source) throws IOException {

        return new ExplorableList(explorabelIOService.loadAll(new File(source)));

    }

    @Override
    public void save(ExplorableList data, String destination) throws IOException {
        explorabelIOService.saveAll(data, new File(destination));
    }

    // -- IOPlugin methods --
    @Override
    public boolean supportsOpen(final String source) {
        
        return source.endsWith(ExplorableIOService.DB_EXTENSION);
        

    }
    
    // -- IOPlugin methods --
    @Override
    public boolean supportsSave(final String source) {

       return source.endsWith(ExplorableIOService.DB_EXTENSION);

    }

    private String getExtension(String source) {
        return FileUtils.getExtension(new File(source));

    }

    
   
}
