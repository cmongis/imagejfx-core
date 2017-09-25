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
import ijfx.core.metadata.MetaDataSet;

/**
 *
 * @author Cyril MONGIS
 */
public class DuplicatedBatchInputWrapper extends NaiveBatchInput {
    
    final BatchSingleInput input;
    
    
    
    public DuplicatedBatchInputWrapper(BatchSingleInput input) {
       this.input = input;
       setSourceFile(input.getSourceFile());
    }
    
    @Override
    public void save() {
        input.save();
    }
    
    @Override
    public void load() {
        input.load();
        setDataset(input.getDataset().duplicate());
    }
    
    @Override
    public MetaDataSet getMetaDataSet() {
        return input.getMetaDataSet();
    }
    
    
    
}
