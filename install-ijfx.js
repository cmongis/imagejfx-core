//@AppService appService
//@CommandService commandService
//@UIService uiService




// retrieve Fiji directory
var fiji = appService.getApp().getBaseDirectory();

// load Update Site configuration
var FilesCollection = Java.type("net.imagej.updater.FilesCollection");
var filesCollection = new FilesCollection(fiji);

// reading the current configuration
filesCollection.read();

var repoName = "ImageJ-FX";
var repoUrl = "http://site.imagejfx.net/";

// adds ImaegJ-FX update site, activates it and saves the new configuration
var updateSite = filesCollection.addUpdateSite(repoName, repoUrl, null, null, 0);
filesCollection.addUpdateSite(updateSite);
filesCollection.markForUpdate(repoName);
updateSite.setActive(true);
filesCollection.write();

// shows confirmation dialog
uiService.showDialog("ImageJ-FX Update site install. Now proceeding to download.");

// runs update :-)
commandService.run("net.imagej.ui.swing.updater.ImageJUpdater",true);
