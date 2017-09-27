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
package ijfx.core;

import java.io.File;
import java.io.IOException;
import net.imagej.ImageJ;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.scijava.Context;
import org.scijava.plugin.Parameter;


/**
 *
 * @author Cyril MONGIS
 */

public abstract class IjfxTest {
    
    private static ImageJ imageJ;
    
    private static boolean injected = false;
    
    @Parameter
    Context context;
    
    protected ImageJ getImageJ() {
        
        if(imageJ == null) {
            imageJ = new ImageJ();
        }
        return imageJ;
        
    }
    
    
    @Before
    public void injectFirst() {
        if(injected == false) {
            System.out.println("Injecting SciJava Context");
            getImageJ().context().inject(this);
        }
    }
    
    public Context getContext() {
        return context;
    }
    
    
     public void displayFile(File file) throws IOException{
        System.out.println(file.toString());
         System.out.println(FileUtils.readFileToString(file));
    }
}
