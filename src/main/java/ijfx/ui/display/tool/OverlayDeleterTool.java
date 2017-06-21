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
package ijfx.ui.display.tool;

import ijfx.core.overlay.OverlaySelectionService;
import ijfx.core.overlay.OverlayUtilsService;
import java.util.List;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.overlay.Overlay;
import org.scijava.display.event.input.KyReleasedEvent;
import org.scijava.input.KeyCode;
import org.scijava.input.MouseCursor;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.tool.Tool;

/**
 *
 * @author cyril
 */
@Plugin(type = Tool.class)
public class OverlayDeleterTool extends ReactiveTool{

    
    @Parameter
    OverlaySelectionService overlaySelectionService;

    @Parameter
    OverlayUtilsService overlayUtilsService;
    
    @Parameter
    ImageDisplayService imageDisplayService;

    public OverlayDeleterTool() {
        startStream();
    }
    
    @Override
    void onStart() {
        
        stream(KyReleasedEvent.class)
                .doOnNext(System.out::println)
                .filter(event->event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE)
                .subscribe(this::onKeyDeletePressed);
        
    }

    private void onKeyDeletePressed(KyReleasedEvent event) {
        
        ImageDisplay imageDisplay = imageDisplayService.getActiveImageDisplay();
        
        List<Overlay> selectedOverlays = overlaySelectionService.getSelectedOverlays(imageDisplay);
        
        overlayUtilsService.removeOverlay(imageDisplay, selectedOverlays);
        
        imageDisplay.update();
        
    }
    
    @Override
    public boolean isAlwaysActive() {
        return true;
    }

    @Override
    public boolean isActiveInAppFrame() {
        return false;
    }

    @Override
    public MouseCursor getCursor() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Listen for keys and deleted the selected overlay when one is selected";
    }
    
}
