#!/bin/bash
setenv J3D_HOME=/linux-64

export LD_LIBRARY_PATH=${J3D_HOME}/lib/i386

export CLASSPATH=./lib/bloch3d.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dcore.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dutils.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/vecmath.jar

java edu.uc.ece.blochSphere.BlochApplication


