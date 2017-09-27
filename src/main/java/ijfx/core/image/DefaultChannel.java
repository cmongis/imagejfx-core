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
package ijfx.core.image;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.imglib2.display.ColorTable;

/**
 *
 * @author Cyril MONGIS
 */


public class DefaultChannel implements Channel {
    
    StringProperty nameProperty = new SimpleStringProperty();
    
    DoubleProperty minProperty = new SimpleDoubleProperty();
    
    DoubleProperty maxProperty = new SimpleDoubleProperty();

    ObjectProperty<ColorTable> colorTableProperty = new SimpleObjectProperty<>();
    
    BooleanProperty active = new SimpleBooleanProperty();
    

    
    @Override
    @JsonGetter("min")
    public double getChannelMin() {
        return minProperty.doubleValue();
    }

    @Override
    @JsonGetter("max")
    public double getChannelMax() {
        return maxProperty.doubleValue();
    }

    @Override
    @JsonSetter("min")
    public void setChannelMin(double min) {
        minProperty.setValue(min);
    }

    @Override
    @JsonSetter("max")
    public void setChannelMax(double max) {
        maxProperty.setValue(max);
    }

    @Override
    @JsonGetter("name")
    public String getChannelName() {
       return nameProperty.getValue();
    }

    @Override
    @JsonSetter("name")
    public void setChannelName(String name) {
        nameProperty.setValue(name);
    }
    
    @Override
    @JsonSetter("colorTable")
    public void setColorTable(ColorTable table) {
        colorTableProperty.setValue(table);
    }
    
    @Override
    @JsonGetter("colorTable")
    public ColorTable getColorTable() {
        return colorTableProperty.getValue();
    }
    
    @JsonGetter("active")
    public boolean isActive() {
        return active.getValue();
    }
    
    @JsonSetter("active")
    public void setActive(boolean active) {
        this.active.setValue(active);
    }
    
    
    
}
