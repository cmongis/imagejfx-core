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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import net.imagej.widget.HistogramBundle;
import net.imagej.widget.HistogramWidget;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author cyril
 */
@Plugin(type = InputWidget.class)
public class HistogramWidgetFX extends AbstractFXInputWidget<HistogramBundle> implements HistogramWidget<Node> {

    private NumberAxis xAxis;// = new NumberAxis();
    private NumberAxis yAxis;// = new NumberAxis();
    private LineChart<Number, Number> lineChart;

    public void set(WidgetModel model) {
        super.set(model);
        ObjectProperty<HistogramBundle> histogramProperty = new SimpleObjectProperty();

        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        
        lineChart.setPrefWidth(300);
        lineChart.setPrefHeight(300);
        lineChart.setAnimated(false);
        
        bindProperty(histogramProperty);
        histogramProperty.addListener((obs,oldValue,newValue)->updateChart());
    }
    @Override
   public boolean supports(WidgetModel model) {
       return super.supports(model) && model.isType(HistogramBundle.class);
   }
   
   @Override
   public void refreshWidget() {
       super.refreshWidget();
       updateChart();
   }
    
    public void updateChart() {

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        lineChart.getData().clear();

        if (getValue() == null) {
            return;
        }

        for (int h = 0; h < getValue().getHistogramCount(); h++) {
            
            final XYChart.Series series = new XYChart.Series();
            long[] histogram = getValue().getHistogram(h).toLongArray();
            double[] values = new double[histogram.length];

            IntStream.range(0, values.length).forEach(i -> {
                values[i] = histogram[i];
            });
            double epsilon = 10;

            final double[] sampledValues = values;//filter.filter(values);
            

            series.setName("Data " + h + 1);

            List<XYChart.Data<Number, Number>> list = IntStream
                    .range(0, values.length)
                    .mapToObj(i -> new XYChart.Data<Number, Number>(getValue().getMinBin() + i, sampledValues[i]))
                    .collect(Collectors.toList());

            final int histogramId = h;

            lineChart.setCreateSymbols(false);
            series.getData().addAll(list);

            lineChart.getData().add(h, series);

        }

    }

    @Override
    public Node getComponent() {
        return lineChart;
    }

}
