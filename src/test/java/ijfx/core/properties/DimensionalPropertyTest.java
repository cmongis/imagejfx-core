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
package ijfx.core.properties;

import ijfx.core.IjfxTest;
import ijfx.core.property.DimensionalProperty;
import javafx.beans.property.adapter.JavaBeanObjectProperty;
import net.imagej.overlay.RectangleOverlay;
import org.junit.Assert;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS
 */
public class DimensionalPropertyTest extends IjfxTest {
    
    @Parameter
    Context context;
    
    @Test
    public void testOverlay() {
        
        RectangleOverlay overlay = new RectangleOverlay();
        
        JavaBeanObjectProperty<Double> xOrigin = DimensionalProperty.createProperty(overlay, double.class,"origin", 0);
        
        System.out.println(overlay.getOrigin(0));
        xOrigin.set(4d);
        System.out.println(overlay.getOrigin(0));
        
        Assert.assertTrue(overlay.getOrigin(0) == 4d);
        
    }
    
}
