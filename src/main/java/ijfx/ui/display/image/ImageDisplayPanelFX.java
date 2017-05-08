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

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import net.imagej.display.ImageDisplay;
import net.imagej.ui.viewer.image.ImageDisplayPanel;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;
import org.scijava.ui.viewer.DisplayWindow;

/**
 *
 * @author cyril
 */
@Plugin(type = FXDisplayPanel.class)
public class ImageDisplayPanelFX extends BorderPane implements ImageDisplayPanel,FXDisplayPanel<ImageDisplay>{

    private DisplayWindow window;
    private ImageDisplay display;

    
    public ImageDisplayPanelFX() {
        
    }
    
    
    public void view(DisplayWindow window, ImageDisplay display) {
        this.window = window;
        this.display = display;
    }
    
    
    
    @Override
    public ImageDisplay getDisplay() {
        return display;
    }

    @Override
    public DisplayWindow getWindow() {
        return window;
    }

    @Override
    public void redoLayout() {
    }

    @Override
    public void setLabel(String s) {
    }

    @Override
    public void redraw() {
    }

    @Override
    public boolean canView(Display display) {
       return display instanceof ImageDisplay;
    }

    @Override
    public void pack() {
    }

    @Override
    public Pane getUIComponent() {
        return this;
    }

    @Override
    public void display(ImageDisplay t) {
        setCenter(new Label(t.getName()));
    }

 
    
}
