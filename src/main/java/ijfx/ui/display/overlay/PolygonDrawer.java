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

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.PolygonOverlay;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.PolygonRegionOfInterest;
import org.scijava.plugin.Plugin;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = OverlayDrawer.class)
public class PolygonDrawer implements OverlayDrawer<PolygonOverlay> {

    Polygon shape;

    /**
     * @Override public Node update(PolygonOverlay overlay, ViewPort viewport) {
     *
     * if (shape == null) {
     *
     * shape = new Polygon();
     *
     * }
     * shape.getPoints().clear();
     *
     * PolygonRegionOfInterest roi = overlay.getRegionOfInterest();
     *
     * Double[] points = new Double[roi.getVertexCount() * 2]; for (int i = 0; i
     * != roi.getVertexCount(); i++) {
     *
     * Point2D positionOnViewPort = new
     * Point2D(roi.getVertex(i).getDoublePosition(0),roi.getVertex(i).getDoublePosition(1));
     * positionOnViewPort = viewport.getPositionOnCamera(positionOnViewPort);
     * points[i * 2] = positionOnViewPort.getX(); points[i * 2 + 1] =
     * positionOnViewPort.getY();
     *
     * }
     *
     * shape.getPoints().addAll(points);
     *
     * OverlayDrawer.color(overlay, shape); return shape;
     *
     * }\
     */
    
    @Override
    public void update(OverlayViewConfiguration<PolygonOverlay> viewConfig, ImageDisplay display, Canvas canvas) {

        PolygonOverlay overlay = viewConfig.getOverlay();
        
        ImageCanvas viewport = display.getCanvas();
        
        GraphicsContext context = canvas.getGraphicsContext2D();
        PolygonRegionOfInterest roi = overlay.getRegionOfInterest();
        double[] xs = new double[roi.getVertexCount()];
        double[] ys = new double[roi.getVertexCount()];
        double[] position = new double[2];
        for (int i = 0; i != roi.getVertexCount(); i++) {
            RealLocalizable vertex = roi.getVertex(i);
            vertex.localize(position);
            IntCoords panelCoords = viewport.dataToPanelCoords(new RealCoords(position[0], position[1]));
            xs[i] = panelCoords.x;
            ys[i] = panelCoords.y;
        }
        context.setFill(viewConfig.getFillCollor());
        context.setLineWidth(viewConfig.getStrokeWidth());
        context.fillPolygon(xs, ys, xs.length);
        context.strokePolygon(xs, ys, xs.length);
        //context.setFont(new Font("Arial", 15));
        //context.fillText("heloo", xs[0], ys[0]);
    }

    public boolean canHandle(Class<?> t) {
        return t == PolygonOverlay.class;
    }

}
