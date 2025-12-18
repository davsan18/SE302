package com.example.services;

import com.example.objects.*;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    public List<Student> searchStudents(List<Student> students, String query) {
        List<Student> results = new ArrayList<>();

        for (Student s : students) {
            if (s.matches(query)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Exam> searchExams(List<Exam> exams, String query) {
        List<Exam> results = new ArrayList<>();

        for (Exam e : exams) {
            if (e.matches(query)) {
                results.add(e);
            }
        }
        return results;
    }
}