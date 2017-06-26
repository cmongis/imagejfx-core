# Developer Manual

## Getting started

### IDE

ImageJ-FX is a regular Maven/Git project so you should be able to edit it with any IDE.

You can fork and clone this repository and start working on it. If you want, to have a more dynamic editing, install DCEVM as alternate virtual machine on your current Java installation and activate the "debug" profile. It will run ImageJ-FX using DCEVM.

## Understanding ImageJ FX

### Context based interface

ImageJFX doesn't have an interface with fixed widgets. The Main Window axes around two main UI Elements : 

 - the **Activity** which occupies center on the screen. Only one activity can be displayed at a time.
 - the **UiPlugins** which occupies the different side regions around the activity. UiPlugin tied themselves to UiContext.


#### What's a UiContext ?
A UiContext is a set of Strings that represent the current data situation. For instance, the UiContext changes if the user is editing a table or an image. ImageJ-FX modifies its interface depending on the UiContext by displaying only the UiPlugin tied to the current UiContext. Several UiContext co-exist at the same time. UiContexts are managed by the UiContextService.

~~~java
@Parameter
UiContextService uiContextService;

public void onClick() {
	
	// UiContext before :
	// ["no-image-open"]

	uiContextService.leave("no-image-open");
	uiContextService.enter("image-open");
	uiContextService.enter("8-bit-image");
	uiContextService.update();
	
	// UiContext after
	["image-open", "8-bit-image"]
	
}

~~~
The previous code will trigger the display of all widgets linked to the *image-open* and *8-bit-image* UiContext. As you can see, eases the creation of button that should always appear when a 8-bits image is edited.


#### Creating an Actitivy

Activities are not tied to UiContext. Their display is managed by the ActivityService.

In order to create Activity, one must implements the Activity interface and set is a Activity SciJavaPlugin : 

~~~java

@Plugin(type = Activity.class,name="dummy-activity")
	public class DummyActivity implements Activity {

	@Parameter
	AnyService anyService;

	private BorderPane contentPane = new BorderPane();

	public Node getContent() {
		return content;
	}
	
	// called on show
	public Task updateOnShow() {
		/*
			This method will be executed each time
			the Activity is displayed on the screen.
			If lengthy tasks must be executed, just return the task and it will started and executed in the asynchronously.
			
			If nothing has to be done on show,
			just return null so nothing happens.
		
		*/
	}
}
~~~ 


#### Creating a UiPlugin

A UiPlugin is the type of SciJava plugin that ImageJ-FX uses for side panels. These plugins are  caracterized by 4 attributes :

* **id** : important, allows the Context Manager to find the UiPlugin
* **localization** : tells the main window where the UiPlugin should appear
* **context** : list of contexts the UiPlugin is associated to.
* **order** : order inside its localization [Optional]. If several plugins co-exist in the same container, order will determine their position inside the container.

Here is a boiler plate widget

~~~java

@Plugin(type = UiPlugin.class)
@UiConfiguration(id = "context-switch-button", localization = Localization.BOTTOM_RIGHT, context="always")
public class ContextSwitchButton {
	
	// the services will be injected *AFTER* instanciation
	@Parameter
	UiContextService uiContextService;
	
	Button button;
	
	// constructor
	public ContextSwitchButton() {
		// creating the button
		button = new Button("Switch to my context");
		// setting the click handler
		button.setOnAction(this::onClick);
	}
	
	
	// method ran after injection of the service
	public UiPlugin init() {
		// you can now uses the services
		
		return this;
	}
	
	// should return the node displayed in the interface
	public Node getUiComponent() {
		return button;
	}
	
	// click event handler for the button
	public void onClick(ActionEvent event) {
	
		// when clicking the button, we enter the context
		uiContextService.enter("my-context");
		
		// if you don't run the update method, the widgets
		// won't be hidden or updated.
		uiContextService.update();
	}

}

~~~

As you can see, a UiContext doesn't need to be pre-existent. You can create as many UiContext as you want but it's also your responsability to provide a way to the user to come back to a previous contexts.

In this way, one could imagine creating an set of widgets and plugins linked to "Super-resolution" processes.

