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

import java.util.Hashtable;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 */
public class DefaultTextArea extends AnchorPane{
    
    private CodeArea codeArea = null;
    private LanguageKeywords languageKeywords;
    private ScriptHighlight scriptHighlight;
    
    private final StringProperty selectedTextProperty;
    private final StringProperty textProperty;
    private final ObjectProperty<IndexRange> selectionProperty;
    
    
    private Hashtable KEYWORDS_PATTERN_TABLE = new Hashtable();
   
    
    public DefaultTextArea() {
        this.languageKeywords = new NanorcParser();
        this.scriptHighlight = new DefaultScriptHighlighting(this.KEYWORDS_PATTERN_TABLE);
        
        this.codeArea = new CodeArea();
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, this.scriptHighlight.computeHighlighting(codeArea.getText()));
                });
        
        selectedTextProperty = new SimpleStringProperty();
        selectedTextProperty.bind(this.codeArea.selectedTextProperty());
        
        
        selectionProperty = new SimpleObjectProperty<>();
        selectionProperty.bind(this.codeArea.selectionProperty());
        
        textProperty = new SimpleStringProperty();
        textProperty.bind(this.codeArea.textProperty());
        
        this.getChildren().add(this.codeArea);
        getStylesheets().add(getClass().getResource("/ijfx/ui/display/code/JavaRichtext.css").toExternalForm());
        
    }

    public void initLanguage(ScriptLanguage language){
        this.languageKeywords.setLanguage(language);
        this.KEYWORDS_PATTERN_TABLE = this.languageKeywords.getKeywords();
        this.scriptHighlight.setKeywords(this.KEYWORDS_PATTERN_TABLE);
    }
    public CodeArea getCodeArea() {
        return this.codeArea;
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
    
    public void detectBracket(){
        
    }
    
}
