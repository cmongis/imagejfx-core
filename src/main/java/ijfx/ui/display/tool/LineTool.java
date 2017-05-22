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

import ijfx.core.utils.SciJavaUtils;
import java.util.List;
import net.imagej.overlay.LineOverlay;
import org.scijava.input.MouseCursor;
import org.scijava.plugin.Plugin;
import org.scijava.tool.Tool;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
@Plugin(type = Tool.class, iconPath="fa:arrows_v",description = "Line tool")
public class LineTool extends AbstractPathTool<LineOverlay>{

    @Override
    protected void onPath(List<RealCoords> coords) {
        
        if(coords.size() < 2) return;
        
        RealCoords begin = coords.get(0);
        RealCoords end = coords.get(coords.size()-1);

        LineOverlay overlay = getOverlay();
        
        overlay.setLineStart(new double[] { begin.x, begin.y});
        overlay.setLineEnd(new double[] {end.x, end.y});
        
        getImageDisplay().update();
    }

    @Override
    protected LineOverlay createOverlay() {
        return new LineOverlay(getContext());
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
        return SciJavaUtils.getDescription(this);
    }
    
}
