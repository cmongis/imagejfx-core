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

import ijfx.core.datamodel.DatasetHolder;
import ijfx.core.datamodel.Iconazable;
import ijfx.core.metadata.MetaDataOwner;


/**
 *
 * @author Cyril MONGIS, 2016
 */
public interface Explorable extends Iconazable, MetaDataOwner, DatasetHolder, Taggable {

    /**
     * Hash representing the data state of the object
     *
     * @return the state of the data
     */
    default int dataHashCode() {

        return getTitle().hashCode()
                + getSubtitle().hashCode()
                + getMetaDataSet()
                        .values()
                        .stream()
                        .parallel()
                        .mapToInt(m -> m.hashCode())
                        .sum()
                + getTagList()
                        .stream()
                .parallel()
                .mapToInt(s->s.hashCode())
                .sum();

    }
    
   

}
