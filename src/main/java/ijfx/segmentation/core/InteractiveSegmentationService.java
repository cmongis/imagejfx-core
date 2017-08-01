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
package ijfx.segmentation.core;

import ijfx.core.IjfxService;
import ijfx.core.batch.BatchSingleInput;
import ijfx.core.image.ImagePlaneService;
import ijfx.core.imagedb.MetaDataExtractionService;
import ijfx.core.metadata.MetaDataSetDisplayService;
import ijfx.core.overlay.MeasurementService;
import ijfx.core.overlay.OverlayStatService;
import ijfx.core.overlay.OverlayUtilsService;
import ijfx.core.segmentation.SegmentationService;
import ijfx.core.uicontext.UiContextService;
import ijfx.core.workflow.Workflow;
import ijfx.core.workflow.WorkflowBuilder;
import ijfx.explorer.ExplorerService;
import ijfx.ui.UiContexts;
import ijfx.ui.display.image.FXImageDisplay;
import ijfx.ui.loading.LoadingScreenService;
import io.scif.services.DatasetIOService;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import mongis.utils.CallbackTask;
import mongis.utils.DefaultUUIDMap;
import mongis.utils.UUIDMap;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.event.AxisPositionEvent;
import net.imagej.overlay.BinaryMaskOverlay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import org.scijava.Priority;
import org.scijava.display.event.DisplayActivatedEvent;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 *
 * @author cyril
 */
@Plugin(type = Service.class)
public class InteractiveSegmentationService extends AbstractService implements IjfxService {

    
    InteractiveSegmentation currentSegmentation;

    @Parameter
    ImageDisplayService imageDisplayService;

    @Parameter
    OverlayUtilsService overlayUtilsService;

    @Parameter
    SegmentationService segmentationService;

    @Parameter
    MeasurementService measurementService;

    @Parameter
    MetaDataExtractionService metaDataSrv;

    @Parameter
    LoadingScreenService loadingScreenService;

    @Parameter
    UiContextService uiContextService;

    
    @Parameter
    ExplorerService explorerService;

    @Parameter
    ImagePlaneService imagePlaneService;

    @Parameter
    OverlayStatService overlayStatService;

    @Parameter
    DatasetIOService datasetIoService;
    
    @Parameter
    MetaDataSetDisplayService metaDataDisplayService;

    private boolean multiPlaneInput = false;
    
    
    private final UUIDMap segmentationMap = new DefaultUUIDMap<InteractiveSegmentation>();

    private final Map<FXImageDisplay, Class<? extends InteractiveSegmentation>> segmentationChoice = new WeakHashMap<>();

    @Parameter
    private PluginService pluginService;

    private final Property<Img<BitType>> maskProperty = new SimpleObjectProperty<>();

    private List<InteractiveSegmentationUI> uiWidgetList;

    private InteractiveSegmentationPanel view;

    @Override
    public void initialize() {
        maskProperty.addListener(this::onMaskChanged);
    }

    public ImageDisplay getImageDisplay() {
        return imageDisplayService.getActiveImageDisplay();
    }

    public List<InteractiveSegmentationUI> getUiWidgets() {
        if (uiWidgetList == null) {
            uiWidgetList = pluginService.createInstancesOfType(InteractiveSegmentationUI.class);
        }
        return uiWidgetList;
    }

    protected boolean isActive() {
        return true;
    }

    public void addPanel(InteractiveSegmentationPanel panel) {
        view = panel;
    }

    public void setSegmentation(Class<? extends InteractiveSegmentation> segType) {

        if (getImageDisplay() == null) {

            return;

        }

        FXImageDisplay display = getCurrentImageDisplay();

        setSegmentation(display, segType);

    }

    public Workflow getWorkflow() {
        return currentSegmentation.getWorkflow();
    }
    
    private FXImageDisplay getCurrentImageDisplay() {
        return (FXImageDisplay) imageDisplayService.getActiveImageDisplay();
    }
    
    private <T> IntervalView<? extends RealType<?>> getCurrentExample() {
        
        FXImageDisplay display = getCurrentImageDisplay();
        
        if(multiPlaneInput) {
            return (IntervalView<? extends RealType<?>>) (RandomAccessibleInterval<T>) imageDisplayService.getActiveDataset(display);
        }
        else {
            return imagePlaneService.planeView(display);
        }
    }

    private void setSegmentation(FXImageDisplay display, Class<? extends InteractiveSegmentation> segType) {

        segmentationChoice.put(display, segType);

        if (segType == NoInteractiveSegmentation.class) {
            maskProperty.unbind();
            maskProperty.setValue(null);
            currentSegmentation = InteractiveSegmentation.NONE;
        } else {
            InteractiveSegmentation segmentation = (InteractiveSegmentation) segmentationMap
                    .key(segType, display)
                    .getOrPutFrom(() -> createSegmentation(segType));

            maskProperty.bind(segmentation.maskProperty());
            currentSegmentation = segmentation;
            currentSegmentation.preview(getCurrentExample());
        }

        updateViews();

    }

