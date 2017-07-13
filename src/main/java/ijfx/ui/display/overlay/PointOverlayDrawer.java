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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.PointOverlay;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Ignore
public class PointOverlayDrawer implements OverlayDrawer<PointOverlay>{

    Group pointNode;
    
    double radius = 5.0;
    
    Property<Point2D> position = new SimpleObjectProperty<Point2D>();
    
    DoubleProperty strokeWidth = new SimpleDoubleProperty(1);
    Property<Paint> strokePaint = new SimpleObjectProperty<>(Color.YELLOW);
    
    Rectangle r;
 
    
    public void update(OverlayViewConfiguration<PointOverlay> viewConfig, ImageDisplay viewport, Canvas canvas) {
        
        
        
        PointOverlay overlay = viewConfig.getOverlay();
        //Point2D onScreen = viewport.getPositionOnCamera(PointOverlayHelper.getOverlayPosition(overlay));
        
        //double[] xy = new double[] { onScreen.getX(), onScreen.getY() };
        
       for(double[] xy : overlay.getPoints()) {
           Point2D onScreen = new Point2D(xy[0],xy[1]);
           xy = new double[] { onScreen.getX(), onScreen.getY() };
           
           canvas
                   .getGraphicsContext2D()
                   .fillRect(xy[0]-radius, xy[1]-radius, radius*2, radius*2);
           
       }
        
        
    }
    
    
    
    

    public boolean canHandle(Class<?> t) {
        return t ==  PointOverlay.class;
    }
    /*
    @Override
    public boolean isOnOverlay(PointOverlay overlay, ViewPort viewport, double xOnImage, double yOnImage) {
        
        if(overlay instanceof PointOverlay) {
            
            PointOverlay pointOverlay = (PointOverlay)overlay;
            
            double[] point = pointOverlay.getPoint(0);
            
            double dx = Math.abs(xOnImage-point[0]);
            double dy = Math.abs(yOnImage-point[1]);
            
            return dx * dy < 10*10;
        }
        else return false;
    }*/

    @Override
    public boolean isOnOverlay(PointOverlay overlay, double xOnImage, double yOnImage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
   
   
}
