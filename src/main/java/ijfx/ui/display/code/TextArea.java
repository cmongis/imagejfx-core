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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptService;
//import org.fxmisc.richtext.model.StyleSpans;
//import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 *
 * @author florian
 */
public class TextArea extends AnchorPane{
    
    private CodeArea codeArea = null;
    
    
    //private StringProperty selectedText;
    
    private final StringProperty selectedTextProperty;
    private final StringProperty textProperty;
    private final ObjectProperty<IndexRange> selectionProperty;
    
    
    private static Hashtable KEYWORDS_TABLE = new Hashtable();
    private static Hashtable KEYWORDS_PATTERN_TABLE = new Hashtable();
    private static String[] KEYWORDS = new String[]{""};
    /*
    private static String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };
    */
    private static String[] WHITE = {};
    private static String[] BLACK = {};
    private static String[] RED = {};
    private static String[] BLUE = {};
    private static String[] GREEN = {};
    private static String[] YELLOW = {};
    private static String[] MAGENTA = {};
    private static String[] CYAN = {};

    //private static String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static String PAREN_PATTERN = "\\(|\\)";
    private static String BRACE_PATTERN = "\\{|\\}";
    private static String BRACKET_PATTERN = "\\[|\\]";
    private static String SEMICOLON_PATTERN = "\\;";
    private static String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    
    private static String OPEN_PAREN = "\\(";
    private static String CLOSE_PAREN = "\\)";
    private static String OPEN_BRACE = "\\[";
    private static String CLOSE_BRACE = "\\]";
    private static String OPEN_BRACKET = "\\{";
    private static String CLOSE_BRACKET = "\\}";
    
    private static Pattern PAREN_PATTERNBIS = Pattern.compile(
        "(?<OPENPAREN>" + OPEN_PAREN + ")" 
        + "|(?<CLOSEPAREN>" + CLOSE_PAREN + ")"
        +"|(?<OPENBRACE>" + OPEN_BRACE + ")" 
        + "|(?<CLOSEBRACE>" + CLOSE_BRACE + ")"
        + "|(?<OPENBRACKET>" + OPEN_BRACKET + ")" 
        + "|(?<CLOSEBRACKET>" + CLOSE_BRACKET + ")"
    );
    
    private static  Pattern PATTERN = Pattern.compile(
            
            //"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            // "|(?<PAREN>" + PAREN_PATTERN + ")"
             "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    
    
    public TextArea() {
        
        this.codeArea = new CodeArea();
        //nanorcParser(getClass().getResource("/ijfx/ui/display/code/javascript.nanorc").getFile());
        //nanoRcParseV2(getClass().getResource("/ijfx/ui/display/code/javascript.nanorc").getFile());
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
                    //codeArea.setStyleSpans(0, computeBracket(codeArea.getText()));
                });
        /*
         this.codeArea.richChanges()
                .filter(ch -> ch.getInserted().equals("(") || ch.getInserted().equals(")")) // XXX
                 
                .subscribe(change -> {
                    
                    Platform.runLater( () ->{
                        codeArea.setStyleSpans(0, computeBracket(codeArea.getText()));
                    });
                    
                    
                });
        */
        selectedTextProperty = new SimpleStringProperty();
        selectedTextProperty.bind(this.codeArea.selectedTextProperty());
        
        
        //scriptDisplay.textProperty().bind(textProperty);
        selectionProperty = new SimpleObjectProperty<>();
        selectionProperty.bind(this.codeArea.selectionProperty());
        
        textProperty = new SimpleStringProperty();
        textProperty.bind(this.codeArea.textProperty());
        
