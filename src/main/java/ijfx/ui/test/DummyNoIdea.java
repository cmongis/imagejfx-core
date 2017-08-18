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

import ijfx.core.metadata.MetaData;
import ijfx.explorer.ExplorableList;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.wrappers.MetaDataSetExplorerWrapper;
import ijfx.ui.utils.CategorizedExplorableController;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 *
 * @author sapho
 */
@Plugin(type = Command.class, menuPath = "Plugins > Test > Generate Dummy No Idea")
public class DummyNoIdea extends ContextCommand {

    public CategorizedExplorableController ctrl = new CategorizedExplorableController();

    @Parameter(type = ItemIO.OUTPUT)
    ExplorableList output;

    @Parameter(type = ItemIO.OUTPUT)
    ExplorableList output2;

    @Override
    public void run() {

        ExplorableList output = new ExplorableList();
        ExplorableList output2 = new ExplorableList();

        for (int i = 0; i != 10; i++) {

            MetaData name = MetaData.create(MetaData.NAME, RandomStringUtils.random(3, true, false));
            MetaData m1 = MetaData.create("Random strings 1", RandomStringUtils.random(5, true, false));
            MetaData m2 = MetaData.create("Random strings 2", RandomStringUtils.random(3, true, false));
            MetaData m3 = MetaData.create("Random double 1", new Random().nextDouble());
            MetaData m4 = MetaData.create("Random double 2", new Random().nextDouble());

            Explorable explorable = new MetaDataSetExplorerWrapper(name, m1, m2, m3, m4);
            explorable.addTag(Tag.create(RandomStringUtils.random(3, true, false)));
            explorable.addTag(Tag.create(RandomStringUtils.random(3, true, false)));
            output.add(explorable);

        }

        for (int i = 0; i != 10; i++) {

            MetaData name = MetaData.create(MetaData.NAME, RandomStringUtils.random(3, true, false));
            MetaData m1 = MetaData.create("Random strings 1", RandomStringUtils.random(5, true, false));
            MetaData m2 = MetaData.create("Random strings 2", RandomStringUtils.random(3, true, false));
            MetaData m3 = MetaData.create("Random double 1", new Random().nextDouble());
            MetaData m4 = MetaData.create("Random double 2", new Random().nextDouble());

            Explorable explorable = new MetaDataSetExplorerWrapper(name, m1, m2, m3, m4);
            explorable.addTag(Tag.create(RandomStringUtils.random(3, true, false)));
            explorable.addTag(Tag.create(RandomStringUtils.random(3, true, false)));
            output2.add(explorable);

        }

        Pane pane = ctrl
                .addCategory("Cat 1")
                .setElements("Cat 1", output) //set the element that the first line will contain
                .addCategory("Cat 2")
                .setElements("Cat 2", output2)
                //.setMaxItemPerCategory(5)
                .generate(); // returns the Pane that contains the view

        Platform.runLater(() -> {

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Label label = new Label("truc");
            pane.getChildren().add(label);
            Scene scene = new Scene(pane, 200, 100);
            stage.setScene(scene);
            stage.show();
            /*


            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Information Dialog");
            //alert.setGraphic(pane);
            //alert.getDialogPane().setContent(pane);
            //alert.getDialogPane().setExpandableContent(pane);
            //alert.getDialogPane().setVisible(true);
            alert.getDialogPane().getChildren().add(pane);
            alert.getDialogPane().setPrefSize(480, 320);

            alert.showAndWait();
*/
        });

        //ctrl.setElements("Cat 2", truc2);
        //ctrl.update(); // updates the pane returned previously
        //pane.getParent();
    }

}
