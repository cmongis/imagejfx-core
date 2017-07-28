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
import ijfx.core.batch.BatchSingleInput;
import ijfx.core.batch.item.DuplicatedBatchInputWrapper;
import ijfx.core.metadata.MetaDataSet;
import ijfx.ui.main.ImageJFX;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import mongis.utils.ObservableProgressHandler;
import mongis.utils.ProgressHandler;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;

/**
 *
 * @author cyril
 */
public class LinearSegmentationExecutor<T> implements SegmentationExecutor<T> {

    List<ProgressHandler> currentTask;

    @Parameter
    BatchService batchService;

    Logger logger = ImageJFX.getLogger();
    
    /**
     * List of all handlers currently executed
     */
    private List<ProgressHandler> subHandlerList = new ArrayList<>();

    private ProgressHandler handler;

    @Override
    public List<T> execute(ProgressHandler handler, List<SegmentationTask<T>> tasks) {

        this.handler = ProgressHandler.check(handler);

        return tasks
                .stream()
                .map(this::execute)
                .map(callable->{
                    try {
                       return  callable.call();
                    }
                    catch(Exception e) {
                        logger.log(Level.SEVERE, "error when executing task", e);
                        return null;
                    }
                })
                .collect(Collectors.toList());

    }

    public Callable<T> execute(SegmentationTask<T> task) {

        // we need to monitor the progress of each task
        final ObservableProgressHandler segmentationHandler = new ObservableProgressHandler();

        final ObservableProgressHandler completionHandler = new ObservableProgressHandler();

        // we add it to the list of current task
        subHandlerList.add(segmentationHandler);
        subHandlerList.add(completionHandler);

        return () -> {
            // when one handler changes, all handlers are queried
            // the big handler is updated
            segmentationHandler.setOnChange(this::updateHandler);
            MetaDataSet set = task.getInput().getMetaDataSet();
            
            
            BatchSingleInput input = new DuplicatedBatchInputWrapper(task.getInput());
            input.load();
            
            batchService.applyWorkflow(segmentationHandler, task.getInput(), task.getWorkflow());

            return task.getHandler().handle(completionHandler,input.getMetaDataSet(),task.getInput().getDataset(), (Img<BitType>) input.getDataset().getImgPlus().getImg());
        };
        
    }

    private synchronized void updateHandler() {
        synchronized (subHandlerList) {
            double globalProgress = subHandlerList
                    .stream()
                    .mapToDouble(ProgressHandler::getProgress)
                    .sum() / subHandlerList.size();

            handler.setProgress(globalProgress);

            ProgressHandler get = subHandlerList
                    .stream()
                    .filter(h -> h.getProgress() < 1)
                    .sorted((o1, o2) -> Double.compare(o2.getProgress(), o1.getProgress()))
                    .findFirst().get();
        }
    }

}
