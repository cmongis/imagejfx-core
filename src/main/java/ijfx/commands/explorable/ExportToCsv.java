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
package ijfx.commands.explorable;

import com.google.common.io.Files;
import ijfx.core.metadata.MetaDataKeyPriority;
import ijfx.core.metadata.MetaDataSetUtils;
import ijfx.explorer.datamodel.Explorable;
import java.io.File;
import java.util.List;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.FileUtils;

/**
 *
 * @author cyril
 */
@Plugin(type= ExplorableDisplayCommand.class, iconPath="fa:save",label = "Export to csv",priority = 0.1)
public class ExportToCsv extends AbstractExplorableDisplayCommand {

    @Parameter(label = "Export file", style = "save csv")
    File file;

    @Override
    public void run(List<? extends Explorable> items) throws Exception {

        if (Files.getFileExtension(file.getName()).equals("csv") == false) {

            file = new File(file.getParentFile(), file.getName() + ".csv");

        }

        String exportToCSVFromOwner = MetaDataSetUtils.exportToCSVFromOwner(items, ",", true, MetaDataKeyPriority.OBJECT);

        FileUtils.writeFile(file, exportToCSVFromOwner.getBytes());

    }

}
