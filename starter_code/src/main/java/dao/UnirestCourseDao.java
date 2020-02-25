package dao;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import exception.DaoException;
import model.Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnirestCourseDao implements CourseDao {

    private static Gson gson = new Gson();;
    public final String BASE_URL = "http://127.0.0.1:7000/";

    @Override
    public void add(Course course) {
        try {
            Unirest.post(BASE_URL + "/courses").body(gson.toJson(course)).asJson();
        } catch (UnirestException e) {
            // TODO deal with errors
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Course course) throws DaoException {

    }

    @Override
    public List<Course> findAll() {
        try {
            HttpResponse<JsonNode> jsonResponse =
                    Unirest.get(BASE_URL + "/courses").asJson();
            Course[] courses = gson.fromJson(jsonResponse.getBody().toString(), Course[].class);
            return new ArrayList<>(Arrays.asList(courses));
        } catch (UnirestException e) {
            // TODO deal with errors
            e.printStackTrace();
        }
        return null;
    }
}