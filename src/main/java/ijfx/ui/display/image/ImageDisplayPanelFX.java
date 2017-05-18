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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.core.timer.Timer;
import ijfx.core.timer.TimerService;
import ijfx.plugins.display.AutoContrast;
import ijfx.ui.display.tool.HandTool;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.widgets.ImageDisplayAdjuster;
import ijfx.ui.widgets.LUTSwitchButton;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mongis.utils.FXUtilities;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.ui.viewer.image.ImageDisplayPanel;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.type.numeric.RealType;
import org.scijava.Context;
import org.scijava.display.Display;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.tool.ToolService;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.util.IntCoords;

/**
 *
 * @author cyril
 */
@Plugin(type = FXDisplayPanel.class)
public class ImageDisplayPanelFX extends AnchorPane implements ImageDisplayPanel, FXDisplayPanel<FXImageDisplay> {

    private DisplayWindow window;
    private FXImageDisplay display;

    /*
        Nodes
     */
    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label infoLabel;

    @FXML
    private Label pixelValueLabel;

    @FXML
    private HBox buttonHBox;

    @FXML
    private VBox sliderVBox;

    @FXML
    private BorderPane bottomPane;

    @FXML
    private BorderPane topBorderPane;

    /*
        Services
     */
    @Parameter
    TimerService timerService;

    @Parameter
    ImageDisplayService imageDisplayService;

    @Parameter
    ToolService toolService;

    @Parameter
    Context context;

    /*
        Drawing related
     */
    private Canvas canvas;

    private WritableImage buffer;

    Logger logger = ImageJFX.getLogger();

