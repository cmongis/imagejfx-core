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
package ijfx.ui.filter.string;

import ijfx.ui.filter.StringFilter;
import ijfx.ui.main.ImageJFX;
import mongis.utils.CollectionsUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Tuan anh TRINH
 */
public class DefaultStringFilter extends BorderPane implements Initializable, StringFilter {

    /**
     * Initializes the controller class.
     */
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField textField;
    @FXML
    private ToggleButton moreButton;
    @FXML
    private ListView<Item> listView;
    
    private final ObservableList<ItemWrapper> allItems = FXCollections.observableArrayList();
    private final ObservableList<Item> displayedItems = FXCollections.observableArrayList();
    //private boolean bigger = false;

    private String CSS_FILE = getClass().getResource("DefaultStringFilter.css").toExternalForm();

    // no static method !!! this is a reusable widget !
    private Property<Predicate<String>> predicate = new SimpleObjectProperty();

    private final BooleanBinding lotOfItems = Bindings.createBooleanBinding(() -> allItems.size() > 5, allItems);
    private final BooleanProperty showAll = new SimpleBooleanProperty(false);

    private final DoubleBinding prefHeight = Bindings.createDoubleBinding(this::calculateHeight, lotOfItems);
    
    private String name;
    
    public DefaultStringFilter() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ijfx/ui/filter/string/DefaultStringFilter.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            
            // loading the FXML
            loader.load();
            
            // setting the visible property of the button so it appears only
            // only when there is a lot of items and the button option showall is not true
            moreButton.visibleProperty().bind(lotOfItems);

            // adding the CSS stylesheet
            getStylesheets().add(CSS_FILE);

            textField.visibleProperty().bind(lotOfItems);
            
            // change the factory to the class method
            listView.setCellFactory(this::createListCell);
            listView.setItems(displayedItems);
            
            // showing the textfield only when there is a lot of items
            //textField.disableProperty().bind(lotOfItems.not());
            listView.prefHeightProperty().bind(prefHeight);
            
           
            
            showAll.bind(moreButton.selectedProperty());
            showAll.addListener(this::onShowAllPropertyChange);
        } catch (IOException e) {
            ImageJFX.getLogger().log(Level.SEVERE,null,e);
        }
    }

    
   
    
    
    
    public ListCell<Item> createListCell(ListView<Item> listView) {

        // I give a pointer to a runnable that will be executed each time
        // the something is ticked.
        // It makes the code more reuable
        ListCellCheckbox listCellCheckBox = new ListCellCheckbox(this::setPredicate);

        return listCellCheckBox;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textField.onKeyPressedProperty();     

    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

    @Override
    public synchronized void setAllPossibleValues(Collection<? extends String> list) {
        
        // map containing the item count
        Map<String, Integer> itemCount = new HashMap<>();
        
        // for each string of the list, we count the number of items
        list.stream().forEach((s) -> {
            if (itemCount.get(s) == null) {
                itemCount.put(s, 1);
            } else {
                itemCount.put(s, itemCount.get(s) + 1);
            }
        });
        
        // after that, we create item wrapper that will contain the item and the count
        List<ItemWrapper> items = 
       
        itemCount
                .keySet()
                .stream()
                .map(key->{
                    
                    ItemWrapper item =  allItems
                            .stream()
                            .filter(existing->existing.getName().equals(key))
                            .findFirst()
                            .orElse(new ItemWrapper(key,itemCount.get(key)));
                    
                    item.setNumber(itemCount.get(key));
                    
                    return item;
                    
                })
                .collect(Collectors.toList());
        
       
        
       
        
        
        /*
        CollectionsUtils.synchronize(items, allItems,this::compare,(i1,i2)->{
            i2.setNumber(i1.getNumber());
        });*/
        
        CollectionsUtils.synchronize(items, allItems);
        
        // updating count
        //allItems.forEach(item->item.setNumber(itemCount.get(item.getName())));
        //allItems.forEach(item->item.setState(false));
        Platform.runLater(this::updateDisplayedItems);
        
        predicateProperty().setValue(null);

    }

    private int compare(Item i1, Item i2) {
        return i1.getName().equals(i2.getName()) ? 0 : 1;
    }
    
    
    private void onShowAllPropertyChange(Observable obs, Boolean oldValue, Boolean newValue) {
       
        updateDisplayedItems(textField.getText(),newValue,lotOfItems.getValue());
    }
    
    @FXML
    private void updateDisplayedItems() {   
        updateDisplayedItems(textField.getText(), showAll.getValue(), lotOfItems.getValue());
    }
    
    private void updateDisplayedItems(String s, boolean showAll, boolean lotOfItems) {
      
        List<ItemWrapper> itemToShow;

        // if there is a query
        // we filter
        if (s != null && s.trim().equals("") == false) {
            itemToShow = allItems.filtered(item -> item.getName().contains(s));
        } // if everything should be shown or if there is not a lot of item, we show everything
        else if (showAll || !lotOfItems) {
            itemToShow = allItems;
        } // otherwise is means only a small set of items should be shown
        else {
            itemToShow = allItems.subList(0, 5);
        }
        
       displayedItems.clear();
       displayedItems.addAll(itemToShow);
        
       CollectionsUtils.synchronize(displayedItems, listView.getItems());

    }

    

    private void setPredicate() {
        List<String> listBuffer = new ArrayList<>();

        if (displayedItems.stream().filter(e -> e.getState()).count() == 0) {

            predicate.setValue(null);
            return;
        }

        listBuffer = displayedItems
                .stream()
                .filter(e -> e.getState())
                .map(e -> e.getName())
                .collect(Collectors.toList());
        predicate.setValue(new ContainStringPredicate(listBuffer));
    }

    @Override
    public Property<Predicate<String>> predicateProperty() {
        return predicate;
    }

    @Override
    public StringProperty keyWordProperty() {
        return textField.textProperty();
    }

    @Override
    public Node getContent() {
        return this;
    }

    private class ContainStringPredicate implements Predicate<String> {

        private final List<String> strings;

        public ContainStringPredicate(List<String> strings) {
            this.strings = strings;
        }

        @Override
        public boolean test(String t) {
            return strings.contains(t);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    
    
    private Double calculateHeight() {
        if(lotOfItems.getValue()) return 200d;
        else return 40d * allItems.size();
    }
}
