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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mongis.utils.CallbackTask;
import mongis.utils.LongConsumer;
import mongis.utils.ProgressHandler;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS
 */
public abstract class AbstractSegmentationTask<T> implements SegmentationTask<T> {

    @Parameter
    protected Context context;

    private SegmentationExecutor executor;

    private List<? extends SegmentationOp> opList;

    private List<T> results;

    private final List<LongConsumer<List<T>>> resultsHandler = new ArrayList<>();

    public AbstractSegmentationTask(Context context) {
        context.inject(this);
        executor = new LinearSegmentationExecutor(context);
    }

    protected AbstractSegmentationTask<T> addHandler(LongConsumer<List<T>> consumer) {
        resultsHandler.add(consumer);
        return this;
    }

    public Context getContext() {
        return context;
    }

    protected SegmentationExecutor getExecutor() {
        return executor;
    }

    protected void setResults(List<T> results) {
        this.results = results;
    }

    public List<T> getResults() {
        return results;
    }

    protected void executeResultsHandlers(ProgressHandler progress) {
        for (LongConsumer<List<T>> consumer : resultsHandler) {
            consumer.consume(progress, getResults());
        }
    }

    @Override
    public SegmentationTask<T> setOpList(List<? extends SegmentationOp> op) {
        this.opList = op;
        return this;
    }

    public List<? extends SegmentationOp> getOpList() {
        return opList;
    }

    @Override
    public SegmentationTask<T> setExecutor(SegmentationExecutor ex) {
        this.executor = ex;
        return this;
    }

    @Override
    public CallbackTask<?, List<T>> executeAsync() {

        return new CallbackTask<Void, List<T>>()
                .call((progress) -> {
                    progress.setStatus("Segmenting...");
                    execute(progress);
                    return getResults();
                })
                .start();

    }

}
