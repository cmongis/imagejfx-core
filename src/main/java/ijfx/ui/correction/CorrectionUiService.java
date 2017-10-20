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
package ijfx.ui.correction;

import ijfx.core.IjfxService;
import ijfx.core.imagedb.ImageLoaderService;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.loading.LoadingScreenService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import mongis.utils.task.FluentTask;
import mongis.utils.task.ProgressHandler;
import net.imagej.Dataset;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import ijfx.explorer.ExplorerViewService;

/**
 *
 * @author Cyril MONGIS, 2016
 */
@Plugin(type = Service.class)
public class CorrectionUiService extends AbstractService implements IjfxService {

    @Parameter
    ImageLoaderService imageLoaderService;

    @Parameter
    LoadingScreenService loadingScreenService;

    @Parameter
    ExplorerViewService indexationService;

    private final ObjectProperty<List<Explorable>> fileListProperty = new SimpleObjectProperty();

    private final List<Explorable> selectedList = new ArrayList<>();
    
    private final ObjectProperty<Dataset> exampleDataset = new SimpleObjectProperty<Dataset>();
    
    private final ObjectProperty<File> sourceFolder = new SimpleObjectProperty<>();

    private final ObjectProperty<File> destinationDirectory = new SimpleObjectProperty<>();
    
    @Override
    public void initialize() {
        sourceFolder.addListener(this::onSourceFolderChanged);
    }

    private void onSourceFolderChanged(Observable obs, File oldFile, File newFile) {

        new FluentTask<File, List<Explorable>>()
                .setInput(newFile)
                .callback(this::checkFolder)
                .then(this::onFoldedChecked)
                .submit(loadingScreenService)
                .start();
    }

    protected List<Explorable> checkFolder(ProgressHandler handler, File input) {

        handler.setProgress(1, 3);
        handler.setStatus(String.format("Checking folder %s", input.getName()));

        handler.setProgress(2, 3);

        return indexationService.indexDirectory(handler, input)
                .collect(Collectors.toList());

    }

    protected void onFoldedChecked(List<Explorable> list) {
        fileListProperty.setValue(list);
    }

    
    public List<? extends Explorable> getSelectedObjects() {
        return selectedList;
    }
    
    public List<Explorable> getSelectedFiles() {
       return selectedList;
   }
    
    public Property<Dataset> exampleDatasetProperty() {
        return exampleDataset;
    }

    ObjectProperty<File> sourceFolderProperty() {
        return sourceFolder;
    }

    ObjectProperty<List<Explorable>> fileListProperty() {
        return fileListProperty;
    }

    
}
