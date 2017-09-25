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
package ijfx.explorer.views;

import ijfx.core.metadata.MetaData;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.wrappers.MetaDataSetExplorerWrapper;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Command.class, menuPath = "Plugins > Test > Generate dummy explorables")
public class GenerateDummyExplorables extends ContextCommand{

    @Parameter(type = ItemIO.OUTPUT)
    ExplorableList output;
    
    @Override
    public void run() {
        
        output = new ExplorableList();
        
        for(int i = 0;i!= 100;i++) {
            
            MetaData name = MetaData.create(MetaData.NAME, RandomStringUtils.random(3, true, false));
            MetaData m1 = MetaData.create("Random strings 1", RandomStringUtils.random(5,true,false));
            MetaData m2 = MetaData.create("Random strings 2", RandomStringUtils.random(3,true,false));
            MetaData m3 = MetaData.create("Random double 1", new Random().nextDouble());
            MetaData m4 = MetaData.create("Random double 2", new Random().nextDouble());
            
            Explorable explorable = new MetaDataSetExplorerWrapper(name,m1,m2,m3,m4);
            explorable.addTag(Tag.create(RandomStringUtils.random(3,true,false)));
            explorable.addTag(Tag.create(RandomStringUtils.random(3,true,false)));
            output.add(explorable);
            
            
        }
        
    }
    
}
