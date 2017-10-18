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
package mongis.utils.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.concurrent.Task;

/**
 * The callback task allows you to easily run a method/lambda in a new thread
 * and use the result in the FX Application Thread.
 *
 * E.g.
 *
 * Let say I want to process a list and transform it into a node that I will
 * later add to my view.
 *
 *
 * HBox hbox = ...
 *
 * List<String> myList = ...
 *
 * new CallbackTask<List<String>>,List<Node>>() .setInput(myList)
 * .run(list->list .stream() .map(str->new Label(str))
 * .collect(Collectors.toList())) .then(hbox::addAll); // equivalent of
 * .then(list->hbox.addAll(list)); .start();
 *
 *
 *
 *
 * @author Cyril MONGIS, 2016
 */
public class FluentTask<INPUT, OUTPUT> extends Task<OUTPUT> implements ProgressHandler, Consumer<INPUT> {

    private INPUT input;

    private Callable<INPUT> inputGetter;

    // Possible operation
    private FailableCallback<INPUT, OUTPUT> callback;
    private FailableCallable<OUTPUT> callable;
    private LongCallback<INPUT, OUTPUT> longCallback;
    private LongCallable<OUTPUT> longCallable;
    private FailableRunnable runnable;
    private LongRunnable longRunnable;
    private FailableConsumer<INPUT> consumer;
    private FailableBiConsumer<ProgressHandler, INPUT> longConsumer;

    // handlers
    private Consumer<Throwable> onError = e -> getLogger().log(Level.SEVERE, null, e);

    private List<Consumer<OUTPUT>> onSuccess = new ArrayList<>();

    private ExecutorService executor = null;

    private static ExecutorService EXECUTOR = null;
    
    private static ExecutorService QUEUE = Executors.newFixedThreadPool(1);
    
    private static Logger LOGGER = Logger.getLogger(FluentTask.class.getName());

    private double total = 1.0;
    private double progress = 0;

    private long elapsed = 0;

    
    
    
    public FluentTask() {
        super();

        setName(getCallerClassName());

    }

    public FluentTask(INPUT input) {
        this();
        setInput(input);
    }

    public FluentTask(FailableCallback<INPUT, OUTPUT> callback) {
        this();
        this.callback = callback;
    }

