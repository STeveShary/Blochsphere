#!/bin/bash
J3D_HOME=linux-64

LD_LIBRARY_PATH=${J3D_HOME}/lib/amd64

CLASSPATH=./libs/Blochsphere.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dcore.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dutils.jar
CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/vecmath.jar

java --class-path $CLASSPATH edu.uc.ece.blochSphere.BlochApplication


