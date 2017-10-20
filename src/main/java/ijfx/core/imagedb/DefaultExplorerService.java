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
package ijfx.core.imagedb;

import com.google.common.collect.Lists;
import ijfx.core.hash.HashService;
import ijfx.core.metadata.MetaDataService;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.notification.NotificationService;
import ijfx.core.prefs.JsonPreferenceService;
import ijfx.ui.main.ImageJFX;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mongis.utils.TextFileUtils;
import mongis.utils.task.ProgressHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.app.StatusService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import rx.subjects.PublishSubject;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = Service.class, priority = Priority.VERY_LOW_PRIORITY)
public class DefaultExplorerService extends AbstractService implements ExplorerService {

    Executor executor = Executors.newFixedThreadPool(1);

    HashMap<File, DefaultImageRecord> recordMap;

    Queue<File> fileQueue = new LinkedList<>();

    @Parameter
    MetaDataService metadataExtractorService;

    @Parameter
    ImageLoaderService imageLoaderService;

    @Parameter
    JsonPreferenceService jsonPreferenceService;

    Logger logger = ImageJFX.getLogger();

    @Parameter
    NotificationService notificationService;

    @Parameter
    StatusService statusService;

    private static String FILE_ADDED = "%s images where analyzed.";

    PublishSubject<ImageRecord> saveQueue = PublishSubject.create();

    @Override
    public void initialize() {
        super.initialize();

        saveQueue
                .observeOn(ImageJFX.getPublishSubjectScheduler())
                .filter(imageRecord -> getRecordMap().containsValue(imageRecord) == false)
                .buffer(10, TimeUnit.SECONDS)
                .filter(list -> list.isEmpty() == false)
                .subscribe(list -> save());

    }

    @Override
    public boolean isPresent(File file) {
        return getRecordMap().containsKey(file);
    }

    private Map<File, DefaultImageRecord> getRecordMap() {
        if (recordMap == null) {
            recordMap = new HashMap<>();
            load().forEach(record -> recordMap.put(record.getFile(), record));
        }
        return recordMap;
    }

    @Override
    public void addRecord(ImageRecord imageRecord) {
        getRecordMap().put(imageRecord.getFile(), (DefaultImageRecord) imageRecord);
        saveQueue.onNext(imageRecord);
    }

    @Override
    public ImageRecord addRecord(File file, MetaDataSet metaDataSet) {
        ImageRecord record = new DefaultImageRecord(file, metaDataSet);
        addRecord(record);
        return record;
    }

    @Override
    public Collection<? extends ImageRecord> getRecords() {
        return getRecordMap().values();
    }

    @Override
    public Collection<? extends ImageRecord> queryRecords(Predicate<ImageRecord> query) {
        return getRecordMap().values().parallelStream().filter(query).collect(Collectors.toList());
    }

    public ImageRecord getRecord(File file) {

        ImageRecord imageRecord;
        if (getRecordMap().containsKey(file) == false) {
            imageRecord = new DefaultImageRecord(file, metadataExtractorService.extractMetaData(file));
            if (imageRecord.getMetaDataSet().size() == 0) {
                throw new IllegalArgumentException("Error when reading file metadata : " + file.getName());
            }
            addRecord(imageRecord);
        } else {
            imageRecord = getRecordMap().get(file);
        }

        return imageRecord;
    }

    private void save() {
        jsonPreferenceService.savePreference(getRecords(), JSON_FILE);
    }

    private List<DefaultImageRecord> load() {
        return jsonPreferenceService.loadListFromJson(JSON_FILE, DefaultImageRecord.class);
    }

    @Override
    public synchronized Collection<? extends ImageRecord> getRecordsFromDirectory(ProgressHandler handler, File directory) {

        List<ImageRecord> records = new ArrayList<>();

        statusService.showStatus(1, 10, "Analyzing folder : " + directory.getName());
        Collection<File> files = getAllImagesFromDirectory(directory);
        int count = 0;
        int total = files.size();

        handler.setTotal(total);

        List<ImageRecord> collect = files
                .stream()
                .map(f -> {
                    handler.increment(1.0);
                    try {
                        return getRecord(f);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(record -> record != null)
                .collect(Collectors.toList());

        return collect;

    }

    public void forceSave() {
        ImageJFX.getThreadPool().execute(this::save);
    }

    @Parameter
    private Context context;
    @Parameter
    private HashService hashService;

    private final String FORMAT_FILE_NAME = "/supportedFormats.txt";
    public List<String> formats = new ArrayList<>();

    @Override
    public IOFileFilter getIOFileFilter() {
        List<IOFileFilter> suffixFilters = new ArrayList<>();
        for (String ext : getSupportedExtensions()) {

            suffixFilters.add(new SuffixFileFilter(ext));
        }
        IOFileFilter suffixFilter = new OrFileFilter(suffixFilters);
        return suffixFilter;
    }

    public static IOFileFilter canReadFilter(IOFileFilter filter) {
        return new AndFileFilter(filter, CanReadFileFilter.CAN_READ);
    }

    public static IOFileFilter getDirectoryFilter() {
        return DirectoryFileFilter.INSTANCE;
    }

    @Override
    public String[] getSupportedExtensions() {
        if (formats.isEmpty()) {
            loadFormats();
        }
        int size = formats.size();
        String[] extensions = new String[size];
        for (int i = 0; i < size; i++) {
            String filterExt = formats.get(i);
            extensions[i] = filterExt.substring(2, filterExt.length());
        }
        return extensions;
    }

    private void loadFormats() {
        try {
            formats.clear();
            String formatsFromFile = TextFileUtils.readFileFromJar(ImageJFX.class, FORMAT_FILE_NAME);

            String[] lines = formatsFromFile.split("\n");
            for (String ext : lines) {
                formats.add("*" + ext);
            }
        } catch (IOException ex) {
            ImageJFX.getLogger().log(Level.SEVERE, "Error when loading the file containing all the possible formats.", ex);
        }
    }

    @Override
    public Collection<File> getAllImagesFromDirectory(File file) {
        if (file.isDirectory() == false) {
            return Lists.newArrayList(file);
        }
        return FileUtils.listFiles(file, getSupportedExtensions(), true);
    }

    @Override
    public Collection<File> getAllImagesFromDirectory(File file, boolean recursive) {
        if (recursive) {
            return getAllImagesFromDirectory(file);
        } else {
            final IOFileFilter filter = getIOFileFilter();
            return Stream.of(file.listFiles())
                    .filter(filter::accept)
                    .collect(Collectors.toList());

        }
    }

}
