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
package ijfx.core.segmentation;

import com.fasterxml.jackson.annotation.JsonGetter;
import ijfx.core.metadata.MetaDataSet;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.ui.main.ImageJFX;
import java.util.Set;
import java.util.logging.Logger;
import org.scijava.Context;

/**
 *
 * @author cyril
 */
public abstract class AbstractTaggableWrapper<T extends Taggable> implements Explorable{
    
    final private T taggable;
    
    final protected Logger logger = ImageJFX.getLogger();
    
    @JsonGetter("taggable")
    protected T taggable() {
        return taggable;
    }
    
    public AbstractTaggableWrapper(T taggable) {
        this.taggable = taggable;
    }

    @Override
    public MetaDataSet getMetaDataSet() {
        return taggable.getMetaDataSet();
    }

    @Override
    public void addTag(Tag tag) {
        taggable.addTag(tag);
    }

    @Override
    public void deleteTag(Tag tag) {
        taggable.deleteTag(tag);
    }

    @Override
    public Set<Tag> getTagList() {
        return taggable.getTagList();
    }
    

    @Override
    public boolean has(Tag tag) {
        return taggable.has(tag);
    }

    @Override
    public void inject(Context context) {
        taggable.inject(context); 
    }
    
    
     
    
}
