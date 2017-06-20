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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.imagej.display.DefaultOverlayView;
import net.imagej.display.OverlayService;
import net.imagej.display.OverlayView;
import net.imagej.overlay.Overlay;
import org.scijava.Context;
import org.scijava.display.event.input.MsDraggedEvent;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.display.event.input.MsReleasedEvent;
import org.scijava.plugin.Parameter;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
public abstract class AbstractPathTool<T extends Overlay> extends ReactiveTool {

    @Parameter
    protected OverlayUtilsService overlayUtilsService;

    @Parameter
    protected OverlayService overlayService;
    
    @Parameter
    protected OverlaySelectionService overlaySelectionService;
    
    private T overlay;
    
    @Parameter
    Context context;
    
    private final Executor executor = Executors.newFixedThreadPool(1);
    
    @Override
    void onStart() {

        stream(MsDraggedEvent.class)
                //.observeOn(Schedulers.from(executor))
                .map(this::positionOnImage)
                .collect(ArrayList::new, this::collect)
                .subscribe(this::onPath);

    }

    private void collect(ArrayList<RealCoords> list, RealCoords coords) {
        
        
        list.add(coords);
        
        log("Path : "+list.size());
        
        onPath(list);
    }

    protected abstract void onPath(List<RealCoords> coords);

    protected abstract T createOverlay();

    @Override
    public void onMouseUp(MsReleasedEvent event) {
        stopStream();
        
        getImageDisplay().update();
        overlay = null;
    }

    @Override
    public void onMouseDown(MsPressedEvent event) {
        
        startStream();
        
        
    }

    protected T getOverlay() {
        if (overlay == null) {
            log("Creating overlay");
            overlay = createOverlay();
            
            getImageDisplay().display(overlay);
            
            //overlayUtilsService.addOverlay(getImageDisplay(), overlay);
        }
        return overlay;
    }

}
