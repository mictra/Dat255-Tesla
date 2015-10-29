# BusExplorer (DAT255 Tesla)

BusExplorer is an android app developed for the electrical buses in Gothenburg, it is meant to make your busride more enjoyable by showing you interesting locations around your bus route. By using the app you are able to follow your bus around a map in real time and see bars, resturants, shops, sightseeing attractions and more. BusExplorer provides an easy filter for the user to select what kind of information is displayed.

## Generating APK

There are generated APK-files in the `apk` folder: release (signed) and debug ready to be installed and tested (release preferred).</br>
There are known issues testing on an emulator, so it is adviced to test on an actual Android device.</br>
To build and generate an own APK-file in debug mode, navigate to the project directory `BusExplorer` and type the following command:
```
./gradlew.bat assembleDebug
```

The generated APK is located in `BusExplorer/app/build/outputs/apk/` named `app-debug.apk` and is ready to be installed on the Android device.

## Running Tests

To be able to run the tests, a device or an emulator needs to be connected.</br>
Navigate to the project directory `BusExplorer` and run the tests (while connected):

```
./gradlew connectedCheck
```

## License

## Team

* Jonas Arvidsson
* Kim Berger
* Patrick Franz
* Sebastian Kvarnström
* Michael Tran
