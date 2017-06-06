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

import ijfx.ui.service.AnnotationService;
import java.util.HashSet;
import java.util.Set;
import org.scijava.plugin.Parameter;

/**
 *
 * @author sapho
 */
public class DefaultTaggable implements Taggable {
    
    @Parameter
    AnnotationService annotationServce;
    
    private Set<Tag> tagSet = new HashSet<Tag> ();

    @Override
    public void addTag(Tag tag) {
        tagSet.add(tag);
        
                }

    @Override
    public void deleteTag(Tag tag) {
        tagSet.remove(tag);
        
                }

    @Override
    public Set<Tag> getTagList() {
        return tagSet;
    }

    @Override
    public boolean has(Tag tag) {
        boolean state = false;
        for (Tag i : tagSet){
           if ( i.equals(tag)){
            state = true;
        }
        }
        return state;
    }

   

    
    
    
    
}
