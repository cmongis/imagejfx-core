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
package ijfx.core.utils;

import java.util.function.BiConsumer;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 *
 * @author cyril
 */
public class DefaultFluentInterval<T extends RealType<T>> implements FluentInterval<T> {
    
    
    final RandomAccessibleInterval<T> interval;

    public DefaultFluentInterval(RandomAccessibleInterval<T> interval) {
        this.interval = interval;
    }

    @Override
    public FluentInterval<T> forEach(BiConsumer<long[], T> consumer) {
        
        IterableInterval<T> iterable = Views.iterable(interval);
        
        Cursor<T> cursor = iterable.localizingCursor();
       
        cursor.reset();
        
        while(cursor.hasNext()) {
            long[] position = new long[iterable.numDimensions()];
            cursor.localize(position);
            consumer.accept(position, cursor.get());
            
        }

        return this;
    }
}
