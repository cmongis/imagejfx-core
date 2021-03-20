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
package ijfx.ui.mainwindow;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import static ij.gui.Line.getWidth;
import ijfx.core.hint.Hint;
import ijfx.ui.IjfxCss;
import ijfx.ui.main.ImageJFX;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import mongis.utils.animation.Animations;

/**
 *
 * @author cyril
 */
public class HintDisplayer {

    // Anchor pane that will display the hints
    final AnchorPane mainAnchorPane;

    private boolean isHintDisplaying = false;

    final private Queue<Hint> hintQueue = new LinkedList<>();

    public HintDisplayer(AnchorPane mainAnchorPane) {
        this.mainAnchorPane = mainAnchorPane;
    }

    public Scene getScene() {
        return mainAnchorPane.getScene();
    }

    public synchronized void queue(Hint hint) {
        hintQueue.add(hint);
        Platform.runLater(this::nextHint);
    }

    private synchronized void showHelpSequence(Hint hint) {

        if (hint == null) {

            return;
        }

        Node node = mainAnchorPane.getScene().lookup(hint.getTarget());

        try {
            isHintDisplaying = true;
            double hintWidth = 200;
            double hintMargin = 20;
            double rectanglePadding = 5;

            final double sceneHeight = getScene().getHeight();
            final double sceneWidth = getScene().getWidth();
            final double finalX;
            final double finalY;

            Button gotItButton = new Button("Got it !");
            // rectangle representing the highlighted node
            Rectangle rectangle;
            if (node != null) {
                Bounds nodeBounds = node.getLocalToSceneTransform().transform(node.getLayoutBounds());
                rectangle = new Rectangle(nodeBounds.getMinX() - rectanglePadding, nodeBounds.getMinY() - rectanglePadding, nodeBounds.getWidth() + rectanglePadding * 2, nodeBounds.getHeight() + rectanglePadding * 2);

            } else {
                rectangle = new Rectangle(getWidth() / 2 + 100, 150, 0, 0);
            }

            Rectangle bigOne = new Rectangle(0, 0, sceneWidth, sceneHeight);

            Shape highligther = Path.subtract(bigOne, rectangle);
            highligther.setFill(Paint.valueOf("black"));
            highligther.setOpacity(0.7);

            mainAnchorPane.getChildren().add(highligther);
            Label label = new Label(hint.getText());
            label.setPadding(new Insets(hintMargin));
            label.getStyleClass().add("help-label");
            label.setMaxWidth(hintWidth);
            label.setWrapText(true);
            label.setPrefWidth(hintWidth);

            Callable<Double> nextToNodeX = () -> {

                Bounds rectangleBounds;
                rectangleBounds = rectangle.getBoundsInLocal();

                Callable<Double> toTheLeft = () -> rectangleBounds.getMaxX() + hintMargin + highligther.getTranslateX();
                Callable<Double> toTheRight = () -> rectangleBounds.getMinX() - hintWidth - hintMargin + highligther.getTranslateX();

                Callable<Boolean> putToRight = () -> rectangleBounds.getMinX() - label.getWidth() - hintMargin < 0;

                return putToRight.call() ? toTheLeft.call() : toTheRight.call();

                /// return null;
            };

            Callable<Double> nextToNodeY = () -> {
                Bounds rectangleBounds;
                rectangleBounds = rectangle.getBoundsInLocal();

                Callable<Double> onTop = () -> rectangleBounds.getMaxY() - label.getHeight() - gotItButton.getHeight() - (hintMargin / 2);
                Callable<Double> under = () -> rectangleBounds.getMinY();

                Callable<Boolean> putOnTop = () -> rectangleBounds.getMinY() + label.getHeight() + (hintMargin) + gotItButton.getHeight() > sceneHeight;

                return putOnTop.call() ? onTop.call() : under.call();
            };

            Callable<Double> saveLevelX = () -> {
                Bounds rectangleBounds;
                rectangleBounds = rectangle.getBoundsInLocal();

                return rectangleBounds.getMinX();
            };

            Bounds rectangleBounds;
            rectangleBounds = rectangle.getBoundsInLocal();

            // callable determining the final position of the box
            // generics:
            //Callable<Double> verticalCenter = () -> 1d * (sceneWidth +  label.getWidth()) / 2;
            //Callable<Double> horizontalCenter = () -> 1d * (sceneHeight + label.getHeight()+hintMargin+gotItButton.getHeight())/2;
            Callable<Double> toTheLeft = () -> rectangleBounds.getMaxX() + hintMargin + highligther.getTranslateX();
            Callable<Double> toTheRight = () -> rectangleBounds.getMinX() - hintWidth - hintMargin + highligther.getTranslateX();

            Callable<Double> onTop = () -> rectangleBounds.getMaxY() - label.getHeight() - gotItButton.getHeight() - (hintMargin / 2);
            Callable<Double> under = () -> rectangleBounds.getMinY();

            // deciding the last position
            Callable<Boolean> putToRight = () -> rectangleBounds.getMinX() - label.getWidth() - hintMargin < 0;
            Callable<Boolean> putOnTop = () -> rectangleBounds.getMinY() + label.getHeight() + (hintMargin) + gotItButton.getHeight() > sceneHeight;

            Callable<Double> xPosition = nextToNodeX;
            Callable<Double> yPosition = nextToNodeY;

            if (rectangle.getBoundsInLocal().getWidth() > sceneWidth * 0.8) {
                xPosition = saveLevelX;
                yPosition = () -> rectangleBounds.getMaxY() + hintMargin;
            }

            // correcting
            // if (rectangleBounds.getWidth() > sceneWidth * 0.9) {
            //   xPosition = horizontalCenter;
            //  yPosition = under;
            //}
            //if(rectangle.getWidth() == 0) xPosition = verticalCenter;
            //if(rectangle.getHeight() == 0) yPosition = horizontalCenter;
            label.translateXProperty().bind(Bindings.createDoubleBinding(xPosition, highligther.translateXProperty(), rectangle.boundsInParentProperty()));
            label.translateYProperty().bind(Bindings.createDoubleBinding(yPosition, label.heightProperty(), gotItButton.heightProperty()));

            //label.setTranslateY(nodeBounds.getMinY());
            gotItButton.translateXProperty().bind(label.translateXProperty());

            gotItButton.setPrefWidth(hintWidth);

            gotItButton.getStyleClass().add(IjfxCss.WARNING);

            gotItButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));

