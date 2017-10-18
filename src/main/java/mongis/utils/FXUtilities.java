/*
 * /*
 *     This file is part of ImageJ FX.
 *
 *     ImageJ FX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ImageJ FX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ImageJ FX.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * 	Copyright 2015,2016 Cyril MONGIS, Michael Knop
 *
 */
package mongis.utils;

import mongis.utils.task.FluentTask;
import com.sun.javafx.tk.Toolkit;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Cyril MONGIS
 */
public class FXUtilities {


    private static final Logger logger = Logger.getLogger(FXUtilities.class.getName());

    public static Node loadView(URL url, Object controller, boolean setRoot) {
        FXMLLoader loader = setLoaderController(createLoader(), controller);
        setLoaderUrl(loader, url);
        if (setRoot) {
            loader.setRoot(controller);
        }
        return loadController(loader);
    }

    public static Pane loadView(URL url) {
        try {
            return setLoaderUrl(createLoader(), url).load();
        } catch (IOException ex) {
            logger.log(Level.SEVERE,null,ex);
        }
        return null;
    }

    

    private static FXMLLoader createLoader() {
        return new FXMLLoader();
    }

    private static FXMLLoader setLoaderController(FXMLLoader loader, Object controller) {
        loader.setController(controller);
        return loader;
    }

    private static FXMLLoader setLoaderUrl(FXMLLoader loader, URL url) {
        loader.setLocation(url);
        //loader.setResources(getResourceBundle());
        return loader;
    }

    public static <T extends Parent> T loadFXML(Object controller, String url) throws IOException {

        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(url));
        loader.setController(controller);

        loader.load();

