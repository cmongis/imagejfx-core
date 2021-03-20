/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var Seq = require("seq");
var spawn = require("child_process").spawn;
var exec = require("child_process").exec;
var fs = require("fs");
var xml2js = require("xml2js");
var zlib = require("zlib");
var extension2 = /-(r?v?\d.*)\.jar/;
var dateformat = require("dateformat");
var Hash = require("hashish");
var path = require("./path.js");

function Repository(src, jarFolder) {

    var self = this;



    self.src = src;
    self.jarFolder = jarFolder;
    self.dbFile = path(src, "db.xml.gz");

    self.db;


    self.now = function () {

        return dateformat(new Date(), "yyyymmHHMMss");

    };

    // read current database file
    self.read = function (callback) {

        // if there no db files
        if (fs.existsSync(self.dbFile) == false) {

            self.db = {
                pluginRecords: {
                    plugin: []
                }
            };

            callback(null, self.db);
        } else {
            new Seq()
                    .seq(function () {
                        var gzip = zlib.createGunzip();

                        var buffer = [];
                        var cb = this;
                        fs
                                .createReadStream(self.dbFile)
                                .pipe(gzip)
                                .on("data", function (data) {
                                    buffer.push(data.toString());
                                })
                                .on("end", function () {
                                    cb(null, buffer.join(""));
                                })
                                .on("error", function (err) {
                                    cb(err);
                                });
                    })
                    .catch(function (err) {
                        callback(err)
                    })
                    .seq(function (xml) {
                        xml2js.parseString(xml, this);
                    })
                    .seq(function (db) {
                        self.db = db;
                        if(db.pluginRecords.plugin == undefined) {
                            db.pluginRecords = {plugin :[]};
                        }
                        callback(null, self.db);
                    });
        }
    };

    // writes updated database files
    self.write = function (cb) {

        try {

            console.log("Writing db.xml.gz");


            var builder = new xml2js.Builder();
            var xml = builder.buildObject(self.db);
            var cdata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE pluginRecords [\n<!ELEMENT pluginRecords ((update-site | disabled-update-site)*, plugin*)>\n<!ELEMENT update-site EMPTY>\n<!ELEMENT disabled-update-site EMPTY>\n<!ELEMENT plugin (platform*, category*, version?, previous-version*)>\n<!ELEMENT version (description?, dependency*, link*, author*)>\n<!ELEMENT previous-version EMPTY>\n<!ELEMENT description (#PCDATA)>\n<!ELEMENT dependency EMPTY>\n<!ELEMENT link (#PCDATA)>\n<!ELEMENT author (#PCDATA)>\n<!ELEMENT platform (#PCDATA)>\n<!ELEMENT category (#PCDATA)>\n<!ATTLIST update-site name CDATA #REQUIRED>\n<!ATTLIST update-site url CDATA #REQUIRED>\n<!ATTLIST update-site ssh-host CDATA #IMPLIED>\n<!ATTLIST update-site upload-directory CDATA #IMPLIED>\n<!ATTLIST update-site description CDATA #IMPLIED>\n<!ATTLIST update-site maintainer CDATA #IMPLIED>\n<!ATTLIST update-site timestamp CDATA #REQUIRED>\n<!ATTLIST disabled-update-site name CDATA #REQUIRED>\n<!ATTLIST disabled-update-site url CDATA #REQUIRED>\n<!ATTLIST disabled-update-site ssh-host CDATA #IMPLIED>\n<!ATTLIST disabled-update-site upload-directory CDATA #IMPLIED>\n<!ATTLIST disabled-update-site description CDATA #IMPLIED>\n<!ATTLIST disabled-update-site maintainer CDATA #IMPLIED>\n<!ATTLIST disabled-update-site timestamp CDATA #REQUIRED>\n<!ATTLIST plugin update-site CDATA #IMPLIED>\n<!ATTLIST plugin filename CDATA #REQUIRED>\n<!ATTLIST plugin executable CDATA #IMPLIED>\n<!ATTLIST dependency filename CDATA #REQUIRED>\n<!ATTLIST dependency timestamp CDATA #IMPLIED>\n<!ATTLIST dependency overrides CDATA #IMPLIED>\n<!ATTLIST version timestamp CDATA #REQUIRED>\n<!ATTLIST version checksum CDATA #REQUIRED>\n<!ATTLIST version filesize CDATA #REQUIRED>\n<!ATTLIST previous-version filename CDATA #IMPLIED>\n<!ATTLIST previous-version timestamp CDATA #REQUIRED>\n<!ATTLIST previous-version checksum CDATA #REQUIRED>]>";

            // creating the output stream
            var output = fs.createWriteStream(path(self.src, "db.xml.gz"));
            var compress = zlib.createGzip();
            compress.pipe(output);


            var begin = "<pluginRecords>";

            // deleting the header of the XML
            xml = xml.substring(xml.indexOf(begin), xml.length);

            // writing the compressed output

            compress.write(cdata);
            compress.write("\n");
            compress.write(xml);
            compress.end();


            // writing an uncompressed output for debug reasons
            var output2 = fs.createWriteStream(path(self.src, "db.xml"));
            output2.write(cdata);
            output2.write("\n");
            output2.write(xml);
            output2.end();

            console.log("db.xml.gz written.");

            cb(null);
        } catch (e) {
            if (cb != undefined)
                cb(e);
        }
    };



    self.getPlugin = function (filename) {

        if (filename.$ != undefined)
            return filename;

        for (var record in self.db.pluginRecords.plugin) {
            // changing from the index to its associated value
            record = self.db.pluginRecords.plugin[record];
            if (self.getId(record.$.filename) == self.getId(filename)) {
                return record;
            }
        }
    };

    const PREVIOUS_VERSION = "previous-version";

    self.addVersion = function (filename, version) {


        // getting the plugin entry
        var plugin = self.getPlugin(filename);


        // creating a entry for the old version (must respect the XML format)
        var oldVersion = {$: self.getVersion(plugin)};

        // creates the entry in the data tree if doesn't exists
        if (plugin[PREVIOUS_VERSION] == undefined) {
            plugin[PREVIOUS_VERSION] = [];
        }
        plugin[PREVIOUS_VERSION].push(oldVersion);

        // if the current the parameter is null
        // it means the jar is not used anymore
        // it's deleted from the tree
        if (version == undefined) {
            delete plugin.version
        }

        // otherwise, the new version of the jar
        // is set as default
        else {
            plugin.$.filename = filename;
            plugin.version = [version];
        }
    };

    /**
     * Returns the current version of the jara
     * @param {type} plugin
     * @returns 
     */
    self.getVersion = function (plugin) {
        try {
            return plugin.version[0].$;
        } catch (e) {
            return {}
        }
    };

    /**
     * Returns the checksum as a string
     * @param {type} filename
     * @returns {unresolved}
     */
    self.getCheckSum = function (filename) {
        return self.getVersion(self.getPlugin(filename)).checksum;
    };

    /**
     * checks the jars contained in the jar folder
     * @param {type} callback : callback
     * @returns {undefined}
     */
    self.checkCurrentJars = function (callback) {

        // read current directory with all files
        // get the checksum
        // get the id
        // find the equivalent in the database
        // if equivalent exist put current as previous version
        // put this one as new
        // if not, create it

        // array that will contain the detected jars
        var check = [];

        // current timestamp
        var now = self.now();

        // starting the sequence
        new Seq()
                // reading the list of files in the jar directory
                .seq(function () {
                    fs.readdir(path(self.src,self.jarFolder), this);
                })

                // transform the resulted list in a sequence stack
                .flatten()

                // for each detected file in the jar folder
                .parEach(2, function (f) {
                    var p = path(self.src, self.jarFolder, f);
                    var filename = path(self.jarFolder,f);
                    // calculating the checksum and storing it
                    self.checksum(filename,p,this.into(f));
                })


                .seq(function () {

                    var result = this.vars;

                    // the checksum associated to the file is retrieved...
                    for (var i in result) {
                        var filename = result[i].filename
                        var checksum = result[i].hash;

                        if (filename.indexOf(".jar") == -1)
                            continue;

                        if (checksum == undefined) {
                            continue;
                        }


                        checksum = checksum.trim();

                        // ...and a new data structure is created
                        check.push({
                            filename: filename
                            , checksum: checksum
                            , timestamp: now
                            , filesize: fs.lstatSync(path(self.src, filename)).size
                        });

                    }
                    // let pass to the next 
                    this(null, check);
                })

                .seq(function (check) {

                    // for each file in the list of created strucutre that
                    // that represent the current jar
                    for (file in check) {

                        file = check[file];

                        // checking if a different version of the
                        // jar already exists in the database
                        var record = self.getPlugin(file.filename);

                        // if not, a record is created
                        if (record == undefined) {
                            // create record
                            console.log("creating", file.filename);
                            console.log("db",self.db);
                            self.db.pluginRecords.plugin.push({
                                $: {
                                    filename: file.filename
                                }
                                , version: [{$: file}]

                            });


                        }
                        // if a record with a different checksum exists,
                        // the old version is flagged as previous version
                        else if (self.getCheckSum(record) != file.checksum) {

                            console.log("updating", record.$.filename);

                            self.addVersion(file.filename, {
                                $: file
                            });
                        }

                    }

                    // function checking if a filename is
                    // in the list of newly discovered jars
                    var has = function (filename) {

                        // getting the id associated to the filename
                        var filenameId = self.getId(filename);

                        return check
                                // getting all the id contained in the "check" array
                                .map(function (file) {
                                    return self.getId(file.filename);
                                })
                                .filter(function (id) {
                                    return id == filenameId;
                                })
                                // checking if a record was spot
                                .length == 0

                    };

                    // deleting the jar that are not used anymore
                    console.log("Deleting missing dependancies");
                    self
                            .db
                            .pluginRecords
                            .plugin
                            .filter(function (plugin) {
                                return has(plugin.$.filename);
                            })
                            .map(function (plugin) {
                                console.log("Deleting " + plugin.$.filename);
                                return plugin.$.filename;
                            })
                            .forEach(function (plugin) {
                                console.log("Deleting", plugin);
                                self.addVersion(plugin);
                            });
                    console.log("JAR check finished.");


                    this();

                })
                .empty()
                .seq(function () {
                    console.log("callback")
                    if (callback != undefined) {
                        callback();
                    }
                })
                .empty();

        ;
        return;
    };

    self.getId = function (jarname) {
        if (jarname.indexOf("/") > -1) {
            jarname = jarname.substr(jarname.indexOf("/") + 1, jarname.length);
        }
        var m = jarname.match(extension2);

        var ext;
        if (m == null) {
            return jarname;
        } else {
            ext = m[0];
        }
        var basename = jarname.substr(0, jarname.indexOf(ext));
        return basename;
    }

    /**
     * Asynchronous checksum
     * @param {type} filename
     * @param {type} cb
     * @returns {undefined}
     */
    self.checksum = function (filename, path, cb) {

        var cmd = "java SHA1 " + path;

        exec(cmd, function (err, stdout) {
            console.log(cmd, "[executing]");
            if (err) {
                console.log(path, "[failed]",err);
                
                cb();
            } else {
                console.log(path, "[success]", stdout.toString());
                cb(err, {filename,path,hash:stdout.toString()});
            }
        });

    };
}
module.exports = Repository;