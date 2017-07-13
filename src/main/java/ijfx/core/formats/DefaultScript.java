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

import org.scijava.script.ScriptLanguage;

/**
 *
 * @author cyril
 */
public class DefaultScript implements Script{

    protected String code;

    protected ScriptLanguage language;
    
    protected String sourceFile;
    
    public DefaultScript(String code) {
        this.code = code;
        
       
    }
    
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSourceFile() {
        return sourceFile;
    }
    
    
    
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public ScriptLanguage getLanguage() {
       return language;
    }

    @Override
    public void setLanguage(ScriptLanguage language) {
        this.language = language;
    }
    
}
