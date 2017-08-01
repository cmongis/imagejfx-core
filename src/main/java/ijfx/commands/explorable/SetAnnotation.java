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
package ijfx.commands.explorable;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataSet;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.display.annotation.AnnotationDialog;
import ijfx.ui.display.annotation.DefaultAnnotationDialog;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import mongis.utils.FXUtilities;
import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */

@Plugin(type = ExplorableDisplayCommand.class, label="Set to annotation...",iconPath="fa:edit")
public class SetAnnotation extends AbstractExplorableDisplayCommand{
    
   
   private AnnotationDialog<Mapper> annot ;
   
   private Mapper mapper;

    @Override
    public void run(List<? extends Explorable> items) {
        
        annot = FXUtilities.runAndWait(DefaultAnnotationDialog::new);
        
        List <MetaDataSet> setList = items
                .stream()
                .map(n -> n.getMetaDataSet())
                .collect(Collectors.toList());
        
        
        annot.fillComboBox(items, setList);
        
        this.mapper = FXUtilities.runAndWait(annot::showAndWait).orElse(null);
        
        if(mapper == null) {
           cancel("The user canceled");
           return;
        }
        
        items
               .stream()
               .forEach(this::setAnnotation);
        
         display.update();
    }
    
    
    public void setAnnotation(Explorable exp) {
        MetaDataSet set = exp.getMetaDataSet();
        for(Map.Entry<String, MetaData> entry : set.entrySet()) {
            
            if(entry.getValue().getName().equals(mapper.getOldKey())) {
                
                MetaData truc = mapper.map(entry.getValue());
                set.put(truc);
            }
           
        
        }
        
    }


    
    
}
