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

import ijfx.core.formats.Script;
import org.scijava.Priority;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptLanguage;

/**
 * TODO : change to DefaultScriptDisplay
 * @author florian
 */
@Plugin(type = Display.class, priority = Priority.HIGH_PRIORITY)
public class DefaultScriptDisplay extends AbstractDisplay<Script> implements ScriptDisplay {

    public DefaultScriptDisplay() {
        super(Script.class);
    }

    private Script getScript() {
        return get(0);
    }

    @Override
    public String getIdentifier() {
        return getScript().getSourceFile();
    }

    @Override
    public ScriptLanguage getLanguage() {
        return get(0).getLanguage();
    }

    @Override
    public void setLanguage(ScriptLanguage language) {
        get(0).setLanguage(language);
    }
}
