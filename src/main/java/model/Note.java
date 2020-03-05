package model;

import java.util.Objects;

public class Note {
    private int id;
    private int courseId;
    private String title;
    private String creator;
    private String filetype;

    public Note(int courseId, String title, String creator, String filetype) {
        this.courseId = courseId;
        this.title = title;
        this.creator = creator;
        this.filetype = filetype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id &&
                courseId == note.courseId &&
                title.equals(note.title) &&
                creator.equals(note.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseId, title, creator);
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}