        this.getChildren().add(this.codeArea);
        getStylesheets().add(getClass().getResource("/ijfx/ui/display/code/JavaRichtext.css").toExternalForm());
        
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        Hashtable<String,List> orphan = detectBracket(text);
        while(matcher.find()) {
            String result = testMatcher(matcher);
            String orphelinBracket = higlightOrphelinBracket(lastKwEnd, matcher.start(), orphan);
            String styleClass =
                    result != null ? result : 
                    orphelinBracket != null ? orphelinBracket:
                    //matcher.group("KEYWORD") != null ? "keyword" :
                    //matcher.group("PAREN") != null ? "paren" :
                    //matcher.group("BRACE") != null ? "brace" :
                    //matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null;  assert styleClass != null;
            
            
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);               // ajoute un style null entre les deux styles 
             spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start()); // ajout du style en question sur le nombre de characteres apropriés
            lastKwEnd = matcher.end();
        }
        //spansBuilder = computeBracket(spansBuilder,text);
        
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    public void init(){
        //this.KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        this.PAREN_PATTERN = "\\(|\\)";
        this.BRACE_PATTERN = "\\{|\\}";
        this.BRACKET_PATTERN = "\\[|\\]";
        this.SEMICOLON_PATTERN = "\\;";
        this.STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        this.COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
        
        this.PATTERN = Pattern.compile(
            //"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
             
            "(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + generatePattern()
        );
    }
    
    public static String generatePattern(){
        String pat = "";
        for (Object key : KEYWORDS_PATTERN_TABLE.keySet()){
            pat = pat.concat("|(?<" + key.toString() + ">" + KEYWORDS_PATTERN_TABLE.get(key) + ")");
        }
        return pat;
    }
    
    public static String testMatcher(Matcher matcher){
        for (Object key : KEYWORDS_PATTERN_TABLE.keySet()){
            if (matcher.group(key.toString()) != null) return key.toString();
        }
        return null;
    }
    public static String higlightOrphelinBracket(int previous, int next, Hashtable<String,List> orphan ){
      for (List<Integer[]> list : orphan.values()){
          for (Integer[] i : list){
                if (i[0] == next){
                    return "uncompleteBracket";
                }
            }
        } 
      return null;
    }
    
    
    public static Hashtable<String,List> detectBracket (String text){
        Matcher matcher = PAREN_PATTERNBIS.matcher(text);
        int lastKwEnd = 0; 
        Hashtable<String,List> orphan = new Hashtable();
        
        orphan.put("OPENPAREN", new ArrayList<Integer[]>());
        orphan.put("CLOSEPAREN", new ArrayList<Integer[]>());
        orphan.put("OPENBRACE", new ArrayList<Integer[]>());
        orphan.put("CLOSEBRACE", new ArrayList<Integer[]>());
        orphan.put("OPENBRACKET", new ArrayList<Integer[]>());
        orphan.put("CLOSEBRACKET", new ArrayList<Integer[]>());
        
        List<Integer[]> uncompleteList = new ArrayList<>();
        while(matcher.find()) {
            if (matcher.group("OPENPAREN") != null ) orphan.get("OPENPAREN").add(new Integer[]{matcher.start(),matcher.end()});
            else if (matcher.group("CLOSEPAREN") != null ){
                if (orphan.get("OPENPAREN").size() != 0) {
                    orphan.get("OPENPAREN").remove(orphan.get("OPENPAREN").size()-1);
                }
                else{
                   orphan.get("CLOSEPAREN").add(new Integer[]{matcher.start(),matcher.end()});
                }
            }
            if (matcher.group("OPENBRACE") != null ) orphan.get("OPENBRACE").add(new Integer[]{matcher.start(),matcher.end()});
            else if (matcher.group("CLOSEBRACE") != null ){
                if (orphan.get("OPENBRACE").size() != 0) {
                    orphan.get("OPENBRACE").remove(orphan.get("OPENBRACE").size()-1);
                }
                else{
                   orphan.get("CLOSEBRACE").add(new Integer[]{matcher.start(),matcher.end()});
                }
            }
            if (matcher.group("OPENBRACKET") != null ) orphan.get("OPENBRACKET").add(new Integer[]{matcher.start(),matcher.end()});
            else if (matcher.group("CLOSEBRACKET") != null ){
                if (orphan.get("OPENBRACKET").size() != 0) {
                    orphan.get("OPENBRACKET").remove(orphan.get("OPENBRACKET").size()-1);
                }
                else{
                   orphan.get("CLOSEBRACKET").add(new Integer[]{matcher.start(),matcher.end()});
                }
            }
            
        }
        return orphan;
    }
    
   
    public void initLanguage(String path){
        nanoRcParseV2(getClass().getResource(path).getFile());
        init();
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
    
    /*
    public void nanorcParser(String path){
        
        Hashtable keywords = new Hashtable(); // creation de la table de hashage
        if (path.equals(null)){
            System.out.println("Fichier null !");
            return;
        }
        List<String> textProperty = new ArrayList<>();
        File file = new File(path);
        try {
            textProperty = Files.readAllLines(file.toPath(), Charset.defaultCharset());// lecture du fichier, tout est mis dans la list textProperty
                    } catch (IOException ex) {
            Logger.getLogger(TextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Pattern p = Pattern.compile("\"\\\\\\<\\(.*");
        //System.out.println(")\\>\"");
        
        Boolean string = false;
        Boolean comments = false;
        
        
        for (String line : textProperty){
            String[] splitedLine = line.split(" ");
            if (splitedLine.length <= 1) continue;
            if (splitedLine[1].matches("Strings.")) { //                     checking if we arrive in the "strings" paragraph
                string = true;
            }
            else if (splitedLine[1].matches("Comments.")){ //                checking if we arrive in the "Comments" paragraph
                comments = true;
            }
            if (splitedLine[0].equals("color") && !string){
                if (splitedLine[2].matches("\"\\\\\\<\\(.*")){             // check if the regex start with ""\<(" = keywords in nanorc
                    
                    List<String> words = new ArrayList<>(); //               creation d'une entree dans la table, la valeur est une liste qui contiendra les mots
                    String chain = splitedLine[2].replace(")\\>\"", ""); // removing the unintersting end of the string
                    chain = chain.replace("\"\\<(", "");                 // same for the beginning
                    for (String word : chain.split("\\|")){ //              spliting with |
                        words.add(word);//                                  adding in the list
                    }
                     
                    if (keywords.containsKey(splitedLine[1])){
                        List list = (List) keywords.get(splitedLine[1]);
                        list.addAll(words);
                        keywords.put(splitedLine[1], list);
                    }
                    else {
                        keywords.put(splitedLine[1], words); //                  adding the list in the hash table
                    }
                    
                }
                
            }
            else if (splitedLine[0].equals("color") && string && !comments){ // don't work all the time
                keywords.put("stringPattern", splitedLine[2]);
            }
            else if (splitedLine[0].equals("color") && string && comments){
                keywords.put("commentPattern", splitedLine[2]);
            }
            
        }
        
        convertNanoToRichText(keywords);
        //System.out.println(KEYWORDS_TABLE);
    }
    
    public void convertNanoToRichText (Hashtable keywords){
        for (Object key : keywords.keySet()){
            
            if ((!key.toString().equals("stringPattern")) && (!key.toString().equals("commentPattern"))){
                List<String> list = (List<String>) keywords.get(key);
                KEYWORDS_TABLE.put((String) key, list);
            }
            List<String> length = new ArrayList<>();
            length.addAll((List) KEYWORDS_TABLE.get("magenta"));
            length.addAll((List) KEYWORDS_TABLE.get("green"));
            length.addAll((List) KEYWORDS_TABLE.get("brightyellow"));
            KEYWORDS = new String[length.size()];
            for (int i = 0; i < length.size(); i++) {
                this.KEYWORDS[i]= length.get(i).toString();
            }
        
        }
    }
    */
    public void nanoRcParseV2(String path){
        List<String> text = new ArrayList<>();
        File file = new File(path);
        try {
            text = Files.readAllLines(file.toPath(), Charset.defaultCharset());// lecture du fichier, tout est mis dans la list textProperty
                    } catch (IOException ex) {
            Logger.getLogger(TextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String line : text){
            String[] splitedLine = line.split(" ");
            if (splitedLine.length <= 1) continue;
            else {
                if (splitedLine[0].equals("color")){
                    if (splitedLine[2].matches("\"\\\\\\<\\(.*")){                                       // check if the regex start with ""\<(" = keywords in nanorc
                        if (KEYWORDS_PATTERN_TABLE.containsKey(splitedLine[1])){
                                String pattern = (String) KEYWORDS_PATTERN_TABLE.get(splitedLine[1]);
                                pattern = pattern.replace(")\\b", "|"+splitedLine[2]);
                                pattern = pattern.concat(")\\b");
                                KEYWORDS_PATTERN_TABLE.put(splitedLine[1], pattern);
                            }
                        else {
                            String pattern = splitedLine[2];
                            pattern= pattern.replace("\"\\<(", "\\b(");
                            pattern = pattern.replace(")\\>\"", ")\\b");
                            KEYWORDS_PATTERN_TABLE.put(splitedLine[1], pattern); //                                  adding the list in the hash table
                        }
                    }
                    
                }
            }
        }
    }
    
}
