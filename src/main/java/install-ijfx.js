//@AppService appService
//@CommandService commandService
//@UIService uiService
var FilesCollection = Java.type("net.imagej.updater.FilesCollection");
var fiji = appService.getApp().getBaseDirectory();
var filesCollection = new FilesCollection(fiji);

var repoName = "ImageJ-FX";
var repoUrl = "http://localhost:8080/";

var updateSite = filesCollection.addUpdateSite(repoName, "http://localhost:8080/", null, null, 0);
filesCollection.addUpdateSite(updateSite);
filesCollection.markForUpdate(repoName);
updateSite.setActive(true);
filesCollection.write();
uiService.showDialog("ImageJ-FX Update site install. Now proceeding to download.");

commandService.run("net.imagej.ui.swing.updater.ImageJUpdater",true);