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

import ijfx.commands.explorable.AbstractExplorableDisplayCommand;
import ijfx.commands.explorable.ExplorableDisplayCommand;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataSet;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.display.annotation.AnnotationDialog;
import ijfx.ui.display.annotation.DefaultAnnotationDialog;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UserInterface;

/**
 *
 * @author sapho
 */

@Plugin(type = ExplorableDisplayCommand.class, label="Set to annotation...",iconPath="fa:edit",initializer = "init")
public class SetAnnotation extends AbstractExplorableDisplayCommand{
    
   
   AnnotationDialog annot = new DefaultAnnotationDialog();
    
  

    @Override
    public void run(List<? extends Explorable> items) {
        System.out.println("WOUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        
        items.stream().forEach(this::setAnnotation);
        display.update();
    }
    
    public void setAnnotation(Explorable exp) {
        
        Mapper finalMapper = annot.mapperAction();
        MetaDataSet set = exp.getMetaDataSet();
        MetaData n = finalMapper.map(set.get(finalMapper.getOldKey()));
        set.put(n);
        
        
    }


    
    
}
