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

import com.google.common.collect.Lists;
import java.util.List;
import net.imagej.overlay.LineOverlay;
import org.scijava.plugin.Plugin;
import org.scijava.util.RealCoords;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = OverlayModifier.class)
public class LineModifier extends EasyOverlayModifier<LineOverlay>{

    MoveablePoint a;
    
    MoveablePoint b;
    
    public LineModifier() {
        super(LineOverlay.class);
    }

    @Override
    public List<MoveablePoint> initPoints() {
         a = new MoveablePoint(getViewport());
         b = new MoveablePoint(getViewport());
         
         return Lists.newArrayList(a,b);
         
    }

    @Override
    public void updateData() {

        LineOverlay lineOverlay = getOverlay();
        
        lineOverlay.setLineStart(toArray(a.positionOnDataProperty().getValue()));
        lineOverlay.setLineEnd(toArray(b.positionOnDataProperty().getValue()));
        
        
        fireOverlayChange();
    }

    @Override
    public void updateFromData() {
        LineOverlay lineOverlay = getOverlay();
        double xStart = lineOverlay.getLineStart(0);
        double yStart = lineOverlay.getLineStart(1);
        double xEnd = lineOverlay.getLineEnd(0);
        double yEnd = lineOverlay.getLineEnd(1);
        
        
        
        updateFromData(a,xStart,yStart);
        updateFromData(b,xEnd,yEnd);
        
    }
    
    private double[] toArray(RealCoords realCoords) {
        return new double[] {realCoords.x,realCoords.y};
    }
    
     
}
