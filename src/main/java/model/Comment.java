package model;

import java.util.Objects;

public class Comment {
    private int id;
    private int noteId;
    private String text;
    private String creator;

    public Comment(int noteId, String text, String creator) {
        this.noteId = noteId;
        this.text = text;
        this.creator = creator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id &&
                noteId == comment.noteId &&
                text.equals(comment.text) &&
                creator.equals(comment.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, noteId, text, creator);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", noteId='" + noteId + '\'' +
                ", text='" + text + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}