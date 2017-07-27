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
package ijfx.ui.display.code;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.scijava.command.CommandInfo;

/**
 *
 * @author florian
 */
public class DefaultAutocompletionListProvider implements AutocompletionList {

    SortedSet<String> entries;

    public DefaultAutocompletionListProvider(SortedSet<String> entries) {
        this.entries = entries;
    }

    public DefaultAutocompletionListProvider(List<CommandInfo> entriesList) {
        this.entries = new TreeSet<>();
        entriesList
                .stream()
                .forEach((command) -> {
                    this.entries.add(command.getClassName());
                });
    }

    public DefaultAutocompletionListProvider() {
        this.entries = new TreeSet<>();
    }
    
    @Override
    public void computeAutocompletion(String text, String word){
        
    }
    
    @Override
    public SortedSet<String> getEntries() {
        return entries;
    }

    @Override
    public void setEntries(SortedSet<String> entries) {
        this.entries = entries;
    }

}
