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

import ijfx.core.overlay.OverlaySelectionService;
import ijfx.core.overlay.OverlayUtilsService;
import ijfx.ui.display.overlay.OverlayDisplayService;
import ijfx.ui.display.overlay.OverlayDrawer;
import ijfx.ui.main.ImageJFX;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.display.OverlayService;
import net.imagej.display.OverlayView;
import net.imagej.display.ZoomService;
import net.imagej.overlay.Overlay;
import org.scijava.display.event.input.KyPressedEvent;
import org.scijava.display.event.input.KyReleasedEvent;
import org.scijava.display.event.input.MsButtonEvent;
import org.scijava.display.event.input.MsDraggedEvent;
import org.scijava.display.event.input.MsMovedEvent;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.display.event.input.MsReleasedEvent;
import org.scijava.input.InputModifiers;
import org.scijava.input.KeyCode;
import org.scijava.plugin.Parameter;
import org.scijava.tool.Tool;
import org.scijava.tool.ToolService;
import org.scijava.util.IntCoords;
import org.scijava.util.PlatformUtils;
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

    @Parameter
    OverlayDisplayService overlayDisplayService;

    @Parameter
    OverlayService overlayService;

    @Parameter
    OverlayUtilsService overlayUtilsService;

    @Parameter
    OverlaySelectionService overlaySelectionService;

    ExecutorService executor = ImageJFX.getThreadQueue();

    final ImageCanvas viewport;

    public CanvasListener(ImageDisplay display, Canvas canvas) {
        this.canvas = canvas;
        this.display = display;

        viewport = display.getCanvas();

        display.getContext().inject(this);
        canvas.setFocusTraversable(true);
        canvas.addEventFilter(MouseEvent.ANY, (e) -> canvas.requestFocus());
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onDragEvent);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMoved);
        canvas.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        canvas.addEventHandler(KeyEvent.KEY_RELEASED, this::onKeyReleased);
        canvas.setOnScroll(this::onScrollEvent);

    }

    private Tool getActiveTool() {
        return toolService.getActiveTool();
    }

    private List<Tool> getAlwaysActiveTools() {

        return toolService.getAlwaysActiveTools();

    }

    /*
        Event handlers
     */
    private void onScrollEvent(ScrollEvent event) {

        double percent = 100 * display.getCanvas().getZoomFactor();

        final double factor = 0.05;

        if (event.getDeltaY() < 0) {
            percent *= (1 + factor);

        } else {
            percent *= (1 - factor);
        }

        RealCoords centerReal = display.getCanvas().getPanCenter();//display.getCanvas().panelToDataCoords(center);

        zoomService.zoomSet(display, percent, centerReal.x, centerReal.y);

        display.update();
    }

    private void onKeyPressed(KeyEvent event) {
        fireEvent(this::onKeyPressed, event);
    }

    private void onKeyPressed(KeyEvent event, Tool tool) {
        KeyCode keyCode = fromEvent(event);
        tool.onKeyDown(new KyPressedEvent(display, extractInputModifiers(event), 0, 0, event.getCharacter().charAt(0), keyCode));
    }

    private void onKeyReleased(KeyEvent event) {
        fireEvent(this::onKeyReleased, event);
    }

    private void onKeyReleased(KeyEvent event, Tool tool) {
        KeyCode keyCode = fromEvent(event);
        tool.onKeyUp(new KyReleasedEvent(display, extractInputModifiers(event), 0, 0, event.getCharacter().charAt(0), keyCode));
    }

    private KeyCode fromEvent(KeyEvent event) {
        return KeyCode.get(event.getCode().name());
    }

    private <T extends InputEvent> void fireEvent(BiConsumer<T, Tool> consumer, T event) {

        executor.execute(() -> {
            consumer.accept(event, getActiveTool());
            getAlwaysActiveTools()
                    .stream()
                    .forEach(tool -> consumer.accept(event, tool));

        });

    }

    private void onMousePressed(MouseEvent event) {
        canvas.requestFocus();
        fireEvent(this::onMousePressed, event);

    }

    private void onMousePressed(MouseEvent event, Tool tool) {
        tool.onMouseDown(new MsPressedEvent(display, extractInputModifiers(event), toInt(event.getX()), toInt(event.getY()), MsButtonEvent.LEFT_BUTTON, 1, true));

    }

    private void onDragEvent(MouseEvent event) {

        onDragEvent(event, getActiveTool());

        getAlwaysActiveTools()
                .forEach(tool -> onDragEvent(event, tool));

        event.consume();
    }

    private void onDragEvent(MouseEvent event, Tool tool) {
        tool.onMouseDrag(
                new MsDraggedEvent(
                        display,
                        extractInputModifiers(event),
                        toInt(event.getX()),
                        toInt(event.getY()),
                        MsButtonEvent.LEFT_BUTTON,
                        1,
                        false
                )
        );
    }

    private void onMouseReleased(MouseEvent event) {
        onMouseReleased(event, getActiveTool());

        getAlwaysActiveTools()
                .forEach(tool -> onMouseReleased(event, tool));

    }

    private void onMouseReleased(MouseEvent event, Tool tool) {
        tool.onMouseUp(new MsReleasedEvent(
                display,
                extractInputModifiers(event),
                toInt(event.getX()),
                toInt(event.getY()),
                MsButtonEvent.LEFT_BUTTON,
                0,
                true));
    }

    private void onMouseMoved(MouseEvent event) {
        onMouseMoved(event, getActiveTool());

        getAlwaysActiveTools()
                .forEach(tool -> onMouseMoved(event, tool));

    }

    private void onMouseMoved(MouseEvent event, Tool tool) {
        tool.onMouseMove(new MsMovedEvent(display, extractInputModifiers(event), 0, 0));
    }

    private void onMouseClicked(MouseEvent event) {

        canvas.requestFocus();

        List<Overlay> overlays = overlayService
                .getOverlays(display);

        display
                .stream()
                .filter(view -> view instanceof OverlayView)
                .map(view -> (OverlayView) view)
                .collect(Collectors.toList());

        for (Overlay overlay : overlays) {
            if (isOnOverlay(event.getX(), event.getY(), overlay)) {
                overlaySelectionService.selectOnlyOneOverlay(display, overlay);
                return;
            }
        }
        overlaySelectionService.unselectedAll(display);
        display.update();

    }

    /*
        Helpers
     */
    private int toInt(double d) {
        return new Double(d).intValue();
    }

    private InputModifiers extractInputModifiers(KeyEvent event) {

        boolean isMetaDown = event.isMetaDown();
        boolean isCtrlDown = event.isControlDown();

        if (PlatformUtils.isMac()) {
            isMetaDown = !isMetaDown;
        }

        return new InputModifiers(
                event.isAltDown(),
                false,
                isMetaDown,
                isCtrlDown,
                event.isShiftDown(),
                false,
                false,
                false
        );

    }

    private InputModifiers extractInputModifiers(MouseEvent event) {

        return new InputModifiers(
                event.isAltDown(),
                false,
                event.isControlDown(),
                event.isMetaDown(),
                event.isShiftDown(),
                event.isPrimaryButtonDown(),
                event.isMiddleButtonDown(),
                event.isSecondaryButtonDown()
        );

    }

    private boolean isOnOverlay(double x, double y, Overlay overlay) {

        OverlayDrawer drawer = overlayDisplayService.getDrawer(overlay);

        if (drawer == null) {
            return false;
        }

        RealCoords onData = display.getCanvas().panelToDataCoords(new IntCoords(toInt(x), toInt(y)));

        boolean result = drawer.isOnOverlay(overlay, onData.x, onData.y);
        return result;
    }
}