    private Class<? extends InteractiveSegmentation> getSegmentationType(FXImageDisplay display) {
        if (segmentationChoice.containsKey(display) == false || segmentationChoice.get(display) == null) {
            segmentationChoice.put(display, NoInteractiveSegmentation.class);
        }
        return segmentationChoice.get(display);
    }

    protected <T extends InteractiveSegmentation> T createSegmentation(Class<T> clazz) {
        try {

            T newInstance = clazz.newInstance();
            getContext().inject(newInstance);

            return newInstance;
        } catch (InstantiationException ex) {
            Logger.getLogger(InteractiveSegmentationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(InteractiveSegmentationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected void updateViews() {
        // first refresh the widget
        uiWidgetList
                .stream()
                .filter(widget -> widget.supports(currentSegmentation))
                .filter(widget -> widget.getCurrentSegmentation() != currentSegmentation)
                .forEach(widget -> widget.bind(currentSegmentation));

        // refresh the widget displayer
        if (view != null) {
            view.refresh();
        }

    }

    public void onMaskChanged(Observable obs, Img<BitType> oldValue, Img<BitType> mask) {
        BinaryMaskOverlay overlay = overlayUtilsService.findOverlayOfType(getImageDisplay(), BinaryMaskOverlay.class);
        if (mask == null && overlay != null) {
            overlayUtilsService.removeAllOverlay(getImageDisplay());
        } else {
            BinaryMaskOverlay updateBinaryMask = overlayUtilsService.updateBinaryMask(getImageDisplay(), mask);
            getImageDisplay().update();
            //overlayUtilsService.updateOverlayView(getImageDisplay(), updateBinaryMask);
        }
    }

    @EventHandler
    public void onImageDisplayChanged(DisplayActivatedEvent event) {

        if (!isActive()) {
            return;
        }
        if (event.getDisplay() instanceof FXImageDisplay) {
            FXImageDisplay imageDisplay = (FXImageDisplay) event.getDisplay();
            setSegmentation(imageDisplay, segmentationChoice.get(imageDisplay));
        }
    }

    @EventHandler
    public void onDataViewUpdated(AxisPositionEvent event) {
        if (isActive() && event.getDisplay() == getImageDisplay()) {
            currentSegmentation.preview(getCurrentExample());
            //currentSegmentation.refresh();
        }
    }

    public void setActive(boolean active) {
        if (active) {

        }
    }

    public Class<? extends InteractiveSegmentation> getCurrentSegmentationType() {
        return getSegmentationType(getCurrentImageDisplay());
    }

    public Img<BitType> getMask() {
        return currentSegmentation.maskProperty().getValue();
    }

    /*
     *   Actions 
     */
    private boolean isExplorer() {
        return uiContextService.isCurrent(UiContexts.EXPLORE);
    }
    
    /*
    public void analyseParticles() {

        if (isExplorer()) {
            new CallbackTask<Workflow, Boolean>().
                    setInput(getWorkflow())
                    .run(segmentationService::measureFromExplorer)
                    .submit(loadingScreenService)
                    .start();
        } else {
            new CallbackTask<Object, Object>()
                    .runLongCallable(progress -> {
                        progress.setProgress(1, 3);

                        measurementService.measureOverlays(getImageDisplay(), getMask(), o -> true);

                        return null;
                    })
                    .submit(loadingScreenService)
                    .start();
        }
    }*/

    public class SegmentationWorkflowBuilder {

        private Workflow workflow;

        private Callable<List<BatchSingleInput>> inputGenerator;

        private List<BatchSingleInput> batchSingleInput;

        private Consumer<BatchSingleInput> forEachInput;

        public SegmentationWorkflowBuilder setForEachInput(Consumer<BatchSingleInput> forEachInput) {
            this.forEachInput = forEachInput;
            return this;
        }

        public SegmentationWorkflowBuilder execute(Workflow workflow) {
            this.workflow = workflow;
            return this;
        }

        public SegmentationWorkflowBuilder generateFrom(Callable<List<BatchSingleInput>> inputGenerator) {
            this.inputGenerator = inputGenerator;
            return this;
        }

        public CallbackTask<?, ?> getTask() {

            if (batchSingleInput == null) {
                try {
                    batchSingleInput = inputGenerator.call();
                } catch (Exception ex) {
                    Logger.getLogger(InteractiveSegmentationService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return new WorkflowBuilder(getContext())
                    .addInput(batchSingleInput)
                    .execute(workflow)
                    .then(forEachInput)
                    .getTask();
        }

    }
    /*
    @FunctionalInterface
    public interface InputProvider extends Callable<List<SegmentationBatchInput>> {

    }

    @FunctionalInterface
    public interface OutputHandler extends Consumer<SegmentedObject> {

    }

    public final InputProvider MEASURE_CURRENT_DISPLAY_PLANE = () -> {

        ImageDisplay display = getImageDisplay();
        SegmentationBatchInput input = new SegmentationBatchInput(display, true)
                .inject(getContext());
        final Dataset source = input.getDataset().duplicate();
        input.setSourceSupplier(() -> source);
        input.setResultHandler(new ObjectDisplayer("Segmentation of "+display.getName()));
        return Lists.newArrayList(input);
    };

    public final InputProvider MEASURE_EACH_PLANE_USING_CURRENT_PLANE = () -> {

        final ImageDisplay display = getImageDisplay();
        SegmentationBatchInput input = new SegmentationBatchInput(display,true);
        input.setSourceSupplier(()->imageDisplayService.getActiveDataset(display));
        input.setResultHandler(new ObjectDisplayer("Measures of "+display.getName()));
        
        return Lists.newArrayList(input);

    };
    
    public final InputProvider MEASURE_EACH_PLANE = ()->{
        
        
         final Dataset dataset = imageDisplayService.getActiveDataset();

         ObjectDisplayer displayer = new ObjectDisplayer("Measures of "+dataset.getName());
         
        // DatasetPlaneWrapper copies the indicate position from the original dataset before
        // the beginning of the process;
        List<SegmentationBatchInput> inputList = Stream
                .of(DimensionUtils.allPossibilities(dataset))
                .map(position->new SegmentationBatchInput(new DatasetPlaneWrapper(getContext(), dataset, position),true))
                .peek(input->input.setResultHandler(displayer))
                .collect(Collectors.toList());
        
         
        return inputList;
        
    };
    
    public final InputProvider SEGMENT_FROM_EXPLORER_AND_MEASURE_SOURCE = ()->{
      
        return explorerService
                .getSelectedItems()
                .stream()
                .map(explorable->new ExplorableBatchInputWrapper(explorable))
                .map(input->new SegmentationBatchInput(input,input.getMetaDataSet().isType(MetaDataSetType.PLANE)))
                .peek(input->input.getMetaDataSet().isType(MetaDataSet.setSourceSupplier(new ExplorableSourceLoader(input))))
                .collect(Collectors.toList());
        
        
    };

    private class ExplorableSourceLoader implements Supplier<Dataset>{
        final BatchSingleInput input;

        public ExplorableSourceLoader(BatchSingleInput input) {
            this.input = input;
            
            if(input.getMetaDataSet().getType().equals(MetaDataSetType.PLANE)) {
                
            }
            
        }

        
        public Dataset get() {
            try {
                return datasetIoService.open(input.getSourceFile());
            } catch (IOException ex) {
                return null;
            }
        };
    }
    
    private List<BatchSingleInput> getCurrentPlaneFromDisplay() {

    }

    private List<BatchSingleInput> getEachPlaneFromDisplay() {
        ImageDisplay imageDisplay = imageDisplayService.getActiveImageDisplay();

        final Dataset dataset = imageDisplayService.getActiveDataset(imageDisplay);
        long[][] planes = DimensionUtils.allPossibilities(imageDisplay);

        return Stream
                .of(planes)
                .map(position -> {

                    Dataset isolatedPlane = imagePlaneService.isolatePlane(dataset, position);

                    BatchSingleInput input = new BatchInputBuilder(getContext())
                            .from(isolatedPlane)
                            .getInput();

                    MetaDataSet set = input.getMetaDataSet();
                    set.setType(MetaDataSetType.PLANE);
                    set.putGeneric(MetaData.NAME, imageDisplay.getName());
                    metaDataSrv.fillPositionMetaData(set, AxisUtils.getAxes(dataset), DimensionUtils.planarToAbsolute(position));
                    return input;

                })
                .collect(Collectors.toList());
    }
    
    public final InputProvider FROM_EXPLORER = () -> {

        return explorerService
                .getSelectedItems()
                .stream()
                .map(ExplorableBatchInputWrapper::new)
                .collect(Collectors.toList());

    };

    public final OutputHandler MEASURE = input -> {

    };

    public final OutputHandler ADD_TO_FOLDER = input -> {

    };

    private class ObjectDisplayer implements Consumer<List<SegmentedObject>> {

        final DefaultObjectDisplay display;

        public ObjectDisplayer(String displayName) {
            display = new DefaultObjectDisplay();
            display.setName(displayName);

        }

        @Override
        public void accept(List<SegmentedObject> t) {
            display.addAll(t);
        }

    }

    public final OutputHandler SEGMENT_AND_MEASURE_ALL_PLANES = input -> {

        long[][] planePossibilities = DimensionUtils.allPossibilities(getCurrentImageDisplay());

        List<? extends Overlay> overlays = getOverlays(progress);

        // progress parameters
        int total = overlays.size() * planePossibilities.length;
        int i = 0;

        Dataset dataset = imageDisplayService.getActiveDataset();
        List<MetaDataSet> result = new ArrayList<>();

        String displayName = input.getDisplay().getName();

        for (long[] position : planePossibilities) {
            position = DimensionUtils.planarToAbsolute(position);
            for (Overlay overlay : overlays) {
                MetaDataSet set = new MetaDataSet(MetaDataSetType.OBJECT);
                set.merge(input.getMetaDataSet());
                OverlayStatistics statistics = overlayStatService.getStatistics(overlay, dataset, position);
                set.merge(overlayStatService.getStatisticsAsMap(statistics));
                result.add(set);
            }
        }

        metaDataDisplayService.findDisplay(String.format("Measure per plane for %s", displayName)).addAll(result);

    };
    */

}
