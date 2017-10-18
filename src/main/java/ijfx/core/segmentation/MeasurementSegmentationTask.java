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
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataService;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetType;
import ijfx.core.overlay.MeasurementService;
import ijfx.core.utils.AxisUtils;
import ijfx.core.utils.DimensionUtils;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.datamodel.Explorable;
import io.scif.services.DatasetIOService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import mongis.utils.task.LongConsumer;
import mongis.utils.task.ProgressHandler;
import net.imagej.Dataset;
import net.imagej.axis.CalibratedAxis;
import net.imagej.overlay.Overlay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.apache.commons.io.FilenameUtils;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

/**
 *
 * @author Cyril MONGIS
 */
public class MeasurementSegmentationTask extends AbstractSegmentationTask<SegmentedObject> {

    @Parameter
    private MetaDataService metaDataService;

    @Parameter
    private MeasurementService measurementService;

    @Parameter
    private UIService uiService;

    @Parameter
    private DatasetIOService datasetIOService;

    private ExplorableList asExplorable;

    private String destinationFolder;

    ConsumerHandler<MeasurementSegmentationTask> onFinished = new ConsumerHandler<>();
    
    
    
    public MeasurementSegmentationTask(Context context) {
        super(context);

    }

    @Override
    public MeasurementSegmentationTask execute(ProgressHandler progress) {

        List<List< ? extends SegmentedObject>> partialResults
                = getExecutor()
                        .execute(progress, this::measure, getOpList()); // returns a list of list

        // flattening the results
        setResults(partialResults.stream()
                
                .flatMap(i -> i.stream())
                .collect(Collectors.toList()));

        onFinished.fireEvent(progress, this);
        
        
        return this;
    }

    public ExplorableList getAsExplorable() {

        if (asExplorable == null) {
           asExplorable =  new ExplorableList(getResults()
                    .stream()
                    .map(SegmentedObjectExplorerWrapper::new)
                   
                    .collect(Collectors.toList()));
        }
        return asExplorable;
    }

    public MeasurementSegmentationTask setDestinationFolder(String destinationFolder) {

        if (destinationFolder.endsWith(File.separator) == false) {
            destinationFolder = destinationFolder + File.separator;
        }
        this.destinationFolder = destinationFolder;
        return this;
    }
    
    public MeasurementSegmentationTask then(LongConsumer<MeasurementSegmentationTask> task) {
        onFinished.add(task);
        return this;
    }
    
    public MeasurementSegmentationTask saveDataset(String folder) {
        this.destinationFolder = folder;
        
        then(this::saveObjects);
        
        return this;
    }

    /*
        Handlers
     */
    private List<? extends SegmentedObject> measure(ProgressHandler handler, MetaDataSet set, Dataset original, Img<BitType> mask) {

        List<Overlay> overlays = Lists.newArrayList(BinaryToOverlay.transform(context, mask, true));

        if (overlays.size() == 1) {
            overlays = Lists.newArrayList(BinaryToOverlay.transform(context, mask, false));
        }
        //uiService.show(set.getStringValue(MetaData.NAME, "No name"), mask);
        handler.setTotal(overlays.size());

        List<SegmentedObject> objects = new ArrayList<SegmentedObject>();

        if (original.numDimensions() > 2) {

            CalibratedAxis[] axes = AxisUtils.getAxes(original);

            for (long[] position : DimensionUtils.allPossibilities(original)) {
                MetaDataSet planeMetaDataSet = new MetaDataSet(MetaDataSetType.PLANE);
                planeMetaDataSet.merge(set);
                
                metaDataService.fillPositionMetaData(planeMetaDataSet, axes, position);

                List<? extends SegmentedObject> measureOverlays = measurementService.measureOverlays(overlays, original, position);
                planeMetaDataSet.putGeneric(MetaData.SOURCE_PATH, original.getSource());
                measureOverlays.forEach(obj -> obj.getMetaDataSet().merge(planeMetaDataSet));

                handler.increment(1);
                objects.addAll(measureOverlays);

            }
        } else {
            List<SegmentedObject> measureOverlays = measurementService.measureOverlays(overlays, (RandomAccessibleInterval) original);
            measureOverlays
                    .forEach(obj -> obj.getMetaDataSet().merge(set));

            objects.addAll(measureOverlays);
        }

        return objects;
    }

    public void saveObjects(ProgressHandler handler, MeasurementSegmentationTask task) {

        if (destinationFolder == null) {
            return;
        }

        for (Explorable exp : getAsExplorable()) {

            exp.load();

            String filename = new StringBuilder(200)
                    .append(destinationFolder)
                    .append("")
                    .append(FilenameUtils.getBaseName(exp.getMetaDataSet().getStringValue(MetaData.FILE_NAME, "None")))
                    .append("_pl")
                    .append(exp.getMetaDataSet().getStringValue(MetaData.PLANE_NON_PLANAR_POSITION, ""))
                    .append("_")
                    .append(exp.getMetaDataSet().getStringValue(MetaData.NAME, "noname"))
                    .append(".tif")
                    .toString();
            
            try {
                if(new File(filename).getParentFile().exists() == false) {
                    new File(filename).getParentFile().mkdirs();
                }
                datasetIOService.save(exp.getDataset(), filename);
            } catch (IOException ex) {
                Logger.getLogger(MeasurementSegmentationTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            exp.dispose();

        }

    }

}
