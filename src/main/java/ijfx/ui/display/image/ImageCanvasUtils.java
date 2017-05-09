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

import net.imagej.display.ImageCanvas;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
public class ImageCanvasUtils {
    
    public static void checkPosition(ImageCanvas viewport) {
            
        IntCoords offset = viewport.getPanOffset();
        
        double dx,dy;
        
        
        dx = offset.x < 0 ? -offset.x : 0;
        dy = offset.y < 0 ? -offset.y : 0;
        
        if(dx != 0 || dy != 0)
        viewport.pan(new RealCoords(dx,dy));
        
        offset = viewport.getPanOffset();
        
        IntCoords rightBottomOnPane = new IntCoords(offset.x + viewport.getViewportWidth(), offset.y+viewport.getViewportHeight());
        
        RealCoords rightBottomOnImage = viewport.panelToDataCoords(rightBottomOnPane);
        
        // calculating the difference bettween the viewport edge on the image
        dx = viewport.getDisplay().dimension(0) - rightBottomOnImage.x;
        dy = viewport.getDisplay().dimension(1) - rightBottomOnImage.y;
        System.out.println("offset : "+offset.x);
        System.out.println("dx : "+ dx);
        
        dx = dx < 0 ? dx : 0;
        dy = dy < 0 ? dy : 0;
       
        if(dx <0 || dy < 0)
        viewport.pan(new RealCoords(dx,dy));
        
        
        
        
    }
    
   

    
}
