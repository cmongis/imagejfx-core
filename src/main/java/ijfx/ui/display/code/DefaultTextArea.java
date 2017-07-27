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
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledText;
import org.reactfx.collection.LiveList;
import org.scijava.command.CommandInfo;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 */
public class DefaultTextArea extends AnchorPane{
    
    private CodeArea codeArea = null;
    private ScriptHighlight scriptHighlight;
    private AutocompletionList listProvider;
    private boolean autocomplete = true;
    private ContextMenu autocompleteMenu = new ContextMenu();
    
    private StringProperty selectedTextProperty;
    private StringProperty textProperty;
    private ObjectProperty<IndexRange> selectionProperty;
    
    private int needIndent = 0;
    private String indent = "";
    
    private Autocompletion autocompletion;
    
    public DefaultTextArea() {
        
        initCodeArea();
    }
    
    public DefaultTextArea(List<CommandInfo> entriesList, TextEditorPreferencies preferencies) {
        
        initCodeArea();
        setAutocompletion(entriesList);
        setPreferencies(preferencies);
    }
    
    public void initCodeArea(){
        
        this.codeArea = new CodeArea();
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                     if ("".equals(this.codeArea.getText().trim()) == false) {
                        this.codeArea.setStyleSpans(0, this.scriptHighlight.computeHighlighting(this.codeArea.getText()));
                        if (this.autocomplete) lauchAutocompletion();
                        addVariableAutocompletion();                        
                        if (change.getInserted().getText().equals("\n")) {
                            autoIndent();
                            change.getInserted().getText().replace("\n", "\n" + indent);
                            
                        }
                    }
                    
                });
        
        this.selectedTextProperty = new SimpleStringProperty();
        this.selectedTextProperty.bind(this.codeArea.selectedTextProperty());
        
        
        this.selectionProperty = new SimpleObjectProperty<>();
        this.selectionProperty.bind(this.codeArea.selectionProperty());
        
        this.textProperty = new SimpleStringProperty();
        this.textProperty.bind(this.codeArea.textProperty());
        
        this.getChildren().add(this.codeArea);
        this.autocompletion = new DefaultAutocompletion(this);
    }
    
    public void setAutocompletion(List<CommandInfo> entriesList){
        this.listProvider = new PythonAtocompletionListProvider(entriesList);
        this.autocompletion.setListProvider(listProvider);
    }
    
    public void initLanguage(ScriptLanguage language){
        this.scriptHighlight = new DefaultScriptHighlighting(language);
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
        if (!style.isEmpty()){
            if (style.toArray()[0].equals("null")){
                word = word.replace("\t", "");
                this.autocompleteMenu = this.autocompletion.computeAutocompletion(word);
                if (autocompleteMenu != null) {
                    autocompleteMenu.setMaxHeight(5);
                    autocompleteMenu.setPrefHeight(5);
                    autocompleteMenu.setHeight(10);
                    autocompleteMenu.show(this, Side.BOTTOM, 0, 0);

                }
            }
        }
        
    }
    
    public void addVariableAutocompletion(){
        codeArea.selectWord();
        String currentWord = codeArea.getSelectedText();
        codeArea.deselect();
        for (Paragraph<Collection<String>, StyledText<Collection<String>>, Collection<String>> paragraph : this.codeArea.getParagraphs()){
            for (StyledText<Collection<String>> word : paragraph.getSegments()){
                if (!word.getStyle().isEmpty()){
                    if (word.getStyle().toArray()[0].equals("null")){
                        String[] newEntries = word.getText().split(" ");
                        for (String newEntry : newEntries){
                            newEntry = newEntry.replace("\t", ""); // removing tabulations from keywords
                            if (!this.listProvider.getEntries().contains(newEntry) && !newEntry.equals(currentWord)){
                                this.listProvider.getEntries().add(newEntry);
                            }
                        }

                    }
                }
            }
            
        }
    }
    
    public void autoIndent(){
        int numberOfTab = 0;
        LiveList<Paragraph<Collection<String>, StyledText<Collection<String>>, Collection<String>>> paragraphs = this.codeArea.getParagraphs();
        Paragraph<Collection<String>, StyledText<Collection<String>>, Collection<String>> prevParagraph = paragraphs.get(this.codeArea.getCurrentParagraph());
        
        if (prevParagraph.getText().startsWith("\t")){
            
            for (char letter : prevParagraph.getText().toCharArray()){
                if (letter == '\t'){
                    numberOfTab+=1;
                }
            }
            for (int i = 0; i < numberOfTab; i++) {
                this.indent.concat("\t");
                //this.codeArea.insertText(this.codeArea.getCaretPosition()-1, "\t");
            }
        }
        
        
        
    }
    
    public void processIndent(){
        /*
        marche pas, parceque on ajoute un charactere, du coup ca declanche le listeneur qui reviens dans cette fonction, etc ...
        et de toute facon ca marche pas et c'est moche
        */
        int indent = this.needIndent;
        this.needIndent = 0;
        for (int i = 0; i < indent; i++) {
            this.codeArea.insertText(this.codeArea.getCaretPosition()-1, "\t");
        }
        
    }
    
    public CodeArea getCodeArea() {
        return this.codeArea;
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
    
   public void changeCss (String path){
       this.getStylesheets().clear();
       this.getStylesheets().add(getClass().getResource(path).toExternalForm());
       
   }
   
    public void setPreferencies(TextEditorPreferencies preferencies){
        this.autocomplete = preferencies.isAutocompletion();
       
       
        if (preferencies.getTheme().equals("darkTheme")){
            changeCss("/ijfx/ui/display/code/TextEditorDarkTheme.css");
        }
        else if (preferencies.getTheme().equals("lightTheme")){
               changeCss("/ijfx/ui/display/code/TextEditorLightTheme.css");
           }
        else{
            changeCss((String) preferencies.getTheme());
           }
           
       
   }
}
