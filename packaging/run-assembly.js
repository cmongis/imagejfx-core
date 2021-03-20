Repository = require('./repository.js');
var Seq = require("seq");

const IJFX_DIST_FOLDER = process.env.IJFX_DIST_FOLDER || "../dist"

repo = new Repository(IJFX_DIST_FOLDER,"jars")

new Seq()
        .seq(function() {
            repo.read(this);
        })
        .seq(function() {
            repo.checkCurrentJars(this);
        })
        .seq(function () {
            repo.write(this);
        })
        .catch(function (err) {
            console.log(err);
        })
        .seq(function () {
            console.log("Build success");
        })
        ;