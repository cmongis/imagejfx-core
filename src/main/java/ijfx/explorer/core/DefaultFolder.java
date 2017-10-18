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
package ijfx.explorer.core;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import ijfx.core.fswatch.DirectoryWatchService;
import ijfx.core.fswatch.FileChangeListener;
import ijfx.core.imagedb.ImageRecord;
import ijfx.core.imagedb.ImageRecordService;
import ijfx.core.imagedb.MetaDataExtractionService;
import ijfx.core.overlay.io.OverlayIOService;
import ijfx.core.segmentation.ObjectSegmentedEvent;
import ijfx.core.segmentation.SegmentedObject;
import ijfx.core.segmentation.SegmentedObjectExplorerWrapper;
import ijfx.core.stats.ImageStatisticsService;

import ijfx.core.timer.TimerService;
import ijfx.explorer.ExplorerService;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.wrappers.OverlayExplorableWrapper;
import ijfx.explorer.datamodel.wrappers.PlaneMetaDataSetWrapper;
import ijfx.explorer.events.FolderUpdatedEvent;
import ijfx.explorer.wrappers.MetaDataSetExplorerWrapper;
import ijfx.ui.loading.LoadingScreenService;
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import mongis.utils.task.ProgressHandler;
import org.scijava.Context;
import org.scijava.app.StatusService;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;

/**
 *
 * @author Cyril MONGIS
 */
public class DefaultFolder implements Folder, FileChangeListener {

    private File file;
    private String name;
    private List<Explorable> files;
    private List<Explorable> planes = new ArrayList<>();
    private List<Explorable> objects = new ArrayList<>();

    Logger logger = ImageJFX.getLogger();

    @Parameter
    private ImageRecordService imageRecordService;

    @Parameter
    private EventService eventService;

    @Parameter
    private Context context;

    @Parameter
    private TimerService timerService;

    @Parameter
    private ImageStatisticsService statsService;

    @Parameter
    private StatusService statusService;

    @Parameter
    private MetaDataExtractionService metadataExtractionService;

    @Parameter
    private DirectoryWatchService dirWatchService;

    @Parameter
    private OverlayIOService overlayIOService;

    @Parameter
    private LoadingScreenService loadingScreenService;
    
    @Parameter
    private ExplorerService explorerService;
    
    private String status = "Click to open";
    
    Property<Task> currentTaskProperty = new SimpleObjectProperty<>();

    public DefaultFolder() {

    }

    public DefaultFolder(File file) {
        setPath(file.getAbsolutePath());
    }

    @Override
    @JsonGetter("name")
    public String getName() {
        return name == null ? file.getName() : name;
    }

    @Override
    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;

    }

    @Override
    public File getDirectory() {
        return file;
    }

    @Override
    public List<Explorable> getFileList(ProgressHandler handler) {
        if (files == null) {            
            fetchFiles(handler);
            listenToDirectoryChange();
        }
       
        return files;
    }

    private List<Explorable> fetchFiles(ProgressHandler progress) {
        
        
        setStatus("Fetching files...");
        
       
        
        List<Explorable> explorables = explorerService.indexDirectory(progress,file)
                .map(this::addPlanes)
                .collect(Collectors.toList());
        
        files = explorables;
        
        setStatus(String.format("%d images / %d planes",explorables.size(),planes.size()));
        
        return explorables;
    }

    private Stream<Explorable> addFile(File file) {
        ImageRecord record = imageRecordService.getRecord(file);
        return explorerService.getSeries(record);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        eventService.publishLater(new FolderUpdatedEvent().setObject(this));
    }

  
  
    private Explorable addPlanes(Explorable explorable) {

        List<Explorable> planeExplorableList = metadataExtractionService.extractPlaneMetaData(explorable.getMetaDataSet())
                .stream()
                .map(m -> new PlaneMetaDataSetWrapper(context, m))
                .collect(Collectors.toList());
        synchronized (planes) {
           
            planes.addAll(planeExplorableList);
        }
        return explorable;
    }

    @JsonSetter("path")
    public void setPath(String path) {
        file = new File(path);
        files = null;
    }

    @JsonGetter("path")
    public String getPath() {
        return file.getAbsolutePath();
    }

    private void addItems(List<Explorable> explorables) {
        files.addAll(explorables);
        eventService.publishLater(new FolderUpdatedEvent().setObject(this));
    }

    @Override
    public List<Explorable> getPlaneList(ProgressHandler handler) {
        
        // the files should be there before the planes
        getFileList(handler);
        
        return planes;
    }

    @Override
    public List<Explorable> getObjectList(ProgressHandler handler) {
        return objects;
    }

    public void onFileAdded(List<Explorable> files) {

    }

    private boolean registered = false;

    private void listenToDirectoryChange() {

        if (registered) {
            return;
        }

        try {
            logger.info("Listening to " + getPath());
            dirWatchService.register(this, getPath());
            registered = true;
        } catch (IOException ex) {
            ImageJFX.getLogger().log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void onFileCreate(String filePath) {
        logger.info("File added " + filePath);

        File file = new File(getDirectory(), filePath);

        addFile(file).forEach(this::addPlanes);
        notifyFolderChange();
        if (file.getName().endsWith(OverlayIOService.OVERLAY_FILE_EXTENSION)) {
            File imageFile = overlayIOService.getImageFileFromOverlayFile(file);

            //getObjectList().addAll(loadOverlay(imageFile, file));
        }

    }

    @Override
    public void onFileModify(String filePath) {
        logger.info("File was modified : " + filePath);
    }

    private List<? extends Explorable> loadOverlay(File imageFile, File overlayJsonFile) {
        List<? extends Explorable> collect = overlayIOService.loadOverlays(overlayJsonFile)
                .stream()
                .filter(o -> o != null)
                .map(overlay -> new OverlayExplorableWrapper(context, imageFile, overlay))
                .filter(expl -> expl.isValid())
                .collect(Collectors.toList());

        logger.info("Collected overlays : " + collect.size());
        return collect;

    }

    @Override
    public Property<Task> currentTaskProperty() {
        return currentTaskProperty;
    }

    /*
    @EventHandler
    public void onObjectSegmented(ObjectSegmentedEvent event) {
        if (event.getFile().getAbsolutePath().indexOf(file.getAbsolutePath()) == 0) {
            logger.info("Adding objects");
            getObjectList().addAll(event
                    .getObject()
                    .stream()
                    .map(o -> new MetaDataSetExplorerWrapper(o.getMetaDataSet()))
                    .collect(Collectors.toList())
            );
        }
    }*/
    /*
    @Override
    public void addObjects(List<SegmentedObject> objects) {

        logger.info(String.format("Adding %d objects", objects.size()));

        getObjectList().clear();

        getObjectList().addAll(objects.stream()
                .map(o -> new SegmentedObjectExplorerWrapper(o))
                .map(o -> {
                    context.inject(o);
                    return o;
                })
                .collect(Collectors.toList()));

        eventService.publishLater(new FolderUpdatedEvent().setObject(this));

    }*/

    public boolean isFilePartOf(File f) {
        return f.getAbsolutePath().startsWith(file.getAbsolutePath());
    }

    private void notifyFolderChange() {
        eventService.publishLater(new FolderUpdatedEvent().setObject(this));
    }

}
