@echo off
rem The path to the Java 3D installation where the lib/ext jars are located.
set JAVA_3D_PATH=windows-64
rem  The path the OS specific binary files are located.
set JAVA_3D_LIBRARY_PATH=%JAVA_3D_PATH%/bin

set CLASSPATH=libs/Blochsphere.jar
set CLASSPATH=%CLASSPATH%;%JAVA_3D_PATH%/lib/ext/j3dcore.jar
set CLASSPATH=%CLASSPATH%;%JAVA_3D_PATH%/lib/ext/j3dutils.jar
set CLASSPATH=%CLASSPATH%;%JAVA_3D_PATH%/lib/ext/vecmath.jar

java -Djava.library.path=%JAVA_3D_LIBRARY_PATH% edu.uc.ece.blochSphere.BlochApplication
