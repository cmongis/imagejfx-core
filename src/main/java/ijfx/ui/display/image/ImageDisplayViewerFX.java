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

import ijfx.core.FXUserInterface;
import net.imagej.Dataset;
import net.imagej.ui.viewer.image.AbstractImageDisplayViewer;
import net.imagej.ui.viewer.image.ImageDisplayViewer;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UserInterface;
import org.scijava.ui.viewer.DisplayPanel;

/**
 *
 * @author cyril
 */
@Plugin(type = ImageDisplayViewer.class)
public class ImageDisplayViewerFX extends AbstractImageDisplayViewer {

    DisplayPanel panel;
    
    @Override
    public void setPanel(DisplayPanel panel) {
        this.panel = panel;
        super.setPanel(panel);
    }
    
    @Override
    public DisplayPanel getPanel() {
        return this.panel;
    }
    
    
    @Override
    public boolean isCompatible(UserInterface ui) {
       return ui instanceof FXUserInterface;
    }

    @Override
    public Dataset capture() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
