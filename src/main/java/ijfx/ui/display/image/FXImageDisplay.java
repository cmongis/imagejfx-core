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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyProperty;
import net.imagej.display.ImageDisplay;
import net.imglib2.display.ColorTable;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author cyril
 */
public interface FXImageDisplay extends ImageDisplay {

    
    int getChannelNumber();
    
    /*
        Current LUT configuration
     */
    void setCurrentLUT(ColorTable table);

    ColorTable getCurrentLUT();

    Property<ColorTable> currentLUTProperty();

    void setCurrentLUTMin(double min);

    double getCurrentLUTMin();

    DoubleProperty currentLUTMinProperty();

    void setCurrentLUTMax(double max);

    double getCurrentLUTMax();

    DoubleProperty currentLUTMaxProperty();
    
    /*
        Dataset min/max
    */
   
    double getDatasetMin();
   
    ReadOnlyDoubleProperty datasetMinProperty();
    
    double getDatasetMax();
    
    ReadOnlyDoubleProperty datasetMaxProperty();
    
    /*
       setting Current Channel
     */
    void setCurrentChannel(int channel);

    int getCurrentChannel();

    Property<Number> currentChannelProperty();
    
    IntegerProperty refreshPerSecond();
    
    /*
        Channel activation
    */
    
    void setChannelComposite(int channel, boolean active);
    
    int[] getCompositeChannels();
    
    ReadOnlyProperty<int[]> compositeChannelsProperty();
    
    default void toggleChannelComposite(int channel) {
        setChannelComposite(channel, !isChannelComposite(channel));
        
    }
    
    default boolean isChannelComposite(int channel) {
        return ArrayUtils.contains(getCompositeChannels(), channel);
    }
    
    public void updateAsync();
    
    public void checkProperties();
    
}
