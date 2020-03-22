# [NoteBook](https://notebook-oose.herokuapp.com/)

Felix Parker \
Matt Chodaczek \
Nico Ivanov \
Alex Hepp \
Anirudh Sharma \
Prashast Bindal

## Iteration 2
We have started using PostgreSQL as our database, which needs to be setup for the app to work locally.
Instructions for installing PostgreSQL can be found [here](https://www.postgresql.org/download/).

On MacOS, run these commands to install and setup the database:
```
brew install postgresql                                         # install postgres
pg_ctl -D /usr/local/var/postgres start                         # start the postgres server
psql postgres -c "CREATE USER dbuser WITH PASSWORD 'dbpasswd'"; # add a postgres user
psql postgres -c "ALTER ROLE dbuser WITH CREATEDB;"             # enable dbuser to make databases
psql postgres -c "CREATE DATABASE notebookdb;"                  # create the database
```

Before running the app, ensure that the following environment variables are set:
```
JDBC_DATABASE_URL='jdbc:postgresql://localhost:5432/notebookdb?user=dbuser&password=dbpasswd'
PORT=7000
AWS_ENABLE=FALSE
```
These can be set manually in IntelliJ by modifying the run configutation, or by running `./heroku_local.sh` which builds and runs the app locally.

## Iteration 1

For our specification and planning documents see docs/. We have implemented all of the core features planned for iteration 1.
In addition to the core requirements, we have also setup the site for deployment on Heroku and implemented file storage on AWS S3 (under the heroku branch).

The site may be found at: https://notebook-oose.herokuapp.com/

A local copy may be built and run using Gradle. A standalone version may be built and run using the following commands:
```
./gradlew stage
java -jar build/libs/notebook-0.1-SNAPSHOT.jar
```
