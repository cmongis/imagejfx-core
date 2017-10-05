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
package ijfx.core.uiextra.choice;

import ijfx.core.uiextra.PixelRaster;
import ijfx.core.Handles;
import ijfx.core.uiextra.Choice;
import java.io.File;
import javafx.scene.image.Image;
import org.scijava.plugin.Plugin;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = Choice.class)
@Handles(type = File.class)
public class FileChoice implements Choice<File> {

    final private File file;

    
    public FileChoice() {
        this.file = null;
    }
    
    public FileChoice(File f) {
        this.file = f;
    }

    public Choice<File> create(File f) {
        return new FileChoice(f);
    }

    @Override
    public String getTitle() {
        return file.getName();
    }

    @Override
    public String getDescription() {
        return "" + file.length();
    }

    @Override
    public PixelRaster getPixelRaster() {
        if (file.getName().endsWith("png")) {

            return PixelRasterUtils.fromImage(new Image(file.getAbsolutePath(),false));
        } else {
            return null;
        }
    }

    @Override
    public File getData() {
        return file;
    }

}
