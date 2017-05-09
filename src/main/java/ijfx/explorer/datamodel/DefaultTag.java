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

/**
 *
 * @author cyril
 */
public class DefaultTag implements Tag {

    final String name;

    public DefaultTag(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Tag o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Tag) {
            return ((Tag) object).getName().equals(getName());
        }

        if (object instanceof String) {
            ((String) object).equals(getName());
        }

        return false;
    }

}
