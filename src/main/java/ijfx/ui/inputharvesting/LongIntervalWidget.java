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
package ijfx.ui.inputharvesting;


import ijfx.core.datamodel.LongInterval;
import ijfx.core.property.ControlableProperty;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.controlsfx.control.RangeSlider;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class)
public class LongIntervalWidget extends AbstractFXInputWidget<LongInterval> {

    Property<LongInterval> interval = new SimpleObjectProperty<LongInterval>();

    Node node;

    public LongIntervalWidget() {
        interval.addListener(this::onIntervalChanged);
    }

    private void onIntervalChanged(Observable value, LongInterval oldValue, LongInterval newValue) {

    }

    public void set(WidgetModel model) {
        super.set(model);

        // Creating the UI Elements
        Label lowValueLabel = new Label();
        Label highValueLabel = new Label();

        RangeSlider rangeSlider = new RangeSlider();

        HBox hbox = new HBox(lowValueLabel, rangeSlider, highValueLabel);

        
        // initializing the properties
        Property<Number> minValueProperty = new ControlableProperty<LongInterval, Number>()
                .setCaller(LongInterval::getMinValue)
                .setBiSetter(this::setMinValue)
                .bindBeanTo(interval);

        Property<Number> maxValueProperty = new ControlableProperty<LongInterval, Number>()
                .setCaller(LongInterval::getMaxValue)
                .setBiSetter(this::setMaxValue)
                .bindBeanTo(interval);

        Property<Number> highValueProperty = new ControlableProperty<LongInterval, Number>()
                .setCaller(LongInterval::getHighValue)
                .setBiSetter(this::setHighValue)
                .bindBeanTo(interval);

        Property<Number> lowValueProperty = new ControlableProperty<LongInterval, Number>()
                .setCaller(LongInterval::getLowValue)
                .setBiSetter(this::setLowValue)
                .bindBeanTo(interval);

        
        // binding the properties
        rangeSlider.lowValueProperty().bindBidirectional(lowValueProperty);
        rangeSlider.highValueProperty().bindBidirectional(highValueProperty);
        rangeSlider.minProperty().bindBidirectional(minValueProperty);
        rangeSlider.maxProperty().bindBidirectional(maxValueProperty);

        
        lowValueLabel.textProperty().bind(rangeSlider.lowValueProperty().asString("%.0f"));
        highValueLabel.textProperty().bind(rangeSlider.highValueProperty().asString("%.0f"));
        
        node = hbox;
        
        bindProperty(interval);
    }

    @Override
    public Node getComponent() {
        return node;
    }

    @Override
    public boolean supports(WidgetModel model) {
        return super.supports(model) && model.isType(LongInterval.class);
    }

    private void setMinValue(LongInterval interval, Number value) {
        interval.setMinValue(value.longValue());
    }

    private void setMaxValue(LongInterval interval, Number value) {
        interval.setMaxValue(value.longValue());
    }

    private void setLowValue(LongInterval interval, Number value) {
        interval.setLowValue(value.longValue());
    }

    private void setHighValue(LongInterval interval, Number value) {
        interval.setHighValue(value.longValue());
    }
}
