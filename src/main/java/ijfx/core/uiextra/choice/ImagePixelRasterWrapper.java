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
package ijfx.core.uiextra.choice;

import ijfx.core.uiextra.PixelRaster;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

/**
 *
 * @author Cyril MONGIS
 */
public class ImagePixelRasterWrapper implements PixelRaster{

    private final Image image;

    private final int[] pixelBuffer;
    
    private final int width;
    private final int height;
    
    public ImagePixelRasterWrapper(Image image) {
        this.image = image;
        
        while(image.getProgress() < 1);
        
        width = new Double(image.getWidth()).intValue();
        height = new Double(image.getHeight()).intValue();
        
        pixelBuffer = new int[getWidth()*getHeight()];
        
        image.getPixelReader().getPixels(0, 0, getWidth(), getHeight(), WritablePixelFormat.getIntArgbInstance(), pixelBuffer, 0, getWidth());
    }
    
    
    public Image getImage() {
        return image;
    }
    
    
    
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
      return height;
    }

    @Override
    public int[] getPixels() {
      return pixelBuffer;
    }
    
}
