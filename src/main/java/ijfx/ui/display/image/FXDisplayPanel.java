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
package ijfx.ui.display.image;

import javafx.scene.layout.Pane;
import org.scijava.display.Display;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.ui.viewer.DisplayPanel;
import org.scijava.ui.viewer.DisplayWindow;

/**
 *
 * @author cyril
 */
public interface FXDisplayPanel<T extends Display> extends DisplayPanel,SciJavaPlugin{
    
    public void view(DisplayWindow window, T display);
    
    public boolean canView(Display<?> display);
    
    public void pack();
    
    public Pane getUIComponent();
    
    public void display(T t);
    
}
