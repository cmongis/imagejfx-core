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

import ijfx.core.batch.BatchService;
import ijfx.core.image.DatasetUtilsService;
import ijfx.ui.main.ImageJFX;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import mongis.utils.ObservableProgressHandler;
import mongis.utils.task.ProgressHandler;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS
 */
public class LinearSegmentationExecutor<T> implements SegmentationExecutor<T> {

    List<ProgressHandler> currentTask;

    @Parameter
    BatchService batchService;

    @Parameter
    DatasetService datasetService;

    @Parameter
    DatasetUtilsService datasetUtilsService;

    Logger logger = ImageJFX.getLogger();

    /**
     * List of all handlers currently executed
     */
    private final List<ProgressHandler> subHandlerList = new ArrayList<>();

    private ProgressHandler progressHandler;

    private MaskHandler<T> maskHandler;

    public LinearSegmentationExecutor(Context context) {
        context.inject(this);
    }

    @Override
    public List<T> execute(ProgressHandler progress,MaskHandler<T> handler, List<SegmentationOp> tasks) {

        this.progressHandler = ProgressHandler.check(progress);
        
        this.maskHandler = handler;
        
        List<T> results = new ArrayList<>(tasks.size());
        for (SegmentationOp op : tasks) {

            try {
                results.add(createTask(op).call());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "error when executing task", e);
                results.add(null);
            }

        }
        return results;
    }

    private Callable<T> createTask(SegmentationOp op) {
        return new Job(op, this::updateHandler);
    }

    private class Job implements Callable<T> {

        private final ObservableProgressHandler segmentationHandler = new ObservableProgressHandler();

        private final ObservableProgressHandler completionHandler = new ObservableProgressHandler();

        private final SegmentationOp op;

        public Job(SegmentationOp op, Runnable onChange) {

            this.op = op;
            // we add it to the list of current task
            subHandlerList.add(segmentationHandler);
            subHandlerList.add(completionHandler);
            segmentationHandler.setOnChange(onChange);
            completionHandler.setOnChange(onChange);
        }

        @Override
        public T call() throws Exception {
            
            segmentationHandler.setTotal(2+op.getWorkflow().getStepList().size());
            
            if (op.getOutput() == null) {
                op.load();
                segmentationHandler.increment(1.0);
                segmentationHandler.setStatus(op.getInput().getName());
                Dataset input = datasetUtilsService.copy(op.getInput());
                Dataset output = batchService.applyWorkflow(segmentationHandler, input, op.getWorkflow());
                
                op.setOutput((Img<BitType>) output.getImgPlus().getImg());
                segmentationHandler.increment(1.0);
            }
            completionHandler.setStatus("Analyzing result...");
            T result = maskHandler.handle(completionHandler, op.getMetaDataSet(), op.getMeasuredDataset(), op.getOutput());
            op.dispose();
            return result;
        }

    }

    protected synchronized void updateHandler() {
        synchronized (subHandlerList) {
            double globalProgress = subHandlerList
                    .stream()
                    .mapToDouble(ProgressHandler::getProgress)
                    .map(d->d < 0 ? 0 : d)
                    .sum() / subHandlerList.size();

            progressHandler.setProgress(globalProgress);
            /*
            ProgressHandler get = subHandlerList
                    .stream()
                    .filter(h -> h.getProgress() < 1)
                    .sorted((o1, o2) -> Double.compare(o2.getProgress(), o1.getProgress()))
                    .findFirst().get();*/
        }
    }

}
