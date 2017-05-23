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
package ijfx.ui.display.code;

import ijfx.ui.display.image.AbstractFXDisplayPanel;
import ijfx.ui.display.image.FXDisplayPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.fxmisc.richtext.CodeArea;
import org.scijava.display.Display;
import org.scijava.display.TextDisplay;
import org.scijava.plugin.Plugin;

/**
 *
 * @author florian
 */
@Plugin(type = FXDisplayPanel.class)
public class TextEditorDisplayPanel extends AbstractFXDisplayPanel<TextDisplay>  {

    AnchorPane root;
    BorderPane borderPane;
    
    public TextEditorDisplayPanel(Class<? extends Display> supportedClass) {
        super(TextDisplay.class);
    }

    @Override
    public void pack() {
        borderPane = new BorderPane();
        root = new AnchorPane();
        root.getChildren().add(borderPane);
        TextArea textAreaCreator = new TextArea();
        CodeArea codeArea = textAreaCreator.getCodeArea();
        borderPane.setCenter(codeArea);

    }

    @Override
    public Pane getUIComponent() {
        return this.root;
    }

    @Override
    public void redoLayout() {
    }

    @Override
    public void setLabel(String string) {
    }

    @Override
    public void redraw() {
    }

    
}
