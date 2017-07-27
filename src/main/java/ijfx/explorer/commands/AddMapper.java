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
package ijfx.explorer.commands;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.metadata.MetaDataSet;
import ijfx.explorer.ExplorableDisplay;
import ijfx.explorer.datamodel.DefaultMapper;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.service.AnnotationService;
import java.util.stream.Stream;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */

@Plugin(type = Command.class,menuPath = "Plugins > Explorer > Add Mapper")
public class AddMapper extends ContextCommand{
    
    @Parameter
    AnnotationService annotationService;
            
    @Parameter(type = ItemIO.BOTH)
    ExplorableDisplay display;
    
    @Parameter(label = "Key")
    String key;
    
    @Parameter(label = "NewKey")
    String newKey;
    
    @Parameter (label = "BasisValue")
    String basisValue;
    
    @Parameter (label = "NewValue")
    String newValue;

    @Override
    public void run() {
        
            
            DefaultMapper mapper = new DefaultMapper(newKey, key);
            mapper.associatedValues(basisValue, newValue);
                        
            for (MetaDataOwner e : display.getSelected()){
                MetaDataSet f = e.getMetaDataSet();
                MetaData d = mapper.map(f.get(key));
                f.put(newKey, d);
            }

              
        }
    
    }
    
    
    
    
    

