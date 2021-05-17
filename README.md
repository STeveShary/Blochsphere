Blochsphere: A Java Bloch Sphere simulation tool.
===========

This was a masters's project to create a tool to view the effects of simple quantum operators in a blochshpere representation.  Presentation page available here <https://eecs.ceas.uc.edu/~cahaymm/blochsphere/>

## Building

This uses the gradle wrapper. To build the project:
```shell
./gradlew jar
```

To fully build the zipped up version for distribution:
```shell
./gradlew deploy
```

The zip file is available in `build/dist/block3dapp.zip`

## Running locally

To build the application, you can use any gradle supported Java IDE.  You can also
run the command:
```shell
./gradlew run
``` 

## Running a pre-built version

The prebuilt version is available [here](bloch3dapp.zip)