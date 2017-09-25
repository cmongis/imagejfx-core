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
package ijfx.core;

import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import mongis.utils.FXUtilities;
import org.scijava.ui.ARGBPlane;
import org.scijava.ui.SystemClipboard;

/**
 *
 * @author Cyril MONGIS
 */public class JavaFXClipboard implements SystemClipboard {

        Clipboard clipboard;

        public JavaFXClipboard() {
            clipboard = FXUtilities.runAndWait(Clipboard::getSystemClipboard);
        }

        @Override
        public void pixelsToSystemClipboard(ARGBPlane plane) {

            final int width = plane.getWidth();
            final int height = plane.getHeight();

            final WritableImage image = new WritableImage(width,height);
            final PixelWriter writer = image.getPixelWriter();
            final int[] pixels = plane.getData();
            writer.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
           
            ClipboardContent content = new ClipboardContent();
            content.putImage(image);
            Platform.runLater(()->{
            clipboard.setContent(content);
            });
            
        }

    }