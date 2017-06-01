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
package ijfx.core.formats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.scijava.Priority;
import org.scijava.io.AbstractIOPlugin;
import org.scijava.io.IOPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptService;
import org.scijava.util.FileUtils;

/**
 *
 * @author cyril
 */
@Plugin(type = IOPlugin.class,priority = Priority.HIGH_PRIORITY)
public class FXScriptIOPlugin extends AbstractIOPlugin<Script> {

    private static final List<String> FORMATS = Arrays
            .asList("py", "js", "json", "java");

    @Parameter
    ScriptService scriptService;
    
    // -- IOPlugin methods --
    @Override
    public boolean supportsOpen(final String source) {

        for (final String ext : getExtensions()) {
            if (getExtension(source).equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;

    }
    
    private String getExtension(String source) {
        return  FileUtils.getExtension(new File(source));

    }

    @Override
    public Script open(final String source) throws IOException {
        
        
        
        DefaultScript script =  new DefaultScript(new String(Files.readAllBytes(Paths.get(source))));
        script.setSourceFile(source);
        script.setLanguage(scriptService.getLanguageByExtension(getExtension(source)));
        return script;
    }

    protected List<String> getExtensions() {
        return FORMATS;
    }

    @Override
    public Class<Script> getDataType() {
        return Script.class;
    }
}
