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
package ijfx.ui.inputharvesting;

import javafx.scene.Node;
import org.scijava.widget.AbstractInputPanel;
import org.scijava.widget.InputWidget;

/**
 *
 * @author Cyril MONGIS
 */
public class DummyFXInputPanel extends AbstractInputPanel<Object,Object>{

    @Override
    public Class getWidgetComponentType() {
        return Node.class;
    }

    @Override
    public Object getComponent() {
        return null;
    }

    @Override
    public Class getComponentType() {
        return Node.class;
    }
    
    @Override
    public boolean supports(final InputWidget<?, ?> widget) {
        return Node.class.isAssignableFrom(widget.getComponentType());
        //return widget.getComponentType().isAssignableFrom(Node.class);
    }
    
}
