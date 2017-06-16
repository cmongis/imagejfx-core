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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 *
 * @author florian
 */
public class DefaultScriptHighlighting implements ScriptHighlight{
    
     private Hashtable KEYWORDS_PATTERN_TABLE;
     private  Pattern PATTERN;
     
     private String OPEN_PAREN = "\\(";
    private String CLOSE_PAREN = "\\)";
    private String OPEN_BRACE = "\\[";
    private String CLOSE_BRACE = "\\]";
    private String OPEN_BRACKET = "\\{";
    private String CLOSE_BRACKET = "\\}";
    
    private Pattern PAREN_PATTERNBIS = Pattern.compile(
        "(?<OPENPAREN>" + OPEN_PAREN + ")" 
        + "|(?<CLOSEPAREN>" + CLOSE_PAREN + ")"
        +"|(?<OPENBRACE>" + OPEN_BRACE + ")" 
        + "|(?<CLOSEBRACE>" + CLOSE_BRACE + ")"
        + "|(?<OPENBRACKET>" + OPEN_BRACKET + ")" 
        + "|(?<CLOSEBRACKET>" + CLOSE_BRACKET + ")"
    );

    public DefaultScriptHighlighting(Hashtable KEYWORDS_PATTERN_TABLE) {
        this.KEYWORDS_PATTERN_TABLE = KEYWORDS_PATTERN_TABLE;
        initPattern();
    }
    
    
    
    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
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
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    "null";  assert styleClass != null;
            
            
            spansBuilder.add(Collections.singleton("null"), matcher.start() - lastKwEnd);               // ajoute un style null entre les deux styles 
             spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start()); // ajout du style en question sur le nombre de characteres apropriés
            lastKwEnd = matcher.end();
        }
        
        spansBuilder.add(Collections.singleton("null"), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
     public String testMatcher(Matcher matcher){
        for (Object key : KEYWORDS_PATTERN_TABLE.keySet()){
            if (matcher.group(key.toString()) != null) return key.toString();
        }
        return null;
    }
    public String higlightOrphelinBracket(int previous, int next, Hashtable<String,List> orphan ){
      for (List<Integer[]> list : orphan.values()){
          for (Integer[] i : list){
                if (i[0] == next){
                    return "uncompleteBracket";
                }
            }
        } 
      return null;
    }
    
    
    public Hashtable<String,List> detectBracket (String text){
        Matcher matcher = PAREN_PATTERNBIS.matcher(text);
        int lastKwEnd = 0; 
        Hashtable<String,List> orphan = new Hashtable();
        
        orphan.put("OPENPAREN", new ArrayList<Integer[]>());
        orphan.put("CLOSEPAREN", new ArrayList<Integer[]>());
        orphan.put("OPENBRACE", new ArrayList<Integer[]>());
        orphan.put("CLOSEBRACE", new ArrayList<Integer[]>());
        orphan.put("OPENBRACKET", new ArrayList<Integer[]>());
        orphan.put("CLOSEBRACKET", new ArrayList<Integer[]>());
        
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
    
    public void initPattern(){
        String PAREN_PATTERN = "\\(|\\)";
        String BRACE_PATTERN = "\\{|\\}";
        String BRACKET_PATTERN = "\\[|\\]";
        String SEMICOLON_PATTERN = "\\;";
        String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
        
        this.PATTERN = Pattern.compile(             
            "(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + generatePattern()
        );
    }
    
    public String generatePattern(){
        String pat = "";
        for (Object key : this.KEYWORDS_PATTERN_TABLE.keySet()){
            pat = pat.concat("|(?<" + key.toString() + ">" + this.KEYWORDS_PATTERN_TABLE.get(key) + ")");
        }
        return pat;
    }

    @Override
    public void setKeywords(Hashtable keywordTable) {
        this.KEYWORDS_PATTERN_TABLE = keywordTable;
        initPattern();
    }
}
