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
package ijfx.explorer.datamodel;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import ijfx.core.metadata.MetaDataSet;
import ijfx.ui.main.ImageJFX;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Cyril MONGIS
 */
public abstract class AbstractTaggable implements Taggable{
    private MetaDataSet metadataSet;

    protected static final Logger logger = ImageJFX.getLogger();

    protected Set<Tag> tagList = new HashSet<>();
    

    @Override
    @JsonGetter("metadataset")
    public MetaDataSet getMetaDataSet() {
        if(metadataSet == null) {
            metadataSet = new MetaDataSet();
        }
        return metadataSet;
    }
    
      @Override
    public void addTag(Tag tag) {
        getTagList().add(tag);
    }

    @Override
    public void deleteTag(Tag tag) {
        getTagList().remove(tag);
    }

    
    public Set<Tag> getTagList() {
        return tagList;
    }
    
    @JsonGetter("tagList")
    protected Set<Tag> tagList() {
        return tagList;
    }
    
    @JsonSetter("tagList")
    protected void setTagList(Collection<Tag> tags) {
        tagList.clear();
        tagList.addAll(tags);
    }
    
    
    @Override
    public boolean has(Tag tag) {
        return tagList.contains(tag);
    }
    
    @JsonSetter("metadataset")
    protected void setMetaDataSet(MetaDataSet set) {
        this.metadataSet = set;
    }
}
