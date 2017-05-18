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

import ijfx.core.IjfxService;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import java.util.List;

/**
 *
 * @author cyril
 */
public interface AnnotationService extends IjfxService{
    
    
    /**
     * Adds a tag to a taggable object
     * @param taggable 
     */
    public void addTag(Taggable taggable, Tag tag);
    
    
    /**
     * remove a tag from a taggable object
     * @param taggable 
     */
    public void removeTag(Taggable taggable, Tag tag);
    
    /**
     * Add a metadata to a MetaDataOwner
     * @param owner
     * @param m MetaData object
     */
    public void addMetaData(MetaDataOwner owner, MetaData m);
    
    
    /**
     * This method removes a metadata from a MetaDataOwner
     * If matchValue is true, the removal happens only if the 
     * input MetaDataOwner has a metadata that matches the key
     * and the value of the requested metadata
     * @param owner
     * @param m
     * @param matchValue 
     */
    public void removeMetaData(MetaDataOwner owner, MetaData m, boolean matchValue);
    
    public void addMetaData(List<? extends MetaDataOwner> list, MetaData m);
    
    public void removeMetaData(List<? extends MetaDataOwner> list, MetaData m);
    
    
    
}
