export JDBC_DATABASE_URL=jdbc:postgres://localhost/notebookdb
export PORT=7000
export AWS_ENABLE=FALSE
./gradlew stage
heroku local