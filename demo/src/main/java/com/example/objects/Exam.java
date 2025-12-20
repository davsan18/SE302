package com.example.objects;

import java.time.LocalDateTime;

public class Exam {
    private final Course course;
    private final Classroom classroom;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public Exam(Course course, Classroom classroom,
                LocalDateTime start, LocalDateTime end) {
        this.course = course;
        this.classroom = classroom;
        this.start = start;
        this.end = end;
    }
    public boolean matches(String query) {
        if (course == null) return false;
        return course.getCourseCode().toLowerCase().contains(query.toLowerCase());
    }
    public Course getCourse() { return course; }
    public Classroom getClassroom() { return classroom; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
}

