[![Build Status](https://travis-ci.org/mtmse/mtm-dotify-addons.svg?branch=master)](https://travis-ci.org/mtmse/mtm-dotify-addons)
[![Type](https://img.shields.io/badge/type-provider_bundle-blue.svg)](https://github.com/brailleapps/wiki/wiki/Types)
[![License: LGPL v2.1](https://img.shields.io/badge/License-LGPL%20v2%2E1%20%28or%20later%29-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1)

# mtm-dotify-addons #
Provides implementations for enhancing epub html to meet the needs of MTM:
- Adding some boilerplate text
- ... 

## Using ##

Access the implementations via the `TaskGroupFactory` API in [streamline-api](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.daisy.streamline%22%20AND%20a%3A%22streamline-api%22) _or_ in an OSGi environment use  `TaskGroupFactoryMakerService`.

## Building ##
Build with `gradlew build` (Windows) or `./gradlew build` (Mac/Linux)

## Testing ##
Tests are run with `gradlew test` (Windows) or `./gradlew test` (Mac/Linux)

## Requirements & Compatibility ##
- Requires JDK 8
- Compatible with SPI and OSGi

## More information ##
See the [Dotify wiki](https://github.com/brailleapps/wiki/wiki) for more information about Dotify.
