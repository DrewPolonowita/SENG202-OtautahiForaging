# Otautahi Foraging - Team 4

A JavaFX desktop application for discovering forageable trees around Christchurch.

## Authors
- Team 4

## Prerequisites
- JDK >= 21
- Gradle (wrapper included)


## Features
- **Map** - Browse forageable trees sourced from Christchurch City Council's ArcGIS Tree API,
overlaid on an OpenStreetMap based Leaflet map.
- **Journal** - Add a field note with an optional photo to any tree directly from its map popup,
and browse saved notes in a dedicated journal screen.
- **Recipes** - Browse recipes sourced from the FatSecret API.
- **Settings** - Toggle dark mode, change the app's accent colour.
- **Table** - View the tree data as a table.

## Running the app
```bash
./gradlew run
```
This compiles and launches the application directly.

## Running tests
```bash
./gradlew test
```
An HTML test report is generated under 'build/reports/tests/test/index.html'.

## Notes
- Tree and recipe data are fetched from an external API at runtime, meaning an interned connection
is required for the map and recipes screen to populate.