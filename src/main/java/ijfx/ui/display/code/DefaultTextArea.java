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
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
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
    
    private CodeArea codeArea = null;
    private LanguageKeywords languageKeywords;
    private ScriptHighlight scriptHighlight;
    private AutocompletionList listProvider;
    private boolean autocomplete = true;
    private ContextMenu autocompleteMenu = new ContextMenu();
    
    private final StringProperty selectedTextProperty;
    private final StringProperty textProperty;
    private final ObjectProperty<IndexRange> selectionProperty;
    
    private String THEME = "dark";
    
    private Hashtable KEYWORDS_PATTERN_TABLE = new Hashtable();
    private Autocompletion autocompletion;
    
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
                        addVariableAutocompletion();
                        
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
        this.autocompletion = new DefaultAutocompletion(this);
        
        
    }
    
    public void setAutocompletion(List<CommandInfo> entriesList){
        this.listProvider = new DefaultAutocompletionListProvider(entriesList);
        this.autocompletion.setEntries(listProvider.getEntries());
    }
    
    public void initLanguage(ScriptLanguage language){
        this.languageKeywords.setLanguage(language);
        this.KEYWORDS_PATTERN_TABLE = this.languageKeywords.getKeywords();
        this.scriptHighlight.setKeywords(this.KEYWORDS_PATTERN_TABLE);
        //this.codeArea.redo();
        
    }
    /**
     * The autocompletion is performed on the closest word from the carret
     * The autocompletion is computed only if the word in question is not affected by a style this avoid try to compute the autocompletion in comments and strins
     */
    public void lauchAutocompletion(){
        codeArea.selectWord();
        String word = codeArea.getSelectedText();
        IndexRange selection = codeArea.getSelection();
        codeArea.deselect();
        
        Paragraph paragraph = codeArea.getParagraph(codeArea.getCurrentParagraph());
        Collection style = (Collection) paragraph.getStyleAtPosition(selection.getStart());
        if (style.toArray()[0].equals("null")){
            this.autocompleteMenu = this.autocompletion.computeAutocompletion(word);
            if (autocompleteMenu != null) {
                autocompleteMenu.setMaxHeight(5);
                autocompleteMenu.setPrefHeight(5);
                autocompleteMenu.setHeight(10);
                if (this.autocomplete == true) autocompleteMenu.show(this, Side.BOTTOM, 0, 0);
                
            }
        }
        
        
    }
    
    public void addVariableAutocompletion(){
        codeArea.selectWord();
        String currentWord = codeArea.getSelectedText();
        codeArea.deselect();
        for (Paragraph<Collection<String>, StyledText<Collection<String>>, Collection<String>> paragraph : this.codeArea.getParagraphs()){
            for (StyledText<Collection<String>> word : paragraph.getSegments()){
                if (word.getStyle().toArray()[0].equals("null")){
                    String[] newEntries = word.getText().split(" ");
                    for (String newEntry : newEntries){
                        if (!this.listProvider.getEntries().contains(newEntry) && !newEntry.equals(currentWord)){
                            this.listProvider.getEntries().add(newEntry);
                        }
                    }
                    
                }
            }
            
        }
    }
    
    public CodeArea getCodeArea() {
        return this.codeArea;
    }
    public void injectContext(Context context){
        context.inject(this);
    }
    public void setText(String text){

        this.codeArea.replaceText(text);
    }
    
    public void replaceWord(String word){
        Platform.runLater( () ->{
            this.codeArea.selectWord();
            this.codeArea.replaceSelection(word);
            this.codeArea.deselect();
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
   
   public void setPreferencies(Hashtable<String,String> preferencies){
       if (preferencies.containsKey("styleSheet") && !getClass().getResource((String) preferencies.get("styleSheet")).equals(null)){
           changeCss(preferencies.get("styleSheet"));
       }
       if (preferencies.containsKey("showAutocompletion") ){
           if (preferencies.get("showAutocompletion").equals("true")){
               this.autocomplete = true;
           }
           else if (preferencies.get("showAutocompletion").equals("false")){
               this.autocomplete = false;
           }
       }
   }
}
