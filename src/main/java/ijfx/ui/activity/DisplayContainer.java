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
package ijfx.ui.activity;

import ijfx.core.activity.Activity;
import ijfx.explorer.datamodel.wrappers.FileExplorableWrapper;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.utils.CategorizedExplorableController;
import java.io.File;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jfxtras.scene.control.window.Window;
import mongis.utils.task.FluentTask;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.display.event.DisplayCreatedEvent;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.event.EventHandler;
import org.scijava.io.RecentFileService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Activity.class, name = "imagej")
public class DisplayContainer extends StackPane implements Activity {

    public AnchorPane anchorPane = new AnchorPane();
    
    private boolean init = false;
    
    @Parameter
    RecentFileService recentFileService;
    
    @Parameter
    DisplayService displayService;
    
    CategorizedExplorableController ctrl;
    
    public DisplayContainer() {
        getChildren().add(anchorPane);
        
    }
    
    public void addWindow(Window window) {

        Platform.runLater(() -> {
            
            anchorPane.getChildren().add(window);
            window.setPrefSize(500, 400);
        });
    }

    @Override
    public Node getContent() {
        
        if(init == false) {
            
        }
        
        return this;
    }

    @Override
    public Task updateOnShow() {
        return null;
        /*return new CallbackTask<Void,Pane>()
                .call(this::createController)
                .then(this::updateRecentPaneVisibility);*/
    }
    
    public void showContextMenu(String menuRoot, Display<?> display, int x, int y) {
        
    }
    /*
    private synchronized void updateRecentPaneVisibility(Pane pane) {
        
        if(displayService.getDisplays().size() > 0 && ctrl != null) {
            getChildren().remove(ctrl);
        }
        else if(displayService.getDisplays().size() == 0 && ctrl != null && getChildren().contains(ctrl) == false) {
            getChildren().add(ctrl);
        }
        
    }
    
    public synchronized Pane createController() {
        
        ctrl = new CategorizedExplorableController()
                .addCategory("Recent files")
                .setElements("Recent files",recentFileService
                .getRecentFiles()
                .stream()
                .map(path->new FileExplorableWrapper(new File(path)))
                .collect(Collectors.toList()))
                ;
        
        ctrl.update();
        
        return ctrl;
        
    }
    
    @EventHandler
    public void onDisplayCreated(DisplayCreatedEvent event) {
         ImageJFX.getThreadPool().submit(updateOnShow());
    }
   
    @EventHandler
    public void onDisplayClosed(DisplayDeletedEvent event) {
        ImageJFX.getThreadPool().submit(updateOnShow());
    }*/

}
