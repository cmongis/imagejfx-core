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

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ZoomService;
import org.scijava.display.event.input.MsButtonEvent;
import org.scijava.display.event.input.MsDraggedEvent;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.display.event.input.MsReleasedEvent;
import org.scijava.plugin.Parameter;
import org.scijava.tool.Tool;
import org.scijava.tool.ToolService;
import org.scijava.util.IntCoords;
import org.scijava.util.RealCoords;

/**
 *
 * @author cyril
 */
public class CanvasListener {

    private final Canvas canvas;

    final ImageDisplay display;

    /*
        Services
     */
    @Parameter
    ToolService toolService;

    @Parameter
    ZoomService zoomService;
    
    public CanvasListener(ImageDisplay display, Canvas canvas) {
        this.canvas = canvas;
        this.display = display;
        display.getContext().inject(this);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onDragEvent);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
        canvas.setOnScroll(this::onScrollEvent);
    }

    private void onScrollEvent(ScrollEvent event) {
        
        double percent = 100 * display.getCanvas().getZoomFactor();
        
        if (event.getDeltaY() < 0) {
            percent-=5;
           

        } else {
            percent+=5;
        }
        
        double eventX = event.getX();
        double eventY = event.getY();
        
        IntCoords center = new IntCoords(toInt(event.getX()),toInt(event.getY()));
        
        RealCoords centerReal = display.getCanvas().getPanCenter();//display.getCanvas().panelToDataCoords(center);
        
        
        zoomService.zoomSet(display, percent, centerReal.x, centerReal.y);
        
        
        //ImageCanvasUtils.checkPosition(display.getCanvas());
        display.update();
    }

    private Tool getActiveTool() {
        return toolService.getActiveTool();
    }

    private void onMousePressed(MouseEvent event) {

        getActiveTool()
                .onMouseDown(new MsPressedEvent(display, null, toInt(event.getX()), toInt(event.getY()), MsButtonEvent.LEFT_BUTTON, 1, true));

    }

    private void onDragEvent(MouseEvent event) {

        getActiveTool()
                .onMouseDrag(
                        new MsDraggedEvent(
                                display,
                                null,
                                toInt(event.getX()),
                                toInt(event.getY()),
                                MsButtonEvent.LEFT_BUTTON,
                                1,
                                false
                        )
                );
        event.consume();
    }

    private void onMouseReleased(MouseEvent event) {
        getActiveTool()
                .onMouseUp(new MsReleasedEvent(display, null, toInt(event.getX()), toInt(event.getY()), MsButtonEvent.LEFT_BUTTON, 0, true));
    }

    private int toInt(double d) {
        return new Double(d).intValue();
    }

    private void onMouseEvent(MouseEvent event) {

    }

}
