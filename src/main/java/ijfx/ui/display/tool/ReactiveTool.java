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

import ijfx.ui.main.ImageJFX;
import io.reactivex.internal.schedulers.IoScheduler;
import java.util.logging.Logger;
import javafx.scene.canvas.Canvas;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imglib2.Point;
import org.scijava.display.event.input.InputEvent;
import org.scijava.display.event.input.KyPressedEvent;
import org.scijava.display.event.input.KyReleasedEvent;
import org.scijava.display.event.input.MsClickedEvent;
import org.scijava.display.event.input.MsDraggedEvent;
import org.scijava.display.event.input.MsMovedEvent;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.display.event.input.MsReleasedEvent;
import org.scijava.display.event.input.MsWheelEvent;
import org.scijava.plugin.AbstractRichPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.tool.Tool;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;
import rx.Scheduler;
import rx.subjects.PublishSubject;

/**
 * Reactive tool uses a 
 * @author cyril
 */
public abstract class ReactiveTool extends AbstractRichPlugin implements Tool{

    boolean active = false;
    
    private PublishSubject<InputEvent> eventStream;
    
    @Parameter
    protected ImageDisplayService imageDisplayService;
    
    
     Logger logger = ImageJFX.getLogger();
    
     protected void log(Object object) {
        logger.info(object.toString());
    }
    
    
    protected void startStream() {
        if(eventStream == null) {
            
            log("Starting stream");
            
            eventStream = PublishSubject.create();
            onStart();
        }
    }
   
    
    protected boolean streamHasStarted() {
        return eventStream != null;
    }
    
    protected void stopStream() {
        log("Stoping stream");
        eventStream.onCompleted();
        eventStream = null;
    }
    
    protected <T extends InputEvent> rx.Observable<T> stream(Class<T> clazz) {
        return eventStream.ofType(clazz);
    }
    
    protected Point positionOnCanvas(InputEvent event) {
        return new Point(event.getX(),event.getY());
    }
    
    protected RealCoords positionOnImage(InputEvent event) {
        return getImageDisplay().getCanvas().panelToDataCoords(new IntCoords(event.getX(), event.getY()));     
    }
    
    
    protected void pushEvent(InputEvent event) {
        
        if(eventStream != null) {
            eventStream.onNext(event);
        }
        
    }
    
    public ImageDisplay getImageDisplay() {
        return imageDisplayService.getActiveImageDisplay();
    }

    public ImageCanvas getCanvas() {
        return getImageDisplay().getCanvas();
    }
    
    abstract void onStart();
    
    

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    
    
    @Override
    public void onKeyDown(KyPressedEvent event) {
        pushEvent(event);
    }

    @Override
    public void onKeyUp(KyReleasedEvent event) {
        pushEvent(event);
    }

    @Override
    public void onMouseDown(MsPressedEvent event) {
        pushEvent(event);
    }

    @Override
    public void onMouseUp(MsReleasedEvent event) {
        pushEvent(event);
    }

    @Override
    public void onMouseClick(MsClickedEvent event) {
        pushEvent(event);
    }

    @Override
    public void onMouseMove(MsMovedEvent event) {
        pushEvent(event);
    }

    @Override
    public void onMouseDrag(MsDraggedEvent event) {
        pushEvent(event);
    }

    @Override
    public void onMouseWheel(MsWheelEvent event) {
        pushEvent(event);
    }

    @Override
    public void configure() {
        
    }
    
}
