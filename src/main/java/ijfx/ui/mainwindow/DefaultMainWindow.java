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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.core.FXUserInterface;
import ijfx.core.activity.Activity;
import ijfx.core.hint.Hint;
import ijfx.core.hint.HintService;
import ijfx.core.icon.FXIconService;
import ijfx.core.mainwindow.MainWindow;
import ijfx.core.uicontext.ContextualContainer;
import ijfx.core.uicontext.ContextualWidget;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.uiplugin.UiPluginService;
import ijfx.core.utils.SciJavaUtils;
import ijfx.ui.UiPlugin;
import ijfx.ui.UiPluginSorter;
import ijfx.ui.loading.LoadingPopup;
import ijfx.ui.plugin.statusbar.DefaultFXStatusBar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.management.Notification;
import mongis.utils.BindingsUtils;
import mongis.utils.TaskList2;
import mongis.utils.animation.Animations;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import ijfx.core.uiplugin.UiCommand;
import ijfx.core.uiplugin.UiCommandService;
import ijfx.ui.main.ImageJFX;
import javafx.application.Platform;

/**
 * Controller class for the main window
 *
 * @author cyril
 */
@Plugin(type = MainWindow.class)
public class DefaultMainWindow implements MainWindow {

    /*
        Constants
     */
    final private static PseudoClass EMPTY = PseudoClass.getPseudoClass("empty");
    final private static PseudoClass HIDDEN = PseudoClass.getPseudoClass("hidden");

    /*
        FXML elements
     */
    @FXML
    private HBox topLeftHBox;

    @FXML
    private HBox topCenterHBox;

    @FXML
    private HBox topRightHBox;

    @FXML
    private VBox leftVBox;

    @FXML
    private VBox rightVBox;

    @FXML
    private HBox bottomLeftHBox;

    @FXML
    private HBox bottomRightHBox;

    @FXML
    private HBox bottomCenterHBox;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private VBox topToolBarVBox;

    @FXML
    private VBox bottomTopVBox;
    
    private SideBar sideBar;

    List<ContextualContainer<Node>> contextualContainer = new ArrayList<>();

    private TaskList2 taskList = new TaskList2();

    private LoadingPopup loadingPopup;

    @Parameter
    UiContextService uiContextService;

    @Parameter
    UiPluginService uiPluginService;

    @Parameter
    HintService hintService;

    @Parameter
    FXUserInterface fxUserInterface;

    @Parameter
    FXIconService fxIconService;

    @Parameter
    UiCommandService uiActionService;

