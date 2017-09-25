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

import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = UiContextCalculator.class)
public class DisplayNumberCalculator extends AbstractUiContextCalculator<Object> {

    @Parameter
    DisplayService displayService;

    public static String NO_DISPLAY_OPEN = "no-display-open";

    public static String ANY_DISPLAY_OPEN = "any-display-open";

    public DisplayNumberCalculator() {
        super(Object.class);
    }

    @Override
    public void calculate(Object t) {

        int displayCount = displayService.getDisplays().size();

        toggle(ANY_DISPLAY_OPEN, displayCount > 0);
        toggle(NO_DISPLAY_OPEN, displayCount <= 0);

    }
    
    @Override
    public boolean supports(Object o) {
        return true;
    }

}
