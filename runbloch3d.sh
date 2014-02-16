#!/bin/bash

# The path to the Java 3D installation where the lib/ext jars are located.
JAVA_3D_PATH=
# The path the OS specific binary files are located.
JAVA_3D_LIBRARY_PATH=

CLASSPATH=bloch3d.jar
CLASSPATH=$CLASSPATH:jep.jar
CLASSPATH=$CLASSPATH:$JAVA_3D_PATH/lib/ext/j3dcore.jar
CLASSPATH=$CLASSPATH:$JAVA_3D_PATH/lib/ext/j3dutils.jar
CLASSPATH=$CLASSPATH:$JAVA_3D_PATH/lib/ext/vecmath.jar

java -cp $CLASSPATH -Djava.library.path=$JAVA_3D_LIBRARY_PATH edu.uc.ece.blochSphere.BlochApplication
