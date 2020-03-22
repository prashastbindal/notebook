package model;

import java.util.Objects;

/**
 * Course.
 */
public class Course {
  private int id;
  private String name;

  /**
   * Instantiates a new course.
   *
   * @param name course title
   */
  public Course(String name) {
    this.name = name;
  }

  /**
   * Gets the course ID.
   *
   * @return course ID
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the course ID.
   *
   * @param id course ID
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the course title.
   *
   * @return course title
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the course title.
   *
   * @param name course title
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Course course = (Course) o;
    return id == course.id &&
        name.equals(course.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return "Course{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
