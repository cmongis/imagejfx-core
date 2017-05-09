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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author cyril
 */
public interface Tag extends Comparable<Tag>{
    
    String getName();
    
    
    public static Tag create(String tagStr) {
       
        return new DefaultTag(tagStr);
    }
    
    public static final String SEPARATOR = "%!%";
    
    public static List<Tag> stringToList(String tagListAsString) {
        return Stream
                .of(tagListAsString.split(SEPARATOR))
                .map(Tag::create)
                .collect(Collectors.toList());
    }
    
    public static String tagsToString(List<Tag> tags) {
        return tags
                .stream()
                .map(Tag::getName)
                .collect(Collectors.joining(SEPARATOR));
    }
    
    
}
