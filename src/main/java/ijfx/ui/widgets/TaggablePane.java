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

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.explorer.datamodel.DefaultTag;
import ijfx.explorer.datamodel.Tag;
import ijfx.explorer.datamodel.Taggable;
import ijfx.ui.main.ImageJFX;
import ijfx.ui.utils.CollectionsUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import mongis.utils.FXUtilities;
import mongis.utils.properties.ListChangeListenerBuilder;
import org.scijava.Context;

/**
 *
 * @author Cyril MONGIS
 */
public class TaggablePane {

    private Parent root;

    @FXML
    private FlowPane tagFlowPane;

    @FXML
    private FlowPane possibleTagsFlowPane;

    @FXML
    private TextField tagTextField;

    private final ObservableList<Tag> tags = FXCollections.observableArrayList();

    private final ObservableList<Tag> possibleTags = FXCollections.observableArrayList();

    private final Property<Taggable> taggableProperty = new SimpleObjectProperty<>();

    private BiConsumer<Taggable, Tag> addAction = (taggable, tag) -> {
    };
    private BiConsumer<Taggable, Tag> removeAction = (taggable, tag) -> {
    };

    private final String TAG_CSS_CLASS = "tag";

    private static final String FXML_VERTICAL = "TaggablePane_V.fxml";
    private static final String FXML_HORIZONTAL = "TaggablePane.fxml";

    public enum Orientation {
        VERTICAL, HORIZONTAL

    }

    public TaggablePane(Context context, Orientation orientation) {

        context.inject(context);

        try {
            String fxml = orientation == Orientation.HORIZONTAL ? FXML_HORIZONTAL : FXML_VERTICAL;
            root
                    = FXUtilities.loadFXML(this, fxml);

            tags.addListener(ListChangeListenerBuilder
                    .<Tag>create()
                    .onAdd(this::onTagAdded)
                    .onRemove(this::onTagRemoved)
                    .build()
            );

            possibleTags.addListener(ListChangeListenerBuilder
                    .<Tag>create()
                    .onAdd(this::onPossibleTagAdded)
                    .onRemove(this::onPossibleTagRemoved)
                    .build()
            );

            //taggableProperty.addListener(this::onTaggableChanged);
            tagTextField.addEventFilter(KeyEvent.KEY_RELEASED, this::onEnterPressed);
        } catch (IOException e) {
            ImageJFX.getLogger().log(Level.SEVERE, "Error when creating taggable pane", e);

        }

    }

    public Parent getContentPane() {
        return root;
    }

    private void onTagAdded(List<? extends Tag> tagList) {


        List<Button> collect = tagList
                .stream()
                .map(this::createRemoveButton)
                .collect(Collectors.toList());

        FXUtilities.addLater(collect, tagFlowPane.getChildren());

    }

    private void onTagRemoved(List<? extends Tag> tagList) {


        List<Node> collect = tagFlowPane
                .getChildren()
                .stream()
                .filter(button -> tagList.contains(button.getUserData()))
                .collect(Collectors.toList());

        FXUtilities.removeLater(collect, tagFlowPane.getChildren());
    }

    private void onPossibleTagAdded(List<? extends Tag> tagList) {

        List<Node> collect = tagList
                .stream()
                .map(this::createAddButton)
                .collect(Collectors.toList());

        FXUtilities.addLater(collect, possibleTagsFlowPane.getChildren());

    }

    private void onPossibleTagRemoved(List<? extends Tag> tagList) {

        List<Node> collect = possibleTagsFlowPane
                .getChildren()
                .stream()
                .filter(button -> tagList.contains(button.getUserData()))
                .collect(Collectors.toList());

        FXUtilities.removeLater(collect, possibleTagsFlowPane.getChildren());
    }

    public void setTags(Collection<Tag> tags) {

        CollectionsUtils.synchronize(tags, this.tags);
    }

    private void onTaggableChanged(Observable obs, Taggable oldValue, Taggable newValue) {

        if (newValue == null) {
            tags.clear();
        } else {
            setTags(newValue.getTagList());
        }
    }

    private void onEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

            addAction.accept(getTaggable(), new DefaultTag(tagTextField.getText()));

            tagTextField.clear();

        }
    }

    public void setPossibleTags(Collection<Tag> tagList) {
        CollectionsUtils.synchronize(tagList, possibleTags);
    }

    public Taggable getTaggable() {
        return taggableProperty.getValue();
    }

    public void setTaggle(Taggable taggable) {
        taggableProperty.setValue(taggable);

    }

    public void refresh() {

        CollectionsUtils.synchronize(getTaggable().getTagList(), tags);

    }

    public Button createRemoveButton(Tag tag) {

        Button button = GlyphsDude.createIconButton(FontAwesomeIcon.REMOVE, tag.getName());
        button.setUserData(tag);
        button.getStyleClass().add(TAG_CSS_CLASS);
        button.setOnAction(event -> removeAction.accept(getTaggable(), tag));
        return button;
    }

    public Button createAddButton(Tag tag) {

        Button button = GlyphsDude.createIconButton(FontAwesomeIcon.PLUS, tag.getName());
        button.setUserData(tag);
        button.getStyleClass().add(TAG_CSS_CLASS);
        button.setOnAction(event -> addAction.accept(getTaggable(), tag));

        return button;
    }

    public TaggablePane setOnRemove(BiConsumer<Taggable, Tag> onRemove) {
        removeAction = onRemove;
        return this;
    }

    public TaggablePane setOnAdd(BiConsumer<Taggable, Tag> onAdd) {
        addAction = onAdd;
        return this;
    }

}
