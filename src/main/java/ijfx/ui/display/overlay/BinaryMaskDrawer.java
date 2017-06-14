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

import ijfx.core.overlay.OverlayDrawingService;
import ijfx.ui.display.image.ImageCanvasUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.overlay.BinaryMaskOverlay;
import net.imagej.overlay.Overlay;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.roi.BinaryMaskRegionOfInterest;
import net.imglib2.type.logic.BitType;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.IntCoords;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = OverlayDrawer.class)
public class BinaryMaskDrawer implements OverlayDrawer<BinaryMaskOverlay> {

    Canvas canvas;

    @Parameter
    OverlayDrawingService drawingService;

    WritableImage image;

    int width;
    int height;
    
    
    private int toInt(double d) {
        return new Double(d).intValue();
    }
    
    public void update(OverlayViewConfiguration<BinaryMaskOverlay> viewConfig, ImageDisplay display, Canvas canvas) {

        ImageCanvas viewport = display.getCanvas();
        
        BinaryMaskOverlay overlay = viewConfig.getOverlay();
        
        BinaryMaskRegionOfInterest roi = (BinaryMaskRegionOfInterest) overlay.getRegionOfInterest();
        
        RandomAccessibleInterval<BitType> rai = roi.getImg();
        RandomAccess<BitType> randomAccess = rai.randomAccess();
        
        Color selectedColor = Color.YELLOW.deriveColor(1.0, 1.0, 1.0, 0.8);
        Color unselectedColor = Color.YELLOW.deriveColor(1.0, 1.0, 1.0, 0.5);
        
        Color color = viewConfig.isSelected() ? selectedColor : unselectedColor;
        
        if (image == null) {
            width = toInt(display.dimension(0));
            height = toInt(display.dimension(1));
            image = new WritableImage(width, height);
        }

       
 
        IntCoords upperLeft = ImageCanvasUtils.getUpperLeftCornerOnData(viewport);
        IntCoords topRight = ImageCanvasUtils.getBottomRightCornerOnData(viewport);
        
        int minX = upperLeft.x;
        int minY = upperLeft.y;
        int maxX = topRight.x;
        int maxY = topRight.y;
        
        if(minX < 0) minX = 0;
        if(minY < 0) minY = 0;
        if(maxX > width) maxX = width;
        if(maxY > height) maxY = height;
       
        
        
        for (int x = minX; x != maxX; x++) {
            for (int y = minY; y != maxY; y++) {
   
                randomAccess.setPosition(x, 0);
                randomAccess.setPosition(y,1);
                if (randomAccess.get().get()) {
                    image.getPixelWriter().setColor(x, y, color);
                } else {
                    image.getPixelWriter().setColor(x, y, Color.TRANSPARENT);
                }
            }
        }

        

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
       

        graphicsContext2D.setFill(Color.TRANSPARENT);
        graphicsContext2D.fill();

        final double sx, sy, sw, sh;

        sx = minX;
        sy = minY;
        sw = maxX - minX;
        sh = maxY - minY;

        graphicsContext2D.drawImage(image, sx, sy, sw, sh, 0, 0, canvas.getWidth(), canvas.getHeight());

    }

    public boolean canHandle(Class<?> t) {
        return t ==  BinaryMaskOverlay.class;
    }
    /*
    @Override
    public boolean isOverlayOnViewPort(Overlay o, ViewPort p) {
        return true;
    }
    */
    @Override
    public boolean isOnOverlay(BinaryMaskOverlay overlay,double xOnImage, double yOnImage) {
        
        long x = Math.round(xOnImage);
        long y = Math.round(yOnImage);
   
          BinaryMaskRegionOfInterest roi = (BinaryMaskRegionOfInterest) overlay.getRegionOfInterest();
        
        RandomAccessibleInterval<BitType> rai = roi.getImg();
        RandomAccess<BitType> randomAccess = rai.randomAccess();
        
        randomAccess.setPosition(x, 0);
        randomAccess.setPosition(y,1);
        return randomAccess.get().get();
        
    }
    
    
    
}
