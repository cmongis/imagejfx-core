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

import ijfx.core.metadata.MetaDataSet;
import java.util.List;
import mongis.utils.task.ProgressHandler;
import net.imagej.Dataset;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import org.scijava.Context;

/**
 *
 * @author Cyril MONGIS
 */
public class RawSegmentation extends AbstractSegmentationTask<Img<BitType>> implements MaskHandler{

    public RawSegmentation(Context context, List<SegmentationOp> ops) {
        super(context);
        setOpList(ops);
    }

    @Override
    public RawSegmentation execute(ProgressHandler handler) {
        setResults(getExecutor().execute(handler, this, getOpList()));
        return this;
    }
    
   
    @Override
    public Img<BitType> handle(ProgressHandler handler, MetaDataSet metadata, Dataset original, Img result) {
        return result;
    }
    
}
