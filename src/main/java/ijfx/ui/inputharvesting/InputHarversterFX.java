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
package ijfx.ui.inputharvesting;

import ijfx.core.uiextra.UIExtraService;
import ijfx.ui.main.ImageJFX;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import mongis.utils.FXUtilities;
import org.scijava.module.Module;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.ui.AbstractInputHarvesterPlugin;
import org.scijava.widget.InputHarvester;
import org.scijava.widget.InputPanel;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = PreprocessorPlugin.class, priority = InputHarvester.PRIORITY)
public class InputHarversterFX extends AbstractInputHarvesterPlugin<Node, Node> {

    UIExtraService uiExtraService;
    
    @Override
    public InputPanel<Node, Node> createInputPanel() {

        return FXUtilities.runAndWait(InputPanelFX::new);

    }

    @Override
    public boolean harvestInputs(InputPanel<Node, Node> inputPanel, Module module) {

        if (module.getInfo().isInteractive()) {

            Platform.runLater(() -> {
                Stage stage = new Stage();
                Scene scene = new Scene((Parent) inputPanel.getComponent());
                scene.getStylesheets().add(ImageJFX.getStylesheet());
                scene.getRoot().getStyleClass().add("side-window");
                stage.setScene(scene);
                stage.setTitle(module.getInfo().getLabel());
                stage.show();
            });
            return true;
        } else {
            Dialog<Boolean> dialog2 = FXUtilities.runAndWait(() -> {

                Dialog<Boolean> dialog = new Dialog<Boolean>();

                dialog.getDialogPane().setContent(inputPanel.getComponent());
                
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dialog.setResultConverter(buttonType -> buttonType == ButtonType.OK);
                dialog.getDialogPane().getStylesheets().add(ImageJFX.getStylesheet());
                return dialog;
            });

            return FXUtilities.runAndWait(() -> dialog2.showAndWait().get());
        }

    }

    @Override
    protected String getUI() {
        return ImageJFX.UI_NAME;
    }

}
