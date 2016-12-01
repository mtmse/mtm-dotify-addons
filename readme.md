[![Build Status](https://travis-ci.org/mtmse/mtm-dotify-addons.svg?branch=master)](https://travis-ci.org/mtmse/mtm-dotify-addons)
[![Type](https://img.shields.io/badge/type-provider_bundle-blue.svg)](https://github.com/brailleapps/wiki/wiki/Types)

# mtm-dotify-addons #
Provides implementations for enhancing epub html to meet the needs of MTM:
- Adding some boilerplate text
- ... 

## Using ##

Access the implementations via the `TaskGroupFactory` API in [dotify.task-api](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.daisy.dotify%22%20%20a%3A%22dotify.task-api%22) _or_ in an OSGi environment use  `TaskGroupFactoryMakerService`.

## Building ##
Build with `gradlew build` (Windows) or `./gradlew build` (Mac/Linux)

## Testing ##
Tests are run with `gradlew test` (Windows) or `./gradlew test` (Mac/Linux)

## Requirements & Compatibility ##
- Requires JDK 8
- Compatible with SPI and OSGi

## More information ##
See the [Dotify wiki](https://github.com/brailleapps/wiki/wiki) for more information about Dotify.
