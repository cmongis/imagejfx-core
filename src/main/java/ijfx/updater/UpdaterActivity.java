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
package ijfx.updater;

import ijfx.core.activity.Activity;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import mongis.utils.FXUtilities;
import org.scijava.plugin.Plugin;

/**
 *
 * @author cyril
 */
//@Plugin(type = Activity.class, name = "Update activity")
public class UpdaterActivity implements Activity{

    @FXML
    BorderPane root;
    
    @Override
    public Node getContent() {
        if(root == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(this.getClass().getResource("UpdaterActivity.fxml"));
                loader.setController(this);
                loader.load();
            } catch (IOException ex) {
                Logger.getLogger(UpdaterActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return root;
    }

    @Override
    public Task updateOnShow() {
        return null;
    }
    
    
    
}
