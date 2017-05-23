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

import ijfx.core.activity.ActivityChangedEvent;
import ijfx.core.activity.ActivityService;
import ijfx.core.mainwindow.MainWindow;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.uiplugin.UiPluginService;
import ijfx.ui.UiPlugin;
import ijfx.ui.activity.DisplayContainer;
import ijfx.ui.dialog.FxPromptDialog;
import ijfx.ui.display.image.DisplayWindowFX;
import ijfx.ui.main.ImageJFX;
import static ijfx.ui.main.ImageJFX.getStylesheet;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mongis.utils.CallbackTask;
import mongis.utils.FXUtilities;
import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.thread.ThreadService;
import org.scijava.ui.ApplicationFrame;
import org.scijava.ui.Desktop;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.StatusBar;
import org.scijava.ui.SystemClipboard;
import org.scijava.ui.ToolBar;
import org.scijava.ui.UIService;
import org.scijava.ui.UserInterface;
import org.scijava.ui.console.ConsolePane;
import org.scijava.ui.viewer.DisplayViewer;

/**
 *
 * @author cyril
 */
@Plugin(type = UserInterface.class, name = ImageJFX.UI_NAME, priority = Priority.NORMAL_PRIORITY)
public class FXUserInterface extends Application implements UserInterface {

    private static MainWindow mainWindow;

    public static Scene SCENE;

    Logger logger = ImageJFX.getLogger();

    PluginInfo<?> infos;

    @Parameter
    static private PluginService pluginService;

    @Parameter
    private static Context context;

    @Parameter
    private UiPluginService uiPluginService;
    
    @Parameter
    private static EventService eventService;
    
    @Parameter
    private UiContextService uiContextService;
    
    @Parameter
    private DisplayService displayService;
    
    @Parameter
    private ThreadService threadService;
    
    @Parameter
    private ActivityService activityService;
    
    @Parameter
    private static UIService uiService;

    public static Stage STAGE;
    
    JavaFXClipboard clipboard;

    public FXUserInterface() {
        super();

    }

    /*
        Starting the UI (FX Thread related methods)
     */
    public void show() {

        new Thread(this::launchFXThread).start();

    }

    public void launchFXThread() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
       
        
        SCENE = new Scene(new BorderPane());
        //scene.setRoot(getMainWindow().getUiComponent());
        SCENE.getStylesheets().add(getStylesheet());
        
        
        SCENE.setRoot(getMainWindow().getUiComponent());
        
        // scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Open+Sans");
        //scene.getStylesheets().add("http://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css");
        //Font.loadFont(FontAwesomeIconView.class.getResource("fontawesome-webfont.ttf").toExternalForm().toString(), 0);
        primaryStage.setTitle("ImageJ FX");
        primaryStage.setScene(SCENE);
        
        primaryStage.setOnCloseRequest(new javafx.event.EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        primaryStage.show();

        FXUserInterface ui = (FXUserInterface) context.getService(UIService.class).getUI(ImageJFX.UI_NAME);
        ui.initialize();
    }

    /*
        Initializing ImageJFX core components
     */
    /**
     * Method ran after launching the FXThread
     */
    public void initialize() {
        
        // registering the ui slots of the MainWindow
        getMainWindow()
                .getContextualContainerList()
                .forEach(uiContextService::addContextualView);
        
        // loading the plugins
        Task task = new CallbackTask<Object,Collection<UiPlugin>>()
                .runLongCallable(uiPluginService::loadAll)
                .then(this::onAllUiPluginLoaded)
                .start();
        
        
        getMainWindow()
                .addForegroundTask(task);
        
    }
    
    public void onAllUiPluginLoaded(Collection<UiPlugin> plugins) {
        uiContextService.enter("imagej","visualize");
        uiContextService.update();
        activityService.open(DisplayContainer.class);
        
    }

    @Override
    public Desktop getDesktop() {
        return null;
    }

