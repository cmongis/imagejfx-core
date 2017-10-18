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
import ijfx.ui.main.ImageJFX;
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
import mongis.utils.task.FluentTask;
import mongis.utils.uuidmap.DefaultUUIDMap;
import mongis.utils.uuidmap.UUIDMap;
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
 * @author Cyril MONGIS
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

    private Boolean updateLock = Boolean.TRUE;

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
        return uiContextService.isCurrent(UiContexts.SEGMENT);
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

        if (multiPlaneInput) {
            return (IntervalView<? extends RealType<?>>) (RandomAccessibleInterval<T>) imageDisplayService.getActiveDataset(display);
        } else {
            return imagePlaneService.planeView(display);
        }
    }

    private void setSegmentation(FXImageDisplay display, final Class<? extends InteractiveSegmentation> segType) {

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
        getUiWidgets()
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
        
        ImageJFX
                .getThreadQueue()
                .submit(()->updateMask(mask));
    }

    private void updateMask(Img<BitType> mask) {

        synchronized (updateLock) {
            BinaryMaskOverlay overlay = overlayUtilsService.findOverlayOfType(getImageDisplay(), BinaryMaskOverlay.class);

            if (mask == null && overlay != null) {
                overlayUtilsService.removeAllOverlay(getImageDisplay());
            } else {

                overlayUtilsService.updateBinaryMask(getImageDisplay(), mask);
                getImageDisplay().update();
                //overlayUtilsService.updateOverlayView(getImageDisplay(), updateBinaryMask);
            }
        }

    }

    @EventHandler
    public void onImageDisplayChanged(DisplayActivatedEvent event) {

        if (!isActive()) {
            return;
        }
        if (event.getDisplay() instanceof FXImageDisplay) {
            FXImageDisplay imageDisplay = (FXImageDisplay) event.getDisplay();
            setSegmentation(imageDisplay, segmentationChoice.getOrDefault(imageDisplay, NoInteractiveSegmentation.class));
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

        public FluentTask<?, ?> getTask() {

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
    

}
