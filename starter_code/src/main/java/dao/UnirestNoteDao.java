package dao;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import exception.DaoException;
import model.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnirestNoteDao implements NoteDao {

    private static Gson gson = new Gson();;
    public final String BASE_URL = "http://127.0.0.1:7000/";

    @Override
    public void add(Note note) throws DaoException {
        try {
            Unirest.post(BASE_URL + "/notes").body(gson.toJson(note)).asJson();
        } catch (UnirestException e) {
            // TODO deal with errors
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Note note) throws DaoException {

    }

    @Override
    public List<Note> findAll() {
        try {
            HttpResponse<JsonNode> jsonResponse =
                    Unirest.get(BASE_URL + "/notes").asJson();
            Note[] notes = gson.fromJson(jsonResponse.getBody().toString(),
                    Note[].class);
            return new ArrayList<>(Arrays.asList(notes));
        } catch (UnirestException e) {
            // TODO deal with errors
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Note> findNoteWithCourseId(int courseId) {
        try {
            HttpResponse<JsonNode> jsonResponse =
                    Unirest.get(BASE_URL + "/notes").asJson();
            // Pulls all the notes, want to sort through them for ones with
            // the correct id
            Note[] notes = gson.fromJson(jsonResponse.getBody().toString(),
                    Note[].class);

            List<Note> idNotes = new ArrayList<>();
            for (Note n : notes) {
                if (n.getCourseId() == courseId) {
                    idNotes.add(n);
                }
            }
            return idNotes;
        } catch (UnirestException e) {
            // TODO deal with errors
            e.printStackTrace();
        }
        return null;
    }
}
