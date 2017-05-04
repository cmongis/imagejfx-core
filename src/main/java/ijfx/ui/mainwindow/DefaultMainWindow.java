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
package ijfx.ui.mainwindow;

import ijfx.core.activity.Activity;
import ijfx.core.hint.Hint;
import ijfx.core.mainwindow.MainWindow;
import ijfx.core.mainwindow.SideMenuAction;
import ijfx.ui.UiPlugin;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javax.management.Notification;
import org.scijava.plugin.Plugin;

/**
 * Controller class for the main window
 *
 * @author cyril
 */
@Plugin(type = MainWindow.class)
public class DefaultMainWindow implements MainWindow {

    @FXML
    AnchorPane root;

    @Override
    public void init() {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setController(this);
            loader.setLocation(getClass().getResource("DefaultMainWindow.fxml"));
            loader.load();
           
           
            
            
        } catch (IOException ex) {
            Logger.getLogger(DefaultMainWindow.class.getName()).log(Level.SEVERE, "Error when loading the DefaultMainWindow FXML", ex);

        }
        
        

    }

    @Override
    public void displayHint(Hint hint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void displayActivity(Activity activity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void displayNotification(Notification notification) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void displaySideMenuAction(SideMenuAction action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerUiPlugin(UiPlugin uiPlugin) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addForegroundTask(Task task) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addBackgroundTask(Task task) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setReady(boolean ready) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Parent getUiComponent() {
        return root;
    }

}
