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

import ijfx.core.property.CoordsHelper;
import ijfx.ui.display.image.ImageCanvasUtils;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.imagej.display.ImageCanvas;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class MoveablePoint extends Rectangle {

    ImageCanvas viewport;
    
    private CoordsHelper positionOnDataHelper = new CoordsHelper();
    private CoordsHelper positionOnCanvasHelper = new CoordsHelper();

   

    private MoveablePoint() {
        super();

        setFill(Color.YELLOW);

        translateXProperty().bind(Bindings.createDoubleBinding(this::calculateTranslateX, widthProperty()));
        translateYProperty().bind(Bindings.createDoubleBinding(this::calculateTranslateY, heightProperty()));
        setWidth(9);
        setHeight(9);

        setOnMouseDragged(this::onMouseDragged);
        
        xProperty().bindBidirectional(positionOnCanvasHelper.intXProperty());
        yProperty().bindBidirectional(positionOnCanvasHelper.intYProperty());
        
        positionOnCanvasHelper.intCoordsProperty().addListener(this::updateData);
        
    }

    public MoveablePoint(ImageCanvas canvas) {
        this();
        this.viewport = canvas;
        
    }

    public void redraw() {

        
        updateFromData();
        

        setVisible(viewport.isInImage(positionOnCanvasHelper.getIntCoords()));

        

    }
    
    private void updateFromData() {        
        positionOnCanvasHelper.setIntCoords(viewport.dataToPanelCoords(positionOnDataHelper.getRealCoords()));
    }
    
    private void updateData(Observable obs, IntCoords oldValue, IntCoords newValue) {
        
        
        
        //positionOnDataHelper.setRealCoords(viewport.panelToDataCoords(newValue));
    }

    

    // place the points without alerting the listeners
    public void placeOnScreen(Point2D positionOnScreen) {
        setX(positionOnScreen.getX());
        setY(positionOnScreen.getY());
    }

    // set the position on the screen without alerting the
    // the observers
    public void setPositionSilently(Point2D positionOnScreen) {
        placeOnScreen(positionOnScreen);
    }

   
    public Double calculateTranslateX() {
        return -getWidth() / 2;
    }

    public Double calculateTranslateY() {
        return -getHeight() / 2;
    }

    private void onMouseDragged(MouseEvent event) {
        
        
        
        //setX(event.getX());
        //setY(event.getY());
        
        positionOnCanvasHelper.setRealCoords(new RealCoords(event.getX(),event.getY()));
        
        // updating model only when there are movement
        positionOnDataHelper.setRealCoords(viewport.panelToDataCoords(positionOnCanvasHelper.getIntCoords()));
        event.consume();
    }

    public Property<IntCoords> positionOnCanvasProperty() {
        return positionOnCanvasHelper.intCoordsProperty();
    }

    public Property<RealCoords> positionOnDataProperty() {
        return positionOnDataHelper.realCoordsProperty();
    }
    
    private int toInt(Double x) {
        return x.intValue();
    }
    
    

}
