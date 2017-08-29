//@AppService appService
//@CommandService commandService
//@UIService uiService
var FilesCollection = Java.type("net.imagej.updater.FilesCollection");

var filesCollection = new FilesCollection(appService.getApp().getBaseDirectory());

var updateSite = filesCollection.addUpdateSite("ImageJ-FX", "http://localhost:8080", null, null, 0);

filesCollection.activateUpdateSite(updateSite,null);

uiService.showDialog("ImageJ-FX Update site install. Now proceeding to download.");

commandService.run("ImageJUpdater",true);

