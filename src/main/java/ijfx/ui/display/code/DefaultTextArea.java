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

import ijfx.ui.main.ImageJFX;
import java.io.File;
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
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.RichTextChange;
import org.fxmisc.richtext.model.SimpleEditableStyledDocument;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledText;
import org.fxmisc.richtext.model.TextChange;
import org.reactfx.collection.LiveList;
import org.scijava.command.CommandInfo;
import org.scijava.script.ScriptLanguage;

/**
 * This class is the main class of the script editor, it's in charge to call all the elements to display code.
 * Here is how it work: 
 * The autocompletion is performed by the DefaultAutocompletion class which implement Autocompletion
 * 
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
        setAutocompletion(entriesList, null);
        setPreferencies(preferencies);
        initCodeArea();
        
    }
    
    public DefaultTextArea(List<CommandInfo> entriesList, ScriptLanguage language, TextEditorPreferencies preferencies) {
        setAutocompletion(entriesList, language);
        setPreferencies(preferencies);
        initCodeArea();
        
        
    }
    
    private void initCodeArea(){
        
        if(codeArea != null) return;
        
        this.codeArea = new CodeArea();
        this.codeArea.getStyleClass().add("code-area");
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                   
                     if ("".equals(this.codeArea.getText().trim()) == false) {
                        this.codeArea.setStyleSpans(0, this.scriptHighlight.computeHighlighting(this.codeArea.getText()));
                        if (this.autocomplete) lauchAutocompletion();
                        addVariableAutocompletion();  
                        //change = change.mergeWith(autoIndent());
                        
                        /*
                        if (change.getInserted().getText().equals("\n")) {
                            autoIndent();
                            change.getInserted().getText().replace("\n", "\n" + indent);
                            
                        }
                        */
                    }
                    
                    
                });
                
        
        this.selectedTextProperty = new SimpleStringProperty();
        this.selectedTextProperty.bind(this.codeArea.selectedTextProperty());
        
        
        this.selectionProperty = new SimpleObjectProperty<>();
        this.selectionProperty.bind(this.codeArea.selectionProperty());
        
        this.textProperty = new SimpleStringProperty();
        this.textProperty.bind(this.codeArea.textProperty());
        
        this.getChildren().add(this.codeArea);
        
    }
    
    public void setAutocompletion(List<CommandInfo> entriesList, ScriptLanguage language){
        this.autocompletion = new DefaultAutocompletion(this);
        if (language.getLanguageName().equals("Python")){
            this.listProvider = new PythonAtocompletionListProvider(entriesList);
        }
        else {
            this.listProvider = new DefaultAutocompletionListProvider(entriesList);
        }
        
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
    
    public TextChange autoIndent(){
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
        
        StyledDocument newText = new SimpleEditableStyledDocument(this.indent, null);

        TextChange textChange= new PlainTextChange(1,"",this.indent);
        return textChange;
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
    
    /**
     * When a line start with four spaces, replace it by a tabulation (for python especialy)
     */
    public void convertTab (){
        for (Paragraph<Collection<String>, StyledText<Collection<String>>, Collection<String>> paragraph : this.codeArea.getParagraphs()){
            if (paragraph.getText().startsWith("    ")){
                paragraph.getText().replaceFirst("    ", "\t");
            }
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
    
    public void changeCss (String path) throws NullPointerException{
       this.getStylesheets().clear();
       this.getStylesheets().add(path);
       
   }
    
   public static String computeConfigPath(String name) {
       String path = "File:"+ImageJFX.getConfigDirectory() + "/ScriptEditorConfig/" + name + ".css";
       path = path.replaceAll("/", File.separator);
       return path;
    }
   
    public void setPreferencies(TextEditorPreferencies preferencies){
        this.autocomplete = preferencies.isAutocompletion();
        String cssPath = computeConfigPath(preferencies.getTheme());
        try {
            changeCss(cssPath);
        } catch (NullPointerException e) {
            System.out.println("Css file not found");
        }
        /*
        if (preferencies.getTheme().equals("darkTheme")){
            changeCss("File:"+ImageJFX.getConfigDirectory() + "/ScriptEditorConfig/darkTheme.css".replaceAll("/", File.separator));
            
        }
        else if (preferencies.getTheme().equals("lightTheme")){
            changeCss("File:"+ImageJFX.getConfigDirectory() + "/ScriptEditorConfig/lightTheme.css".replaceAll("/", File.separator));
           }
        else{
            try {
                String path = preferencies.getTheme();
                path = "file:"+path;
                changeCss((String) preferencies.getTheme());
            } catch (Exception NullPointerException) {
                changeCss("File:"+ImageJFX.getConfigDirectory() + "/ScriptEditorConfig/darkTheme.css".replaceAll("/", File.separator));
            }
            
           }
           
       */
   }
    
}
