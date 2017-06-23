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
import java.util.List;
import javafx.application.Platform;
import net.imglib2.Point;
import net.imglib2.RealLocalizable;
import org.scijava.display.event.input.MsDraggedEvent;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.display.event.input.MsReleasedEvent;
import org.scijava.input.MouseCursor;
import org.scijava.plugin.Plugin;
import org.scijava.tool.Tool;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
@Plugin(type = Tool.class, description = "Move the image around", iconPath = "fa:hand_paper_alt")
public class HandTool extends ReactiveTool {

    @Override
    void onStart() {
        stream(MsDraggedEvent.class)
                .map(this::positionOnCanvas)
                .buffer(2)
                .subscribe(this::onPath);

    }

    private void onPath(List<Point> twoLast) {
        if (twoLast.size() != 2) {
            return;
        }
        RealLocalizable p1 = twoLast.get(0);
        RealLocalizable p2 = twoLast.get(1);
        double dx = p2.getDoublePosition(0) - p1.getDoublePosition(0);
        double dy = p2.getDoublePosition(1) - p1.getDoublePosition(1);
        double x = getCanvas().getPanCenter().getIntX() - dx;
        double y = getCanvas().getPanCenter().getIntY() - dy;

        getCanvas().pan(new RealCoords(-dx, -dy));
        ImageCanvasUtils.checkPosition(getCanvas());
        //System.out.println("updating");
        Platform.runLater(getImageDisplay()::update);
    }

    @Override
    public void onMouseUp(MsReleasedEvent event) {
        stopStream();
    }

    @Override
    public void onMouseDown(MsPressedEvent event) {
        startStream();
    }

    @Override
    public boolean isAlwaysActive() {
        return false;
    }

    @Override
    public boolean isActiveInAppFrame() {
        return false;
    }

    @Override
    public MouseCursor getCursor() {
        return MouseCursor.HAND;
    }

    @Override
    public String getDescription() {
        return "Damn";
    }

}
