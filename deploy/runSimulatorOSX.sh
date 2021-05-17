#!/bin/bash
J3D_HOME=osx-x86

DYLD_FALLBACK_LIBRARY_PATH=${J3D_HOME}/jogl/lib

CLASSPATH=./libs/Blochsphere.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dcore.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dutils.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/vecmath.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/jogl/lib/jogl.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/jogl/lib/gluegen-rt.jar

java --class-path $CLASSPATH edu.uc.ece.blochSphere.BlochApplication