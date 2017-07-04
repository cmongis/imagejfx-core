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

    /**
     * Returns a int array that contains the [x,y,width,height] of the viewport
     * as data coordinates.
     *
     * @param canvas
     * @return
     */
    public static int[] getSeenRectangle(ImageCanvas canvas) {

        IntCoords min = getUpperLeftCornerOnData(canvas);
        IntCoords max = getBottomRightCornerOnData(canvas);

        return new int[]{min.x, min.y, max.x - min.x, max.y - min.y};

    }

    public static IntCoords getUpperLeftCornerOnData(ImageCanvas viewport) {

        return toInt(topLeftOnData(viewport));

    }

    public static IntCoords getBottomRightCornerOnData(ImageCanvas viewport) {
        return toInt(viewport
                .panelToDataCoords(
                        new IntCoords(viewport.getViewportWidth() - 1, viewport.getViewportHeight() - 1)));
    }

    public static IntCoords toInt(RealCoords realCoords) {
        return new IntCoords(toInt(realCoords.x), toInt(realCoords.y));
    }

    private static int toInt(double d) {
        return new Double(d).intValue();
    }

    /**
     * Returns the x/y coordinates of the panel on the data space
     */
    public static RealCoords topLeftOnData(ImageCanvas viewport) {
        final RealCoords center = viewport.getPanCenter();
        final double zoomFactor = viewport.getZoomFactor();
        final double sx = center.x - (viewport.getViewportWidth() / 2 / zoomFactor);
        final double sy = center.y - (viewport.getViewportHeight() / 2 / zoomFactor);
        return new RealCoords(sx, sy);
    }

    /**
     * Returns the x/y coordinates of the left bottom corner of the panel in data space
     * @param viewport
     * @return 
     */
    public static RealCoords bottomRightOnData(ImageCanvas viewport) {

        RealCoords topLeftOnData = topLeftOnData(viewport);
        final double zoomFactor = viewport.getZoomFactor();
        final double sx = topLeftOnData.x;
        final double sy = topLeftOnData.y;
        final double sw = 1.0 * viewport.getViewportWidth() / zoomFactor;
        final double sh = 1.0 * viewport.getViewportHeight() / zoomFactor;
        
        return new RealCoords(sx+sw,sy+sh);
    }
    
    public static boolean contains(RealCoords point, RealCoords topLeft, RealCoords bottomRight) {
        return contains(point.x,point.y,topLeft,bottomRight);
    }
    
    public static boolean contains (double x, double y, RealCoords topLeft, RealCoords bottomRight) {
        return x >= topLeft.x && x <= bottomRight.x && y >= topLeft.y && y <= bottomRight.y;
    }
    
    public static boolean contains(ImageCanvas imageCanvas,double x, double y) {
        
        return contains(x, y, topLeftOnData(imageCanvas), bottomRightOnData(imageCanvas));
        
    }

    public static RealCoords dataToPanelCoords(ImageCanvas canvas, RealCoords dataCoords) {

        final double viewportImageWidth = canvas.getViewportWidth() / canvas.getZoomFactor();
        double leftImageX = canvas.getPanCenter().x - viewportImageWidth / 2d;

        final double viewportImageHeight = canvas.getViewportHeight() / canvas.getZoomFactor();
        double topImageY = canvas.getPanCenter().y - viewportImageHeight / 2d;

        final double panelX
                = canvas.getZoomFactor() * (dataCoords.x - leftImageX);
        final double panelY
                = canvas.getZoomFactor() * (dataCoords.y - topImageY);
        return new RealCoords(panelX, panelY);
    }

    public static void checkPosition(ImageCanvas viewport) {

        RealCoords topLeftCorner = topLeftOnData(viewport);
        
        double dx, dy;

        dx = topLeftCorner.x < 0 ? -topLeftCorner.x : 0;
        dy = topLeftCorner.y < 0 ? -topLeftCorner.y : 0;

        dx /= viewport.getZoomFactor();
        dy /= viewport.getZoomFactor();

        if (dx != 0 || dy != 0) {
            viewport.pan(new RealCoords(dx, dy));
        }

        topLeftCorner = topLeftOnData(viewport);
        
        
        
        RealCoords bottomRightCorner = bottomRightOnData(viewport);

        // calculating the difference bettween the viewport edge on the image
        dx = viewport.getDisplay().dimension(0) - bottomRightCorner.x;
        dy = viewport.getDisplay().dimension(1) - bottomRightCorner.y;
        System.out.println("offset : " + topLeftCorner.x);
        System.out.println("dx : " + dx);

        dx = dx < 0 ? dx : 0;
        dy = dy < 0 ? dy : 0;

        dx /= viewport.getZoomFactor();
        dy /= viewport.getZoomFactor();

        if (dx < 0 || dy < 0) {
            viewport.pan(new RealCoords(dx, dy));
        }

    }

}
