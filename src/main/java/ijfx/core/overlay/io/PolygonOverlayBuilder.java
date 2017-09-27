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
package ijfx.core.overlay.io;

import net.imagej.overlay.PolygonOverlay;
import net.imglib2.RealPoint;
import org.scijava.Context;

/**
 *
 * @author cyril
 */
public class PolygonOverlayBuilder {
    
    private PolygonOverlay overlay;

    private int i = 0;
    
    public PolygonOverlayBuilder(Context context) {
        
        overlay = new PolygonOverlay(context);
    }
    
    public PolygonOverlayBuilder addVertex(int x, int y) {
        overlay.getRegionOfInterest().addVertex(i++,new RealPoint(x,y) );
        return this;
    }
    
    
    public PolygonOverlay build() {
        return overlay;
    }
    
    
    
}
