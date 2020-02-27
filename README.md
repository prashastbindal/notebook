# NoteBook

Felix Parker \
Matt Chodaczek \
Nico Ivanov \
Alex Hepp \
Anirudh Sharma \
Prashast Bindal

## Iteration 1

For our specification and planning documents see docs/. We have implemented all of the core features planned for iteration 1.
In addition to the core requirements, we have also setup the site for deployment on Heroku and implemented file storage on AWS S3 (under the heroku branch).

The site may be found at: https://notebook-oose.herokuapp.com/

A local copy may be built and run using Gradle. A standalone version may be built and run using the following commands:
```
./gradlew stage
java -jar build/libs/notebook-0.1-SNAPSHOT.jar
```
