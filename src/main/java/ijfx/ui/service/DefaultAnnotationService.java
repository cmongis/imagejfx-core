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
package ijfx.ui.service;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.explorer.datamodel.Taggable;
import ijfx.explorer.datamodel.Tag;
import ijfx.ui.service.Events.AddTagEvent;
import java.util.List;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;


/**
 *
 * @author sapho
 */
@Plugin(type = Service.class)
public class DefaultAnnotationService extends AbstractService implements AnnotationService {
    
    @Parameter
    EventService eventService;
    

    @Override
    public void addTag(Taggable taggable, Tag tag) {
        taggable.addTag(tag);
        eventService.publish(new AddTagEvent());
        
        
    }

    @Override
    public void removeTag(Taggable taggable, Tag tag) {
        taggable.deleteTag(tag);
        //eventService.publis(new EventMachinChose(param));
    }

    @Override
    public void addMetaData(MetaDataOwner owner, MetaData m) {
        if (m != null) {
            owner.getMetaDataSet().put(m);
            //eventService.publish(new EventMachinChose(param));
        }
        
        
    }

    @Override
    public void removeMetaData(MetaDataOwner owner, MetaData m, boolean matchValue) {
        if (matchValue) {
            if (owner.getMetaDataSet().containMetaData(m)){
                m = null;
                //eventService.publish(new EventMachinChose(param));
            }
            else {
                System.out.println("Pas de m correspondant");
            }
            
        }
        
    }

    @Override
    public void addMetaData(List<? extends MetaDataOwner> list, MetaData m) {
        if (m !=null){
            list.stream().map(c->c.getMetaDataSet().put(m));
            //eventService.publish(new EventMachinChose(param));
        }
    }

    @Override
    public void removeMetaData(List<? extends MetaDataOwner> list, MetaData m) {
        if (m!=null){
            list.stream().filter(c -> c.getMetaDataSet().containMetaData(m)).forEach((c)-> removeMetaData(c, m, true));
            //eventService.publish(new EventMachinChose(param));
        }
        
    }
    
}
