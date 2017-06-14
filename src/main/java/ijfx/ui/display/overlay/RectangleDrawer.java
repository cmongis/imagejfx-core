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

import ijfx.ui.display.image.ImageCanvasUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.RectangleOverlay;
import org.scijava.plugin.Plugin;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = OverlayDrawer.class)
public class RectangleDrawer implements OverlayDrawer<RectangleOverlay> {

    RectangleOverlayHelper helper;

    public void update(OverlayViewConfiguration<RectangleOverlay> viewConfig, ImageDisplay display, Canvas canvas) {

        ImageCanvas viewport = display.getCanvas();
        RectangleOverlay overlay = viewConfig.getOverlay();

        GraphicsContext context2d = canvas.getGraphicsContext2D();

        double originX = overlay.getOrigin(0);
        double originY = overlay.getOrigin(1);
        
        double extentX = overlay.getExtent(0) + originX;
        double extentY = overlay.getExtent(1) + originY;
        System.out.println("Viewport width");
        System.out.println(viewport.getViewportWidth());
        
        RealCoords minEdge = ImageCanvasUtils.dataToPanelCoords(viewport,new RealCoords(originX,originY));
        RealCoords maxEdge = ImageCanvasUtils.dataToPanelCoords(viewport,new RealCoords(extentX,extentY));

        double x = minEdge.x;
        double y = minEdge.y;
        double w = maxEdge.x - minEdge.x;
        double h = maxEdge.y - minEdge.y;
        
       
        
        //viewConfig.configureContext(context2d);
        context2d.setFill(Color.YELLOW.deriveColor(1.0, 1.0, 1.0, 0.2));
        context2d.setStroke(Color.YELLOW);
        context2d.setLineWidth(1.0);
        context2d.fillRect(x, y, w, h);
        context2d.strokeRect(x, y, w, h);

    }

    public boolean canHandle(Class<?> t) {
        return t == RectangleOverlay.class;
    }
    
    
   

    @Override
    public boolean isOnOverlay(RectangleOverlay overlay, double x, double y) {
          double ox = overlay.getOrigin(0);
        double oy = overlay.getOrigin(1);
        double extentX = overlay.getExtent(0);
        double extentY = overlay.getExtent(1);
        
        return x > ox && y > oy && ox < ox + extentX && oy < oy + extentY;
    }

}
