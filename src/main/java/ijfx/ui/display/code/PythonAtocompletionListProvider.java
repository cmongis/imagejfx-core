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

/**
 *
 * @author florian
 */
public class PythonAtocompletionListProvider extends DefaultAutocompletionListProvider{
    
    
    public void addImport(String text, String word){
        String[] lines = text.split("\t");
        for (String line : lines){
            if (line.startsWith("import")){
                String importWord = line.split(" ")[1];
                for (String entry : this.entries){
                    if (entry.contains(importWord)){
                        this.entries.remove(entry);
                    }
                }
                this.entries.add(importWord.split(".")[-1]);
            }
             if (line.startsWith("from")){
                String importWord = line.split(" ")[1];
                for (String entry : this.entries){
                    if (entry.contains(importWord)){
                        this.entries.remove(entry);
                    }
                }
                this.entries.add(line.split(" ")[3]);
            }
        }
            
    }
}
