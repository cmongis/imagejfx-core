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
package ijfx.core.batch.item;

import ijfx.core.batch.BatchSingleInput;
import ijfx.core.image.SilentImageDisplay;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.save.SaveOptions;
import ijfx.ui.save.SaveType;
import ijfx.ui.utils.NamingUtils;
import java.io.File;
import static java.time.LocalDateTime.from;
import java.util.function.Consumer;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.display.ImageDisplay;
import net.imglib2.RandomAccessibleInterval;
import org.scijava.Context;
import org.scijava.plugin.Parameter;

/**
 * Work in progress, allow to build any BatchSingleInput from any situation. Uses the Builder and Decorator Pattern.
 *
 * @author Cyril MONGIS, 2016
 */
public class BatchItemBuilder {

    @Parameter
    Context context;

    BatchSingleInput input;

    private static String suffixSeparator = "_";
    
    @Parameter
    DatasetService datasetService;
    
    
    public BatchItemBuilder(Context context) {
        
        context.inject(this);
    }
    
    public BatchItemBuilder wrap(BatchSingleInput input) {
        this.input = input;
        return this;
    }
    
    public void tryInject(Object object) {
        try {
            context.inject(object);
        }
        catch(Exception e) {
            
        }
    }
    
    public BatchItemBuilder from(File file) {
        input = new FileBatchInputLoader(file);
        context.inject(input);
        return this;
    }
    
    public BatchItemBuilder from(RandomAccessibleInterval interval) {
        return from(datasetService.create(interval));
    }
    
    public BatchItemBuilder from(ImageDisplay imageDisplay) {
        //throw new IllegalStateException("Not yet implemented !");
        input = new ImageDisplayBatchInput(imageDisplay, false);
        
        return this;
    }

    public BatchItemBuilder from(Dataset dataset) {
        
        NaiveBatchInput naiveInput = new NaiveBatchInput();
        naiveInput.setDataset(dataset);
        naiveInput.setSourceFile(dataset.getSource());
        naiveInput.setDisplay(new SilentImageDisplay(context, dataset));
        input = naiveInput;
        return this;
    }
    
    public BatchItemBuilder from(Dataset dataset, long[] planePosition) {
        input = new DatasetPlaneWrapper(context, dataset, planePosition);
        return this;
    }

    public BatchItemBuilder from(Explorable holder) {
        input = new ExplorableBatchInputWrapper(holder);
        tryInject(input);
        return this;
    }

    public BatchItemBuilder saveTo(File file) {
        input = new SaveToFileWrapper(context, input, file);
        return this;
    }
    
    public BatchItemBuilder display() {
        input = new DisplayDatasetWrapper(context, input);
        return this;
    }
    
    public BatchItemBuilder displayWithSuffix(String suffix) {
        input = new DisplayDatasetWrapper(context, input,suffix);
        return this;
    }
    
   
    
    public BatchItemBuilder overwriteOriginal() {
        input = new ReplaceOriginalFileSaver(context,input);
        return this;
    }
    
    public BatchItemBuilder onFinished(Consumer<BatchSingleInput> action) {
        input = new ConsumerBatchInputWrapper(input, action);
        return this;
    }
    
    
    public BatchItemBuilder saveUsingOptions(SaveOptions options) {
        
        if(options.saveType().getValue() == SaveType.REPLACE) {
            return overwriteOriginal();
        }
        else {
            return saveIn(options.folder().getValue(),options.suffix().getValue());
        }
        
    }
    
    public BatchItemBuilder saveIn(File directory) {
        input =  new SaveToFileWrapper(context,input, new File(directory,input.getName()));
        return this;
    }
    
    public BatchItemBuilder saveIn(File directory, String suffix) {
        
        if(suffix == null || "".equals(suffix.trim())) {
            return saveIn(directory);
        }
        input = new SaveToFileWrapper(context,input,new File(directory,input.getName()),suffix);
        return this;
    }
    
   public BatchItemBuilder saveNextToSourceWithPrefix(String suffix) {
       input = new SaveToFileWrapper(context, input, new File(input.getSourceFile()),suffix);
       return this;
   }
   public BatchItemBuilder saveNextToSourceWithPrefix(String suffix, String extension) {
       if(extension.startsWith(".") == false) extension = new StringBuilder().append(".").append(extension).toString();
       File f = NamingUtils.replaceWithExtension(new File(input.getSourceFile()),extension);
       input = new SaveToFileWrapper(context,input,f,suffix);
               return this;
   }
    public BatchSingleInput getInput() {
        return input;
    }

}
