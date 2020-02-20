import model.Course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("all")
public class LambdaDemo {

  public static void main(String[] args) {
//    useAnonymousInnerClass();
//    useLongFormLambda();
//    useShortFormLambda();
    useMethodRefrences();
  }

  private static void useMethodRefrences() {
    List<Course> courses = getSampleCourses();
    Collections.sort(courses, Comparator.comparing(Course::getName));
    courses.forEach(System.out::println);
  }

  private static void useShortFormLambda() {
    List<Course> courses = getSampleCourses();

    Collections.sort(courses, (c1, c2) -> c1.getName().compareTo(c2.getName()));

    courses.forEach(course -> System.out.println(course));

//    for (Course c: courses) {
//      System.out.println(c);
//    }
  }

  private static void useLongFormLambda() {
    List<Course> courses = getSampleCourses();

    Collections.sort(courses, (Course c1, Course c2) -> {
        return c1.getName().compareTo(c2.getName());
      }
    );

    for (Course c: courses) {
      System.out.println(c);
    }
  }


  private static void useAnonymousInnerClass() {
    List<Course> courses = getSampleCourses();

    Collections.sort(courses, new Comparator<Course>() {
      @Override
      public int compare(Course c1, Course c2) {
        return c1.getName().compareTo(c2.getName());
      }
    });

//    for (Course c: courses) {
//      System.out.println(c);
//    }

    Iterator<Course> it = courses.iterator();
    while (it.hasNext()) {
      Course c = it.next();
      System.out.println(c);
    }
  }

  private static List<Course> getSampleCourses() {
    List<Course> courses = new ArrayList<>();
    courses.add(new Course("OOSE", "oos.com"));
    courses.add(new Course("Into CS", "cs.com"));
    courses.add(new Course("Data Structures", "ds.com"));
    return courses;
  }
}
