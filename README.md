# [NoteBook](https://notebook-oose.herokuapp.com/)

[![Actions Status](https://github.com/jhu-oose/2020-spring-group-NoteBook/workflows/Build%20and%20Test/badge.svg)](https://github.com/jhu-oose/2020-spring-group-NoteBook/actions)

Felix Parker \
Matt Chodaczek \
Nico Ivanov \
Alex Hepp \
Anirudh Sharma \
Prashast Bindal


## Iteration 2
In this iteration we added key features including the ability to comment on notes, searching for courses and notes,
and sorting courses and notes by name. We also improved the UI (still a work in progess), and made many improvements on
the backend such as modularizing the page controller code and adding tests. We completed all of our tasks planned for
iteration 2. In addition we added an [admin page](https://notebook-oose.herokuapp.com/admin) for managing the database,
removing notes/comments, and adding courses.

As in iteration 1, the site may be found [here](https://notebook-oose.herokuapp.com/).
The best way to build/run the site locally is by running `./heroku_local.sh`, or using IntelliJ with the environment
variables listed below. Tests can be run through IntelliJ or by running `./gradlew test`.

Tests are also automatically run through GitHub CI and can be seen [here](https://github.com/jhu-oose/2020-spring-group-NoteBook/actions).

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
Similar commands will work for other platforms.

Before running the app, ensure that the following environment variables are set:
```
JDBC_DATABASE_URL='jdbc:postgresql://localhost:5432/notebookdb?user=dbuser&password=dbpasswd'
PORT=7000
AWS_ENABLE=FALSE
```
These can be set manually in IntelliJ by modifying the run configutation, or by running `./heroku_local.sh`
which builds and runs the app locally.

## Iteration 1

For our specification and planning documents see docs/. We have implemented all of the core features planned for iteration 1.
In addition to the core requirements, we have also setup the site for deployment on Heroku and implemented file storage on AWS S3 (under the heroku branch).

The site may be found at: https://notebook-oose.herokuapp.com/

A local copy may be built and run using Gradle. A standalone version may be built and run using the following commands:
```
./gradlew stage
java -jar build/libs/notebook-0.1-SNAPSHOT.jar
```