    public FluentTask<INPUT, OUTPUT> setInput(Callable<INPUT> inputGetter) {
        this.inputGetter = inputGetter;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> setName(String name) {
        updateTitle(name);
        updateMessage(name);
        return this;
    }

    public FluentTask<INPUT, OUTPUT> callback(FailableCallback<INPUT, OUTPUT> callback) {
        this.callback = callback;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> run(Runnable runnable) {
        if (runnable == null) {
            getLogger().warning("Setting null as runnable");
            return this;
        }
        this.runnable = () -> runnable.run();
        return this;
    }

    public FluentTask<INPUT, OUTPUT> run(LongRunnable longRunnable) {
        this.longRunnable = longRunnable;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> tryRun(FailableRunnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> consume(FailableConsumer<INPUT> consumer) {
        this.consumer = consumer;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> consume(FailableBiConsumer<ProgressHandler, INPUT> biConsumer) {
        this.longConsumer = biConsumer;
        return this;
    }

    
    
    public FluentTask<INPUT, OUTPUT> call(FailableCallable<OUTPUT> callable) {
        this.callable = callable;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> callback(LongCallback<INPUT, OUTPUT> longCallback) {
        this.longCallback = longCallback;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> call(LongCallable<OUTPUT> longCallable) {
        this.longCallable = longCallable;
        return this;
    }

    @Override
    public OUTPUT call() throws Exception {

        elapsed = System.currentTimeMillis();

        // first we check if the task was cancelled BEFORE RUNNING IT
        if (isCancelled()) {
            return null;
        }

        if (inputGetter != null) {
            input = inputGetter.call();
        }

        OUTPUT output = null;

        if (longCallback != null) {
            output = longCallback.handle(this, input);
        } else if (callback != null) {
            output = callback.call(input);
        } else if (longRunnable != null) {
            longRunnable.run(this);
            output = null;
        } else if (longCallable != null) {
            output = longCallable.call(this);
        } else if (callable != null) {
            output = callable.call();
        } else if (consumer != null) {
            consumer.accept(input);
        } else if (longConsumer != null) {
            longConsumer.accept(this, input);
        } else if (runnable != null) {
            runnable.run();
        }

        elapsed = System.currentTimeMillis() - elapsed;

        if (isCancelled()) {
            return null;
        }

        return output;
    }

    @Override
    protected void failed() {
        super.failed();
        getLogger().log(Level.SEVERE, String.format("%s failed", getCallerClassName()), getException());
        if (onError != null) {
            onError.accept(getException());
        }
    }

    public FluentTask<INPUT, OUTPUT> setInput(INPUT input) {
        this.input = input;
        return this;
    }

    public INPUT getInput() {
        return input;
    }

    public FluentTask<INPUT, OUTPUT> startIn(ExecutorService executorService) {
        executorService.execute(this);
        return this;
    }

    public FluentTask<INPUT, OUTPUT> start() {
        getExecutor().execute(this);
        return this;
    }

    public FluentTask<INPUT, OUTPUT> startInFXThread() {
        Platform.runLater(this);
        return this;
    }
    
    public FluentTask<INPUT,OUTPUT> startInNewThread() {
        new Thread(this).start();
        return this;
    }

    public FluentTask<INPUT, OUTPUT> queue() {
        QUEUE.execute(this);
        return this;
    }

    @Override
    public void succeeded() {

        getLogger().fine(String.format("%s '%s' executed in %d", getClass().getSimpleName(), getTitle(), elapsed));
        super.succeeded();
        for(Consumer<OUTPUT> handler : onSuccess) {
            handler.accept(getValue());
        }
        
      

    }

    @Override
    public void cancelled() {
        getLogger().info(getCallerClassName() + "task was cancelled...");
        super.cancelled();
    }

    public FluentTask<INPUT, OUTPUT> then(Consumer<OUTPUT> consumer) {
        onSuccess.add(consumer);
        return this;
    }

    public FluentTask<INPUT, OUTPUT> thenRunnable(Runnable runnable) {
        return then(item -> runnable.run());
    }

    public <NEXTOUTPUT> FluentTask<OUTPUT, NEXTOUTPUT> thenTask(FailableCallback<OUTPUT, NEXTOUTPUT> callback) {
        FluentTask<OUTPUT, NEXTOUTPUT> task = new FluentTask<OUTPUT, NEXTOUTPUT>()
                .setInput(this::getValue)
                .callback(callback);
        then(task);
        return task;
    }

    public FluentTask<INPUT, OUTPUT> ui() throws Exception {
        if (Platform.isFxApplicationThread()) {
            FluentTask.this.call();
        } else {
            Platform.runLater(this);
        }

        return this;
    }

    @Override
    public void setProgress(double progress) {
        updateProgress(progress, 1);
    }

    @Override
    public void setProgress(double workDone, double total) {
        updateProgress(workDone, total);
    }

    @Override
    public void setProgress(long workDone, long total) {
        updateProgress(workDone, total);
    }

    @Override
    public void setStatus(String message) {
        updateMessage(message);
    }

    @Override
    public void accept(INPUT t) {

        getLogger().fine("Consumed a new input " + t);
        setInput(t);
        start();
    }

    public FluentTask<INPUT, OUTPUT> error(Consumer<Throwable> handler) {
        onError = handler;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public FluentTask<INPUT, OUTPUT> setIn(Property<Task> taskProperty) {
        Platform.runLater(() -> taskProperty.setValue(this));
        return this;
    }

    public ExecutorService getExecutor() {
        
        if(executor == null) {
            executor = getCommonPool();
        }
        
        return executor;
    }

    public FluentTask<INPUT, OUTPUT> submit(Consumer<Task> consumer) {
        if (consumer == null) {
            getLogger().warning("Submitting task to null !");
        } else {
            consumer.accept(this);
        }
        return this;
    }

    public FluentTask<INPUT, OUTPUT> submit(Consumer<Task> consumer, boolean condition) {
        if (condition) {
            return submit(consumer);
        } else {
            return this;
        }
    }

    @Override
    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public void increment(double d) {
        progress += d;
        setProgress(progress, total);
    }

    public FluentTask<INPUT, OUTPUT> setInitialProgress(double p) {
        setProgress(progress);
        return this;
    }

    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];

            if (ste.getClassName().contains(FluentTask.class.getSimpleName()) == false) {
                return stElements[i].getClassName() + "." + ste.getMethodName();
            }
        }
        return null;
    }

  
    
    
    /*
        Static method linked to the Executor
    */
    
    
    public static ExecutorService getCommonQueue() {
        return QUEUE;
    }
    
    public static ExecutorService getCommonPool() {
        
        if(EXECUTOR == null) {
            EXECUTOR = Executors.newCachedThreadPool();
        }
        return EXECUTOR;   
    }
    public static void setCommonPool(ExecutorService executorService) {
        EXECUTOR = executorService;
    }
    
    public static void setDefaultLogger(Logger l) {
       LOGGER = l;
    }
    
    public static Logger getLogger() {
        return LOGGER;
    }

}
