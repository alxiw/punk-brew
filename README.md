# Punk Brew

<img align="right" width="120" src="img/sample-icon.png">

The project is [Kotlin](https://github.com/JetBrains/kotlin) based Android application, which represents [BrewDog](https://www.brewdog.com) beer catalogue obtaining data via [Punk API](https://punkapi.com) and store the data locally. In this application you can view the entire catalog of BrewDog kinds of beer, detailed information about each kind of beer as well as add it to favorites.

## Usage

Home Activity                       | Beer Details                       | Favourite Beers                        
:----------------------------------:|:----------------------------------:|:--------------------------------------:
![Home Activity](img/sample-01.png) | ![Beer Details](img/sample-02.png) | ![Favourite Beers](img/sample-03.png)  

## Dependencies

* [Kotlin](https://github.com/JetBrains/kotlin) 1.3.41
* [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) 1.3.0-M1
* [Dagger](https://github.com/google/dagger) 2.23.2
* [Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room) 1.1.1
* [Retrofit](https://github.com/square/retrofit) 2.6.0
* [Picasso](https://github.com/square/picasso) 2.71828

## Building

Run [Gradle](https://github.com/gradle/gradle) from the root directory of the project to build it.

``` bash
./gradlew build
```

After completion there will be two directories in app/build/outputs/apk with apk files for debug and release.

## License

[Apache 2.0](LICENSE) © [alxiw](https://github.com/alxiw)
