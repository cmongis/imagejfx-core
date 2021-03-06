/*
 * /*
 *     This file is part of ImageJ FX.
 *
 *     ImageJ FX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ImageJ FX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * 	Copyright 2015,2016 Cyril MONGIS, Michael Knop
 *
 */
package ijfx.ui.plugin.overlay;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.core.events.SciJavaEventBus;
import ijfx.core.overlay.MeasurementService;
import ijfx.core.overlay.OverlaySelectedEvent;
import ijfx.core.overlay.OverlaySelectionService;
import ijfx.core.overlay.OverlayStatService;
import ijfx.core.timer.Timer;
import ijfx.core.timer.TimerService;
import ijfx.core.uiplugin.Localization;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import net.imagej.DatasetService;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.OverlayService;
import org.scijava.display.DisplayService;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import mongis.utils.FXUtilities;
import ijfx.ui.UiPlugin;
import ijfx.ui.UiConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import mongis.utils.task.FluentTask;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.OverlayView;
import net.imagej.display.event.DataViewUpdatedEvent;
import net.imagej.event.OverlayUpdatedEvent;
import net.imagej.overlay.LineOverlay;
import net.imagej.overlay.Overlay;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.controlsfx.control.PopOver;
import org.scijava.Context;
import org.scijava.display.event.DisplayActivatedEvent;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.display.event.DisplayUpdatedEvent;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;

/**
 *
 * @author Cyril MONGIS, 2015
 */
@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "overlayPanel", localization = Localization.RIGHT, context = "imagej+overlay-selected")
public class OverlayPanel extends BorderPane implements UiPlugin {

    @Parameter
    OverlayService overlayService;

    @Parameter
    OverlaySelectionService overlaySelectionService;

    @Parameter
    OverlayStatService statsService;

    @Parameter
    DatasetService datasetService;

    @Parameter
    ImageDisplayService imageDisplayService;

    @Parameter
    DisplayService displayService;

    @Parameter
    TimerService timerService;

    @Parameter
    MeasurementService measurementSrv;

    @Parameter
    Context context;

    @FXML
    TableView<MyEntry> tableView;

    @FXML
    TableColumn<MyEntry, String> keyColumn;

    @FXML
    TableColumn<MyEntry, Double> valueColumn;

    ObservableList<MyEntry> entries = FXCollections.observableArrayList();

    @FXML
    TextField overlayNameField;

    @FXML
    LineChart<Double, Double> lineChart;

    @FXML
    AreaChart<Double, Double> areaChart;

    @FXML
    VBox chartVBox;

    @FXML
    BorderPane chartBorderPane;

    @FXML
    TitledPane chartTitledPane;

    SciJavaEventBus eventBus = new SciJavaEventBus();

    PopOver optionsPane;

    OverlayOptions overlayOptions;

    Text gearIcon;

    int TEXT_GAP = 160;
    double BASAL_OPACITY = 0.4;

    private final static String EMPTY_FIELD = "Name your overlay here :-)";

    ObjectProperty<Overlay> overlayProperty = new SimpleObjectProperty<>();

