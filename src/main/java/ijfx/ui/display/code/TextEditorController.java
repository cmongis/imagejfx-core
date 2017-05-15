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

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.fxmisc.richtext.CodeArea;
import org.scijava.plugin.Parameter;

/**
 *
 * @author florian
 */


public class TextEditorController extends AnchorPane {
    
    @Parameter
    Scene scene;
    @FXML
    BorderPane borderPane;
    
    
    Language LANGUAGE = Language.JAVASCRIPT;
    
    public TextEditorController() throws IOException {
        System.out.println("Bonjour et bienvenue dans ce nouveau controlleur j'espere qu'il vous plaira");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ijfx/ui/display/code/TextEditorMain.fxml"));
        getStylesheets().add(getClass().getResource("/ijfx/ui/display/code/JavaRichtext.css").toExternalForm());
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        /*
        TextArea textAreaCreator = new TextArea();
        borderPane.setCenter(textAreaCreator.getCodeArea());
        textAreaCreator.getCodeArea().getSelectedText();
*/
        RichTextEditor richTextEditor =new RichTextEditor();
        this.getChildren().add(richTextEditor.init());
        
    }
    
    public void qqch (){
        
    }
    
}
