package model;

import java.util.Objects;

/**
 * Comment.
 */
public class Comment {
    private int id;
    private int noteId;
    private String text;
    private String creator;

    /**
     * Instantiates a new comment.
     *
     * @param noteId  the note id
     * @param text    the text
     * @param creator the creator
     */
    public Comment(int noteId, String text, String creator) {
        this.noteId = noteId;
        this.text = text;
        this.creator = creator;
    }

    /**
     * Gets the comment ID.
     *
     * @return comment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the comment ID.
     *
     * @param id comment ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the associated note.
     *
     * @return note ID
     */
    public int getNoteId() {
        return noteId;
    }

    /**
     * Sets the associated note.
     *
     * @param noteId note ID
     */
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    /**
     * Gets the comment text.
     *
     * @return comment text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the comment text.
     *
     * @param text comment text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the creator name.
     *
     * @return creator name
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the creator name.
     *
     * @param creator creator name
     */
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