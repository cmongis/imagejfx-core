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

import ijfx.commands.binary.BinaryToOverlay;
import ijfx.core.metadata.DefaultMetaDataOwner;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataOwner;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetDisplayService;
import ijfx.core.overlay.OverlayStatService;
import ijfx.explorer.datamodel.MetaDataOwnerDisplay;
import java.util.List;
import java.util.stream.Stream;
import mongis.utils.LongConsumer;
import mongis.utils.ProgressHandler;
import net.imagej.Dataset;
import net.imagej.overlay.Overlay;
import net.imglib2.img.Img;
import org.scijava.Context;
import org.scijava.plugin.Parameter;
/**
 *
 * @author Cyril MONGIS
 */
public class CountObjectSegmentationTask extends AbstractSegmentationTask<MetaDataOwner> implements MaskHandler {

    ConsumerHandler<CountObjectSegmentationTask> consumers = new ConsumerHandler<>();

    

    private double minimumSize = 1;

    @Parameter
    MetaDataSetDisplayService metadataDisplayService;

    
    @Parameter
    OverlayStatService overlayStatService;
    
    
    public CountObjectSegmentationTask(Context context, List<? extends SegmentationOp> list) {
        super(context);
        setOpList(list);

    }

    @Override
    public CountObjectSegmentationTask execute(ProgressHandler handler) {

        List<MetaDataOwner> execute = getExecutor().execute(handler, this, getOpList());

        setResults(execute);
        consumers.fireEvent(handler, this);

        return this;

    }

    public CountObjectSegmentationTask then(LongConsumer<CountObjectSegmentationTask> consumer) {
        consumers.add(consumer);
        return this;
    }

    public CountObjectSegmentationTask display(String str) {

        return then((progress, task) -> {
            MetaDataOwnerDisplay display = metadataDisplayService
                    .findDisplay(String.format(str + "(min size = %.0f)", minimumSize));
                    display.add(getResults());
                    
                    display.update();

        });

    }

    public CountObjectSegmentationTask setMinimumSize(double minimumSize) {
        this.minimumSize = minimumSize;
        return this;
    }

    @Override
    public MetaDataOwner handle(ProgressHandler handler, MetaDataSet metadata, Dataset original, Img result) {

        handler.setStatus("CC analysis...");
        handler.setProgress(0.3);
        MetaDataSet set = new MetaDataSet();

        Overlay[] transform = BinaryToOverlay.transform(getContext(), result, true);

        handler.setStatus("Counting objects...");
        handler.setProgress(0.7);
        long count = Stream.of(transform)
                .map(overlayStatService::getShapeStatistics)
                .filter(stats -> stats.getArea() > minimumSize)
                .count();

        set.merge(metadata);

        set.putGeneric(MetaData.COUNT, count);

        return new DefaultMetaDataOwner(set);

    }

}