    public ImageDisplayPanelFX() {

        try {
            FXUtilities.injectFXML(this);

            widthProperty().addListener(this::onPanelSizeChanged);
            heightProperty().addListener(this::onPanelSizeChanged);

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void pack() {

        redoLayout();
    }

    private int getViewPortWidth() {
        return new Double(getCanvasWidth()).intValue();
    }

    private int getViewPortHeight() {
        return new Double(getCanvasHeight()).intValue();
    }

    public void view(DisplayWindow window, FXImageDisplay display) {
        this.window = window;
        this.display = display;
    }

    @Override
    public ImageDisplay getDisplay() {
        return display;
    }

    @Override
    public DisplayWindow getWindow() {
        return window;
    }

    @Override
    public void redoLayout() {
        canvas = new Canvas();

        new CanvasListener(display, canvas);

        stackPane.getChildren().add(canvas);
        toolService.setActiveTool(toolService.getTool(HandTool.class));
        redraw();

       ImageDisplayAdjuster adjuster = new ImageDisplayAdjuster(context)
                .addButton("Auto contrast", FontAwesomeIcon.MAGIC, "Calculate the min and max of each channel and adjust their display range accordingly", AutoContrast.class);
        //.addButton("Split channels", FontAwesomeIcon.COPY, "Split all the channels into separate images", SeparateChannels.class)
        //.addButton("Convert to RGB", FontAwesomeIcon.CIRCLE, "Use the current channels settings and create a RGB image.", ChannelMerger.class)

        //.addAction("Spread the settings of this channel...",FontAwesomeIcon.UPLOAD,"Copy the color settings of this channel and apply it to all open images.",SpreadCurrentChannelSettings.class)
        //.addAction("Spread all channels settings...",FontAwesomeIcon.UPLOAD,"Take the color settings of each channels and apply it to all opened images.",SpreadChannelSettings.class);;
       // adjuster.datasetViewProperty().setValue((DefaultFXImageDisplay)display);
        anchorPane.getChildren().add(1, adjuster);
        anchorPane.setTopAnchor(adjuster, 0d);
        anchorPane.setLeftAnchor(adjuster, 0d);
        anchorPane.setRightAnchor(adjuster, 0d);
        
        adjuster.datasetViewProperty().setValue(display);
        
        createColorButtons();

    }

    public DatasetView getDatasetView() {
        try {
            return imageDisplayService.getActiveDatasetView(display);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error when loading the dataset view", e);
        }
        return null;
    }

    @Override
    public void setLabel(String s) {
        Platform.runLater(() -> pixelValueLabel.setText(s));
    }

    @Override
    public void redraw() {

        Platform.runLater(() -> {

            updateViewPort();
            useIjfxRender();
        });
    }

    @Override
    public boolean canView(Display display) {
        return display instanceof ImageDisplay;
    }

    @Override
    public Pane getUIComponent() {
        return this;
    }

    @Override
    public void display(FXImageDisplay t) {
        this.display = t;
        redoLayout();
        redraw();
    }

    private void onPanelSizeChanged(Observable obs, Number oldValue, Number newValue) {
        Platform.runLater(() -> {

            redraw();
        });
    }

    private <T extends RealType<T>> void useIjfxRender() {

        if (timerService == null) {
            return;
        }

        ImageCanvasUtils.checkPosition(getDisplay().getCanvas());

        System.out.println("rendering again");
        final Timer t = timerService.getTimer(this.getClass());
        t.start();
        t.elapsed("since last time");

        final DatasetView view = imageDisplayService.getActiveDatasetView(getDisplay());
        if (view == null) {
            return;
        }
        final ARGBScreenImage screenImage = view.getScreenImage();

        WritableImage buffer = getBuffer(screenImage);

        System.out.println(screenImage.dimension(0));

        t.elapsed("getting screen image");

        final int[] pixels = screenImage.getData();
        final int width = (int) screenImage.dimension(0);
        final int height = (int) screenImage.dimension(1);
        buffer
                .getPixelWriter()
                .setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        ImageCanvas viewport = getDisplay().getCanvas();

        IntCoords panOffset = viewport.getPanOffset();

        final double sx = panOffset.x;
        final double sy = panOffset.y;

        final double sw = 1.0 * viewport.getViewportWidth() / viewport.getZoomFactor();
        final double sh = 1.0 * viewport.getViewportHeight() / viewport.getZoomFactor();

        // the target image (which is the canvas itself
        final double tx = 0;
        final double ty = 0;
        final double tw = canvas.getWidth();
        final double th = canvas.getHeight();

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // drawing the part of the image seen by the camera into
        // the canvas
        canvas
                .getGraphicsContext2D()
                .drawImage(buffer, sx, sy, sw, sh, tx, ty, tw, th);

        t.elapsed("pixel transformation");

    }

    private WritableImage getBuffer(ARGBScreenImage image) {
        if (buffer == null) {
            buffer = new WritableImage((int) image.dimension(0), (int) image.dimension(1));
        } else if (buffer.getWidth() != image.dimension(0) || buffer.getHeight() != image.dimension(1)) {
            buffer = new WritableImage((int) image.dimension(0), (int) image.dimension(1));
        }
        return buffer;
    }

    private double getCanvasWidth() {

        double stackWidth = stackPane.getWidth();
        double displayedWidth = 1.0 * getDisplay().dimension(0) * (0 + getDisplay().getCanvas().getZoomFactor());

        System.out.println(displayedWidth);

        if (stackWidth > displayedWidth) {
            return displayedWidth;
        } else {
            return stackWidth;
        }

    }

    private double getCanvasHeight() {
        double stackPaneHeight = stackPane.getHeight();
        double displayedHeight = 1.0 * getDisplay().dimension(1) * (0 + getDisplay().getCanvas().getZoomFactor());

        if (stackPaneHeight > displayedHeight) {
            return displayedHeight;
        } else {
            return stackPaneHeight;
        }
    }

    private void updateViewPort() {

        canvas.setWidth(getCanvasWidth());
        canvas.setHeight(getCanvasHeight());

        getDisplay().getCanvas().setViewportSize(getViewPortWidth(), getViewPortHeight());

    }

    private DatasetView getDatasetview() {
        return imageDisplayService.getActiveDatasetView(display);
    }
    
    private void createColorButtons() {

        buttonHBox.getChildren().clear();

        if (getDatasetview().getChannelCount() > 1) {

            for (int i = 1; i != getDatasetview().getChannelCount() + 1; i++) {
                LUTSwitchButton button = new LUTSwitchButton(display);
                
                context.inject(button);
                final int channel = i - 1;
                button.imageDisplayProperty().setValue(display);
                button.channelProperty().set(channel);
                buttonHBox.getChildren().add(button);

            }

        }

    }
}