        return (T) loader.getRoot();

    }

    private static Node loadController(FXMLLoader loader) {
        try {
            loader.load();
            Node controller = (Node) loader.getController();
            return controller;
        } catch (IOException ex) {
            logger.log(Level.SEVERE,null,ex);
            return null;
        }
    }

    public static FluentTask<Void,WebView> createWebView() {
        return new FluentTask<Void,WebView>()
                .call(WebView::new)
                .startInFXThread();
    }


    public static void modifyUiThreadSafe(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public static void close(Pane pane) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    public static <T> void addLater(Collection<? extends T> source, Collection<T> target) {

        Platform.runLater(() -> {
            if (target.containsAll(source) == false) {
                target.addAll(source);
            }

        });

    }

    public static <T extends Node> void addLater(Collection<? extends T> source, Pane parent) {
        addLater(source, parent.getChildren());
    }

    public static <T> void removeLater(Collection<? extends T> source, Collection<T> target) {

        Platform.runLater(() -> target.removeAll(source));

    }

    /**
     * Simple helper class.
     *
     * @author hendrikebbers
     *
     */
    public static final String BUTTON_PRIMARY_CLASS = "primary";
    public static final String BUTTON_SUCCESS_CLASS = "success";
    public static final String BUTTON_DANGER_CLASS = "danger";

    public static void setAnchors(Node node, double top, double right, double bottom, double left) {

        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setBottomAnchor(node, bottom);
        AnchorPane.setLeftAnchor(node, left);

    }

    public static void setAnchors(Node node, double anchors) {
        setAnchors(node, anchors, anchors, anchors, anchors);
    }

    private static class ThrowableWrapper {

        Throwable t;
    }

    /**
     * Invokes a Runnable in JFX Thread and waits while it's finished. Like
     * SwingUtilities.invokeAndWait does for EDT.
     *
     * @param run The Runnable that has to be called on JFX thread.
     * @throws InterruptedException f the execution is interrupted.
     * @throws ExecutionException If a exception is occurred in the run method
     * of the Runnable
     */
    public static void runAndWait(final Runnable run)
            throws InterruptedException, ExecutionException {
        if (Platform.isFxApplicationThread()) {
            try {
                run.run();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        } else {
            final Lock lock = new ReentrantLock();
            final Condition condition = lock.newCondition();
            final ThrowableWrapper throwableWrapper = new ThrowableWrapper();
            lock.lock();
            try {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        lock.lock();
                        try {
                            run.run();
                        } catch (Throwable e) {
                            throwableWrapper.t = e;
                        } finally {
                            try {
                                condition.signal();
                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                });
                condition.await();
                if (throwableWrapper.t != null) {
                    throw new ExecutionException(throwableWrapper.t);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static <T> T runAndWait(Callable<T> t) {

        try {
            if (Platform.isFxApplicationThread()) {
                return t.call();
            }
            return new FluentTask<Void,T>()
                    .call(()->t.call())
                    .startInFXThread()
                    .get();
        } catch (InterruptedException ex) {
            Logger.getLogger(FXUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(FXUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(FXUtilities.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public static void injectFXML(Object rootAndController) throws IOException {

        String fileName = rootAndController.getClass().getSimpleName() + ".fxml";

        //System.out.println(rootAndController.getClass().getResource(fileName));
        injectFXML(rootAndController, fileName);
    }

    public static void injectFXMLUnsafe(Object rootAndController) {
        try {
            injectFXML(rootAndController);
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, null, ioe);
        }
    }


    public static Node lookup(String id, Node node) {

        if (id.equals(node.getId())) {
            return node;
        } else if (node instanceof TitledPane) {

            return lookup(id, ((TitledPane) node).getContent());

        } else if (node instanceof MenuButton) {
            return lookupItems(id, ((MenuButton) node).getItems());
        } else if (node instanceof Parent) {

            return lookup(id, ((Parent) node)
                    .getChildrenUnmodifiable());

        } else {
            return null;
        }

    }

    private static Node lookupItems(String id, Collection<? extends MenuItem> list) {
        return list.stream()
                .map(child -> lookup(id, child.getGraphic()))
                .filter(child -> child != null)
                .findFirst()
                .orElse(null);
    }
    
    private static Node lookup(String id, Collection<? extends Node> list) {
        return list.stream()
                .map(child -> lookup(id, child))
                .filter(child -> child != null)
                .findFirst()
                .orElse(null);
    }

    private static FXMLLoader loader = new FXMLLoader();

    public static void injectFXML(Object rootController, String location) throws IOException {
        FXMLLoader loader = new FXMLLoader(rootController.getClass().getResource(location));
        loader.setRoot(rootController);
        loader.setController(rootController);

        loader.setLocation(rootController.getClass().getResource(location));
        loader.setClassLoader(rootController.getClass().getClassLoader());

        loader.load();

        URL css
                = rootController.getClass().getResource(rootController.getClass().getSimpleName() + ".css");

        try {
            if (css != null) {

                // gets the root of the loader
                Node root = loader.getRoot();

                // get the url of the css
                String url = css.toExternalForm();

                // going through the list to check if there is an existing URL
                String existingURL = root.getScene()
                        .getStylesheets()
                        .stream()
                        .filter(str -> str.equals(url))
                        .findFirst().orElse(null);

                // if there is, it's deleted from the list to update the Scene with
                // possible modification
                if (existingURL != null) {
                    root.getScene().getStylesheets().remove(existingURL);
                }
                root.getScene().getStylesheets().add(css.toExternalForm());
            }
        } catch (Exception e) {
            Logger.getGlobal().warning("No CSS found for " + rootController.getClass().getSimpleName());
        }

    }

    public static void injectFXMLSafe(final Object controller) throws IOException {
        try {
            runAndWait(() -> {
                try {
                    injectFXML(controller);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, null, e);
                }
            });
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, "Couldn't load CSS", e);
        }

    }

    public static String javaClassToName(String className) {

        StringBuilder builder = new StringBuilder(30);

        if (className == null) {
            return "";
        }

        int start;

        if (className.contains(".")) {
            start = className.lastIndexOf('.') + 2;
        } else {
            start = 1;
        }

        builder.append(className.charAt(start - 1));

        boolean isFirstUppercase = true;

        //if(className.length() < 2) return className;
        for (int i = start; i < className.length(); i++) {

            char c = className.charAt(i);

            // if(i == start) builder.append(c);
            if (Character.isUpperCase(c)) {
                if (isFirstUppercase) {
                    builder.append(" ");
                }

                isFirstUppercase = false;
            } else {

                isFirstUppercase = true;
            }

            builder.append(Character.toLowerCase(c));
        }

        return builder.toString();
    }

    public static String javaClassToName(Class<?> clazz) {
        return javaClassToName(clazz.getName());
    }

    public static File openFile(String title, String defaultFolder, String extensionTitle, String... extensions) {
        Task<File> task = new Task<File>() {
            public File call() {
                return openFileSync(title, defaultFolder, extensionTitle, extensions);
            }
        };
        Platform.runLater(task);

        try {
            return task.get();
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Error when selecting file", ex);
        } catch (ExecutionException ex) {
            logger.log(Level.SEVERE, "Error when selecting file", ex);
        }

        return null;
    }

    public static File openFileSync(String title, String defaultFolder, String extensionTitle, String... extensions) {
        FileChooser fileChooser = new FileChooser();

        File file = null;
        fileChooser.setTitle(title);
        if (defaultFolder != null) {
            fileChooser.setInitialDirectory(new File(defaultFolder));
        }
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(extensionTitle, extensions));
        file = fileChooser.showOpenDialog(null);
        return file;
    }

    public static List<File> openFiles(String title, String defaultFolder, String extensionTitle, String... extensions) {
        Task<List<File>> task = new Task<List<File>>() {
            public List<File> call() {
                FileChooser fileChooser = new FileChooser();

                List<File> files = null;
                fileChooser.setTitle(title);
                if (defaultFolder != null) {
                    fileChooser.setInitialDirectory(new File(defaultFolder));
                }
                if (extensionTitle != null) {
                    fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(extensionTitle, extensions));
                }
                files = fileChooser.showOpenMultipleDialog(null);

                return files;
            }
        };

        try {
            runAndWait(task);

            return task.get();
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE,null,ex);
        } catch (ExecutionException ex) {
            logger.log(Level.SEVERE,null,ex);
        }

        return null;
    }

    public static File openFolder(String title, String defaultFolder) {
        Task<File> task = new Task<File>() {
            public File call() {
                DirectoryChooser fileChooser = new DirectoryChooser();

                File file = null;
                fileChooser.setTitle(title);
                if (defaultFolder != null) {
                    fileChooser.setInitialDirectory(new File(defaultFolder));
                }

                file = fileChooser.showDialog(null);

                return file;
            }
        };

        try {
            if (Platform.isFxApplicationThread()) {
                task.run();
            } else {
                runAndWait(task);
            }

            return task.get();
        } catch (Exception ex) {
           logger.log(Level.SEVERE,null,ex);
        }
        return null;

    }

    public static File saveFileSync(String title, String defaultFolder, String extensionTitle, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        File file = null;
        fileChooser.setTitle(title);
        if (defaultFolder != null) {
            fileChooser.setInitialDirectory(new File(defaultFolder));
        }
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(extensionTitle, extensions));
        file = fileChooser.showSaveDialog(null);
        return file;
    }

    public static File saveFile(String title, String defaultFolder, String extensionTitle, String... extensions) {
        Task<File> task = new Task<File>() {
            public File call() {
                FileChooser fileChooser = new FileChooser();
                File file = null;
                fileChooser.setTitle(title);
                fileChooser.setInitialDirectory(new File(defaultFolder));
                fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(extensionTitle, extensions));
                file = fileChooser.showSaveDialog(null);
                return file;
            }
        };

        try {

            if (Platform.isFxApplicationThread()) {
                task.run();

            } else {
                Platform.runLater(task);
            }

            return task.get();
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE,null,ex);
        } catch (ExecutionException ex) {
             logger.log(Level.SEVERE,null,ex);
        }

        return null;
    }

    public static void toggleCssStyle(Node node, String styleClass) {

        toggleCssStyle(node, styleClass, node.getStyleClass().contains(styleClass));

    }

    public static void toggleCssStyle(Node node, String styleClass, boolean toggle) {

        if (toggle) {
            if (node.getStyleClass().contains(styleClass) == false) {
                node.getStyleClass().add(styleClass);
            }
        } else {
            while (node.getStyleClass().contains(styleClass)) {
                node.getStyleClass().remove(styleClass);
            }
        }

    }

    public static final String TOGGLE_GROUP = "toggle-group";

    public static final String TOGGLE_GROUP_FIRST = "first";

    public static final String TOGGLE_GROUP_LAST = "last";

    public static void makeToggleGroup(Node parent, List<? extends Node> childen) {
        toggleCssStyle(parent, TOGGLE_GROUP, true);
        makeToogleGroup(childen);
    }

    public static void makeToogleGroup(List<? extends Node> children) {

        children
                .forEach(child -> toggleCssStyle(child, TOGGLE_GROUP_FIRST, false));
        children
                .forEach(child -> toggleCssStyle(child, TOGGLE_GROUP_LAST, false));
        if (children.size() >= 2) {
            toggleCssStyle(children.get(0), TOGGLE_GROUP_FIRST, true);
            toggleCssStyle(children.get(children.size() - 1), TOGGLE_GROUP_LAST, true);
        }

    }

    public static <T> void bindList(final ObservableList<T> listToUpdate, final ObservableList<? extends T> changingList) {

        changingList.addListener((ListChangeListener.Change<? extends T> c) -> {

            while (c.next()) {

                listToUpdate.removeAll(c.getRemoved());
                listToUpdate.addAll(c.getAddedSubList());

            }
        });

    }

    public static void styleDialogButton(Dialog dialog, ButtonType buttonType, String styleClass, FontAwesomeIcon icon) {

        Button button = (Button) dialog.getDialogPane().lookupButton(buttonType);

        if (button == null) {
            return;
        }

        button.getStyleClass().add(styleClass);
        button.setGraphic(new FontAwesomeIconView(icon));

    }

    public static void styleDialogButtons(Dialog dialog, String styleClass, FontAwesomeIcon icon, ButtonType... buttonTypes) {
        for (ButtonType type : buttonTypes) {
            styleDialogButton(dialog, type, styleClass, icon);
        }
    }

    public static void styleDialogButtons(Dialog dialog) {

        styleDialogButtons(dialog, BUTTON_DANGER_CLASS, FontAwesomeIcon.CLOSE, ButtonType.NO, ButtonType.CANCEL);

        styleDialogButtons(dialog, "warning", FontAwesomeIcon.EXCLAMATION, ButtonType.CANCEL);

        styleDialogButtons(dialog, BUTTON_SUCCESS_CLASS, FontAwesomeIcon.CHECK, ButtonType.APPLY, ButtonType.NEXT, ButtonType.OK, ButtonType.YES);
    }


    public static void makeListViewMultipleSelection(ListView listView) {

        EventHandler<MouseEvent> eventHandler = (event)
                -> {
            if (!event.isShortcutDown()) {
                Event.fireEvent(event.getTarget(), cloneMouseEvent(event));
                event.consume();
            }
        };

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, eventHandler);
        listView.addEventFilter(MouseEvent.MOUSE_RELEASED, eventHandler);

    }

    private static MouseEvent cloneMouseEvent(MouseEvent event) {
        switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
            case SHIFT:
                return new MouseEvent(
                        event.getSource(),
                        event.getTarget(),
                        event.getEventType(),
                        event.getX(),
                        event.getY(),
                        event.getScreenX(),
                        event.getScreenY(),
                        event.getButton(),
                        event.getClickCount(),
                        true,
                        event.isControlDown(),
                        event.isAltDown(),
                        event.isMetaDown(),
                        event.isPrimaryButtonDown(),
                        event.isMiddleButtonDown(),
                        event.isSecondaryButtonDown(),
                        event.isSynthesized(),
                        event.isPopupTrigger(),
                        event.isStillSincePress(),
                        event.getPickResult()
                );

            case CONTROL:
                return new MouseEvent(
                        event.getSource(),
                        event.getTarget(),
                        event.getEventType(),
                        event.getX(),
                        event.getY(),
                        event.getScreenX(),
                        event.getScreenY(),
                        event.getButton(),
                        event.getClickCount(),
                        event.isShiftDown(),
                        true,
                        event.isAltDown(),
                        event.isMetaDown(),
                        event.isPrimaryButtonDown(),
                        event.isMiddleButtonDown(),
                        event.isSecondaryButtonDown(),
                        event.isSynthesized(),
                        event.isPopupTrigger(),
                        event.isStillSincePress(),
                        event.getPickResult()
                );

            case ALT:
                return new MouseEvent(
                        event.getSource(),
                        event.getTarget(),
                        event.getEventType(),
                        event.getX(),
                        event.getY(),
                        event.getScreenX(),
                        event.getScreenY(),
                        event.getButton(),
                        event.getClickCount(),
                        event.isShiftDown(),
                        event.isControlDown(),
                        true,
                        event.isMetaDown(),
                        event.isPrimaryButtonDown(),
                        event.isMiddleButtonDown(),
                        event.isSecondaryButtonDown(),
                        event.isSynthesized(),
                        event.isPopupTrigger(),
                        event.isStillSincePress(),
                        event.getPickResult()
                );

            case META:
                return new MouseEvent(
                        event.getSource(),
                        event.getTarget(),
                        event.getEventType(),
                        event.getX(),
                        event.getY(),
                        event.getScreenX(),
                        event.getScreenY(),
                        event.getButton(),
                        event.getClickCount(),
                        event.isShiftDown(),
                        event.isControlDown(),
                        event.isAltDown(),
                        true,
                        event.isPrimaryButtonDown(),
                        event.isMiddleButtonDown(),
                        event.isSecondaryButtonDown(),
                        event.isSynthesized(),
                        event.isPopupTrigger(),
                        event.isStillSincePress(),
                        event.getPickResult()
                );

            default: // well return itself then
                return event;

        }
    }

}
