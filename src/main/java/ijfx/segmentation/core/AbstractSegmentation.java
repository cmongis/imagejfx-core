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
package ijfx.segmentation.core;

import ijfx.ui.display.image.FXImageDisplay;
import java.lang.ref.WeakReference;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;

/**
 *
 * @author cyril
 */
public abstract class AbstractSegmentation implements InteractiveSegmentation{
    
    private final ObjectProperty<Img<BitType>> maskProperty = new SimpleObjectProperty();
    
  
    
    RandomAccessibleInterval<?> example;
    
    
    
  
    
    
    protected void setMask(Img<BitType> mask) {
        maskProperty.setValue(mask);
    }
    
    public Property<Img<BitType>> maskProperty() {
        return maskProperty;
    }

    public void setExample(RandomAccessibleInterval<?> example) {
        this.example = example;
    }

    public RandomAccessibleInterval<?> getExample() {
        return example;
    }
    
    
    
    
    
}
