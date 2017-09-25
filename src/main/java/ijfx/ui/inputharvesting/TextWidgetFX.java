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
package ijfx.ui.inputharvesting;

import ijfx.ui.main.ImageJFX;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.scijava.plugin.Plugin;
import org.scijava.widget.InputWidget;
import org.scijava.widget.TextWidget;
import org.scijava.widget.WidgetModel;

/**
 *
 * @author Cyril MONGIS
 */
@Plugin(type = InputWidget.class)
public class TextWidgetFX extends AbstractFXInputWidget<String> implements TextWidget<Node> {

    private TextArea textArea;

    private TextField textField;

    private PasswordField passwordField;

    private final StringProperty textProperty = new SimpleStringProperty();

    private ComboBox<String> comboBox;

    private ModelBinder<String> modelBinder;

    private Label label;
    private Node node;

    private AutoCompletionBinding<String> autocompleteBinding;

    public static final String AUTOCOMPLETED_TEXTFIELD = "autocompleted textfield";

    @Override
    public void set(WidgetModel model) {

        super.set(model);
        String[] choices = null;

        try {
            choices = model.getChoices();
        } catch (NullPointerException exp) {
            ImageJFX.getLogger().fine("No choices for " + model.getItem().getName());
        }

        if (isStyle(AUTOCOMPLETED_TEXTFIELD)) {
            textField = new TextField();
            //autocompleteBinding = TextFields.bindAutoCompletion(textField, this::getSuggestions);
            node = textField;
            bindProperty(textField.textProperty());
        } else if (choices != null && choices.length > 0) {
            comboBox = new ComboBox<>();
            comboBox.getItems().addAll(model.getChoices());
            bindProperty(comboBox.valueProperty());
            node = comboBox;

        } else if (isStyle(AREA_STYLE)) {
            textArea = new TextArea();
            node = textArea;
            //textArea.textProperty().bindBidirectional(textProperty);
            bindProperty(textArea.textProperty());

        } else if (isStyle(PASSWORD_STYLE)) {
            passwordField = new PasswordField();
            node = passwordField;
            bindProperty(passwordField.textProperty());
        } else if (model.isMessage()) {
            label = new Label();
            label.getStyleClass().add("input-message");
            bindProperty(label.textProperty());
            node = label;
        } else {
            textField = new TextField();

            if (model.isStyle(AUTOCOMPLETED_TEXTFIELD)) {
                autocompleteBinding = TextFields.bindAutoCompletion(textField, this::getSuggestions);
            }

            node = textField;
            bindProperty(textField.textProperty());
        }

    }

    private boolean isStyle(String style) {

        return get().isStyle(style);
    }

    @Override
    public Node getComponent() {
        return node;
    }

    @Override
    public Class<Node> getComponentType() {
        return Node.class;
    }

    @Override
    public boolean supports(WidgetModel model) {
        return super.supports(model) && model.isText();
    }

    @Override
    public void refreshWidget() {
        super.refreshWidget();

        if (get().isStyle(AUTOCOMPLETED_TEXTFIELD)) {

        }

    }

    private Collection<String> getSuggestions(AutoCompletionBinding.ISuggestionRequest request) {

        if (get().getChoices() != null) {
            return Stream.of(get()
                    .getChoices())
                    .filter(str -> str.contains(request.getUserText()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }

}