However, Widgets should only be used to provide an nice UI elements. The logic of your UiContext should be inside a SciJava Service and the image process should operate through ImageJ Modules.

#### Display a new type of object

ImageJ-FX uses the ImageJ API for displaying any type of object. In order to display data on the screen, Image requires the following classes : 

 - **Display<?>** : hold the model for the display of the data
 - **DisplayPanel<? extends Display>** : UI element that displays the data from the Display<?>
 - **DisplayWindow** : UI element that contains the DisplayPanel
 - **DisplayViewer<? extends Display>** : serves as controller of the DisplayWindow and the DisplayPanel.

In order to create your own display, the easiest is to use Abstract classes provided by both ImageJ and ImageJ-FX. They implement most of the methods and logic necessary for each type of classes.

First, create a display that will hold your data and act as a model for the view.

~~~java 
@Plugin(type = Display.class)
public class MyDataDisplay extends AbstractDisplay() {
	public MyDataDisplay() {
		super(MyData.class);
   }
   
   // methods that should
   // be called in order
   // to modify the way the object
   // is viewed.
   public void setColor(Color color) {
   	 ...
   }
   
}
~~~

Then, create a DisplayViewer that inherit from ImageJFX **FXDisplayViewer** helper class : 

~~~java 

@Plugin(type = DisplayViewer.class)
public class MyDataDisplayViewer extends FXDisplayViewer<MyDataDisplay>{
	 // The display viewer will be associated
	 // to the Data type.
    public MyDataDisplayViewer() {
    
    		// put here the type of Display
    		// the DisplayViewer should handle
        super(MyDataDisplay.class);
    }
}
~~~

Once done, the only missing elements is the DisplayPanel and the DisplayWindows. However, ImageJ-FX already provides a generic DisplayWindow that will load an appropriate FXDisplayPanel.

~~~java

@Plugin(type = FXDisplayPanel.class)
public class MyDataDisplayPanel extends AbstractFXDisplayPanel<DataDisplay> {
  

    private Pane somePane;
    
    public MyDataDisplayPanel() {
    		/* 
    			Again, put here
    		   the kind of Display 
    		   the DisplayPanel should handle.
    		
    		 	Don't create any UI element !
    		 	Create them in the pack method.
    		*/
    		
        super(MyDataDisplay.class);
    }
    
    @Override
    public void pack() {
    		// this method should create the 
    		// the UI Elements here, not in the
    		// constructor.
        pane = new AnchorPane();
    }

    @Override
    public void redoLayout() {
        /* 
        
        if the update of the data
        can cause reorganization of the
        UI layout, place the logic here
        
	     */ 
    }

    @Override
    public void setLabel(String s) {
        getWindow().setTitle(s);
    }

    @Override
    public void redraw() {
        /*
        	Method that update the UI 
        	after that the data have been
        	changed.
        */
    }

    @Override
    public Pane getUIComponent() {
    
    		// returns a JavaFX Pane
    		// that will be added to the Window
        return pane;
    }
~~~

ImageJ-FX is now available to display your data by calling of the **UIService.show(myData)** method. However, when executing a plugin that output your data type, ImageJ-FX won't take care of it. To do so, you must also create a post PostprocessorPlugin that will examine each executed plugin to the search of your data.

In order to get ImageJ-FX to display your data after set as output of one of your plugin, for instance this one : 

~~~java

@Plugin(type = Command.class)
public DataGenerator extends ContextCommand {

		@Parameter(type = ItemIO.OUTPUT)
		Data output;
		
		public void run() {
			...
		}
}
~~~

... just extend one of ImageJ-FX Abstract Helper :

~~~java

@Plugin(type = PostprocessorPlugin.class)
public class MyDataDisplayer extends DataDisplayPostprocessor{
	public MyDataDisplayer {
		super(Data.class);
	}
}
~~~

Any object of your data type will now be automatically wrapped into a **Display** and shown into ImageJ-FX.


## ImageJ FX Programming Guide lines

If you plan to extend ImageJ-FX with a new context, logic and UI Elements, you should follow the following guide line :

### Logic inside SciJava Service

ImageJ2 provides a really powerfull API in order to deal with services and dependancy injection. You can create a Service that contains the whole logic and inject it inside your widgets. The Widgets should only be a link between the user and the service.

As an example, let's create a Service that manages favorites file for the user. The usual behaviour is to create an java interface representing the service and implements it later with an concrete classes. This allows easier switching of implementation when necessary.

~~~java

public interface FavoriteFileService extends ImageJService {
	
