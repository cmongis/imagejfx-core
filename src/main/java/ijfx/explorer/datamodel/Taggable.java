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

import ijfx.core.metadata.MetaDataOwner;
import ijfx.ui.main.ImageJFX;
import java.util.Set;
import org.scijava.Context;

/**
 *
 * @author Cyril MONGIS
 */
public interface Taggable extends MetaDataOwner{

    void addTag(Tag tag);

    void deleteTag(Tag tag);

    Set<Tag> getTagList();
    
    boolean has(Tag tag);
  
     /**
     * Utility method mainly used when loaded Explorables from JSON
     * @param context 
     */
    public default void inject(Context context) {
        injectSafe(this,context);
    }
    
    public static void injectSafe(Object taggable, Context context) {
        try {
            context.inject(taggable);
        }catch(Exception e) {
            ImageJFX.getLogger().warning("Context already injected in "+taggable);
        }
    }
    
}
