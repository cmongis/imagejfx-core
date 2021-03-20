#!/bin/bash
set -e
export IJFX_DIST_FOLDER="${VARIABLE:-$(pwd)/dist}"
echo $IJFX_DIST_FOLDER;
mvn install
mvn assembly:assembly

mkdir -p $IJFX_DIST_FOLDER
mkdir -p $IJFX_DIST_FOLDER/jars
cp -v target/imagejfx-core*bin/lib/* $IJFX_DIST_FOLDER/jars/
cp -v target/imagejfx*.jar $IJFX_DIST_FOLDER/jars/

cd packaging
javac SHA1.java
npm install
npm run pack

