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
package ijfx.core.thread;

import ijfx.ui.main.ImageJFX;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import javafx.application.Platform;
import org.scijava.Priority;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

/**
 *
 * @author cyril
 */
@Plugin(type = Service.class, priority = Priority.HIGH_PRIORITY)
public class FXThreadService extends AbstractService implements
        ThreadService {

    private static final String SCIJAVA_THREAD_PREFIX = "SciJava-";

    private static WeakHashMap<Thread, Thread> parents
            = new WeakHashMap<>();

    @Parameter
    private LogService log;

    private ExecutorService executor;

    /**
     * Mapping from ID to single-thread {@link ExecutorService} queue.
     */
    private Map<String, ExecutorService> queues;

    private int nextThread = 0;

    private boolean disposed;

    private boolean javaFXMode = false;

    private ExecutorService eventThread = Executors.newFixedThreadPool(1);

    // -- ThreadService methods --
    @Override
    public <V> Future<V> run(final Callable<V> code) {
        if (disposed) {
            return null;
        }
        return executor().submit(wrap(code));
    }

    @Override
    public Future<?> run(final Runnable code) {
        if (disposed) {
            return null;
        }
        return executor().submit(wrap(code));
    }

    @Override
    public ExecutorService getExecutorService() {
        return executor();
    }

    @Override
    public void setExecutorService(final ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public boolean isDispatchThread() {
        return EventQueue.isDispatchThread();
    }

    @Override
    public void invoke(final Runnable code) throws InterruptedException,
            InvocationTargetException {
        try {
            if (isJavaFXMode()) {

                if (Platform.isFxApplicationThread()) {
                    code.run();
                } else {
                    Platform.runLater(code);

                    //eventThread.execute(code);
                }
            } else {

                if (isDispatchThread()) {
                    // just call the code
                    code.run();
                } else {
                    // invoke on the EDT
                    EventQueue.invokeAndWait(wrap(code));
                }
            }
        } catch (IllegalStateException e) {
            if (isDispatchThread()) {
                // just call the code
                code.run();
            } else {
                // invoke on the EDT
                EventQueue.invokeAndWait(wrap(code));
            }
        }
    }

    @Override
    public void queue(final Runnable code
    ) {

        if (isJavaFXMode()) {
            Platform.runLater(code);
        } else {
            EventQueue.invokeLater(wrap(code));
        }
    }

    @Override
    public Thread getParent(final Thread thread
    ) {
        return parents.get(thread != null ? thread : Thread.currentThread());
    }

    @Override
    public ThreadService.ThreadContext getThreadContext(final Thread thread
    ) {
        final String name = thread.getName();

        // check for same context
        if (name.startsWith(contextThreadPrefix())) {
            return ThreadService.ThreadContext.SAME;
        }

        // check for different context
        if (name.startsWith(SCIJAVA_THREAD_PREFIX)) {
            return ThreadService.ThreadContext.OTHER;
        }

        // recursively check parent thread
        final Thread parent = getParent(thread);
        if (parent == thread || parent == null) {
            return ThreadService.ThreadContext.NONE;
        }
        return getThreadContext(parent);
    }

    // -- Disposable methods --
    @Override
    public void dispose() {
        disposed = true;
        if (executor != null) {
            executor.shutdown();
        }
    }

    // -- ThreadFactory methods --
    @Override
    public Thread newThread(final Runnable r
    ) {
        final String threadName = contextThreadPrefix() + nextThread++;
        return new Thread(r, threadName);
    }
    // -- Helper methods --

    private ExecutorService executor() {
        if (executor == null) {
            executor = Executors.newCachedThreadPool(this);
        }
        return executor;
    }

    private synchronized ExecutorService executor(final String id) {
        if (disposed) {
            return null;
        }
        if (queues == null) {
            queues = new HashMap<>();
        }
        if (!queues.containsKey(id)) {
            final ThreadFactory factory = new ThreadFactory() {

                @Override
                public Thread newThread(final Runnable r) {
                    final String threadName = contextThreadPrefix() + id;
                    return new Thread(r, threadName);
                }

            };
            final ExecutorService queue = Executors.newSingleThreadExecutor(factory);
            queues.put(id, queue);
        }
        return queues.get(id);
    }

    private Runnable wrap(final Runnable r) {
        final Thread parent = Thread.currentThread();
        return new Runnable() {
            @Override
            public void run() {
                final Thread thread = Thread.currentThread();
                try {
                    if (parent != thread) {
                        parents.put(thread, parent);
                    }
                    r.run();
                } finally {
                    if (parent != thread) {
                        parents.remove(thread);
                    }
                }
            }
        };
    }

    private <V> Callable<V> wrap(final Callable<V> c) {
        final Thread parent = Thread.currentThread();
        return new Callable<V>() {
            @Override
            public V call() throws Exception {
                final Thread thread = Thread.currentThread();
                try {
                    if (parent != thread) {
                        parents.put(thread, parent);
                    }
                    return c.call();
                } finally {
                    if (parent != thread) {
                        parents.remove(thread);
                    }
                }
            }
        };
    }

    private String contextThreadPrefix() {
        final String contextHash = Integer.toHexString(context().hashCode());
        return SCIJAVA_THREAD_PREFIX + contextHash + "-Thread-";
    }

    public void setJavaFXMode(boolean javaFXMode) {
        this.javaFXMode = javaFXMode;
    }

    public boolean isJavaFXMode() {
        return javaFXMode;
    }

    @Override
    public Future<?> queue(final String id, final Runnable code) {
        return executor(id).submit(wrap(code));
    }

    @Override
    public <V> Future<V> queue(final String id, final Callable<V> code) {
        return executor(id).submit(wrap(code));
    }
}
