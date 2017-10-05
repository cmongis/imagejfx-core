
/*
 * /*
 *     This file is part of ImageJ FX.
 *
 *     ImageJ FX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ImageJ FX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * 	Copyright 2015,2016 Cyril MONGIS, Michael Knop
 *
 */
package ijfx.core.uicontext;

import ijfx.core.overlay.OverlaySelectionEvent;
import ijfx.core.overlay.OverlaySelectionService;
import ijfx.core.overlay.OverlayUtilsService;
import ijfx.core.uicontext.calculator.UiContextCalculator;
import ijfx.ui.main.ImageJFX;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.imagej.ImageJService;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.OverlayService;
import org.scijava.display.DisplayService;
import org.scijava.display.event.DisplayActivatedEvent;
import org.scijava.display.event.DisplayCreatedEvent;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.display.event.DisplayUpdatedEvent;
import org.scijava.event.EventHandler;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 *
 * @author Cyril MONGIS, 2015
 */
@Plugin(type = Service.class)
public class UiContextCalculatorService extends AbstractService implements ImageJService {

    @Parameter
    private DisplayService displayService;

    @Parameter
    private ImageDisplayService imageDisplayService;

    @Parameter
    private OverlayService overlayService;

    @Parameter
    private OverlaySelectionService overlaySelectionService;

    @Parameter
    private UiContextService contextService;

    @Parameter
    private OverlayUtilsService overlayUtilsService;

    @Parameter
    private PluginService pluginService;

    Logger logger = ImageJFX.getLogger();

    public static String NO_DISPLAY_OPEN = "no-display-open";

    private List<UiContextCalculator> calculatorList;

    PublishSubject<ContextCalculationTask> taskStream = PublishSubject.create();

    public void determineContext(Object object) {

        if (calculatorList == null) {
            calculatorList = pluginService.createInstancesOfType(UiContextCalculator.class);
        }

        taskStream.onNext(new ContextCalculationTask(object));

    }

    @Override
    public void initialize() {

        taskStream
                .observeOn(ImageJFX.getPublishSubjectScheduler())
                // buffers request for 100 MS
                .buffer(500, TimeUnit.MILLISECONDS)
                .filter(list -> list.isEmpty() == false)
                // takes the list
                .subscribe(this::onTask);

        determineContext(null);

    }

    public void onTask(List<ContextCalculationTask> taskList) {
        
        Set<ContextCalculationTask> taskSet = taskList
                .stream()
                // groups by the object they should handle
                .collect(Collectors.toSet());
        logger.info(String.format("Reduced %d to %d calculation tasks.", taskList.size(), taskSet.size()));

        // just take the first task since they are all the same
        taskSet.forEach(this::executeTask);

        logger.info("End of task execution.");

        contextService.update();

    }

    private void executeTask(ContextCalculationTask task) {

        logger.info(String.format("Executing task for %s", task.getObject()));

        task.run();
    }

    public static final Integer NULL = new Integer(0);

    private class ContextCalculationTask {

        final Object object;

        public ContextCalculationTask(Object object) {

            if (object == null) {
                this.object = NULL;
            } else {
                this.object = object;
            }

        }

        public Object getObject() {
            return object;
        }

        @Override
        public boolean equals(Object o) {

            if (o instanceof ContextCalculationTask) {
                ContextCalculationTask task = ((ContextCalculationTask) o);
                return task.getObject() == object || object.equals(task.getObject());
            }
            return false;

        }

        @Override
        public int hashCode() {
            return object.hashCode();
        }

        public void run() {
            for (UiContextCalculator plugin : calculatorList) {
                if (object == NULL || plugin.supports(object) == false) {
                    plugin.calculate(null);
                } else {
                    plugin.calculate(object);
                }
            }
        }

    }

    @EventHandler
    public void handleEvent(DisplayCreatedEvent event) {
        determineContext(event.getObject());
    }

    @EventHandler
    public void handleEvent(DisplayUpdatedEvent event) {
        determineContext(event.getDisplay());
    }

    @EventHandler
    public void handleEvent(DisplayActivatedEvent event) {

        determineContext(event.getDisplay());
    }

    @EventHandler
    public void handleEvent(OverlaySelectionEvent event) {
        determineContext(event.getDisplay());

    }

    @EventHandler
    public void handleEvent(DisplayDeletedEvent event) {

        if (imageDisplayService.getImageDisplays().size() > 0) {

            displayService.setActiveDisplay(imageDisplayService.getImageDisplays().get(0));
            determineContext(imageDisplayService.getImageDisplays().get(0));
        } else {

            determineContext(null);
        }

    }

}
