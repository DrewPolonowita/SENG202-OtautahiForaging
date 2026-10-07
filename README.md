# Otautahi Foraging - Team 4

A JavaFX desktop application for discovering forageable trees around Christchurch.

## Authors
- Drew Polonowita
- Adina Nasir
- George Mounce
- Daniel Warner
- Oliver Taunt
- Bridget Candy

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
To run the application through Gradle:
Include .env file found on Mattermost in Team 4 Teaching comms in the root directory.

Example .env:
CONSUMER_SECRET=consumer_secret_key
CONSUMER_KEY=consumer_key

```bash
./gradlew run
```
To run the application from the JAR:
```bash
java -jar seng202_team4-1.0-SNAPSHOT.jar
```
## Running tests
```bash
./gradlew test
```
An HTML test report is generated under 'build/reports/tests/test/index.html'.

## Notes
- Tree and recipe data are fetched from an external API at runtime, meaning an internet connection
is required for the map and recipes screen to populate.