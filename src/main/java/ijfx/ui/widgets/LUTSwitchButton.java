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
package ijfx.ui.widgets;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.core.usage.Usage;
import ijfx.ui.display.image.FXImageDisplay;
import ijfx.ui.main.ImageJFX;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import mongis.utils.bindings.BindingsUtils;
import net.imagej.axis.Axes;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.event.AxisPositionEvent;
import net.imglib2.display.ColorTable;
import net.imglib2.display.ColorTable8;
import net.mongis.usage.UsageLocation;
import org.scijava.command.CommandService;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;

/**
 * Button displaying the brightest color of a color table in a rectangle
 *
 * @author Cyril MONGIS
 */
public class LUTSwitchButton extends Button {

    private Property<FXImageDisplay> imageDisplayProperty = new SimpleObjectProperty();

    private IntegerProperty channelProperty = new SimpleIntegerProperty();

    private static final double RECTANGLE_SIZE_WHEN_CURRENT_CHANNEL = 16;
    private static final double RECTANGLE_SIZE = 20;

    private Rectangle toggleRectangle = new Rectangle(RECTANGLE_SIZE, RECTANGLE_SIZE);

    private final BooleanProperty channelActivatedProperty = new SimpleBooleanProperty();

    private final BooleanProperty channelSelectedProperty = new SimpleBooleanProperty();

    private ContextMenu contextMenu = new ContextMenu();

    private static final UsageLocation TABLE_COLOR_BUTTON = UsageLocation.get("Channel Rectangle");

    private DoubleBinding rectangleSize = Bindings.createDoubleBinding(this::getRectangleSize, channelSelectedProperty);

    @Parameter
    private CommandService commandService;

    @Parameter
    private EventService eventService;

    @Parameter
    private ImageDisplayService imageDisplayService;

    private Rectangle selector = new Rectangle();

    private VBox vbox = new VBox();

    public LUTSwitchButton(FXImageDisplay display) {
        super();
        display.getContext().inject(this);
        toggleRectangle.getStyleClass().add("rectangle");
        selector.getStyleClass().add("selector");
        selector.setWidth(RECTANGLE_SIZE);
        selector.setHeight(RECTANGLE_SIZE / 2);
        vbox.getChildren().addAll(toggleRectangle, selector);
        vbox.getStyleClass().add("container");
        getStyleClass().add("color-button");
        setGraphic(vbox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        imageDisplayProperty.setValue(display);
        Usage.listenButton(this, TABLE_COLOR_BUTTON, "Channel switch");

        channelActivatedProperty.setValue(Boolean.TRUE);

        channelActivatedProperty.bind(Bindings.createBooleanBinding(() -> getDisplay().isChannelComposite(getChannelId()), getDisplay().compositeChannelsProperty()));
        channelSelectedProperty.bind(Bindings.createBooleanBinding(() -> getDisplay().getCurrentChannel() == getChannelId(), getDisplay().currentChannelProperty()));

        toggleRectangle.fillProperty().bind(Bindings.createObjectBinding(this::getColor, getDisplay().currentLUTProperty(), getDisplay().currentChannelProperty(), channelActivatedProperty));

        selector.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onChannelSelectorClicked);
        toggleRectangle.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onChannelToggleClicked);

        PseudoClass currentChannelPseudoClass = PseudoClass.getPseudoClass("current-channel");
        PseudoClass selectedPseudoClass = PseudoClass.getPseudoClass("selected");

        BindingsUtils.bindNodeToPseudoClass(currentChannelPseudoClass, this, channelSelectedProperty);
        BindingsUtils.bindNodeToPseudoClass(selectedPseudoClass, this, channelActivatedProperty);

        setContextMenu(contextMenu);
        addAction("Edit this channel", FontAwesomeIcon.COG, this::editThisChannel);
        addAction("Only display this channel", FontAwesomeIcon.EYE, this::displayOnlyThis);
        addAction("Isolate this channel", FontAwesomeIcon.COPY, this::isolateChannel);

