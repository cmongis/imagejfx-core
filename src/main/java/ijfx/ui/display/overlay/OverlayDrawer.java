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

import com.sun.prism.image.ViewPort;
import ijfx.ui.display.image.ImageCanvasUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.Overlay;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.util.ColorRGB;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 * The OverlayDrawer takes an Overlay and a ViewPort and returns a Node object
 * depending on the Overlay condition.
 *
 *
 * @author Cyril MONGIS, 2016
 */
public interface OverlayDrawer<T extends Overlay> extends SciJavaPlugin {

    // returns a node updated according to the overlay parameter
    // with size and position depending on the viewport
    public void update(OverlayViewConfiguration<T> overlay, ImageDisplay viewport, Canvas canvas);

    public boolean canHandle(Class<?> o);

    public static Color toFxColor(ColorRGB color) {
        return toFxColor(color, 1.0);
    }

    public static Color toFxColor(ColorRGB colorRGB, double f) {
        double red = 1.0 * colorRGB.getRed() / 255;
        double green = 1.0 * colorRGB.getGreen() / 255;
        double blue = 1.0 * colorRGB.getBlue() / 255;
        double alpha = colorRGB.getAlpha() / 255 * f;
        //return new fillColor
        return new Color(red, green, blue, alpha);
    }

    public default boolean isClickOnOverlay(T overlay, ImageCanvas viewport, double xOnCanvas, double yOnCanvas) {
        RealCoords dataCoords = viewport.panelToDataCoords(new IntCoords(toInt(xOnCanvas), toInt(yOnCanvas)));
        return isOnOverlay(overlay, dataCoords.x, dataCoords.y);
    }

    public default boolean isOnOverlay(T overlay, double xOnImage, double yOnImage) {
        double x1 = overlay.getRegionOfInterest().realMin(0);
        double y1 = overlay.getRegionOfInterest().realMin(1);
        double x2 = overlay.getRegionOfInterest().realMax(0);
        double y2 = overlay.getRegionOfInterest().realMax(1);

        
        
        return contains(new double[]{x1,y1,x2,y2}, xOnImage, yOnImage);
        
    }

    public static double[] getOverlayBounds(Overlay overlay) {
        double x1 = overlay.getRegionOfInterest().realMin(0);
        double y1 = overlay.getRegionOfInterest().realMin(1);
        double x2 = overlay.getRegionOfInterest().realMax(0);
        double y2 = overlay.getRegionOfInterest().realMax(1);

        //System.out.println(String.format("(%.0f,%.0f), (%.0f,%.0f)", x1, y1, x2, y2));
       return new double[]{x1, y1, x2 - x1, y2 - y1};
      
    }

    public default boolean isOverlayOnViewPort(Overlay overlay, ImageCanvas viewport) {
        
        double[] bounds = getOverlayBounds(overlay);
        int[] seenRectangle = ImageCanvasUtils.getSeenRectangle(viewport);
        
        return contains(seenRectangle, bounds[0], bounds[1]) && contains(seenRectangle, bounds[2], bounds[3]);
    }
    
  
    
    public static boolean contains(int[] rectangle, double x, double y) {
        if(rectangle.length < 4) {
            throw new IllegalArgumentException("The input array must contain at least 4 elements !");
        }
        
        double minX = rectangle[0];
        double minY = rectangle[1];
        double maxX = minX + rectangle[2];
        double maxY = minY + rectangle[3];
        
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
    
    public static boolean contains(double[] rectangle,double x, double y) {
        if(rectangle.length < 4) {
            throw new IllegalArgumentException("The input array must contain at least 4 elements !");
        }
        
        double minX = rectangle[0];
        double minY = rectangle[1];
        double maxX = minX + rectangle[2];
        double maxY = minY + rectangle[3];
        
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
        
    }
    

    public static void color(Overlay overlay, Shape shape) {
        ColorRGB fillColor = overlay.getFillColor();
        Color fxFillColor = toFxColor(fillColor, 0).deriveColor(1.0, 0, 0, 0.0);
        shape.setFill(fxFillColor);
        shape.setStroke(toFxColor(overlay.getLineColor(), 1.0));

        shape.setStrokeWidth(overlay.getLineWidth());
        shape.setOpacity(1.0);
    }

    public static int toInt(double d) {
        return new Double(d).intValue();
    }
}
