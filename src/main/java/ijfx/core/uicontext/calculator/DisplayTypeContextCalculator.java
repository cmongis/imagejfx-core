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
package ijfx.core.uicontext.calculator;

import ijfx.explorer.ExplorableDisplay;
import ijfx.ui.display.code.ScriptDisplay;
import net.imagej.display.ImageDisplay;
import org.scijava.display.Display;
import org.scijava.display.TextDisplay;
import org.scijava.plugin.Plugin;
import org.scijava.table.TableDisplay;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = UiContextCalculator.class)
public class DisplayTypeContextCalculator extends AbstractUiContextCalculator<Display>{

    
    Class<?>[] displayList = new Class<?>[] {
        ImageDisplay.class
        ,TextDisplay.class
        ,ScriptDisplay.class  
        ,TableDisplay.class
        ,ExplorableDisplay.class
    };

    public DisplayTypeContextCalculator() {
        super(Display.class);
    }
    
    
    @Override
    public void calculate(Display t) {

        
        toggle("any-display-open",t != null);
        
        for(Class c : displayList) {
            String contextName = getUiContxt(c);
            boolean tog =  t != null && c.isAssignableFrom(t.getClass());
            toggle(contextName,tog);
        }

    }
    
    private String getUiContxt(Class<? extends Display> displayType) {
        
        String displayName = displayType.getSimpleName();
        
        
        return new StringBuilder(30)
                .append(displayName.substring(0, displayName.indexOf("Display")).toLowerCase())
                .append("-open")
                .toString();
        
    }
    
    
    
    
    
}
