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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledText;
import org.reactfx.util.Either;
import org.reactfx.util.Tuple2;
import org.scijava.plugin.Parameter;


/**
 *
 * @author florian
 */


public class TextEditorController extends AnchorPane {
    
    @Parameter
    Scene scene;
    @Parameter
    Stage stage;
    @FXML
    BorderPane borderPane;
    @FXML
    MenuButton fileButton;
    @FXML
    MenuButton editButton;
    
    
    Language LANGUAGE = Language.JAVASCRIPT;
    
    CodeArea codeArea = null;
    
    public TextEditorController() throws IOException {
        System.out.println("Bonjour et bienvenue dans ce nouveau controlleur j'espere qu'il vous plaira");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ijfx/ui/display/code/TextEditorMain.fxml"));
        getStylesheets().add(getClass().getResource("/ijfx/ui/display/code/JavaRichtext.css").toExternalForm());
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        
        
        RichTextEditor richTextEditor =new RichTextEditor();
        
        TextArea textAreaCreator = new TextArea();
        this.codeArea = textAreaCreator.getCodeArea();
        init();
        borderPane.setCenter(codeArea);
        
        //File javascriptrc = new File("javascript.nanorc");
        Platform.runLater( () ->{

            File javascriptrc = openNanorc();
        });
        
        System.out.println(javascriptrc.getAbsolutePath());
        
        textAreaCreator.nanorcParser(javascriptrc);
        //borderPane.setTop(richTextEditor.init());
        //textAreaCreator.getCodeArea().getSelectedText();

        
        //VBox mainBox = richTextEditor.init();
        //mainBox.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        //this.getChildren().add(mainBox);
        //this.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        
    }
    
    public void init () {
        fileButton.getItems().add(creatMenuItem("loadfile", this::loadDocument));
        fileButton.getItems().add(creatMenuItem("savefile", this::saveDocument));
        editButton.getItems().add(creatMenuItem("undo", codeArea::undo));
        editButton.getItems().add(creatMenuItem("redo", codeArea::redo));
        editButton.getItems().add(creatMenuItem("cut", codeArea::cut));
        editButton.getItems().add(creatMenuItem("copy", codeArea::copy));
        editButton.getItems().add(creatMenuItem("paste", codeArea::paste));
        
        
    }
    
    public MenuItem creatMenuItem(String styleClass, Runnable action){
        
        MenuItem menuItem = new MenuItem();
        menuItem.setText(styleClass);
        menuItem.setOnAction(evt -> {
            action.run();
            
        });
        
        return menuItem;
    }
    
    public File openNanorc (){
        String initialDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load document");
        fileChooser.setInitialDirectory(new File(initialDir));
        File selectedFile = fileChooser.showOpenDialog(stage);
        return selectedFile;
        
    }
     private void loadDocument() {
        String initialDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load document");
        fileChooser.setInitialDirectory(new File(initialDir));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            //area.clear();
            load(selectedFile);
        }
    }

    private void load(File file) {
        //TODO
        System.out.println("loading");
        List<String> text = null;
        try {
            text = Files.readAllLines(file.toPath());
        } catch (IOException ex) {
            Logger.getLogger(TextEditorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        codeArea.replaceText(0,0,  String.join("\n", text));
    }


    private void saveDocument() {
        String initialDir = System.getProperty("user.dir");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save document");
        fileChooser.setInitialDirectory(new File(initialDir));
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            save(selectedFile);
        }
    }


    private void save(File file) {
        //TODO
    }
}
