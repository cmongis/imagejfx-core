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
package ijfx.commands.script;

import ijfx.core.formats.DefaultScript;
import ijfx.core.formats.Script;
import ijfx.ui.display.code.DefaultScriptDisplay;
import ijfx.ui.display.code.ScriptDisplay;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author cyril
 */
@Plugin(type = Command.class, menuPath = "File > New script...")
public class NewScript extends ContextCommand implements ScriptCommand{
    
    @Parameter(label = "Language")
    ScriptLanguage language;
    
    @Parameter(type = ItemIO.OUTPUT)
    Script script;
    
    

    @Override
    public void run() {
        
        
        script = new DefaultScript("");
        script.setLanguage(language);
        
      
        
        
    }
    
}
