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
package mongis.utils.panecell;

import ijfx.explorer.views.DataClickEvent;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import mongis.utils.CallbackTask;
import mongis.utils.FailableCallback;

/**
 *
 * @author Cyril MONGIS
 */
public class SimplePaneCell<T> implements PaneCell<T> {

    private BorderPane borderPane = new BorderPane();
    private Label title = new Label();
    private ImageView imageView = new ImageView();
    private Callback<T, String> titleFactory = (s) -> "No name";
    private FailableCallback<T, Image> imageFactory = (i) -> null;
    private T item;
    private BooleanProperty booleanProperty = new SimpleBooleanProperty();
    private BooleanProperty onScreenProperty = new SimpleBooleanProperty();

    private Consumer<DataClickEvent<T>> onClickEvent;

    public SimplePaneCell() {
        borderPane.setCenter(imageView);
        borderPane.setBottom(title);
        borderPane.getStyleClass().add("simple-pane-cell");
        borderPane.setOnMouseClicked(this::onMouseClicked);
        imageView.setOnMouseClicked(this::onMouseClicked);
    }

    @Override
    public void setItem(T item) {
        title.setText(titleFactory.call(item));
        this.item = item;
        new CallbackTask<T, Image>().setInput(item).callback(imageFactory).then(imageView::setImage).start();
    }

    @Override
    public T getItem() {
        return item;
    }

    @Override
    public Node getContent() {
        return borderPane;
    }

    @Override
    public BooleanProperty selectedProperty() {
        return selectedProperty();
    }

    private void onMouseClicked(MouseEvent event) {
        event.consume();

        onClickEvent.accept(new DataClickEvent<>(getItem(), event, event.getClickCount() == 2));

    }

    public SimplePaneCell<T> setTitleFactory(Callback<T, String> factory) {
        this.titleFactory = factory;
        return this;
    }

    public SimplePaneCell<T> setImageFactory(FailableCallback<T, Image> factory) {
        this.imageFactory = factory;
        return this;
    }



    public SimplePaneCell<T> setWidth(double width) {
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(true);
        return this;
    }

    public BooleanProperty onScreenProperty() {
        return onScreenProperty;
    }

    public void setOnDataClick(Consumer<DataClickEvent<T>> onSimpleClick) {
        this.onClickEvent = onSimpleClick;
    }

}
