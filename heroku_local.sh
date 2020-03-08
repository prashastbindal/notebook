./gradlew stage
JDBC_DATABASE_URL='jdbc:postgresql://localhost:5432/notebookdb?user=dbuser&password=dbpasswd' PORT=7000 AWS_ENABLE=FALSE heroku local