    @Override
    public void init() {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setController(this);
            loader.setLocation(getClass().getResource("DefaultMainWindow.fxml"));
            loader.load();

            Font.loadFont(FontAwesomeIcon.class.getResource("fontawesome-webfont.ttf").toExternalForm(), 16);

            // binding the sides to the pseudo class empty
            BindingsUtils.bindNodeToPseudoClass(EMPTY, leftVBox, new SimpleListProperty<Node>(leftVBox.getChildren()).emptyProperty());
            BindingsUtils.bindNodeToPseudoClass(EMPTY, rightVBox, new SimpleListProperty<Node>(rightVBox.getChildren()).emptyProperty());
            leftVBox.pseudoClassStateChanged(EMPTY, true);

            Bindings.isEmpty(rightVBox.getChildren()).addListener((obs, oldValue, newValue) -> {
                rightVBox.pseudoClassStateChanged(EMPTY, newValue);
            });

            mainBorderPane.setOpacity(1.0);
            mainBorderPane.setCenter(new Label("Loading..."));

            getLoadingPopup().taskProperty().bind(taskList.foregroundTaskProperty());
            getLoadingPopup()
                    .setCanCancel(false)
                    .attachTo(FXUserInterface.SCENE)
                    .closeOnFinished();

            registerWidgetControllers();

            configureSideBar(new SideBar());

        } catch (IOException ex) {
            Logger.getLogger(DefaultMainWindow.class.getName()).log(Level.SEVERE, "Error when loading the DefaultMainWindow FXML", ex);

        }

    }

    /*
        Initialization methods
     */
    private void registerWidgetControllers() {
        registerContextualContainer(topLeftHBox)
                .setAnimationOnHide(Animations.DISAPPEARS_LEFT)
                .setAnimationOnShow(Animations.APPEARS_LEFT);

        registerContextualContainer(topRightHBox)
                .setAnimationOnHide(Animations.DISAPPEARS_UP)
                .setAnimationOnShow(Animations.APPEARS_UP);

        registerContextualContainer(leftVBox)
                .setAnimationOnHide(Animations.DISAPPEARS_LEFT)
                .setAnimationOnShow(Animations.APPEARS_LEFT);

        registerContextualContainer(rightVBox)
                .setAnimationOnHide(Animations.DISAPPEARS_RIGHT)
                .setAnimationOnShow(Animations.APPEARS_RIGHT);

        registerContextualContainer(bottomLeftHBox)
                .setAnimationOnHide(Animations.DISAPPEARS_DOWN)
                .setAnimationOnHide(Animations.APPEARS_DOWN);

        registerContextualContainer(bottomRightHBox)
                .setAnimationOnHide(Animations.DISAPPEARS_DOWN)
                .setAnimationOnShow(Animations.APPEARS_DOWN);

        /*
        registerPaneCtrl(centerStackPane)
                .setAnimationOnHide(Animations.FADEOUT)
                .setAnimationOnShow(Animations.FADEIN);*/
        registerContextualContainer(topCenterHBox)
                .setAnimationOnHide(Animations.DISAPPEARS_UP)
                .setAnimationOnShow(Animations.APPEARS_UP);

        registerContextualContainer(bottomCenterHBox)
                .setAnimationOnShow(Animations.APPEARS_DOWN)
                .setAnimationOnHide(Animations.DISAPPEARS_DOWN);

        registerContextualContainer(topToolBarVBox)
                .setAnimationOnShow(Animations.FADEIN)
                .setAnimationOnHide(Animations.FADEOUT);
        
        registerContextualContainer(bottomTopVBox)
                .setAnimationOnShow(Animations.QUICK_EXPAND)
                .setAnimationOnHide(Animations.DISAPPEARS_DOWN);
    }

    private void configureSideBar(SideBar sideBar) {

        this.sideBar = sideBar;

        mainAnchorPane.getChildren().add(sideBar.getNode());

        BindingsUtils.bindNodeToPseudoClass(HIDDEN, sideBar.getNode(), Bindings.createBooleanBinding(() -> sideBar.getNode().getTranslateX() <= -1.0 * sideBar.getNode().getWidth() + 2, sideBar.getNode().translateXProperty()));

    }

    private AnimatedPaneContextualView registerContextualContainer(Pane node) {

        // The UI plugin service implements the interface used to sort nodes inside the contextual containers
        UiPluginSorter sorter = uiPluginService;

        AnimatedPaneContextualView ctrl = new AnimatedPaneContextualView(sorter, node)
                .setOnUiPluginDisplayed(this::onUiPluginDisplaed);

        contextualContainer.add(ctrl);

        //uiPluginCtrl.put(node.getId(), ctrl);
        return ctrl;

    }

    public void onUiPluginDisplaed(ContextualWidget<Node> uiPlugin) {

        hintService.displayHints(uiPlugin.getObject().getClass(), false);

    }

    /*
        Loading related methods
     */
    private LoadingPopup getLoadingPopup() {
        if (loadingPopup == null) {
            loadingPopup = new LoadingPopup();
        }
        return loadingPopup;
    }

    /*
        Events
 
     */
 /*
        Overriden methods
     */
    @Override
    public void displayHint(Hint hint) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void displayActivity(Activity activity) {
        /*
        Transition configure = Animations.ZOOMOUT.configure(mainBorderPane.getCenter(), 400);

        configure.setOnFinished(event -> {
            mainBorderPane.setCenter(activity.getContent());
            Animations.ZOOMIN.configure(mainBorderPane.getCenter(), 400).play();
        });

        configure.play();*

         */
        Platform.runLater(() -> {
            mainBorderPane.setCenter(activity.getContent());
        });
        
        
        Task task = activity.updateOnShow();
        if(task!= null) {
            ImageJFX
                    .getThreadPool()
                    .submit(task);
        }

    }

    @Override
    public void displayNotification(Notification notification) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void displaySideMenuAction(UiCommand<MainWindow> action) {

        final FontAwesomeIcon icon = fxIconService.getIcon(SciJavaUtils.getIconPath(action));

        final String label = SciJavaUtils.getLabel(action);

        SideMenuButton sideMenuButton = new SideMenuButton(label, icon);
        sideMenuButton.setOnMouseClicked(event -> {
            action.run(this);
        });
        sideBar.addButton(sideMenuButton);
    }

    @Override
    public void registerUiPlugin(UiPlugin uiPlugin) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addForegroundTask(Task task) {
        taskList.submitTask(task);
    }

    @Override
    public void addBackgroundTask(Task task) {
        uiPluginService
                .getUiPlugin(DefaultFXStatusBar.class)
                .addTask(task);
    }

    @Override
    public void setReady(boolean ready) {

    }

    @Override
    public Parent getUiComponent() {
        return mainAnchorPane;
    }

    @Override
    public List<ContextualContainer<Node>> getContextualContainerList() {
        return contextualContainer;
    }

}