            // when clicking on the anywhere on the screen
            highligther.setOnMouseClicked(event -> {

                // creating a transition
                Transition transition = Animations.DISAPPEARS_RIGHT.configure(highligther, Animations.getAnimationDurationAsDouble());

                // and when the transition if finished
                transition
                        .setOnFinished(event2
                                -> {
                            //deleting the elements fromthe main panel
                            mainAnchorPane.getChildren().removeAll(highligther, label, gotItButton);
                        });

                // set that the hint is not displayed again
                isHintDisplaying = false;
                hintQueue.poll();
                // displaying the next hint
                nextHint();
                transition.play();

            });
            mainAnchorPane.getChildren().addAll(label, gotItButton);

            gotItButton.translateYProperty().bind(
                    Bindings.createDoubleBinding(
                            () -> {
                                return label.translateYProperty().getValue() + label.heightProperty().getValue() + hintMargin / 2;
                            }, label.translateYProperty(), label.heightProperty()));

            gotItButton.setOnAction(event -> {
                highligther.getOnMouseClicked().handle(null);
                hint.setRead();
            });

            Animations.APPEARS_LEFT.configure(highligther, Animations.getAnimationDurationAsDouble()).play();

        } catch (Exception e) {

            isHintDisplaying = false;
            nextHint();
            ImageJFX.getLogger().log(Level.SEVERE, "Error when showing hint.", e);
        }

    }

    public synchronized void nextHint() {

        if (isHintDisplaying) {
            return;
        }
        if (hintQueue.size() > 0) {
            showHelpSequence(hintQueue.peek());
        }

    }
}
