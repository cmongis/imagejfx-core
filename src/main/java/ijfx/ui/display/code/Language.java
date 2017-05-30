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

import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 */
public enum Language {
    JAVASCRIPT ("Javascript","/ijfx/ui/display/code/javascript.nanorc"),
    JAVA ("Java","/ijfx/ui/display/code/java.nanorc"),
    PYTHON ("Python","/ijfx/ui/display/code/python.nanorc");
    
    private String name = "";
    private String path = "";

    private Language(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    public String getPath (){
        return path;
    }

    public String getName() {
        return name;
    }
    
    
    public static String findFile(ScriptLanguage language) {
       return String.format("/ijfx/ui/display/code/%s.nanorc",language.getLanguageName().toLowerCase().replace(" ", ""));
    }
    
    
}
