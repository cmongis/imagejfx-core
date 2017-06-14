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

import ijfx.core.overlay.OverlayUtilsService;
import java.util.ArrayList;
import java.util.List;
import net.imagej.overlay.Overlay;
import org.scijava.display.event.input.MsDraggedEvent;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.display.event.input.MsReleasedEvent;
import org.scijava.plugin.Parameter;
import org.scijava.util.RealCoords;
import rx.schedulers.Schedulers;

/**
 *
 * @author cyril
 */
public abstract class AbstractPathTool<T extends Overlay> extends ReactiveTool {

    @Parameter
    protected OverlayUtilsService overlayUtilsService;

    private T overlay;

    @Override
    void onStart() {

        stream(MsDraggedEvent.class)
                .observeOn(Schedulers.computation())
                .map(this::positionOnImage)
                .collect(ArrayList::new, this::collect)
                .subscribe(this::onPath);

    }

    private void collect(ArrayList<RealCoords> list, RealCoords coords) {
        list.add(coords);
        onPath(list);
    }

    protected abstract void onPath(List<RealCoords> coords);

    protected abstract T createOverlay();

    @Override
    public void onMouseUp(MsReleasedEvent event) {
        stopStream();
        overlay = null;
    }

    @Override
    public void onMouseDown(MsPressedEvent event) {

        startStream();
    }

    protected T getOverlay() {
        if (overlay == null) {
            overlay = createOverlay();

            overlayUtilsService.addOverlay(getImageDisplay(), overlay);
        }
        return overlay;
    }

}
