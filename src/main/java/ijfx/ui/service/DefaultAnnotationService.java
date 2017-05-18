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
import ijfx.ui.service.AnnotationService;
import java.util.List;
import org.scijava.service.AbstractService;


/**
 *
 * @author sapho
 */
public class DefaultAnnotationService extends AbstractService implements AnnotationService {
    
    Tag tag;

    @Override
    public void addTag(Taggable taggable) {
        taggable.addTag(tag);
        
        
    }

    @Override
    public void removeTag(Taggable taggable) {
        taggable.deleteTag(tag);
    }

    @Override
    public void addMetaData(MetaDataOwner owner, MetaData m) {
        
    }

    @Override
    public void removeMetaData(MetaDataOwner owner, MetaData m, boolean matchValue) {
    }

    @Override
    public void addMetaData(List<? extends MetaDataOwner> list, MetaData m) {
    }

    @Override
    public void removeMetaData(List<? extends MetaDataOwner> list, MetaData m) {
    }
    
}
