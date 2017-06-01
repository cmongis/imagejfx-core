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

import ijfx.core.uicontext.UiContextService;
import org.scijava.plugin.AbstractTypedPlugin;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public abstract class AbstractUiContextCalculator<T> extends AbstractTypedPlugin<T> implements UiContextCalculator<T>{

    Class<T> type;

    @Parameter
    UiContextService uiContextService;
    
    public AbstractUiContextCalculator(Class<T> type) {
        this.type = type;
    }
   
    
    @Override
    public Class<T> getType() {
        return type;
    }
    @Override
    public boolean supports(Object t) {
        return type.isAssignableFrom(t.getClass());
    }

    protected void toggle(String context, boolean toggle) {
        uiContextService.toggleContext(context, toggle);
    }
    
}

