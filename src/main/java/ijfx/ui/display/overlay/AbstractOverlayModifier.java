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

import net.imagej.display.ImageDisplay;
import net.imagej.event.OverlayUpdatedEvent;
import net.imagej.overlay.Overlay;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS
 */
public abstract class AbstractOverlayModifier<T extends Overlay> implements OverlayModifier<T> {
    
    final Class<? extends Overlay> handledType;

    private T overlay;
    
    private ImageDisplay display;
    
   
    @Parameter
    EventService eventService;
    
    public void setOverlay(T t) {
        this.overlay = t;
    }
    
    public T getOverlay() {
        return overlay;
    }

    public void init(T overlay, ImageDisplay display) {
        setOverlay(overlay);
        setDisplay(display);
    }
    public void setDisplay(ImageDisplay display) {
        this.display = display;
    }

    public ImageDisplay getDisplay() {
        return display;
    }
    
    
    
    
    public AbstractOverlayModifier(Class<? extends Overlay> handledType) {
        this.handledType = handledType;
        System.out.println("Click :Creating modifier !");
    }
    
    @Override
    public boolean canHandle(Overlay overlay) {
        return overlay.getClass().isAssignableFrom(handledType);
    }
    
    protected void fireOverlayChange() {
        eventService.publish(new OverlayUpdatedEvent(overlay));
    }
    
}
