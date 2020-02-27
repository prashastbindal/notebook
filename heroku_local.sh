export JDBC_DATABASE_URL=jdbc:sqlite:Courses.db
export PORT=7000
export AWS_ACCESS_KEY_ID=AKIAJIX3Q6GVAGATZOHA
export AWS_SECRET_ACCESS_KEY=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
export AWS_S3_BUCKET=notebook-oose
export AWS_ENABLE=TRUE
./gradlew stage
heroku local