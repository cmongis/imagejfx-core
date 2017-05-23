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

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import net.imagej.display.ImageCanvas;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
public class ImageCanvasViewPortWrapper implements ViewPort {

    final ImageCanvas canvas;

    public ImageCanvasViewPortWrapper(ImageCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public double getProjectedWidth() {
        return canvas.getViewportWidth() * canvas.getZoomFactor();
    }

    @Override
    public double getProjectedHeight() {
        return canvas.getViewportHeight() * canvas.getZoomFactor();
    }

    @Override
    public Point2D getPositionOnCamera(Point2D point) {

        RealCoords coords = point2DToRealCoords(point);

        IntCoords panCoords = canvas.dataToPanelCoords(coords);

        return intCoordsToPoint2D(panCoords);
    }

    @Override
    public void localizeOnCamera(double[] position) {
        if (position.length < 2) {
            throw new IllegalArgumentException("The position should at least contain 2 coordinates");
        }
        canvas.setPanCenter(new RealCoords(position[0], position[1]));
    }

    @Override
    public Point2D getPositionOnImage(Point2D point) {
      IntCoords coords =  point2DToIntCoords(point);
      
      RealCoords dataCoords = canvas.panelToDataCoords(coords);
      
      return realCoordsToPoint2D(dataCoords);
      
    }

    @Override
    public Rectangle2D getSeenRectangle() {
        
        final double x,y,w,h;
        
        IntCoords panOffset = canvas.getPanOffset();
        x = panOffset.x;
        y = panOffset.y;
        w = canvas.getViewportWidth() * canvas.getZoomFactor();
        h = canvas.getViewportHeight() * canvas.getZoomFactor();
        
        return new Rectangle2D(x, y, w, h);
        
    }

    @Override
    public double getImageWidth() {
        return canvas.getDisplay().dimension(0);
    }

    @Override
    public double getImageHeight() {
        return canvas.getDisplay().dimension(1);
    }

    @Override
    public double getViewPortWidth() {
        return canvas.getViewportWidth();
    }

    @Override
    public double getViewPortHeight() {
        return canvas.getViewportHeight();
    }

    @Override
    public double getZoom() {
        return canvas.getZoomFactor();
    }

    @Override
    public void setZoom(double zoom) {
        canvas.setZoom(zoom);
    }

    private RealCoords point2DToRealCoords(Point2D point) {
        return new RealCoords(point.getX(), point.getY());
    }

    private Point2D realCoordsToPoint2D(RealCoords coords) {
        return new Point2D(coords.x, coords.y);
    }

    private IntCoords point2DToIntCoords(Point2D point) {
        return new IntCoords(toInt(point.getX()), toInt(point.getY()));
    }

    private Point2D intCoordsToPoint2D(IntCoords coords) {
        return new Point2D(coords.x, coords.y);
    }

    private int toInt(double d) {
        return new Double(d).intValue();
    }

}
