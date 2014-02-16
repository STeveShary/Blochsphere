rem The path to the Java 3D installation where the lib/ext jars are located.
set JAVA_3D_PATH=
rem  The path the OS specific binary files are located.
set JAVA_3D_LIBRARY_PATH=

set CLASSPATH=bloch3d.jar
set CLASSPATH=%CLASSPATH%;%JAVA_3D_PATH%/lib/ext/j3dcore.jar
set CLASSPATH=%CLASSPATH%;%JAVA_3D_PATH%/lib/ext/j3dutils.jar
set CLASSPATH=%CLASSPATH%;%JAVA_3D_PATH%/lib/ext/vecmath.jar

C:\jdk1.6.0\bin\java -cp %CLASSPATH% -Djava.library.path=%JAVA_3D_LIBRARY_PATH% edu.uc.ece.blochSphere.BlochApplication
