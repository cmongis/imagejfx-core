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
package ijfx.ui.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rjeschke.txtmark.Processor;
import ijfx.ui.RichMessageDisplayer;
import ijfx.ui.main.ImageJFX;
import java.net.URL;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import mongis.utils.task.FluentTask;
import net.imglib2.display.ColorTable;

/**
 *
 * @author cyril
 */
public class FXUtils {

    public static ObjectMapper mapper = new ObjectMapper();
    
    
    public static Image colorTableToImage(ColorTable table, int width, int height) {
        return colorTableToImage(table, width, height, 2);
    }

    public static Image colorTableToImage(ColorTable table, int width, int height, int border) {
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        int color;
        for (int x = border; x != width - border; x++) {
            color = table.lookupARGB(border, width - 1 - (border * 2), x);
            for (int y = border; y != height - border; y++) {
                pixelWriter.setArgb(x, y, color);
            }
        }
        return image;
    }

    public static FluentTask<String, WebView> createWebView(Object root, String mdFile) {
        return new FluentTask<String, WebView>().setInput(mdFile).callback((String input) -> {
            WebView webView = new WebView();
            RichMessageDisplayer displayer = new RichMessageDisplayer(webView).addStringProcessor(Processor::process);
            try {
                displayer.setContent(root.getClass(), input);
            } catch (Exception e) {
                ImageJFX.getLogger().log(Level.SEVERE, null, e);
            }
            return webView;
        }).submit(Platform::runLater);
    }

    public static HelpConfiguration getHelpConfiguration(Object object) {
        String file = object.getClass().getSimpleName() + ".json";
        URL resource = object.getClass().getResource(file);
        if (resource != null) {
            try {
                HelpConfiguration readValue = mapper.readValue(resource.openStream(), HelpConfiguration.class);
                return readValue;
            } catch (Exception e) {
                ImageJFX.getLogger().log(Level.WARNING, "Error when loading " + file, e);
                return null;
            }
        } else {
            return null;
        }
    }
    
}
