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

import java.util.LinkedList;
import java.util.SortedSet;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import org.fxmisc.richtext.CodeArea;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import org.scijava.script.ScriptLanguage;

/**
 *
 * @author florian
 */
public class DefaultAutocompletion implements Autocompletion {

    /**
     * The existing autocomplete entries.
     */
    private SortedSet<String> entries;
    /**
     * The popup used to select an entry.
     */
    private ContextMenu entriesPopup;
    //private StringProperty textProperty;
    //private Node codeArea;
    private DefaultTextArea textArea;
    private ScriptLanguage language;
    private AutocompletionList listProvider;

    /**
     * Construct a new AutoCompleteTextField.
     */
    public DefaultAutocompletion(DefaultTextArea textArea, SortedSet<String> entries) {
        super();
        this.entries = entries;
        //this.textProperty = textProperty;
        this.textArea = textArea;
        //this.codeArea = textArea.getCodeArea();
        this.entriesPopup = new ContextMenu();
    }

    public DefaultAutocompletion(DefaultTextArea textArea, AutocompletionList listProvider) {
        this.textArea = textArea;
        this.listProvider = listProvider;

    }

    public DefaultAutocompletion(DefaultTextArea textArea) {
        this.textArea = textArea;
        //this.codeArea = textArea.getCodeArea();
        this.entriesPopup = new ContextMenu();
        this.entries = new TreeSet<>();
    }

    /**
     * Compute the autocompletion on the given word, the context menu
     * entriesPopup is shown from here
     *
     * @param word a string representing the word on wich the autocompletion
     * will be computed
     */
    @Override
    public ContextMenu computeAutocompletion(String word) {
        
        this.listProvider.computeAutocompletion(this.textArea.getCodeArea().getText(), word);
        this.entries = this.listProvider.getEntries();

        if (word.length() == 0) {
            //this.entriesPopup.hide();
            return null;
        } else {
            LinkedList<String> searchResult = new LinkedList<>();
            final List<String> filteredEntries = entries
                    .stream()
                    .filter(e -> e.toLowerCase().contains(word.toLowerCase())).collect(Collectors.toList());
            searchResult.addAll(filteredEntries);
            if (entries.size() > 0) {
                populatePopup(searchResult);
                if (!this.entriesPopup.isShowing()) {
                    //this.entriesPopup.show(this.codeArea, Side.BOTTOM, 0, 0);
                    return this.entriesPopup;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() {
        return entries;
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        // I would like to put it at max value, but i dont't know how to resize the contextMenu and make it scrollable
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    textArea.replaceWord(result);
                    entriesPopup.hide();
                }
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }

    @Override
    public void setEntries(SortedSet<String> entries) {
        this.entries = entries;
    }

    public void setTextArea(DefaultTextArea textArea) {
        this.textArea = textArea;
    }

    public DefaultTextArea getTextArea() {
        return textArea;
    }

    @Override
    public void setListProvider(AutocompletionList listProvider) {
        this.listProvider = listProvider;
    }

    @Override
    public AutocompletionList getListProvider() {
        return this.listProvider;
    }

}
