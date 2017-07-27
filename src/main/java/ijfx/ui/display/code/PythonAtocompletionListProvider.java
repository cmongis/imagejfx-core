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
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.scijava.command.CommandInfo;

/**
 *
 * @author florian
 */
public class PythonAtocompletionListProvider extends DefaultAutocompletionListProvider {

    public PythonAtocompletionListProvider(List<CommandInfo> entriesList) {
        super(entriesList);
    }

    @Override
    public void computeAutocompletion(String text, String word) {
        super.computeAutocompletion(text, word);
        addImport(text, word);
    }

    public void addImport(String text, String word) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            String[] splittedLine = line.split(" ");
            if (splittedLine.length >= 2) {
                if (splittedLine[0].equals("import") && splittedLine.length >= 2) {
                    String importWord = splittedLine[1];

                    /*
                    Set newEntries = this.entries
                            .stream()
                            .filter(e -> e.contains(importWord))
                            
                            .collect(Collectors.toSet());
                    this.entries = new TreeSet<>(newEntries);
                     */
                    String[] splittedImport = importWord.split(".");
                    if (splittedImport.length >= 1) {
                        this.entries.add(splittedImport[splittedImport.length - 1]);
                    } else {
                        this.entries.add(importWord);
                    }
                    this.entries.forEach((e) -> {
                        if (splittedImport.length >= 1) {
                            e = e.replace(importWord, splittedImport[splittedImport.length - 1]);
                        }

                    });
                }
                if (splittedLine[0].equals("from")) {
                    String importWord = splittedLine[1];

                    
                    Set newEntries = this.entries
                            .stream()
                            
                            .map((String e) -> {
                                if (splittedLine.length <=4 ){
                                    e = e.replace(importWord , splittedLine[3]);
                                }
                                else if (splittedLine.length > 4 ){
                                    e = e.replace(importWord , splittedLine[5]);
                                }
                                return e;
                            })
                            .collect(Collectors.toSet());
                    this.entries = new TreeSet<>(newEntries);
                     /*
                    this.entries.add(line.split(" ")[3]);
                    this.entries.forEach((e) -> {
                        e = e.replace(importWord, "");
                        this.entries.add(e);
                    });
                    */
                }
            }
        }

    }
}
