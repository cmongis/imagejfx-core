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
package ijfx.ui.display.tool;

import ijfx.core.overlay.OverlayUtilsService;
import ijfx.ui.display.overlay.OverlayDisplayService;
import java.util.List;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.OverlayService;
import net.imagej.overlay.Overlay;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.tool.Tool;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
@Plugin(type = Tool.class, iconPath = "fa:arrows", description = "Moves overlay")
public class MoveOverlayTool extends AbstractPathTool<Overlay>{

    @Parameter
    OverlayUtilsService overlayUtilsService;
    
    @Parameter
    OverlayService overlayService;
    
    @Parameter
    OverlayDisplayService overlayDrawerService;
    
    @Parameter
    ImageDisplayService imageDisplayService;
    
    @Override
    protected void onPath(List<RealCoords> coords) {
        if(coords.size() > 2) {
            
            RealCoords diff = diff(coords,coords.size()-2,coords.size()-1);
            
            getOverlay().move(new double[]{diff.x,diff.y});
            
            getImageDisplay().update();
        }
    }

    @Override
    protected Overlay createOverlay() {
        
       return overlayDrawerService.findOverlay(getImageDisplay(), getLastPositionX(), getLastPositionY());
        
        
        
    }
    
}
