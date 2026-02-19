## Gradle Tasks

### Resource Processing
* generatePotFile - Generates a `src/jsMain/resources/modules/i18n/messages.pot` translation template file.
### Running
* ./gradlew -t run - Starts a webpack dev server on port 3000. And, hotReloads on code changes.
### Packaging
* ./gradlew jsBrowserDistribution - Bundles the compiled js files into `build/dist/js/productionExecutable`
* ./gradlew zip - Packages a zip archive with all required files into `build/libs/*.zip`
