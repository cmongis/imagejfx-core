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
package ijfx.ui.test;

import ijfx.core.datamodel.MetaDataOwnerList;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.metadata.MetaDataSet;
import java.util.Random;
import java.util.stream.Stream;
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
@Plugin(type = Command.class, menuPath="Plugins > Test > Generate Dummy MetaDataOwner")
public class DummyMetaData extends ContextCommand{
    
    @Parameter(type = ItemIO.OUTPUT)
    MetaDataOwnerList list;
    
    public void run() {
        
        
        
        list  = new MetaDataOwnerList();
        
        for(int i = 0;i!= 100;i++) {
            
            MetaData name = MetaData.create(MetaData.NAME, RandomStringUtils.random(3, true, false));
            MetaData m1 = MetaData.create("Random strings 1", RandomStringUtils.random(5,true,false));
            MetaData m2 = MetaData.create("Random strings 2", RandomStringUtils.random(3,true,false));
            MetaData m3 = MetaData.create("Random double 1", new Random().nextDouble());
            MetaData m4 = MetaData.create("Random double 2", new Random().nextDouble());
            
            list.add(new SimpleMetaDataOwner(name,m1,m2,m3,m4));
            
            
        }
        
        
        
    }
    
    private class SimpleMetaDataOwner implements MetaDataOwner{
       
        MetaDataSet set = new MetaDataSet();

        SimpleMetaDataOwner(MetaData... initial) {
            Stream.of(initial).forEach(set::put);
        }
        
        @Override
        public MetaDataSet getMetaDataSet() {
            return set;
        }
    }
    
    
}