    @Override
    public ApplicationFrame getApplicationFrame() {
        return null;
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public StatusBar getStatusBar() {
        return null;
    }

    @Override
    public ConsolePane<?> getConsolePane() {
        return null;
    }

    @Override
    public SystemClipboard getSystemClipboard() {

        if (clipboard == null) {
            clipboard = new JavaFXClipboard();
        }
        return clipboard;

    }

    @Override
    public DisplayWindowFX createDisplayWindow(Display<?> display) {
       
        return new DisplayWindowFX(display);
        
    }

    @Override
    public DialogPrompt dialogPrompt(String title, String content, DialogPrompt.MessageType messageType, DialogPrompt.OptionType ot) {

        // runs a FX Dialog in the JavaFX Thread and wait for it to be finished
        return FXUtilities.runAndWait(() -> new FxPromptDialog(title, content, messageType, ot));

    }

    @Override
    public File chooseFile(File file, String style) {

        return chooseFile("", file, style);
    }

    @Override
    public File chooseFile(String title, File file, String style) {
        logger.info("Choosing file..." + (file != null ? file.getAbsolutePath() : "(no file)") + " " + style);

        // starting a file chooser
        final FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        if (file != null && file.isDirectory()) {
            chooser.setInitialDirectory(file);
        }

        //runs the open file dialog and wait for it
        try {
            return FXUtilities.runAndWait(() -> {

                if (style != null && style.toLowerCase().contains("save")) {

                    return chooser.showSaveDialog(null);
                } else {
                    return chooser.showOpenDialog(null);
                }

            });
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error when choosing file", ex);
        }

        return null;
    }

    @Override
    public void showContextMenu(String menuRoot, Display<?> display, int x, int y) {
        
        if(activityService.getCurrentActivityAsClass() == DisplayContainer.class) {
            activityService.getActivity(DisplayContainer.class).showContextMenu(menuRoot,display,x,y);
        }
        //getMainWindow().showContextMenu(menuRoot, Display<?> display,x, y);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean requiresEDT() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static MainWindow getMainWindow() {

        if (mainWindow == null) {
            try {
                // get the first MainWindow plugin
                mainWindow = (MainWindow) pluginService.createInstancesOfType(MainWindow.class).get(0);
                mainWindow.init();
            } catch (Exception e) {
                ImageJFX.getLogger().log(Level.SEVERE, "No main window plugin where found.", e);
            }
        }
        return mainWindow;
    }

    @Override
    public boolean isVisible() {
        try {
            return getMainWindow().getUiComponent().isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void show(String name, Object o) {
      final Display<?> display;
		if (o instanceof Display) {
			display = (Display<?>) o;
		}
		else {
			display = displayService.createDisplay(name, o);
		}
		if (!isVisible()) {
			// NB: If this UI is invisible, the display will not be automatically
			// shown. So in that case, we show it explicitly here.
			show(display);
		}
    }

    @Override
    public void show(Display<?> display) {
        
        Logger log = ImageJFX.getLogger();
        
       if (uiService.getDisplayViewer(display) != null) {
			// display is already being shown
			return;
		}

		final List<PluginInfo<DisplayViewer<?>>> viewers =
			uiService.getViewerPlugins();

		DisplayViewer<?> displayViewer = null;
		for (final PluginInfo<DisplayViewer<?>> info : viewers) {
			// check that viewer can actually handle the given display
			final DisplayViewer<?> viewer = pluginService.createInstance(info);
			if (viewer == null) continue;
			if (!viewer.canView(display)) continue;
			if (!viewer.isCompatible(this)) continue;
			displayViewer = viewer;
			break; // found a suitable viewer; we are done
		}
		if (displayViewer == null) {
			log.warning("For UI '" + getClass().getName() +
				"': no suitable viewer for display: " + display);
			return;
		}

		final DisplayViewer<?> finalViewer = displayViewer;
		threadService.queue(new Runnable() {
			@Override
			public void run() {
				final DisplayWindowFX displayWindow = createDisplayWindow(display);
				finalViewer.view(displayWindow, display);
                                finalViewer.setPanel(displayWindow.getDisplayPanel());
				displayWindow.setTitle(display.getName());
				uiService.addDisplayViewer(finalViewer);
				displayWindow.showDisplay(true);
				display.update();
                                
                                activityService.getActivity(DisplayContainer.class).addWindow(displayWindow);
                                activityService.open(DisplayContainer.class);
			}
		});
    }

    @Override
    public void saveLocation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void restoreLocation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
                FX Thread related methods
     */
 /*
     * 
            Basic method implementation
     * 
     */
    @Override
    public Context context() {
        return context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
        context.inject(this);
    }

    @Override
    public PluginInfo<?> getInfo() {
        return infos;
    }

    @Override
    public void setInfo(PluginInfo<?> info) {
        this.infos = info;
    }

    @Override
    public double getPriority() {
        return Priority.NORMAL_PRIORITY;
    }

    @Override
    public void setPriority(double priority) {

    }
    
   /*
        Event
    */
    
    @EventHandler
    public void onActivityChanged(ActivityChangedEvent event) {
        getMainWindow().displayActivity(event.getActivity());
    }
}
