# Otautahi Foraging - Team 4

A desktop JavaFX application on Linux for discovering and locating forageable trees around the Christchurch region.
The application features an interactive map with map markers, personal journals, recipes
for provided fruits and a data view table. This is to aid users in achieving their foraging
needs.

The application uses publicly available API's such as CCC GIS database's and FatSecretAPI
to gather information and thus requires an internet connection.

## Authors
- Drew Polonowita
- Adina Nasir
- George Mounce
- Daniel Warner
- Oliver Taunt
- Bridget Candy

## Prerequisites
- JDK >= 25
- Gradle (wrapper included)
- An internet connection


## Features
New features in final product version:
- **Journalling how much of a food you foraged, food nutrition values** This helps nutrition conscious users understand what the value of the food on a tree is.
- Consistent filtering, select all deselect all
- Foraging guidelines and ettiquette page
- Help section
- **Map marker clustering** This helps the map look less like noise, so users can find what they are looking for easier 
- **Major performance improvements when loading recipes** This makes the user experience much better as they don't have to wait for 10+ seconds to load data
- Quality of life improvements  


Feature overview

- **Map** - Browse forageable trees sourced from Christchurch City Council's ArcGIS Tree API,
overlaid on an OpenStreetMap based Leaflet map.
- **Journal** - Add a field note with an optional photo to any tree directly from its map popup,
and browse saved notes in a dedicated journal screen.
- **Recipes** - Browse recipes sourced from the FatSecret API.
- **Settings** - Toggle dark mode, change the app's accent colour.
- **Table** - View the tree data as a table.  

Features not implemented

- Adding and deleting user discovered trees
    - We judged this as being of lower importance to making the existing markers give the users more value. Would involve updating the database schema to differentiate between api trees and user generated trees and it would involve designing the interface for what details we allow them to include.  
- Adding a note to a recipe
    - We decided to focus on adding notes to trees in our time, because it provides more to our stakeholders who focus more on the foraging aspect of our app. Would require updating the database schema to allow notes to work for both trees and recipe, and it would require new UI design.
- Persistent storage of the users theme Settings
    - It wasn't worth the implementation time for a minor quality of life improvement. It would require integrating into our database system and new testing. 




## Running the app
Find the PDF User Manual for more details about how to the app
To run the application through Gradle:
Include .env file found on Mattermost in Team 4 Teaching comms in the root directory.

Example .env:
```bash
CONSUMER_SECRET=consumer_secret_key
CONSUMER_KEY=consumer_key
```
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