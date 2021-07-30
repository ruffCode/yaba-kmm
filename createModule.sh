#!/bin/bash
#source /home/alexi/.zshrc

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if test -z "$1"; then
  echo "must specify module name"
  exit
else
  echo "creating module \$1 "
fi

MODULE_DIR=$1

FILE=androidApp/src/main/AndroidManifest.xml
if [ -f "$FILE" ]; then
  echo "starting build"
else
  echo "no manifest"
  exit
fi

MAIN=$MODULE_DIR/src/commonMain
TEST=$MODULE_DIR/src/commonTest
ANDROID_MAIN=$MODULE_DIR/src/androidMain
ANDROID_TEST=$MODULE_DIR/src/androidTest

FULL_PACKAGE=$(xml2 <androidApp/src/main/AndroidManifest.xml | grep package= | cut -d= -f2)
PACKAGE_SUB=$(echo "$FULL_PACKAGE" | sed 's![^.]*$!!')
NEW_PACKAGE=$PACKAGE_SUB$1

PACKAGE_PATH=$(echo "$NEW_PACKAGE" | sed -r 's/(\s?\.)/\//g')

function main() {
  createDirectories
#  createFiles
  createManifest
  createBuildGradle
#  includeProject
}

function createDirectoriesTest() {
  echo "creating directory  $MAIN/kotlin/$PACKAGE_PATH"
  echo "creating directory  $TEST/kotlin/$PACKAGE_PATH"
  echo "creating directory  $ANDROID_TEST/kotlin/$PACKAGE_PATH"
  echo "creating directory  $ANDROID_MAIN/kotlin/$PACKAGE_PATH"
}

function createDirectories() {

  /usr/bin/mkdir -p "$MAIN/kotlin/$PACKAGE_PATH"
  /usr/bin/mkdir -p "$TEST/kotlin/$PACKAGE_PATH"
  /usr/bin/mkdir -p "$ANDROID_TEST/kotlin/$PACKAGE_PATH"
  /usr/bin/mkdir -p "$ANDROID_MAIN/kotlin/$PACKAGE_PATH"
}

function createManifest() {
  tee "$ANDROID_MAIN"/AndroidManifest.xml <<EOF
<?xml version="1.0" encoding="utf-8"?>
<manifest package="${NEW_PACKAGE}"/>
EOF
}

function createFiles() {

  tee "$MODULE_DIR"/.gitignore <<EOF
/build
EOF
}

#function includeProject() {
#  /usr/bin/cat <<EOF >>settings.gradle.kts
#
#include(":${MODULE_DIR}")
#
#EOF
#}

function createBuildGradle() {

 tee "$MODULE_DIR"/build.gradle.kts <<EOF
plugins {
    id("android-lib")
    id("multiplatform-plugin")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("static-analysis")
}

dependencies {
    commonMainApi(projects.base)
}
EOF
}

main
