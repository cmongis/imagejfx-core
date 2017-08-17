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
package ijfx.ui.test;

import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.utils.CategorizedExplorableController;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */
@Plugin(type = Command.class, menuPath = "Plugins > Test > Generate Dummy Something")
public class DummyNoIdea extends ContextCommand {

    public CategorizedExplorableController ctrl = new CategorizedExplorableController();

    @Override
    public void run() {
        
        List <Explorable> truc = new ArrayList<>();

        
        Pane pane = ctrl
                .addCategory("Cat 1")
                .setElements("Cat 1", truc) //set the element that the first line will contain
                .addCategory("Cat 2")
                .setElements("Cat 2",truc)
                .setMaxItemPerCategory(5)
                .generate(); // returns the Pane that contains the view

        //ctrl.setElements("Cat 2", truc);
        ctrl.update(); // updates the pane returned previously

    }

}
