package dao;

import exception.DaoException;
import model.Note;

import java.util.List;

public interface NoteDao {
    void add(Note note) throws DaoException;
    void remove(Note note) throws DaoException;
    List<Note> findNoteWithCourseId(int courseId);
    Note findNote(int noteId);
}