	public List<File> getFavoriteFileList();
	
	public void addFile(File file);
	
	public void deleteFile(File file);
	
}
~~~

Now we may want to communicate with other elements of the software that files has been added or deleted. For this, we can use the Event system provided by SciJava and create specific events.

We must create new classes that inherit from SciJavaEvent;

FavoriteFileAddedEvent.java

~~~java

public class FavoriteFileAddedEvent extends SciJavaEvent {
	File file;
	
	public FavoriteFileAddedEvent(File f) {
		this.file = f;
	}
	
	public File getFile() {
		return file;
	}
}

~~~

FavoriteFileDeletedEvent.java

~~~java

public class FavoriteFileDeletedEvent extends SciJavaEvent {
	File file;
	
	public FavoriteFileDeletedEvent(File f) {
		this.file = f;
	}
	
	public File getFile() {
		return file;
	}
}

~~~


Then let's create an implementation of our service :

~~~java
public class DefaultFavoriteFileService extends AbstractService implements FavoriteFileService {


	private List<File> fileList = new ArrayList<>();
	
	/*
		ImageJ2 Services
	*/
	
	@Parameter
	EventService eventService; // for propagating events
	
	
	public List<File> getFavoriteFileList() {
		return fileList;
	}
	
	public void addFile(File f) {
		
		fileList.add(f);
		
		// publishing the event so that listeners
		// are notified of the new added file
		eventService.publish(new FavoriteFileAddedEvent(f));
		
	}
	
	public void deleteFile(File f) {
	
		fileList.remove(f);
		
		// publishing the event so that listeners
		// are notified of the new added file
		eventService.publish(new FavoriteFileDeletedEvent(f));
		
	}
	
}
~~~

Now if we want to listen for such events in our Widget or in (any other Context injected object), you can just add the following method to your class :

~~~java

ObservableList<File> favoriteFiles;

@EventHandler
public void handleEventXZY(FavoriteFileAddedEvent event) {
	
	// adding the added file to the View Model
	favoriteFiles.add(event.getFile()); 
	
}

~~~


This is a very naive example but it illustrates what can be done with the different part of the API.

#### Note concerning JavaFX Observable API

JavaFX provides an API to listen easily to the model. An alternative to the previous design would be to propose a service that only returns an ObservableList<File>. Since this type of object already deals with event propagation, SciJavaEvent classes would become useless. However, this implementation would enforce the use of JavaFX API and perhaps close the possibility of linking your service with other types of interface.


## Useful ImageJFX Classes

### AbstractContextButton

This class allows you to create easily a button that will appear in a certain context to execute a single action.

~~~java
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import ijfx.ui.main.Localization;
import org.scijava.plugin.Plugin;
import ijfx.service.widget.Widget;
import javafx.event.ActionEvent;
import net.imagej.plugins.commands.typechange.TypeChanger;
import net.imagej.types.DataTypeService;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;

@Plugin(type = FxWidgetPlugin.class)
@Widget(id = "float-image-button", context = "image-open", localization = Localization.LEFT)
public class FloatTheImage extends AbstractContextButton {

    @Parameter
    CommandService commandService;

    @Parameter
    DataTypeService dataTypeService;

    public FloatTheImage() {
        // defines an icon of the button
        super(FontAwesomeIcon.LEAF);
    }

    @Override
    public void onAction(ActionEvent event) {
		
        final Future future = commandService.run(TypeChanger.class, true,
                // setting command parameters
                "typeName", dataTypeService.getTypeByAttributes(32, true, false, true, true).longName(), "combineChannels", false);
        
        submitFuture(future);

    }    
} // end of class
~~~

