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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mongis.utils.MemoryUtils;
import mongis.utils.transition.TransitionBinding;

/**
 *
 * @author cyril
 */
public class SideBar {

    private static final String FXML = "SideBar.fxml";

    @FXML
    private BorderPane sideMenu;

    @FXML
    private VBox sideMenuMainTopVBox;

    @FXML
    private VBox sideMenuTopVBox;

    @FXML
    private VBox sideMenuBottomVBox;

    @FXML
    private ProgressBar memoryProgressBar;

    @FXML
    private Label memoryLabel;

    private static final int MEMORY_REFRESH_RATE = 1000;

    private final TransitionBinding<Number> sideMenuWidthBinding = new TransitionBinding<Number>(10d, 250d);

    BooleanProperty menuActivated = new SimpleBooleanProperty(false);

    
    public SideBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML));
            loader.setController(this);
            loader.load();

            sideMenu.setTranslateZ(0);
            sideMenu.setTranslateX(0);
            sideMenu.setPrefWidth(30);
            memoryThread.start();

            memoryProgressBar.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMemoryProgressBarClicked);

            sideMenuWidthBinding.bind(menuActivated, sideMenu.prefWidthProperty());

            sideMenu.setOnMouseEntered(event -> menuActivated.setValue(true));
            sideMenu.setOnMouseExited(event -> menuActivated.setValue(false));

            new TransitionBinding<Number>(0d, 1d)
                    .bind(sideMenuWidthBinding.stateProperty(), memoryLabel.opacityProperty())
                    .setDuration(Duration.millis(150));

            new TransitionBinding<Number>(0d, 1d)
                    .bind(sideMenuWidthBinding.stateProperty(), memoryProgressBar.opacityProperty())
                    .setDuration(Duration.millis(150));

            menuActivated.setValue(false);

            
            addButton(new SideMenuButton("Hello",FontAwesomeIcon.ADN));
            
        } catch (IOException ex) {
            Logger.getLogger(SideBar.class.getName()).log(Level.SEVERE, "Error when loading the SideBar", ex);
        }
    }

    protected Pane getNode() {
        return sideMenu;
    }

    protected void onMemoryUsageChanged() {

        if (memoryLabel == null) {
            return;
        }

        int free = (int) MemoryUtils.getAvailableMemory();
        int max = (int) MemoryUtils.getTotalMemory();
        int used = (max - free);

        double progress = 1.0 * (used) / max;

        // System.out.println(progress);
        memoryProgressBar.setProgress(progress);
        memoryLabel.setText(String.format("%d / %d MB", used, max));

    }
    
    public void addButton(SideMenuButton button) {
        
        
        button.extendedProperty().bind(menuActivated);
        sideMenuTopVBox.getChildren().add(button);
        
    }

    private void onMemoryProgressBarClicked(MouseEvent event) {
        System.gc();
    }

    private Thread memoryThread = new Thread(() -> {

        while (true) {

            Platform.runLater(() -> onMemoryUsageChanged());
            try {
                Thread.sleep(MEMORY_REFRESH_RATE);
            } catch (Exception e) {
            }
        }

    });

}
