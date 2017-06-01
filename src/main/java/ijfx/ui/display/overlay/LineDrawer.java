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
package ijfx.ui.display.overlay;

import java.util.concurrent.Callable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.LineOverlay;
import org.scijava.plugin.Plugin;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = OverlayDrawer.class)
public class LineDrawer implements OverlayDrawer<LineOverlay> {

   

    @Override
    public boolean canHandle(Class<?> t) {
        return t == LineOverlay.class;
    }

    @Override
    public void update(OverlayViewConfiguration<LineOverlay> viewConfig, ImageDisplay display, Canvas canvas) {

        
        ImageCanvas viewport = display.getCanvas();
        LineOverlay overlay = viewConfig.getOverlay();
        
        IntCoords origin = viewport.dataToPanelCoords(new RealCoords(overlay.getLineStart(0), overlay.getLineStart(1)));
        IntCoords end = viewport.dataToPanelCoords(new RealCoords(overlay.getLineEnd(0),overlay.getLineEnd(1)));
        
        int ox = origin.x;
        int oy = origin.y;
        int dx = end.x;
        int dy = end.y;
        
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        
        graphicsContext2D.setStroke(viewConfig.getStrokeColor());
        
        graphicsContext2D.strokeLine(ox, oy, dx, dy);
      
        
    }
    
    public int getAsInt(Callable<Double> d) {
        try {
            return new Double(d.call()).intValue();
        } catch (Exception ex) {
           return 0;
        }
    }

}
