package dao;

import exception.DaoException;
import model.Comment;

import java.util.List;

public interface CommentDao {
    void add(Comment comment) throws DaoException;
    void remove(Comment comment) throws DaoException;
    List<Comment> findCommentWithNoteId(int noteId);
    Comment findComment(int commentId);
}
