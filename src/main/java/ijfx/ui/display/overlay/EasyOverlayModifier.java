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

import java.util.List;
import javafx.application.Platform;
import javafx.beans.Observable;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.Overlay;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
public abstract class EasyOverlayModifier<T extends Overlay> extends AbstractOverlayModifier<T> {

    List<MoveablePoint> points;

    ImageDisplay display;
    T overlay;
    ImageCanvas viewport;

    public EasyOverlayModifier(Class<? extends Overlay> handledType) {
        super(handledType);
    }

    protected ImageCanvas getViewport() {
        return viewport;
    }

    @Override
    public List<MoveablePoint> getModifiers(ImageDisplay display, T overlay) {
        if (viewport == null) {

            this.overlay = overlay;
            this.display = display;
            viewport = display.getCanvas();
            
            points = initPoints();

            // subscribe events for each point
            points.forEach(point -> point.positionOnDataProperty().addListener(this::onPointMoved));

            refresh();

        }

        return points;
    }

    /**
     * Method only called when the point is dragged by the user. Serves to
     * update the model
     *
     * @param obs
     * @param oldValue
     * @param newValue
     */
    private void onPointMoved(Observable obs, RealCoords oldValue, RealCoords newValue) {
        Platform.runLater(()->{
            updateData();
            display.update();
           
        });
    }

    public abstract List<MoveablePoint> initPoints();

    public abstract void updateData();

    public abstract void updateFromData();

    public T getOverlay() {
        return overlay;
    }
    
    public void refresh() {
        updateFromData();

        points.forEach(MoveablePoint::redraw);

    }
    
    protected void updateFromData(MoveablePoint point, RealCoords positionOnData) {
        
        
        point.positionOnCanvasProperty().setValue(getViewport().dataToPanelCoords(positionOnData));
        //point.positionOnDataProperty().setValue(positionOnData);
        
    }
    
    protected void updateFromData(MoveablePoint point, double xOnData, double yOnData) {
        updateFromData(point, new RealCoords(xOnData,yOnData));
    }
    
}
