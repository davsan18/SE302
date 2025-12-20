package com.example.objects;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private final String courseCode;
    private final List<Student> students = new ArrayList<>();

    public Course(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student s) {
        students.add(s);
    }

    @Override
    public String toString() {
        return courseCode + " (" + students.size() + " students)";
    }
}
