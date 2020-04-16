package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Date;

/**
 * Note.
 */
public class Note {
    private int id;
    private int courseId;
    private String title;
    private String creator;
    private String filetype;
    private String date;
    private int upvotes;
    private String fulltext;

    /**
     * Instantiates a new note.
     *
     * @param courseId course ID
     * @param title    note title
     * @param creator  note creator name
     * @param filetype note file extension
     */
    public Note(int courseId, String title, String creator, String filetype) {
        this.courseId = courseId;
        this.title = title;
        this.creator = creator;
        this.filetype = filetype;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
        Date date = new Date();
        this.date = dateFormat.format(date);
        this.upvotes = 0;
        this.fulltext = "";
    }

    /**
     * Gets the note ID.
     *
     * @return note ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the note ID.
     *
     * @param id note ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the course this note belongs to.
     *
     * @return course ID
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Sets the course this note belongs to.
     *
     * @param courseId course ID
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the note title.
     *
     * @return note title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the note title.
     *
     * @param title note title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the note creator name.
     *
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the note creator name.
     *
     * @param creator note creator name
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Gets the filetype for the associated file.
     *
     * @return file extension
     */
    public String getFiletype() {
        return filetype;
    }

    /**
     * Sets the filetype for the associated file.
     *
     * @param filetype file extension
     */
    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    /**
     * Gets fulltext.
     *
     * @return the fulltext
     */
    public String getFulltext() {
        return fulltext;
    }

    /**
     * Sets fulltext.
     *
     * @param fulltext the fulltext
     */
    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    /**
     * Gets the date for the associated note.
     *
     * @return date of the note
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Sets the date for the associated note.
     *
     * @param date of the note
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the upvotes for the associated note.
     *
     * @return upvotes of the note
     */
    public int getUpvotes() {
        return this.upvotes;
    }

    /**
     * Sets the upvotes for the associated note.
     *
     * @param upvotes of the note
     */
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    /**
     * Increases the number of upvotes by 1
     *
     */
    public void upvote() {this.upvotes++;}

    /**
     * undos an upvote
     *
     */
    public void unvote() {this.upvotes--;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id &&
                courseId == note.courseId &&
                title.equals(note.title) &&
                creator.equals(note.creator) &&
                date.equals(note.date) &&
                upvotes == note.upvotes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, title, creator, date, upvotes);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                ", date='" + date + '\'' +
                ", upvotes='" + upvotes + '\'' +
                '}';
    }
}