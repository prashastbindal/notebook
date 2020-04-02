export JDBC_DATABASE_URL='jdbc:postgresql://localhost:5432/notebookdb?user=dbuser&password=dbpasswd'
export PORT=7000
export AWS_ENABLE=FALSE
./gradlew stage
heroku local