
setenv J3D_HOME=/osx-x86

export DYLD_LIBRARY_PATH=${J3D_HOME}/jogl/lib

export CLASSPATH=./lib/bloch3d.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dcore.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/j3dutils.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/lib/ext/vecmath.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/jogl/lib/jogl.jar
export CLASSPATH=${CLASSPATH}:${J3D_HOME}/jogl/lib/gluegen-rt.jar

java edu.uc.ece.blochSphere.BlochApplication