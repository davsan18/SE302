package com.example.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamScheduler {

    public final List<Student> students = new ArrayList<>();
    public final List<Classroom> classrooms = new ArrayList<>();
    public final List<Course> courses = new ArrayList<>();

    private final Map<String, Student> studentMap = new HashMap<>();

    public void loadStudents(File csv) throws IOException {
        students.clear();
        studentMap.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;

            // First line must be header
            line = br.readLine();
            if (line == null || !line.toUpperCase().contains("ALL OF THE STUDENTS IN THE SYSTEM")) {
                throw new IOException("Invalid Students file, the .csv file needs to contain \"ALL OF THE STUDENTS IN THE SYSTEM\" at the top of the file. (without the quotations).");
            }

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                Student s = new Student(line.trim());
                students.add(s);
                studentMap.put(s.getStudentId(), s);
            }
        }
    }

    /* ================= CLASSROOMS ================= */
    public void loadClassrooms(File csv) throws IOException {
        classrooms.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;

            // First line must be header
            line = br.readLine();
            if (line == null || !line.toUpperCase().contains("CLASSROOM")) {
                throw new IOException("Invalid Classroom file, the .csv file needs to contain \"ALL OF THE CLASSROOM; IN THE SYSTEM\" at the top of the file. (without the quotations).");
            }

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(";");
                if (parts.length != 2) continue;

                classrooms.add(new Classroom(
                        parts[0].trim(),
                        Integer.parseInt(parts[1].trim())
                ));
            }
        }
    }

    /* ================= COURSES ================= */
    public void loadCourses(File csv) throws IOException {
        courses.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                // This line is a course code
                String courseCode = line.trim();
                Course course = new Course(courseCode);

                // Next line must be the student list
                String studentLine = br.readLine();
                if (studentLine == null) break;

                studentLine = studentLine.trim();

                // Remove [ ]
                studentLine = studentLine
                        .replace("[", "")
                        .replace("]", "");

                String[] ids = studentLine.split(",");

                for (String id : ids) {
                    id = id.replace("'", "").trim();
                    Student s = studentMap.get(id);
                    if (s != null) {
                        course.addStudent(s);
                    }
                }

                courses.add(course);
            }
        }
    }
}

