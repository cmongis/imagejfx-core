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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 */
public class NanorcParser implements LanguageKeywords{
    
    private Hashtable keywordsTable;
    private ScriptLanguage language;
    private String nanorcFile;

    public NanorcParser(ScriptLanguage language) {
        keywordsTable = new Hashtable();
        this.language = language;
    }

    public NanorcParser() {
        keywordsTable = new Hashtable();
    }
    
    
    
    @Override
    public Hashtable getKeywords() {
        return this.keywordsTable;
    }

    @Override
    public void setLanguage(ScriptLanguage language) {
        this.language = language;
        run();
    }

    @Override
    public void run() {
        try {
            this.keywordsTable.clear();
            this.nanorcFile = findFileLanguage(language);
            nanoRcParseV2(this.nanorcFile);
            //computeComment();
        } catch (NullPointerException e) {
            this.keywordsTable = new Hashtable();
        }
        
    }
    
    public static String findFileLanguage(ScriptLanguage language) {
       String path = ImageJFX.getConfigDirectory() + "/ScriptEditorConfig/%s.nanorc".replaceAll("/", File.separator);
       return String.format(path,language.getLanguageName().toLowerCase().replace(" ", ""));
    }
    /*
    public void computeComment(){
        if (this.language.getLanguageName().equals("Python")){
            this.keywordsTable.put("COMMENT", "#[^\n]*" + "|" + "\"\"\"(.|\\R)*?\"\"\""+ "|" + "\'\'\'(.|\\R)*?\'\'\'");
        }
        else{*/
    
            //this.keywordsTable.put("COMMENT", "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/");
    /*
        }
    }
    */
    public void nanoRcParseV2(String path){
        List<String> text = new ArrayList<>();
        File file = new File(path);
        try {
            text = Files.readAllLines(file.toPath(), Charset.defaultCharset());// lecture du fichier, tout est mis dans la list textProperty
                    } 
        catch (IOException ex) {
            Logger.getLogger(DefaultTextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String line : text){
            String[] splitedLine = line.split(" ");
            if (splitedLine.length <= 1) continue;
            else {
                if (splitedLine[0].equals("color")){
                    if (splitedLine[2].matches("\"\\\\\\<\\(.*")){                                       // check if the regex start with ""\<(" = keywords in nanorc
                        if (this.keywordsTable.containsKey(splitedLine[1])){
                                String pattern = (String) this.keywordsTable.get(splitedLine[1]);
                                String newWords = splitedLine[2];
                                newWords = newWords.replace("\"\\<(", "").replace(")\\>\"", "");
                                pattern = pattern.replace(")\\b", "|"+newWords);
                                pattern = pattern.concat(")\\b");
                                addKeyword(pattern, splitedLine[1]);
                                //this.keywordsTable.put(splitedLine[1], pattern);
                            }
                        else {
                            String pattern = splitedLine[2];
                            pattern= pattern.replace("\"\\<(", "\\b(");
                            pattern = pattern.replace(")\\>\"", ")\\b");
                            addKeyword(pattern, splitedLine[1]);
                            //this.keywordsTable.put(splitedLine[1], pattern); //                                  adding the list in the hash table
                        }
                    }
                    else if (splitedLine[2].matches("start.*")){
                        if (this.keywordsTable.containsKey(splitedLine[1])){
                            String pattern = (String) this.keywordsTable.get(splitedLine[1]);
                            
                            String[] patterns = computeCommentPattern(splitedLine);
                            pattern = pattern.concat("|" + patterns[0] + "(.|\\R)*?" + patterns[1]);
                            addKeyword(pattern, splitedLine[1]);
                            //this.keywordsTable.put(splitedLine[1], pattern);
                        }
                        else {
                           
                            String[] patterns = computeCommentPattern(splitedLine);
                            String pattern = patterns[0].concat("(.|\\R)*?" + patterns[1]);
                            addKeyword(pattern, splitedLine[1]);
                            //this.keywordsTable.put(splitedLine[1], pattern);
                        }
                    //System.out.println(this.keywordsTable.get(splitedLine[1]));
                    }
                    else {
                        if (this.keywordsTable.containsKey(splitedLine[1])){
                            // TODO : gerer quand il faut concatener avec une couleur existente
                            String pattern = splitedLine[2];
                            pattern = pattern.substring(1, pattern.length()-1);
                            pattern = pattern.replace(".*$", "[^\n]*");
                            String oldpattern = (String) this.keywordsTable.get(splitedLine[1]);
                            pattern = oldpattern.concat("|" + pattern);
                            addKeyword(pattern, splitedLine[1]);
                            //this.keywordsTable.put(splitedLine[1], pattern);
                        }
                        else {
                            String pattern = splitedLine[2];
                            pattern = pattern.substring(1, pattern.length()-1);
                            pattern = pattern.replace(".*$", "[^\n]*");
                            addKeyword(pattern, splitedLine[1]);
                            //this.keywordsTable.put(splitedLine[1], pattern);
                        }
                        
                    }
                    
                    
                }
            }
        }
    }
    
    public String[] computeCommentPattern(String[] splitedLine){
         String patternStart = splitedLine[2];
        patternStart = patternStart.replace("start=\"", "");
        patternStart = patternStart.substring(0, patternStart.length()-1);
        patternStart = patternStart.replace("[^'),]", "");
        patternStart = patternStart.replace("[^\"),]", "");
        patternStart = patternStart.replace("\\\"\\\"\\\"", "\"\"\"");
        String patternEnd = splitedLine[3];
        patternEnd = patternEnd.replace("end=\"(^|[^(\\])", "");
        patternEnd = patternEnd.substring(0, patternEnd.length()-1);
        patternEnd = patternEnd.replace("\\\"\\\"\\\"", "\"\"\"");
        return new String[]{patternStart,patternEnd};
    }
    
    public void addKeyword(String pattern, String key){
        try {
            Pattern.compile(pattern.concat("|(?<" + key.toString() + ">" + pattern + ")"));
            this.keywordsTable.put(key, pattern);
        } catch (Exception e) {
        }
        
    }
}
