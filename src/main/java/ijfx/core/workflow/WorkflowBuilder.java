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
package ijfx.core.workflow;
import ijfx.core.batch.BatchService;
import ijfx.core.batch.BatchSingleInput;
import ijfx.core.batch.item.BatchItemBuilder;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.loading.LoadingScreenService;
import ijfx.ui.save.SaveOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import mongis.utils.task.FluentTask;
import mongis.utils.task.ProgressHandler;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import static net.imglib2.view.Views.interval;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class WorkflowBuilder {

    @Parameter
    private Context context;

    @Parameter
    private BatchService batchService;

    @Parameter
    private LoadingScreenService loadingScreenService;

    private List<BatchItemBuilder> inputs = new ArrayList<>();

    private List<WorkflowStep> steps = new ArrayList<>();

    private String name;
    
    public WorkflowBuilder(Context context) {
        context.inject(this);
    }

    public WorkflowBuilder addInput(Collection<? extends BatchSingleInput> inputs) {
        inputs.forEach(this::addInput);
        return this;
    }

    public WorkflowBuilder addInputs(Collection<? extends Explorable> explorableList) {
        explorableList.forEach(this::addInput);
        return this;
    }

    public WorkflowBuilder addInput(BatchSingleInput input) {

        inputs.add(new BatchItemBuilder(context).wrap(input));

        return this;
    }

    public WorkflowBuilder addInput(File file) {
        inputs.add(new BatchItemBuilder(context).from(file));
        return this;
    }
    
    public WorkflowBuilder addInputFiles(Collection<Explorable> files) {
        files.forEach(this::addInput);
        return this;
    }
    
    public WorkflowBuilder addInput(Dataset dataset) {
        inputs.add(new BatchItemBuilder(context).from(dataset));
        return this;
    }
    
    public WorkflowBuilder addInput(RandomAccessibleInterval<?> interval) {
        inputs.add(new BatchItemBuilder(context).from(interval));
        return this;
    }
    
    public WorkflowBuilder addInput(Dataset dataset, long[] planePosition) {
        inputs.add(new BatchItemBuilder(context).from(dataset,planePosition));
        return this;
    }

    public WorkflowBuilder addInput(Explorable exp) {
        inputs.add(new BatchItemBuilder(context).from(exp));
        return this;
    }

    public WorkflowBuilder addInput(ImageDisplay imageDisplay) {
        inputs.add(new BatchItemBuilder(context).from(imageDisplay));
        return this;
    }

    public WorkflowBuilder addStepIfTrue(boolean predicateResult,Class<?> moduleClass, Object... params) {
        if(predicateResult) {
           return  addStep(moduleClass,params);
        }
        else {
            return this;
        }
    }
    
    public WorkflowBuilder addStep(Class<?> moduleClass, Object... params) {

        DefaultWorkflowStep step = new DefaultWorkflowStep(context,moduleClass.getName());
        for (int i = 0; i != params.length; i += 2) {
            step.setParameter(params[i].toString(), params[i + 1]);
        }

        steps.add(step);

        return this;

    }

    public WorkflowBuilder then(Consumer<BatchSingleInput> consumer) {

        remapInputs(builder -> builder.onFinished(consumer));
        return this;
    }
    public WorkflowBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public WorkflowBuilder thenUseDataset(Consumer<Dataset> consumer) {
        then(batchInput -> consumer.accept(batchInput.getDataset()));
        return this;
    }

    public <OUTPUT> FluentTask<BatchSingleInput, OUTPUT> thenMapToTask(Class<? extends OUTPUT> output) {
        FluentTask<BatchSingleInput, OUTPUT> task = new FluentTask<>();
        then(task);
        return task;
    }

    protected void remapInputs(Function<? super BatchItemBuilder, ? extends BatchItemBuilder> remapper) {
        inputs = inputs.stream().map(remapper).collect(Collectors.toList());
    }
    public WorkflowBuilder and(Consumer<BatchItemBuilder> consumer) {
        inputs.forEach(consumer);
        return this;
    }
    
    public WorkflowBuilder saveUsingOptions(SaveOptions options) {
        remapInputs(builder->builder.saveUsingOptions(options));
        return this;
    }
    
    public WorkflowBuilder saveTo(File directory) {

        remapInputs(builder -> builder.saveIn(directory));
        return this;

    }

    public WorkflowBuilder execute(Collection<WorkflowStep> stepList) {
        steps.addAll(stepList);
        return this;
    }

    public WorkflowBuilder execute(Workflow workflow) {
        steps.addAll(workflow.getStepList());
        return this;
    }
    
    public List<WorkflowStep> buildList() {
        return steps;
    }

    public Workflow getWorkflow(String name) {
        DefaultWorkflow workflow = new DefaultWorkflow(steps);
        workflow.setName(name);
        return workflow;
    }

    private List<BatchSingleInput> getInputs() {
        return inputs.stream().map(builder -> builder.getInput()).collect(Collectors.toList());
    }

    public FluentTask<?,Boolean> start() {

        DefaultWorkflow workflow = new DefaultWorkflow(steps);

        return new FluentTask<List<BatchSingleInput>, Boolean>()
                .setInput(getInputs())
                .callback((progress, input) -> batchService.applyWorkflow(progress, input, workflow))
                .start();
    }

    public FluentTask<?,Boolean> startAndShow() {
        FluentTask<?,Boolean> task = start();
        loadingScreenService.frontEndTask(task, true);
        return task;
    }
    
    public FluentTask<?, Boolean> getTask() {
        return new FluentTask<List<BatchSingleInput>, Boolean>()
                .call(this::runSync);
    }

    public boolean runSync(ProgressHandler handler) {
        handler = ProgressHandler.check(handler);

        return batchService.applyWorkflow(handler, getInputs(), new DefaultWorkflow(steps));

    }
}
