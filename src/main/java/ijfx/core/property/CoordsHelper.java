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
package ijfx.core.property;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.adapter.JavaBeanDoubleProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerProperty;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
public class CoordsHelper {

    RealCoords realCoords = new RealCoords(0, 0);

    IntCoords intCoords = new IntCoords(0, 0);

    JavaBeanDoubleProperty realXProperty;

    JavaBeanDoubleProperty realYProperty;

    JavaBeanIntegerProperty intXProperty;

    JavaBeanIntegerProperty intYProperty;

    JavaBeanObjectProperty<RealCoords> realCoordsProperty;

    JavaBeanObjectProperty<IntCoords> intCoordsProperty;

    public CoordsHelper() {

        try {
            intXProperty = new JavaBeanIntegerPropertyBuilder()
                    .name("intX")
                    .bean(this)
                    .build();

            intYProperty = new JavaBeanIntegerPropertyBuilder()
                    .name("intY")
                    .bean(this)
                    .build();

            realXProperty = new JavaBeanDoublePropertyBuilder()
                    .name("realX")
                    .bean(this)
                    .build();

            realYProperty = new JavaBeanDoublePropertyBuilder()
                    .name("realY")
                    .bean(this)
                    .build();

            realCoordsProperty = new JavaBeanObjectPropertyBuilder<>()
                    .name("realCoords")
                    .bean(this)
                    .build();

            intCoordsProperty = new JavaBeanObjectPropertyBuilder<>()
                    .name("intCoords")
                    .bean(this)
                    .build();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(CoordsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
        X
     */
    public void setIntX(int x) {
        intCoords.x = x;
        realCoords.x = x;

        intXProperty.fireValueChangedEvent();

        realCoordsProperty.fireValueChangedEvent();
        intCoordsProperty.fireValueChangedEvent();
    }

    private void fireValueChangedEvents() {
        intXProperty.fireValueChangedEvent();
        intYProperty.fireValueChangedEvent();
        realXProperty.fireValueChangedEvent();
        realYProperty.fireValueChangedEvent();
        //realCoordsProperty.fireValueChangedEvent();
        //intCoordsProperty.fireValueChangedEvent();
    }

    public void setRealX(double x) {
        intCoords.x = toInt(x);
        realCoords.x = x;

        intXProperty.fireValueChangedEvent();

        realCoordsProperty.fireValueChangedEvent();
        intCoordsProperty.fireValueChangedEvent();

    }

    public int getIntX() {
        return intCoords.x;
    }

    public double getRealX() {
        return realCoords.x;
    }

    /*
        Y
     */
    public void setIntY(int y) {
        intCoords.y = y;
        realCoords.y = y;

        intYProperty.fireValueChangedEvent();
        realCoordsProperty.fireValueChangedEvent();
        intCoordsProperty.fireValueChangedEvent();
    }

    public void setRealY(double y) {
        intCoords.y = toInt(y);
        realCoords.y = y;

        intYProperty.fireValueChangedEvent();
        realCoordsProperty.fireValueChangedEvent();
        intCoordsProperty.fireValueChangedEvent();

    }

    public int getIntY() {
        return intCoords.y;
    }

    public double getRealY() {
        return realCoords.y;
    }

    /*
        Coords
     */
    public void setIntCoords(IntCoords intCoords) {
        this.intCoords = intCoords;
        realCoords = new RealCoords(intCoords.x, intCoords.y);

       fireValueChangedEvents();

    }

    public void setRealCoords(RealCoords realCoords) {
        this.intCoords = new IntCoords(toInt(realCoords.x), toInt(realCoords.y));
        this.realCoords = realCoords;
        
        
        
        fireValueChangedEvents();
        
        
        realCoordsProperty.fireValueChangedEvent();
        intCoordsProperty.fireValueChangedEvent();
    }
    
    public RealCoords getRealCoords() {
        return realCoords;
    }
    
    public IntCoords getIntCoords() {
        return intCoords;
    }

    public  static int toInt(Double x) {
        return x.intValue();
    }
    
    public DoubleProperty realXProperty() {
        return realXProperty;
    }
    
    public DoubleProperty realYProperty() {
        return realYProperty;
    }
    
    public IntegerProperty intXProperty() {
        return intXProperty;
    }
    
    public IntegerProperty intYProperty() {
        return intYProperty;
    }
    
    public Property<RealCoords> realCoordsProperty() {
        return realCoordsProperty;
    }
    
    public Property<IntCoords> intCoordsProperty() {
        return intCoordsProperty;
    }

    public static IntCoords toInt(RealCoords realCoords) {
        return new IntCoords(toInt(realCoords.x),toInt(realCoords.y));
    }
    
}
