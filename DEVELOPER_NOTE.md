
# ImageJ-FX as an ImageJ User Interface


## ImageJ supported APIs

 - Display / DisplayViewer / DisplayPanel
 - UserInterface : ImageJ-FX is now a functional API
 - Tool API : ImageJ-FX discovers new ImageJ tool plugin and re-roots event
 


## Issues with ImageJ Integration

### MacOS X and event thread

Swing and Java-FX both use the same event Thread in MacOSX, resulting to some pretty
nasty deadlocks when not handled properly. Morever, the DefaultThreadService
always use the EDT-Thread to execute code ran with publishLater.

In order to fix the issues caused by the requirements of each interface, I created
a FXThreadService which overrides the DefaultThreadService. When switching
on ImageJ-FX UI. ImageJ-FX activates the JavaFX mode, causing all published
event to be executed on the Java-FX Thread instead on the EDT-Thread.
When ImageJ-FX is disposed, the "Java-FX mode" is deactivated.


