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
package ijfx.core.segmentation;

import com.google.common.collect.Lists;
import ijfx.commands.binary.BinaryToOverlay;
import ijfx.core.batch.item.BatchItemBuilder;
import ijfx.core.image.ImagePlaneService;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataService;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.overlay.MeasurementService;
import ijfx.core.overlay.OverlayUtilsService;
import ijfx.core.utils.DimensionUtils;
import ijfx.core.workflow.DefaultWorkflow;
import ijfx.core.workflow.Workflow;
import ijfx.core.workflow.WorkflowService;
import ijfx.core.workflow.WorkflowStep;
import ijfx.explorer.core.FolderManagerService;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mongis.utils.task.ProgressHandler;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;
import ijfx.explorer.ExplorerViewService;
import ijfx.core.imagedb.ExplorerService;

/**
 *
 * @author Cyril MONGIS
 */
public class SegmentationTaskBuilder {

    boolean separatePlane = true;

    final BatchItemBuilder itemBuilder;

    DefaultWorkflow workflow = new DefaultWorkflow();

    Logger logger = ImageJFX.getLogger();

    @Parameter
    Context context;

    @Parameter
    MeasurementService measurementService;

    @Parameter
    ImagePlaneService imagePlaneService;

    @Parameter
    MetaDataService metaDataService;

    @Parameter
    OverlayUtilsService overlayUtilsService;

    @Parameter
    DatasetService datasetService;

    @Parameter
    ImageDisplayService imageDisplayService;

    @Parameter
    ExplorerService imageRecordService;

    @Parameter
    ExplorerViewService explorerService;

    @Parameter
    FolderManagerService folderManagerService;

    @Parameter
    UIService uiService;

   
    @Parameter
    WorkflowService workflowService;

    List<SegmentationOp> opList = new ArrayList<>();

    public SegmentationTaskBuilder(Context context) {
        context.inject(this);
        itemBuilder = new BatchItemBuilder(context);

    }

    /**
     * This function will retrieve any BitMask from the image display and use it
     * as a mask for each plane. If no mask is given, the workflow will then be
     * executed on the current image
     *
     * @param display
     * @return Builder
     */
    public SegmentationTaskBuilder addImageDisplay(ImageDisplay display) {

        Img<BitType> mask = overlayUtilsService.extractBinaryMask(display);

        Dataset dataset = imageDisplayService.getActiveDataset(display);

        // String source = imageDisplayService.getActiveDataset(display).getSource();
        MetaDataSet set = new MetaDataSet();
        set.put(MetaData.create(MetaData.NAME, display.getName()));
        set.putGeneric(MetaData.SOURCE_PATH, dataset.getSource());
        if (mask == null) {
            Dataset maskDataset = datasetService.create((RandomAccessibleInterval) imagePlaneService.planeView(display));

            opList.add(new DefaultSegmentationOp(imageDisplayService.getActiveDataset(display), maskDataset, workflow, set));

        } else {
            opList.add(new DefaultSegmentationOp(dataset, mask, workflow, set));
        }

        return this;
    }

    public SegmentationTaskBuilder add(Collection<? extends Explorable> list) {

        opList.addAll(list
                .stream()
                .map(exp -> new ExplorableSegmentationTask(exp, workflow))
                .peek(context::inject)
                .collect(Collectors.toList()));

        return this;

    }

    public SegmentationTaskBuilder addStep(Class<? extends Command> cmd, Object... params) {
        workflow.getStepList().add(workflowService.createStep(cmd, params));
        return this;
    }

    public SegmentationTaskBuilder add(String folder, boolean separatePlanes) {

        List<Explorable> collect = explorerService
                .indexDirectory(ProgressHandler.console(), new File(folder))
                .collect(Collectors.toList());

        if (separatePlanes) {
            add(folderManagerService.extractPlanes(collect));
        } else {
            add(collect);
        }

        return this;

    }

    public <T> SegmentationTaskBuilder filterNumber(String key, Predicate<Double> predicate) {

        opList = opList
                .stream()
                .filter(op
                        -> predicate.test(op.getMetaDataSet().getDoubleValue(key, 0)))
                .collect(Collectors.toList());

        return this;
    }

    public SegmentationTaskBuilder addDataset(Dataset dataset, MetaDataSet set, boolean separatePlane) {

        List<SegmentationOp> ops = new ArrayList<>();

        if (separatePlane = false || dataset.numDimensions() == 2) {
            ops = Lists.newArrayList(new DefaultSegmentationOp(dataset, workflow, set));
        } else {
            long[][] possibilities = DimensionUtils.allPossibilities(dataset);
            ops = Stream
                    .of(possibilities)
                    .map(position -> imagePlaneService.isolatePlane(dataset, DimensionUtils.planarToAbsolute(position)))
                    .map(plane -> new DefaultSegmentationOp(plane, workflow, set))
                    .collect(Collectors.toList());
        }
        return this;
    }

    public SegmentationTaskBuilder addInterval(RandomAccessibleInterval<?> interval) {
        Dataset dataset = datasetService.create((RandomAccessibleInterval<? extends RealType>)interval);
        opList.add(new DefaultSegmentationOp(dataset, dataset,workflow, null));
        return this;
    }

    public SegmentationTaskBuilder setWorkflow(List<WorkflowStep> steps) {
        workflow.getStepList().addAll(steps);
        return this;
    }

    public SegmentationTaskBuilder setWorkflow(Workflow workflow) {
        this.workflow.getStepList().addAll(workflow.getStepList());
        return this;
    }

    private SegmentationTaskBuilder separatePlanes() {
        separatePlane = true;
        return this;
    }

    public MeasurementSegmentationTask measure() {
        MeasurementSegmentationTask measurementSegmentationTask = new MeasurementSegmentationTask(context);
        measurementSegmentationTask.setOpList(opList);
        return measurementSegmentationTask;            
    }
    
    public CountObjectSegmentationTask count() {
        return new CountObjectSegmentationTask(context,opList);
    }
    
    public RawSegmentation getAsMask() {
        return new RawSegmentation(context,opList);
    }

    private Img<BitType> getAsMask(ProgressHandler handler, MetaDataSet set, Dataset original, Img<BitType> mask) {
        return mask;
    }

    private MetaDataSet count(ProgressHandler handler, MetaDataSet set, Dataset original, Img<BitType> mask) {

        MetaDataSet finalSet = new MetaDataSet();

        finalSet.merge(set);

        set.put(MetaData.create(MetaData.COUNT, BinaryToOverlay.transform(context, mask, false).length));

        return finalSet;

    }

}
