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

import ijfx.core.image.DisplayRangeService;
import ijfx.core.overlay.OverlayDrawingService;
import ijfx.core.overlay.OverlayUtilsService;
import ijfx.core.timer.Timer;
import ijfx.core.timer.TimerService;
import ijfx.core.uicontext.UiContextService;
import ijfx.ui.display.overlay.MoveablePoint;
import ijfx.ui.display.overlay.OverlayModifier;
import ijfx.ui.display.tool.HandTool;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.widgets.ImageDisplayAdjuster;
import ijfx.ui.widgets.LUTSwitchButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import mongis.utils.FXUtilities;
import mongis.utils.transition.TransitionBinding;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageCanvas;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.OverlayService;
import net.imagej.display.OverlayView;
import net.imagej.ui.viewer.image.ImageDisplayPanel;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.type.numeric.RealType;
import org.reactfx.EventStreams;
import org.scijava.Context;
import org.scijava.display.Display;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.tool.ToolService;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.util.RealCoords;

/**
 *
 * @author Cyril MONGIS
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

    private AnchorPane modifiersAnchorPane;
    
    private Label fpsLabel = new Label();
    
    private IntegerProperty fpsProperty = new SimpleIntegerProperty();
    
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

    @Parameter
    OverlayDrawingService overlayDrawingService;

    @Parameter
    OverlayUtilsService overlayUtilsService;

    @Parameter
    OverlayService overlayService;
    
    @Parameter
    UiContextService uiContextService;

    @Parameter
    DisplayRangeService displayRangeService;
    
    
    /*
        Drawing related
     */
    private Canvas canvas;

    private CanvasListener canvasListener;

    private WritableImage buffer;

    private OverlayDrawingManager overlayDrawingManager;

    /*
        Extra UI Elements
     */
    private static final Logger logger = ImageJFX.getLogger();

    private ImageDisplayAdjuster adjuster;

    private boolean packed = false;

    private final List<AxisSlider> axisSliderList = new ArrayList<>();

    /*
        Properties
     */
    private final BooleanProperty showTopPanel = new SimpleBooleanProperty(true);

    private final BooleanProperty showBottomPanel = new SimpleBooleanProperty(true);

    private final BooleanProperty anyAxisSliderInUse = new SimpleBooleanProperty(true);

    
    
    
    public ImageDisplayPanelFX() {

        try {
            FXUtilities.injectFXML(this);

            widthProperty().addListener(this::onPanelSizeChanged);
            heightProperty().addListener(this::onPanelSizeChanged);

            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(widthProperty());
            clip.heightProperty().bind(heightProperty().add(-5));

            getStyleClass().add("image-display-pane");
            setClip(clip);

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void pack() {

        // abort if it's been already pack
        if (packed) {
            return;
        }

        installCanvas();
        
     
        toolService.setActiveTool(toolService.getTool(HandTool.class));
        anchorPane.addEventHandler(KeyEvent.ANY, System.out::println);
        setFocusTraversable(false);
       
        
        showBottomPanel
                .bind(
                        anyAxisSliderInUse
                                .or(bottomPane.hoverProperty()));

        pixelValueLabel.setText("For now nothing to show but it's coming");
        
        new TransitionBinding<Number>()
                .bindOnFalse(sliderVBox.heightProperty())
                .setOnTrue(0.0)
                .bind(showBottomPanel, bottomPane.translateYProperty());

        new TransitionBinding<Number>()
                .bindOnFalse(sliderVBox.heightProperty())
                .setOnTrue(0.0)
                .bind(showBottomPanel, bottomPane.translateYProperty());

        
        Label fpsLabel = new Label();
        
        fpsLabel.textProperty().bind(fpsProperty.asString());
        
        
        AnchorPane.setBottomAnchor(fpsLabel, 10d);
        AnchorPane.setRightAnchor(fpsLabel,10d);
        
        modifiersAnchorPane.getChildren().add(fpsLabel);
        modifiersAnchorPane.addEventHandler(MouseEvent.ANY, event->canvas.requestFocus());
        /*
            Creating fixed UI elements and properties
         */
        //.addButton("Split channels", FontAwesomeIcon.COPY, "Split all the channels into separate images", SeparateChannels.class)
        //.addButton("Convert to RGB", FontAwesomeIcon.CIRCLE, "Use the current channels settings and create a RGB image.", ChannelMerger.class)
        //.addAction("Spread the settings of this channel...",FontAwesomeIcon.UPLOAD,"Copy the color settings of this channel and apply it to all open images.",SpreadCurrentChannelSettings.class)
        //.addAction("Spread all channels settings...",FontAwesomeIcon.UPLOAD,"Take the color settings of each channels and apply it to all opened images.",SpreadChannelSettings.class);;
        // adjuster.datasetViewProperty().setValue((DefaultFXImageDisplay)display);
        packed = true;

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
        
        fpsProperty.bind(display.refreshPerSecond());
        
        installCanvas();

    }

    @Override
    public FXImageDisplay getDisplay() {
        return display;
    }

    @Override
    public DisplayWindow getWindow() {
        return window;
    }

    @Override
    public void redoLayout() {

        Platform.runLater(this::redoLayoutSafe);
    }

    public void redoLayoutSafe() {
        // removing the adjuster if exists

        installAdjuster();

        installCanvas();

        // redrawing
        redraw();

        // creating the channel switches
        createChannelSwitchs();

        resetAxisSliders();

    }

    private void installCanvas() {
        if (display != null && canvas == null && canvasListener == null) {
            
            // creates a FX Canvas to draw things inside
            canvas = new Canvas();
            
            // the canvas listener
            // listen for input events modify the display
            // in consequence
            new CanvasListener(display, canvas);
            

            // adding the canvas to an anchorpane
            // so it can be fluid
            modifiersAnchorPane = new AnchorPane();
            modifiersAnchorPane.prefWidthProperty().bind(canvas.widthProperty());
            modifiersAnchorPane.prefHeightProperty().bind(canvas.heightProperty());
            modifiersAnchorPane.setPickOnBounds(false);
            modifiersAnchorPane.maxWidthProperty().bind(canvas.widthProperty());
            modifiersAnchorPane.maxHeightProperty().bind(canvas.heightProperty());
            stackPane.getChildren().addAll(canvas,modifiersAnchorPane);
            
         
        }
    }

    private void installAdjuster() {
        if (adjuster != null) {
            return;
        }

        // creating a new one
        adjuster = new ImageDisplayAdjuster(context);

        // adding it to the AnchorPane
        anchorPane.getChildren().add(1, adjuster);
        anchorPane.setTopAnchor(adjuster, 0d);
        anchorPane.setLeftAnchor(adjuster, 0d);
        anchorPane.setRightAnchor(adjuster, 0d);

        // creating the binding that will display it
        new TransitionBinding<Number>()
                .bindOnFalse(adjuster.heightProperty().multiply(-1.1))
                .setOnTrue(-2)
                .bind(showTopPanel, adjuster.translateYProperty());

        // binding the showTopPanel to it
        showTopPanel.bind(Bindings.or(adjuster.inUseProperty(), topBorderPane.hoverProperty()));

        // tying the new adjuster to the display
        adjuster.imageDisplayProperty().setValue(display);
        adjuster.refresh();
    }

    private void resetAxisSliders() {

        // reseting the list
        sliderVBox.getChildren().clear();
        axisSliderList.clear();

        // creating the sliders
        for (int i = 2; i != display.numDimensions(); i++) {

            AxisSlider slider = new AxisSlider(display, i);

            sliderVBox.getChildren().add(slider);
            axisSliderList.add(slider);
            EventStreams
                    .valuesOf(slider.usedProperty())
                    .feedTo(anyAxisSliderInUse::setValue);
        }

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
        Platform.runLater(() -> infoLabel.setText(s));
    }

    @Override
    public void redraw() {

        Platform.runLater(() -> {

            updateViewPort();
            useIjfxRender();
            axisSliderList
                    .forEach(AxisSlider::refresh);

            redrawOverlays();
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
        
       fpsProperty.bind(t.refreshPerSecond());
        
        redoLayout();
        
        displayRangeService.autoContrast(display);
        for(int i =2 ; i!= display.numDimensions();i++) {
            display.setPosition(0,i);
        }
        display.checkProperties();
        imageDisplayService.getActiveDatasetView(display).getProjector().map();
        redraw();
        
    }

    private void onPanelSizeChanged(Observable obs, Number oldValue, Number newValue) {
        Platform.runLater(this::redraw);
    }

    private <T extends RealType<T>> void useIjfxRender() {

        if (timerService == null) {
            return;
        }

        ImageCanvasUtils.checkPosition(getDisplay().getCanvas());

     
        final Timer t = timerService.getTimer(this.getClass());
        t.start();
        t.elapsed("since last time");

        final DatasetView view = imageDisplayService.getActiveDatasetView(getDisplay());
        if (view == null) {
            return;
        }
        final ARGBScreenImage screenImage = view.getScreenImage();

        WritableImage buffer = getBuffer(screenImage);
    
        t.elapsed("getting screen image");

        final int[] pixels = screenImage.getData();
        final int width = (int) screenImage.dimension(0);
        final int height = (int) screenImage.dimension(1);
        buffer
                .getPixelWriter()
                .setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        ImageCanvas viewport = getDisplay().getCanvas();

        RealCoords center = viewport.getPanCenter();
        
        double zoomFactor = viewport.getZoomFactor();

        final double sx = center.x - (viewport.getViewportWidth() / 2 / zoomFactor);
        final double sy = center.y - (viewport.getViewportHeight() / 2 / zoomFactor);

        final double sw = 1.0 * viewport.getViewportWidth() / zoomFactor;
        final double sh = 1.0 * viewport.getViewportHeight() / zoomFactor;

       
        // the target image (which is the canvas itself
        final double tx = 0;
        final double ty = 0;
        final double tw = canvas.getWidth();
        final double th = canvas.getHeight();

        canvas.getGraphicsContext2D().clearRect(0, 0, tw, th);
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

    private void createChannelSwitchs() {

        if (buttonHBox.getChildren().size() == display.getChannelNumber()) {
            return;
        }

        buttonHBox.getChildren().clear();

        if (getDatasetview().getChannelCount() > 1) {

            for (int i = 1; i != getDatasetview().getChannelCount() + 1; i++) {
                LUTSwitchButton button = new LUTSwitchButton(display);
                final int channel = i - 1;
                button.imageDisplayProperty().setValue(display);
                button.channelProperty().set(channel);
                buttonHBox.getChildren().add(button);
                
               
            }

        }

    }

    private void redrawOverlays() {
        if (overlayDrawingManager == null) {
            overlayDrawingManager = new OverlayDrawingManager(display, canvas);
        }
        overlayDrawingManager.redraw();
        refreshModifiers();
    }

    private void refreshModifiers() {
        
        
        
        // checking modifiers of overlay that has been deleted
        
        try {
        List<Node> points = overlayDrawingManager
                .checkDeletedOverlay(overlayService.getOverlays(display))
                .stream()
                .peek(overlayDrawingManager::delete)
                .map(modifier->modifier.getModifiers(display, null))
                
                .flatMap(List<MoveablePoint>::stream)
                .collect(Collectors.toList());
        
        modifiersAnchorPane.getChildren().removeAll(points);
        
        display
                .stream()
                .filter((view) -> view instanceof OverlayView)
                .map(o -> (OverlayView) o)
                .forEach(this::checkModifier);
        
        }
        catch(Exception e) {
            logger.log(Level.WARNING,"Error when deleting modifier",e);
        }

    }

    private void checkModifier(OverlayView overlayView) {

        OverlayModifier modifier = overlayDrawingManager.getModifier(overlayView.getData());
        if(modifier == null) return;
        List<MoveablePoint> modifiers = modifier.getModifiers(display, overlayView.getData());
        if (overlayView.isSelected()) {
            if (modifiersAnchorPane.getChildren().containsAll(modifiers) == false) {
                modifiersAnchorPane.getChildren().addAll(modifiers);
                 
            }
            modifier.refresh();
        }
        else {
            modifiersAnchorPane.getChildren().removeAll(modifiers);
           
        }
        modifier.refresh();
    }
}
