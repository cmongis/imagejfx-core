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

import ijfx.ui.display.image.ImageCanvasUtils;
import ijfx.ui.main.ImageJFX;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Platform;
import net.imagej.overlay.RectangleOverlay;
import org.scijava.input.MouseCursor;
import org.scijava.plugin.Plugin;
import org.scijava.tool.Tool;
import org.scijava.util.RealCoords;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Tool.class, iconPath = "fa:square_alt", description = "Draw a damn rectangle")
public class RectangleTool extends AbstractPathTool<RectangleOverlay> {

    
    private boolean move = false;
       
    @Override
    protected void onPath(List<RealCoords> coords) {
        
        if (coords.size() >= 3) {
            
            
            
            RealCoords first = coords.get(0);

            RealCoords last = coords.get(coords.size() - 1);

            RectangleOverlay overlay = getOverlay();
            if(overlay == null) return;
            overlay.setOrigin(first.getIntX(), 0);
            overlay.setOrigin(first.getIntY(), 1);
            overlay.setExtent(last.getIntX() - first.getIntX(), 0);
            overlay.setExtent(last.getIntY() - first.getIntY(), 1);

            Platform.runLater(getImageDisplay()::update);
            
        }

    }
    


    @Override
    protected RectangleOverlay createOverlay() {
        
       
        return new RectangleOverlay(getContext());
    }

    @Override
    public boolean isAlwaysActive() {
        return false;
    }

    @Override
    public boolean isActiveInAppFrame() {
        return true;
    }

    @Override
    public MouseCursor getCursor() {
        return MouseCursor.CROSSHAIR;
    }

    @Override
    public String getDescription() {
        return "Draw a rectangle selection";
    }

}