        setTooltip(new Tooltip("The first click select the channel, the second click activate or deactivate it."));

    }

    private void onChannelSelectorClicked(MouseEvent event) {

        setAsCurrentChannel();
        event.consume();
    }

    private void onChannelToggleClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            toggle();
            event.consume();
        }
    }

    private long getCloser(long currentId, long min, long max) {

        if (currentId == 0) {
            return currentId + 1;
        }
        if (currentId == max) {
            return currentId - 1;
        }
        return currentId + 1;

    }

    private void toggle() {

        getDisplay().toggleChannelComposite(getChannelId());
        getDisplay().updateAsync();

    }

    public Property<FXImageDisplay> imageDisplayProperty() {
        return imageDisplayProperty;
    }

    private FXImageDisplay getDisplay() {
        return imageDisplayProperty().getValue();
    }

    public IntegerProperty channelProperty() {
        return channelProperty;
    }

    private DatasetView getCurrentView() {
        return imageDisplayService.getActiveDatasetView(getDisplay());
    }

    private Color getCurrentBrightestColor() {

        DatasetView view = getCurrentView();
        int channelId = channelProperty.get();
        if (channelId == -1 || view == null) {
            return Color.BLACK;
        }

        if (channelId >= view.getColorTables().size()) {
            return Color.BLACK;
        }

        return getBrighterColor(view.getColorTables().get(channelId));
    }

    private Boolean isSelected() {
        return getDisplay().isChannelComposite(getChannelId());
    }

    private Paint getColor() {
        if (!isSelected()) {
            return Color.BLACK;
        } else {
            return getCurrentBrightestColor();
        }

    }

    private Boolean isActivated(DatasetView view) {
        if (view == null) {
            return false;
        }
        return view.getProjector().isComposite(channelProperty().get());

    }

    private void setAsCurrentChannel() {
        getDisplay().setCurrentChannel(getChannelId());
        getDisplay().updateAsync();
    }

    private Boolean isCurrentChannel() {
        if (getCurrentView() == null) {
            return false;
        }
        return getCurrentView().getLongPosition(Axes.CHANNEL) == (long) getChannelId();
    }

    private int getChannelId() {
        return channelProperty().get();
    }

    private Color getBrighterColor(ColorTable colorTable) {

        if (colorTable instanceof ColorTable8) {
            return getBrighterColor((ColorTable8) colorTable);
        } else {
            return Color.BLACK;
        }

    }

    private Color getBrighterColor(ColorTable8 colorTable) {

        double red = colorTable.get(ColorTable.RED, 255);
        double green = colorTable.get(ColorTable.GREEN, 255);
        double blue = colorTable.get(ColorTable.BLUE, 255);

        return new Color(red / 255, green / 255, blue / 255, 1.0);

    }

    public void addAction(String text, FontAwesomeIcon icon, Runnable action) {

        MenuItem item = new MenuItem(text, GlyphsDude.createIcon(icon));
        item.setOnAction(event -> action.run());
        contextMenu.getItems().add(item);
    }

    public void emitAxisEvent() {

        imageDisplayService
                .getImageDisplays()
                .stream()
                .filter(display -> display.contains(getCurrentView()))
                .map(AxisPositionEvent::new)
                .forEach(eventService::publish);

    }

    private void displayOnlyThis() {

        DatasetView view = getCurrentView();
        int channel = channelProperty().get();

        for (int i = 0; i != view.getChannelCount(); i++) {

            view.getProjector().setComposite(i, i == channel);
        }
        view.setPosition(channel, Axes.CHANNEL);
        view.getProjector().map();
        view.update();
        ImageJFX.getThreadPool().execute(this::emitAxisEvent);

    }

    private void isolateChannel() {

        if (commandService == null) {
            imageDisplayProperty().getValue().context().inject(this);
        }

    }

    private void editThisChannel() {
        imageDisplayProperty().getValue().setPosition(channelProperty().intValue(), Axes.CHANNEL);
        imageDisplayProperty().getValue().update();
    }

    private Double getRectangleSize() {
        return isCurrentChannel() && isSelected() ? RECTANGLE_SIZE_WHEN_CURRENT_CHANNEL : RECTANGLE_SIZE;
    }

}
