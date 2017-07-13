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

import java.util.Hashtable;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 * 
 * This interface describe how the keywords of language should be retrieved
 * By default a nanorc file is parsed
 * The structure of the Hashtable should be : 
 *  "color" : regular expression pattern
 *  ex of pattern : "\\b(KEYWORD|KEYWORD2|...)\\b"
 */
public interface LanguageKeywords {
    
    
    public Hashtable getKeywords();
    public void setLanguage(ScriptLanguage language);
    public void run();
    
}
