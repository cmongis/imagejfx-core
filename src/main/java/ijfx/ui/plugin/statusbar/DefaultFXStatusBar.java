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
package ijfx.ui.plugin.statusbar;

import ijfx.core.uiplugin.Localization;
import ijfx.ui.UiConfiguration;
import ijfx.ui.UiPlugin;
import java.time.Duration;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import mongis.utils.task.TaskList;
import mongis.utils.transition.OpacityTransitionBinding;
import org.controlsfx.control.StatusBar;
import org.reactfx.EventStreams;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "status-bar", localization = Localization.BOTTOM_LEFT, context = "always")
public class DefaultFXStatusBar implements FXStatusBar, UiPlugin {

    /**
     * Status messages submitted by the IJ API
     */
    private final StringProperty ijStatus = new SimpleStringProperty();

    /**
     * Progress submitted by the IJ API
     */
    private final DoubleProperty ijProgress = new SimpleDoubleProperty();

    /**
     * Current task displayed by the progress bar
     */
    private final ObjectProperty<Task> taskProperty = new SimpleObjectProperty<>();

    /**
     * Displayed status property
     */
    private final StringProperty statusProperty = new SimpleStringProperty();

    /**
     * Displayed progress property
     */
    private final DoubleProperty progressProperty = new SimpleDoubleProperty();

    /**
     * TaskList which handles the current task.
     */
    private final TaskList taskList = new TaskList();

    /**
     * Value inverted each time the status or the progress changes
     */
    private final BooleanProperty changeClock = new SimpleBooleanProperty(true);

    /**
     * True if the progress bar should be shown
     */
    BooleanProperty shouldShow = new SimpleBooleanProperty(true);

    private final static int DELAY_BEFORE_HIDING = 5;

    private ProgressBar progressBar = new ProgressBar();

    private Label statusLabel = new Label();

    private HBox pane = new HBox(progressBar, statusLabel);

    private Boolean changeLock = Boolean.TRUE;

    public DefaultFXStatusBar() {

        taskProperty.addListener(this::onTaskChanged);

        taskProperty.bind(taskList.foregroundTaskProperty());

        statusProperty.addListener(this::onChange);
        progressProperty.addListener(this::onChange);

        statusProperty.bind(ijStatus);
        progressProperty.bind(ijProgress);

        progressBar.visibleProperty().bind(Bindings.createBooleanBinding(this::shouldShowProgressBar, progressBar.progressProperty()));
        
        
        
        // if the progress or status doen't change
        // for a certain time, the status will be hidden
        EventStreams
                .changesOf(changeClock)
                .successionEnds(Duration.ofSeconds(DELAY_BEFORE_HIDING))
                .subscribe(this::onNoChange);

        pane.getStyleClass().add("status-bar");

    }

    /**
     * When the task is changed...
     *
     * @param obs
     * @param oldVAlue
     * @param newValue
     */
    private void onTaskChanged(Observable obs, Task oldVAlue, Task newValue) {

        /*
            If there no current task,
            the displayed message are the ones coming from ImageJ
         */
        if (newValue == null) {
            statusProperty.bind(ijStatus);
            progressProperty.bind(ijProgress);
        } /*
            Otherwise, it displays the messages and 
            progress of the current task
         */ else {
            statusProperty.bind(newValue.messageProperty());
            progressProperty.bind(newValue.progressProperty());
        }
    }

    private void onChange(Observable obs, Object oldValue, Object newValue) {
        shouldShow.setValue(true);
        toggleChangeClock();
    }

    private synchronized void toggleChangeClock() {
        changeClock.setValue(!changeClock.getValue());
    }

    private void onNoChange(Object event) {
        shouldShow.setValue(false);
    }

    @Override
    public void addTask(Task task) {
        taskList.submitTask(task);
    }

    @Override
    public void setStatus(String message) {
        
        Platform.runLater(() -> ijStatus.setValue(message));
    }

    @Override
    public void setProgress(int val, int max) {
        Platform.runLater(() -> ijProgress.setValue(1.0 * val / max));
    }

    @Override
    public Node getUiElement() {
        return pane;
    }

    
    
    @Override
    public UiPlugin init() throws Exception {
        
        
        
        
        pane.getStyleClass().add("fx-status-bar");
        progressBar.progressProperty().bind(progressProperty);
        
        statusLabel.textProperty().bind(statusProperty);

        new OpacityTransitionBinding(pane, shouldShow);

        return this;

    }
    
     
    private boolean shouldShowProgressBar() {
        return progressBar.getProgress() > 0;
    }

}
