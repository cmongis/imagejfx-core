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
package ijfx.ui.widgets;

import static com.squareup.okhttp.internal.Internal.logger;
import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataKeyPriority;
import ijfx.explorer.datamodel.Explorable;
import ijfx.ui.display.metadataowner.ExplorableTableHelper;
import ijfx.ui.display.metadataowner.MetaDataOwnerHelper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import mongis.utils.FXUtilities;

/**
 *
 * @author Cyril MONGIS, 2016
 */
public class ExplorableSelector extends BorderPane {

    @FXML
    TableView<Explorable> tableView;

    @FXML
    TextField filterTextField;

    @FXML
    Button markButton;

    @FXML
    Label markedLabel; // label representing the number of marked object

    private final ObservableList<Explorable> filteredFiles = FXCollections.observableArrayList();

    private final ObservableList<Explorable> addedFiles = FXCollections.observableArrayList();

    private ObservableList<Explorable> markedItemProperty;

    ExplorableTableHelper helper;

    BooleanBinding isFilterOn;

    /**
     * List of selected explorable in the table view
     */
    ListProperty<Explorable> selectedCountProperty;

    /**
     * Binding representing is multiple object are selected inside the table
     * view
     */
    BooleanBinding isMultipleSelection;

    /**
     * Binding representing the lalbel of the button used to mark and unmark
     * elements
     */
    StringBinding markButtonText;

    StringBinding markLabelText;

    private final static String MARK_SELECTION = "Mark selection";
    private final static String MARK_ALL = "Mark all";
    private final static String MARK_ALL_FILTERED = "Mark all filtered";

    private final static String MARK_LABEL_TEXT = "%d files marked for processing";


    public ExplorableSelector() {

        try {
            FXUtilities.injectFXML(this, "/ijfx/ui/widgets/ExplorableSelector.fxml");

            helper = new ExplorableTableHelper(tableView);
            helper.setPriority(MetaData.NAME, MetaData.FILE_SIZE);
            tableView.setItems(filteredFiles);
            markedItemProperty = helper.getMarkedItemList();
            //markedColumn.setCellFactory(this::generateCheckBoxCell);
            //    markedColumn.setEditable(true);
            tableView.setEditable(true);
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableView.setItems(filteredFiles);

            filterTextField.setOnKeyTyped(this::onKeyTyped);

            // defining remaining properties
            // true when the filter has something written inside
            isFilterOn = Bindings.notEqual("", filterTextField.textProperty());

            // property giving access to the number of selected files inside the table view
            selectedCountProperty = new SimpleListProperty<>(tableView.getSelectionModel().getSelectedItems());

            // true when multiple selection
            isMultipleSelection = Bindings.greaterThan(selectedCountProperty.sizeProperty(), 1);

            // creating a binding calling a method deciding for the mark name
            markButtonText = Bindings.createStringBinding(this::getMarkButtonText,tableView.getSelectionModel().getSelectedItems(), isFilterOn, isMultipleSelection);

            // binding the mark button to the property
            markButton.textProperty().bind(markButtonText);

            markLabelText = Bindings.createStringBinding(this::updateMarkedLabel, addedFiles,markedItemProperty());

            markedLabel.textProperty().bind(markLabelText);

            addedFiles.addListener(this::onItemsAdded);

            //new OpacityTransitionBinding(this, selectedCountProperty.emptyProperty().not());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            markedItemProperty = null;
        }
        
    }

    /*
    
        FXML UI Methods
    
     */
    private String updateMarkedLabel() {

        long count = markedItemProperty().size();

        return String.format(MARK_LABEL_TEXT, count);

    }

    @FXML
    private void markSelection() {
        
        
        /*
        if (isMultipleSelection.getValue()) {
            tableView
                    .getSelectionModel()
                    .getSelectedItems()
                    .forEach(this::markFileForSelection);
        } else if (isFilterOn.getValue()) {
            filteredFiles.forEach(this::markFileForSelection);
        } else {
            addedFiles.forEach(this::markFileForSelection);
        }*/
        
        getToModify().forEach(this::markFile);
        
        
        helper.refresh();

    }
    
    private boolean shouldModifyAll() {
        return tableView.getSelectionModel().getSelectedItems().size() > 1 && selectedCountProperty.size() != markedItemProperty().size();
    }
    
    
    private List<Explorable> getToModify() {
        if(shouldModifyAll()) {
            
            return tableView.getSelectionModel().getSelectedItems();
            
        }
        else {
            return tableView.getItems();
        }
    }

    @FXML
    private void unmarkSelection() {
      getToModify().forEach(this::unmarkFile);
      helper.refresh();
    }

    @FXML
    private void deleteSelection() {
       addedFiles.removeAll(tableView.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void deleteAll() {

        addedFiles.clear();
        filteredFiles.clear();
    }

    private void markFile(Explorable fileInputModel) {
        if(markedItemProperty.contains(fileInputModel) == false)
        markedItemProperty.add(fileInputModel);
    }
    
    private void unmarkFile(Explorable exp) {
        markedItemProperty.remove(exp);
    }

    private boolean isMarked(Explorable finputModel) {
        return markedItemProperty.contains(finputModel);
    }

    private String getMarkButtonText() {

        if (shouldModifyAll()) {
            return MARK_SELECTION;
        } else if (isFilterOn.getValue()) {
            return MARK_ALL_FILTERED;
        } else {
            return MARK_ALL;
        }

    }

    private void onKeyTyped(KeyEvent event) {
        updateFilter();
    }

    private void updateFilter() {
        final String filterContent = filterTextField.getText().toLowerCase();

        // if nothing is on the filter field
        if (filterContent.trim().equals("")) {
            this.filteredFiles.clear();
            this.filteredFiles.addAll(addedFiles);

        } // filtering the files
        else {
            List<Explorable> filteredFiles = addedFiles
                    .parallelStream()
                    .filter(explorable -> {
                        return explorable.getTitle().toLowerCase().contains(filterContent);

                    }).collect(Collectors.toList());

            this.filteredFiles.clear();
            this.filteredFiles.addAll(filteredFiles);

        }
    }

    public void setItems(Collection<? extends Explorable> items) {
        this.addedFiles.clear();
        addItem(items);
    }

    private void addItem(Collection<? extends Explorable> items) {
        if (items != null) {
            this.addedFiles.addAll(items);
        }
        
    }

    private void onItemsAdded(Change<? extends Explorable> change) {
        while (change.next()) {

           

        }
        if (addedFiles.size() > 0) {
            helper.setPriority(MetaDataKeyPriority.getPriority(addedFiles.get(0).getMetaDataSet()));
        }

        helper.setColumnsFromItems(addedFiles);

        updateFilter();
    }

    private void onExplorableMarked(Explorable exp, Boolean newValue) {
        if (newValue && markedItemProperty.contains(exp) == false) {
            markedItemProperty.add(exp);
        } else {
            markedItemProperty.remove(exp);
        }

        markLabelText.invalidate();
        markLabelText.getValue();
    }

    public ObservableList<Explorable> itemProperty() {
        return addedFiles;
    }

    /**
     * Return the list of items explicitly marked for processing (using
     * checkboxs)
     *
     * @return the observable list of items which have been marked for
     * processing
     */
    public ObservableList<Explorable> markedItemProperty() {
        return markedItemProperty;
    }

    public void dispose() {
        addedFiles.clear();
    }

}
