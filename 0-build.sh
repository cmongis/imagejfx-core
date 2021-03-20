#!/bin/bash
set -e
export IJFX_DIST_FOLDER="${VARIABLE:-$(pwd)/dist}"
echo $IJFX_DIST_FOLDER;
mvn install
mvn assembly:assembly


