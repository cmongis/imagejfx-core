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
import ijfx.explorer.datamodel.DefaultMapper;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.service.AnnotationService;
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
 * @author sapho
 */

@Plugin(type = Command.class, menuPath="Plugins > Test > Generate Dummy Mapper")
public class DummyMapper extends ContextCommand {
    
    
    @Parameter
    AnnotationService annotationService;
    
    @Parameter(type = ItemIO.OUTPUT)
    MetaDataOwnerList list;
    String newkey = "channel";
    String filterKey = "NotRandom";
    DefaultMapper mapper = new DefaultMapper(newkey, filterKey);
    Object gfp = "Gfp";
    Object ncherry = "ncherry";
    Object bright = "bright";

    @Override
    public void run() {
        
        
        
        
        list  = new MetaDataOwnerList();
        
        for(int i = 0;i!= 10;i++) {
            Random r = new Random();
            int valeurMin = 0;
            int valeurMax = 6;
            Object valeur = valeurMin + r.nextInt(valeurMax - valeurMin );
            
            mapper.associatedValues(1.0, gfp);
            mapper.associatedValues(5.0, ncherry);
            mapper.associatedValues(3.0, bright);
            
            
            
            
            MetaData name = MetaData.create(MetaData.NAME, RandomStringUtils.random(3, true, false));
            MetaData m1 = MetaData.create("NotRandom", valeur);
            MetaData mapper5 = mapper.map(m1);
            list.add(new DummyMapper.SimpleMetaDataOwnerMapper(name, mapper5, m1));
            
            
            
            
        }
        
        
        
    }
    
    
private class SimpleMetaDataOwnerMapper implements MetaDataOwner{
       
        MetaDataSet set = new MetaDataSet();

        SimpleMetaDataOwnerMapper(MetaData... initial) {
            Stream.of(initial).forEach(set::put);
        }
        
        @Override
        public MetaDataSet getMetaDataSet() {
            return set;
        }
    }
}

