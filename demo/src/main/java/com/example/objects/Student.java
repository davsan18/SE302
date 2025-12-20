package com.example.objects;

import java.io.Serializable;

public class Student implements Serializable {
    private final String studentId;

    public Student(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public String toString() {
        return studentId;
    }


    public boolean matches(String query) {
        if (studentId == null) return false;
        return studentId.toLowerCase().contains(query.toLowerCase());
    }
}