    public OverlayPanel() throws IOException {
        super();

        FXUtilities.injectFXML(this);

        keyColumn.setCellValueFactory(new PropertyValueFactory("name"));
        valueColumn.setCellValueFactory(new PropertyValueFactory("value"));

        tableView.setItems(entries);

        entries.add(new MyEntry("nothing", 0d));

        overlayNameField.setPromptText(EMPTY_FIELD);
        overlayNameField.textProperty().addListener(this::onOverlayNameChanged);

        overlayProperty.addListener(this::onOverlaySelectionChanged);

        chartVBox.getChildren().removeAll(areaChart, lineChart);

        overlayOptions = new OverlayOptions();

        optionsPane = new PopOver(overlayOptions);
        optionsPane.setArrowLocation(PopOver.ArrowLocation.RIGHT_TOP);
        optionsPane.setDetachable(true);
        optionsPane.setAutoHide(false);
        optionsPane.titleProperty().setValue("Overlay settings");
        optionsPane.setConsumeAutoHidingEvents(false);

        gearIcon = GlyphsDude.createIcon(FontAwesomeIcon.GEAR, "15");
        gearIcon.setOpacity(BASAL_OPACITY);
        gearIcon.setOnMouseEntered(e -> gearIcon.setOpacity(1.0));
        gearIcon.setOnMouseExited(e -> gearIcon.setOpacity(BASAL_OPACITY));
        gearIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onGearClicked);
        gearIcon.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            e.consume();
        });
        gearIcon.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            e.consume();
        });

        chartTitledPane.setGraphic(gearIcon);
        chartTitledPane.setContentDisplay(ContentDisplay.RIGHT);
        chartTitledPane.graphicTextGapProperty().setValue(TEXT_GAP);

        // channeling all the DataViewUpdatedEvent so it updates the
        // current selected overlay
        eventBus.getStream(OverlayUpdatedEvent.class)
                .filter(event -> event.getObject().equals(overlayProperty.getValue()))
                .subscribe(event -> Platform.runLater(this::updateStats));

        eventBus.getStream(DataViewUpdatedEvent.class)
                .map(event -> event.getView())
                .filter(view -> view instanceof OverlayView)
                .cast(OverlayView.class)
                .filter(view -> view.isSelected())
                .throttleWithTimeout(1000, TimeUnit.MILLISECONDS)
                .subscribe(overlay -> {
                    overlayProperty.setValue(overlay.getData());
                });

        eventBus.getStream(DataViewUpdatedEvent.class)
                .filter(event -> event.getView() instanceof DatasetView)
                .map(event -> event.getView())
                .cast(DatasetView.class)
                .map(view -> {
                    return view;
                })
                .filter(view -> currentDisplay().contains(view))
                .throttleWithTimeout(100, TimeUnit.MILLISECONDS)
                .subscribe(event -> Platform.runLater(this::updateStats));
        eventBus.getStream(DisplayUpdatedEvent.class)
                .filter(event -> event.getDisplay() instanceof ImageDisplay)
                .subscribe(this::checkCurrentOverlay);
    }

    ImageDisplay imageDisplay;

    public void onOverlaySelectionChanged() {

        if (imageDisplay == null) {
            return;
        }

        // check if multiple
        // if multiple, do statistics with all and hide editable fields
        // else do statistic with one
        // update stats entry
        // calculate new statistics
        // update view
        //
    }

    public ImageDisplay currentDisplay() {
        return displayService.getActiveDisplay(ImageDisplay.class);
    }

    @EventHandler
    public void handleEvent(OverlaySelectedEvent event) {

        if (event.getOverlay() == null) {
            return;
        }

        overlayProperty.setValue(event.getOverlay());
        Platform.runLater(() -> updateChart(event.getOverlay()));
        updateTable();

    }

    public void updateStats() {
        if (overlayProperty.getValue() == null) {
            return;
        }
        updateChart(overlayProperty.getValue());
        updateTable();
    }

    protected void updateTable() {

        new FluentTask<Overlay, Map<String, Double>>()
                .setInput(overlayProperty.getValue())
                .callback(overlay -> {
                    return statsService.getStatisticsAsMap(imageDisplay, overlay);
                })
                .then(map -> {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(
                            map.entrySet()
                                    .stream()
                                    .map(entry -> new MyEntry(entry.getKey(), entry.getValue()))
                                    .collect(Collectors.toList())
                    );

                })
                .start();

    }

    @EventHandler
    public void onActiveDisplayChanged(DisplayActivatedEvent event) {
        if (event.getDisplay() instanceof ImageDisplay) {
            imageDisplay = (ImageDisplay) event.getDisplay();
        }

    }

    private void updateCurrentOverlay() {
        if (imageDisplay != null) {
            List<Overlay> selectedOverlays = overlaySelectionService.getSelectedOverlays(imageDisplay);
            if (selectedOverlays.size() > 0) {
                overlayProperty.setValue(selectedOverlays.get(0));
            }
        } else {
            overlayProperty.setValue(null);
        }
    }

    @EventHandler
    public void onDisplayUpdatedEvent(DisplayUpdatedEvent event) {
        eventBus.channel(event);
    }

    @EventHandler
    public void onImageDisplayClosed(DisplayDeletedEvent event) {

        if (event instanceof ImageDisplay) {
            imageDisplay = null;
        }

        updateCurrentOverlay();

    }

    @EventHandler
    public void onDataViewUpdated(DataViewUpdatedEvent event) {

        eventBus.channel(event);

    }

    @EventHandler
    public void onOverlayUpdated(OverlayUpdatedEvent event) {
        eventBus.channel(event);
    }

    private void checkCurrentOverlay(Object o) {

        Overlay selectedOverlay = overlaySelectionService.getSelectedOverlay(imageDisplayService.getActiveImageDisplay());

        if (selectedOverlay != overlayProperty.getValue()) {
            overlayProperty.setValue(selectedOverlay);
        }
    }

    private void updateChart(Overlay overlay) {

        boolean isLineOverlay = overlay instanceof LineOverlay;

        if (isLineOverlay) {
            chartBorderPane.setCenter(lineChart);
        } else {
            chartBorderPane.setCenter(areaChart);
        }

        if (isLineOverlay) {

            updateLineChart((LineOverlay) overlay);
        } else {
            updateAreaChart(overlay);
        }
    }

    /*
        Area Chart related methods
     */
    private void updateAreaChart(Overlay overlay) {

        Timer timer = timerService.getTimer(this.getClass());

        new FluentTask<Overlay, XYChart.Series<Double, Double>>()
                .setInput(overlay)
                .callback(this::getOverlayHistogram)
                .then(serie -> {

                    timer.start();
                    areaChart.getData().clear();
                    areaChart.getData().add(serie);
                    timer.elapsed("Area Chart rendering");

                    timer.logAll();

                })
                .start();

    }

    protected XYChart.Series<Double, Double> getOverlayHistogram(Overlay overlay) {

        Timer timer = timerService.getTimer(this.getClass());
        timer.start();
        Double[] valueList = statsService.getValueListFromImageDisplay(currentDisplay(), overlay);
        timer.elapsed("Getting the stats");
        SummaryStatistics sumup = new SummaryStatistics();
        for (Double v : valueList) {
            sumup.addValue(v);
        }
        timer.elapsed("Building the sumup");

        double min = sumup.getMin();
        double max = sumup.getMax();
        double range = max - min;
        int bins = 100;//new Double(max - min).intValue();

        EmpiricalDistribution distribution = new EmpiricalDistribution(bins);

        double[] values = ArrayUtils.toPrimitive(valueList);
        Arrays.parallelSort(values);
        distribution.load(values);

        timer.elapsed("Sort and distrubution repartition up");

        XYChart.Series<Double, Double> serie = new XYChart.Series<>();
        ArrayList<Data<Double, Double>> data = new ArrayList<>(bins);
        double k = min;
        for (SummaryStatistics st : distribution.getBinStats()) {
            data.add(new Data<Double, Double>(k, new Double(st.getN())));
            k += range / bins;
        }

        serie.getData().clear();
        serie.getData().addAll(data);
        timer.elapsed("Creating charts");
        return serie;
    }

    /*
    
        Line Chart related methods
     */
    private void updateLineChart(LineOverlay overlay) {
        new FluentTask<Overlay, XYChart.Series<Double, Double>>()
                .setInput(overlay)
                .callback(this::getLineChartSerie)
                .then(this::updateLineChart)
                .start();
    }

    private void updateLineChart(XYChart.Series<Double, Double> serie) {
        lineChart.getData().clear();
        lineChart.getData().add(serie);
    }

    protected XYChart.Series<Double, Double> getLineChartSerie(Overlay overlay) {
        Double[] valueList = statsService.getValueListFromImageDisplay(currentDisplay(), overlay);

        ArrayList<Data<Double, Double>> data = new ArrayList<>(valueList.length);
        for (int i = 0; i != valueList.length; i++) {
            data.add(new Data<>(new Double(i), valueList[i]));
        }

        XYChart.Series<Double, Double> serie = new XYChart.Series<>();
        serie.getData().addAll(data);
        return serie;
    }

    @Parameter
    EventService eventService;

    @FXML
    public void deleteOverlay() {

        ImageDisplay display = displayService.getActiveDisplay(ImageDisplay.class);

        overlaySelectionService.getSelectedOverlays(display).forEach(overlay -> {
            overlayService.removeOverlay(display, overlay);
            //eventService.publishLater(new OverlayDeletedEvent(overlay));
        });

    }

    @FXML
    public void measure() {

        measurementSrv.measureSelectedOverlay();

    }

    @Override
    public Node getUiElement() {
        return this;
    }

    @Override
    public UiPlugin init() {
        context.inject(overlayOptions);
        return this;
    }

    public class MyEntry {

        protected String name;
        protected Double value;

        public MyEntry(java.lang.String name, Double value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }
    }

    public void onOverlaySelectionChanged(Observable obs, Overlay oldValue, Overlay newValue) {
        Platform.runLater(() -> {
            if (newValue != null) {
                overlayNameField.setText(newValue.getName());
                updateChart(newValue);
                updateTable();
            }
        });
    }

    public void onOverlayNameChanged(Observable obs, String oldText, String newText) {
        if (overlayProperty.getValue() != null) {
            overlayProperty.getValue().setName(newText);
        }
    }

    public void onGearClicked(MouseEvent e) {
        if (!optionsPane.isShowing()) {
            RotateTransition rotate = new RotateTransition(Duration.millis(500), gearIcon);
            rotate.setByAngle(180);
            rotate.play();
            optionsPane.show(gearIcon);
        } else {
            RotateTransition rotate = new RotateTransition(Duration.millis(500), gearIcon);
            rotate.setByAngle(-180);
            rotate.play();
            optionsPane.hide();
        }

        e.consume();
    }

}
