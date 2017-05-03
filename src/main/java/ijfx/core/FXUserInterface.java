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
package ijfx.core;

import ijfx.core.mainwindow.MainWindow;
import ijfx.ui.main.ImageJFX;
import static ijfx.ui.main.ImageJFX.getStylesheet;
import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.scijava.display.Display;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.ui.AbstractUserInterface;
import org.scijava.ui.ApplicationFrame;
import org.scijava.ui.Desktop;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.StatusBar;
import org.scijava.ui.SystemClipboard;
import org.scijava.ui.ToolBar;
import org.scijava.ui.UserInterface;
import org.scijava.ui.console.ConsolePane;
import org.scijava.ui.viewer.DisplayWindow;

/**
 *
 * @author cyril
 */
@Plugin(type = UserInterface.class)
public class FXUserInterface extends AbstractUserInterface {

    private FXApplication application;

    private MainWindow mainWindow;

    @Parameter
    private PluginService pluginService;

    public FXUserInterface() {
        super();

    }

    public void initialize() {

    }

    @Override
    public Desktop getDesktop() {
        return super.getDesktop(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ApplicationFrame getApplicationFrame() {
        return super.getApplicationFrame(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ToolBar getToolBar() {
        return super.getToolBar(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusBar getStatusBar() {
        return super.getStatusBar(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ConsolePane<?> getConsolePane() {
        return super.getConsolePane(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SystemClipboard getSystemClipboard() {
        return super.getSystemClipboard(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DisplayWindow createDisplayWindow(Display<?> display) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DialogPrompt dialogPrompt(String message, String title, DialogPrompt.MessageType messageType, DialogPrompt.OptionType optionType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File chooseFile(File file, String style) {
        return super.chooseFile(file, style); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File chooseFile(String title, File file, String style) {
        return super.chooseFile(title, file, style); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showContextMenu(String menuRoot, Display<?> display, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean requiresEDT() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public MainWindow getMainWindow() {

        if (mainWindow == null) {
            try {
               mainWindow = (MainWindow) pluginService.getPluginsOfClass(MainWindow.class).get(0);

                

            } catch (Exception e) {
                ImageJFX.getLogger().severe("No main window plugin where found.");
            }
        }
        return mainWindow;
    }

    public void show() {

        // starts the FXApplication thread
        application = new FXApplication();
        
        // getting the main window ui element
        application.getScene().setRoot(getMainWindow().getUiComponent());
   
        // launching the FX Thread
        application.launch();

    }

    private class FXApplication extends Application {

        private Scene scene;

        @Override
        public void start(Stage primaryStage) throws Exception {

            scene = new Scene(new BorderPane());

            scene.getStylesheets().add(getStylesheet());

            // scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Open+Sans");
            //scene.getStylesheets().add("http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css");
            //Font.loadFont(FontAwesomeIconView.class.getResource("fontawesome-webfont.ttf").toExternalForm().toString(), 0);
            primaryStage.setTitle("ImageJ FX");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            primaryStage.show();

        }

        public Scene getScene() {
            return scene;
        }

    }

}
