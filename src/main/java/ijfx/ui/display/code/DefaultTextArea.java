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

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledText;
import org.scijava.Context;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 */
public class DefaultTextArea extends AnchorPane{
    @Parameter
    CommandService commandService;
    private CodeArea codeArea = null;
    private LanguageKeywords languageKeywords;
    private ScriptHighlight scriptHighlight;
    
    private final StringProperty selectedTextProperty;
    private final StringProperty textProperty;
    private final ObjectProperty<IndexRange> selectionProperty;
    
    private String THEME = "dark";
    
    private Hashtable KEYWORDS_PATTERN_TABLE = new Hashtable();
   private DefaultAutocompletion autocompletion;
    
    public DefaultTextArea() {
        
        this.languageKeywords = new NanorcParser();
        this.scriptHighlight = new DefaultScriptHighlighting(this.KEYWORDS_PATTERN_TABLE);
        
        this.codeArea = new CodeArea();
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                     if ("".equals(codeArea.getText().trim()) == false) {
                        codeArea.setStyleSpans(0, this.scriptHighlight.computeHighlighting(codeArea.getText()));
                        lauchAutocompletion();
                    }
                    
                });
        
        selectedTextProperty = new SimpleStringProperty();
        selectedTextProperty.bind(this.codeArea.selectedTextProperty());
        
        
        selectionProperty = new SimpleObjectProperty<>();
        selectionProperty.bind(this.codeArea.selectionProperty());
        
        textProperty = new SimpleStringProperty();
        textProperty.bind(this.codeArea.textProperty());
        
        this.getChildren().add(this.codeArea);
        getStylesheets().add(getClass().getResource("/ijfx/ui/display/code/TextEditorDarkTheme.css").toExternalForm());
        
        
        
    }

    public void initLanguage(ScriptLanguage language){
        this.languageKeywords.setLanguage(language);
        this.KEYWORDS_PATTERN_TABLE = this.languageKeywords.getKeywords();
        this.scriptHighlight.setKeywords(this.KEYWORDS_PATTERN_TABLE);
        this.codeArea.redo();
        SortedSet<String> entries = new TreeSet<>();
        for (CommandInfo command : commandService.getCommands()){
            entries.add(command.getClassName());
        }
        autocompletion = new DefaultAutocompletion(this, entries);
    }
    public void lauchAutocompletion(){
        codeArea.selectWord();
        String word = codeArea.getSelectedText();
        codeArea.deselect();
        autocompletion.computeAutocompletion(word);
        
        
    }
    public CodeArea getCodeArea() {
        return this.codeArea;
    }
    public void injectContext(Context context){
        context.inject(this);
    }
    public void setText(String text){
        Platform.runLater( () ->{
            this.codeArea.replaceText(text);
        });
    }
    public StringProperty textProperty(){
        return this.textProperty;
    }
    public StringProperty selectedTextProperty(){
        return this.selectedTextProperty;
    }
    public ObjectProperty selectionProperty(){
        return this.selectionProperty;
    }
    public String getSelectedText(){
        return this.codeArea.getSelectedText();
    }
    
    public void undo(){
        this.codeArea.undo();
    }
    public void redo(){
        this.codeArea.redo();
    }
    public void switchTheme(){
        if (THEME.equals("light")){
            changeCss("/ijfx/ui/display/code/TextEditorDarkTheme.css");
            THEME = "dark";
        }
        else if (THEME.equals("dark")){
             changeCss("/ijfx/ui/display/code/TextEditorLightTheme.css");
             THEME = "light";
             
        }
    }
   public void changeCss (String path){
       this.getStylesheets().clear();
       this.getStylesheets().add(getClass().getResource(path).toExternalForm());
       
   }
    
}
