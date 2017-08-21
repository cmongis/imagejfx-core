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
package ijfx.ui.display.annotation;

import ijfx.core.metadata.MetaData;
import ijfx.core.metadata.MetaDataSet;
import ijfx.core.metadata.MetaDataSetUtils;
import ijfx.explorer.datamodel.DefaultMapper;
import ijfx.explorer.datamodel.Explorable;
import ijfx.explorer.datamodel.Mapper;
import ijfx.ui.service.AnnotationService;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.scijava.plugin.Parameter;

/**
 *
 * @author sapho
 */
public class DefaultAnnotationDialog extends Dialog<Mapper> implements AnnotationDialog {

    @Parameter
    private AnnotationService annotationService;

    @FXML
    private TextField newKey;

    @FXML
    private ComboBox<String> cBox;

    @FXML
    private VBox vBox;

    private final URL CSSURL = getClass().getResource("/ijfx/ui/flatterfx.css");

    private static final String FXMLWAY = "/ijfx/ui/widgets/AnnotationDialog2.fxml";
    private List<? extends Explorable> items;
    private List<MetaDataSet> setList;

    //Main List. This observableList observe this tow objects properties in each controller. If theses properties change, a notification is sended.
    private final ObservableList<DataAnnotationController> ctrlList = FXCollections.observableArrayList(c -> new ObservableValue[]{c.getValueTextProperty(), c.getNewValueTextProperty()});
    //Secondary list. Purpose : stock temporaly controller to know which controller can create new controller or not.
    private final List<DataAnnotationController> updatedList = new ArrayList<>();

    Mapper mapper = new DefaultMapper();

    public DefaultAnnotationDialog() {

        loadFXML();

        initiazeListeners();

        firstUse();

        cBox.setOnAction(this::onComboBoxClicked);

    }

    /**
     * Traitment according properties state and kind of notification.
     */
    private void initiazeListeners() {
        ctrlList.addListener((ListChangeListener.Change<? extends DataAnnotationController> change) -> {
            while (change.next()) {

                if (change.wasAdded()) {
                    if (!vBox.getChildren().contains(change.getList().get(change.getFrom()))) {
                        vBox.getChildren().addAll(change.getAddedSubList());

                    }
                }
                if (change.wasRemoved()) {
                    vBox.getChildren().removeAll(change.getRemoved());
                }
                if (change.wasUpdated()) {
                    if (!updatedList.contains(change.getList().get(change.getFrom()))) {
                        updatedList.add(change.getList().get(change.getFrom()));
                        DataAnnotationController y = new DataAnnotationController();
                        ctrlList.add(y);
                    }
                    if (change.getList().get(change.getFrom()).getState()) {
                        ctrlList.remove(change.getList().get(change.getFrom()));
                    }

                }

            }
        });

    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(FXMLWAY));
        loader.setController(DefaultAnnotationDialog.this);

        try {
            loader.load();

        } catch (IOException ex) {
            Logger.getLogger(DefaultAnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        Pane root = loader.getRoot();
        getDialogPane().setContent(root);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter(DefaultAnnotationDialog.this::convert);

        newKey.setPromptText("new key");

        if (CSSURL != null) {
            this.getDialogPane().getStylesheets().add(CSSURL.toExternalForm());

        }

    }

    /**
     * First controller creation and put in the Observable List
     *
     * @return
     */
    @Override
    public ObservableList firstUse() { //création du premier controlleur et mise dans la liste
        DataAnnotationController x = new DataAnnotationController();
        ctrlList.add(x);
        cBox.setPromptText("Key");

        return ctrlList;
    }

    /**
     * Combobox Choice action.
     *
     * @param event
     */
    private void onComboBoxClicked(ActionEvent event) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");

        if (CSSURL != null) {
            alert.getDialogPane().getStylesheets().add(CSSURL.toExternalForm());

        }

        ctrlList.clear();

        String temp = cBox.getSelectionModel().getSelectedItem();

        Set<MetaData> setM = MetaDataSetUtils.getValues(items, temp);
        int setSize = setM.size();

        alert.setHeaderText("There are " + setSize + " values to display, do you want to continue ? ");

        if (setM.size() > 10) {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // ... user chose OK
                controlDisplay(setM, temp);

            } else {
                // ... user chose not OK
                firstUse();
            }
        } else {
            controlDisplay(setM, temp);
        }

    }

    private void controlDisplay(Set<MetaData> setM, String temp) {
        for (MetaData m : setM) {
            if (m.getName().equals(temp)) {

                DataAnnotationController z = new DataAnnotationController();
                z.setValue(m.getValue().toString());

                ctrlList.add(z);
                updatedList.add(z);
            }
        }
    }

    /**
     * Keys retriever to mapper
     */
    @Override
    public void bindData() {

        mapper.setOldKey(cBox.getValue().toString());
        mapper.setNewKey(newKey.getText());

    }

    /**
     * Dialog need : methode run when the button is cliqued
     *
     * @param button
     * @return
     */
    public Mapper convert(ButtonType button) { //CA CA MARCHE

        if (button == ButtonType.OK) {
            return mapperAction();
        } else {
            return null;
        }

    }

    /**
     * Add new data in the mapper hasmap.
     *
     * @return
     */
    public Mapper mapperAction() {

        bindData();

        for (DataAnnotationController item : ctrlList) {
            if (item.getNewValue() != " ") {
                mapper.associatedValues(item.getValue(), item.getNewValue());
            }
        }
        System.out.println("mapper" + mapper.getMapObject());

        return mapper;

    }

    public Mapper getMapper() {
        return this.mapper;
    }

    /**
     * Using in SetAnnotation : add all explorables need.
     *
     * @param items
     * @param setList
     * @return
     */
    @Override
    public String fillComboBox(List items, List setList) {
        this.items = items;
        this.setList = setList;
        cBox.getItems().addAll(MetaDataSetUtils.getAllPossibleKeys(setList));
        cBox.setEditable(true);

        return cBox.getSelectionModel().getSelectedItem();

    }

}
