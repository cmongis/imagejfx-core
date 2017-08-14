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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.Window;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginService;
import org.scijava.ui.viewer.DisplayPanel;
import org.scijava.ui.viewer.DisplayWindow;

/**
 *
 * @author cyril
 */
public class DisplayWindowFX extends Window implements DisplayWindow {

    static String TITLE_CLASS_NAME = "ijfx-window-titlebar";

    static String WINDOW_CLASS_NAME = "ijfx-window";

    @Parameter
    PluginService pluginService;

    @Parameter
    EventService eventService;

    @Parameter
    DisplayService displayService;

    FXDisplayPanel panel;

    Display<?> display;

    public DisplayWindowFX(Display<?> display) {

        setWidth(400);
        setHeight(300);
        this.display = display;
        display.getContext().inject(this);

        panel = pluginService
                .createInstancesOfType(FXDisplayPanel.class)
                .stream()
                .filter(plugin -> plugin.canView(display))
                .findFirst()
                .orElse(null);

        panel.view(this, display);

        if (panel != null) {
            panel.display(display);
            panel.pack();

            setContentPane(panel.getUIComponent());
        } else {
            setContentPane(new StackPane(new Label("No Display plugin :-(")));
        }

        for (EventType<? extends MouseEvent> t : new EventType[]{MouseEvent.MOUSE_CLICKED, MouseEvent.DRAG_DETECTED, MouseEvent.MOUSE_PRESSED}) {
            addEventHandler(t, this::putInFront);
            getContentPane().addEventHandler(t, this::putInFront);

        }

        // close icon
        CloseIcon closeIcon = new CloseIcon(this);

        getRightIcons().add(closeIcon);

        setOnCloseAction(this::onWindowClosed);

        getStyleClass().add(WINDOW_CLASS_NAME);
        setTitleBarStyleClass(TITLE_CLASS_NAME);
        setMovable(true);

    }

    public Display<?> getDisplay() {
        return display;
    }

    public FXDisplayPanel getDisplayPanel() {
        return panel;
    }

    @Override
    public void setContent(DisplayPanel panel) {

    }

    @Override
    public void pack() {

    }

    @Override
    public void showDisplay(boolean visible) {
        setVisible(visible);
    }

    @Override
    public int findDisplayContentScreenX() {
        return 0;
    }

    @Override
    public int findDisplayContentScreenY() {
        return 0;
    }

    protected void onWindowClosed(ActionEvent event) {
        getDisplay().close();
        eventService.publishLater(new DisplayDeletedEvent(getDisplay()));
    }

    void putInFront(Event event) {
        if(displayService.getActiveDisplay() != getDisplay())
        displayService.setActiveDisplay(getDisplay());
    }
    
    @Override
    public void requestFocus() {
        displayService.setActiveDisplay(getDisplay());
        Platform.runLater(super::requestFocus);
        
    }
}
