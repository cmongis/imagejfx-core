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
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
//import org.fxmisc.richtext.model.StyleSpans;
//import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 *
 * @author florian
 */
public class TextArea{
    CodeArea codeArea = null;
    private static Hashtable KEYWORDS_TABLE = new Hashtable();
    private static Hashtable KEYWORDS_PATTERN_TABLE = new Hashtable();
    //private static String[] KEYWORDS = new String[]{""};
    
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
    
    private static String[] WHITE = {};
    private static String[] BLACK = {};
    private static String[] RED = {};
    private static String[] BLUE = {};
    private static String[] GREEN = {};
    private static String[] YELLOW = {};
    private static String[] MAGENTA = {};
    private static String[] CYAN = {};

    private static String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static String PAREN_PATTERN = "\\(|\\)";
    private static String BRACE_PATTERN = "\\{|\\}";
    private static String BRACKET_PATTERN = "\\[|\\]";
    private static String SEMICOLON_PATTERN = "\\;";
    private static String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    
    private static  Pattern PATTERN = Pattern.compile(
            
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    
    
     private static final String sampleCode = String.join("\n", new String[] {
        "package com.example;",
        "",
        "import java.util.*;",
        "",
        "public class Foo extends Bar implements Baz {",
        "",
        "    /*",
        "     * multi-line comment",
        "     */",
        "    public static void main(String[] args) {",
        "        // single-line comment",
        "        for(String arg: args) {",
        "            if(arg.length() != 0)",
        "                System.out.println(arg);",
        "            else",
        "                System.err.println(\"Warning: empty string as argument\");",
        "        }",
        "    }",
        "",
        "}"
});
     
    public TextArea() {
        codeArea = new CodeArea();
        //nanorcParser(getClass().getResource("/ijfx/ui/display/code/javascript.nanorc").getFile());
        //nanoRcParseV2(getClass().getResource("/ijfx/ui/display/code/javascript.nanorc").getFile());
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    this.codeArea.setStyleSpans(0, computeHighlighting(this.codeArea.getText()));
                });
        //this.codeArea.replaceText(0, 0, sampleCode);
        
    }
    
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        
        while(matcher.find()) {
            String result = testMatcher(matcher);
            String styleClass =
                    result != null ? result : 
                    //matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null;  assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    public void init(){
        this.KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        this.PAREN_PATTERN = "\\(|\\)";
        this.BRACE_PATTERN = "\\{|\\}";
        this.BRACKET_PATTERN = "\\[|\\]";
        this.SEMICOLON_PATTERN = "\\;";
        this.STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        this.COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
        
        this.PATTERN = Pattern.compile(
            //"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
             generatePattern()
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        );
    }
    
    public static String generatePattern(){
        String pat = "";
        for (Object key : KEYWORDS_PATTERN_TABLE.keySet()){
            pat = pat.concat("(?<" + key.toString() + ">" + KEYWORDS_PATTERN_TABLE.get(key) + ")");
        }
        return pat;
    }
    
    public static String testMatcher(Matcher matcher){
        for (Object key : KEYWORDS_PATTERN_TABLE.keySet()){
            if (matcher.group(key.toString()) != null) return key.toString();
        }
        return null;
    }
    public void initLanguage(String path){
        nanoRcParseV2(getClass().getResource(path).getFile());
        init();
    }
    public CodeArea getCodeArea() {
        return this.codeArea;
    }
    public void setText(String text){
        this.codeArea.replaceText(0, 0, text);
    }
    /*
    public void nanorcParser(String path){
        
        Hashtable keywords = new Hashtable(); // creation de la table de hashage
        if (path.equals(null)){
            System.out.println("Fichier null !");
            return;
        }
        List<String> text = new ArrayList<>();
        File file = new File(path);
        try {
            text = Files.readAllLines(file.toPath(), Charset.defaultCharset());// lecture du fichier, tout est mis dans la list text
                    } catch (IOException ex) {
            Logger.getLogger(TextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Pattern p = Pattern.compile("\"\\\\\\<\\(.*");
        //System.out.println(")\\>\"");
        
        Boolean string = false;
        Boolean comments = false;
        
        
        for (String line : text){
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
            text = Files.readAllLines(file.toPath(), Charset.defaultCharset());// lecture du fichier, tout est mis dans la list text
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
