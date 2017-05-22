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

import ijfx.ui.display.overlay.DefaultOverlayViewConfiguration;
import ijfx.ui.display.overlay.OverlayDisplayService;
import ijfx.ui.display.overlay.OverlayDrawer;
import ijfx.ui.display.overlay.OverlayModifier;
import ijfx.ui.main.ImageJFX;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.scene.canvas.Canvas;
import net.imagej.display.ImageDisplay;
import net.imagej.display.OverlayView;
import net.imagej.overlay.Overlay;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class OverlayDrawingManager {
 
    
    
    private final ImageDisplay display;
    
    private final Canvas canvas;

    private final Map<Class<? extends Overlay>,OverlayDrawer> drawerMap = new HashMap<>();
    
    private final Map<Overlay, OverlayModifier> modifierMap = new HashMap<>();
    
    private final static Logger logger = ImageJFX.getLogger();
    
    private final ViewPort viewport;
    
    @Parameter
    OverlayDisplayService overlayDisplayService;
    
    
    
    public OverlayDrawingManager(ImageDisplay display, Canvas canvas) {
        
        display.getContext().inject(this);
        this.display = display;
        this.canvas = canvas;
        this.viewport = new ImageCanvasViewPortWrapper(display.getCanvas());
    }
    
    
    protected OverlayDrawer getDrawer(Overlay overlay) {
        //logger.info("Searching a drawer for "+overlay.getClass().getSimpleName());
        if (drawerMap.get(overlay.getClass()) == null) {
            OverlayDrawer drawer = overlayDisplayService.createDrawer(overlay.getClass());
            if (drawer == null) {
                logger.warning("No overlay compatible for " + overlay.getClass().getSimpleName());
                return null;
            } else {
                drawerMap.put(overlay.getClass(), drawer);
                return drawer;
            }
        } else {
            return drawerMap.get(overlay.getClass());
        }
    }

    protected OverlayModifier getModifier(Overlay overlay) {

        return modifierMap.computeIfAbsent(overlay, overlayDisplayService::createModifier);
    }
    
    public void redraw() {
     display
                .stream()
                .filter(view->view instanceof OverlayView)
                .map(view->(OverlayView)view)
                .forEach(this::draw);
    
    }
    
    private void draw(OverlayView view) {
        
        getDrawer(view.getData())
                .update(new DefaultOverlayViewConfiguration(view, view.getData()), viewport, canvas);
        
        
        
    }
    
}
