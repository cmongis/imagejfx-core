#!/bin/bash
set -e
export IJFX_DIST_FOLDER="${IJFX_DIST_FOLDER:-$(pwd)/dist}"
echo $IJFX_DIST_FOLDER;


mkdir -p $IJFX_DIST_FOLDER
mkdir -p $IJFX_DIST_FOLDER/jars
cp -v target/imagejfx-core*bin/lib/* $IJFX_DIST_FOLDER/jars/
cp -v target/imagejfx*.jar $IJFX_DIST_FOLDER/jars/

cd packaging
npm install
npm run pack