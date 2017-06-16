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

import ijfx.core.property.CoordsHelper;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.Observable;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.RectangleOverlay;
import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
@Plugin(type = OverlayModifier.class, priority = Priority.HIGH_PRIORITY)

public class RectangleModifier extends AbstractOverlayModifier<RectangleOverlay> {

    ImageCanvas viewport;
 
    List<MoveablePoint> points;

    CoordsHelper minEdgeOnCanvas = new CoordsHelper();

    CoordsHelper maxEdgeOnCanvas = new CoordsHelper();

  

    MoveablePoint a;
    MoveablePoint b;

    public RectangleModifier() {
        super(RectangleOverlay.class);
    }

    @Override
    public List<MoveablePoint> getModifiers(ImageDisplay display, RectangleOverlay overlay) {
        if (viewport == null) {
            points = new ArrayList<>();

            init(overlay, display);
            
            viewport = display.getCanvas();
            a = new MoveablePoint(viewport);
            b = new MoveablePoint(viewport);

            points.add(a);
            points.add(b);

            
           
            
            a.positionOnDataProperty().addListener(this::onMinEdgeChanged);
            b.positionOnDataProperty().addListener(this::onMaxEdgeChanged);
            
            updateFromData();
            refresh();
           
        }

        return points;
    }

    public void updateFromData() {
        
        RectangleOverlay overlay = getOverlay();
        
        RealCoords minEdge = new RealCoords(overlay.getOrigin(0), overlay.getOrigin(1));
        RealCoords maxEdge = new RealCoords(minEdge.x + overlay.getExtent(0), minEdge.y + overlay.getExtent(1));

        a.positionOnDataProperty().setValue(minEdge);
        b.positionOnDataProperty().setValue(maxEdge);

        a.positionOnCanvasProperty().setValue(viewport.dataToPanelCoords(minEdge));
        b.positionOnCanvasProperty().setValue(viewport.dataToPanelCoords(maxEdge));

    }

    public void onMinEdgeChanged(Observable obs, RealCoords oldValue, RealCoords newValue) {

        double dx, dy;
        
        RectangleOverlay overlay = getOverlay();
        
        dx = overlay.getOrigin(0) - newValue.x;
        dy = overlay.getOrigin(1) - newValue.y;
        
        overlay.setOrigin(newValue.x, 0);
        overlay.setOrigin(newValue.y, 1);
        overlay.setExtent(overlay.getExtent(0) + dx, 0);
        overlay.setExtent(overlay.getExtent(1) + dy,1);
        getDisplay().update();
    }

    public void onMaxEdgeChanged(Observable obs, RealCoords oldValue, RealCoords newValue) {

        RectangleOverlay overlay = getOverlay();
        
        overlay.setExtent(newValue.x - overlay.getOrigin(0), 0);
        overlay.setExtent(newValue.y - overlay.getOrigin(1), 1);

        getDisplay().update();

    }

    @Override
    public void refresh() {
        //updateFromData();
        points.forEach(MoveablePoint::redraw);
    }

}
