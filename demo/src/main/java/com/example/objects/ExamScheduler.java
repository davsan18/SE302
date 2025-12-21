package com.example.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExamScheduler implements Serializable {

    public final List<Student> students = new ArrayList<>();
    public final List<Classroom> classrooms = new ArrayList<>();
    public final List<Course> courses = new ArrayList<>();
    public List<Exam> exams = new ArrayList<>();

    private final Map<String, Student> studentMap = new HashMap<>();
    private final Map<String, Classroom> classroomMap = new HashMap<>();
    private final Map<String, Course> courseMap = new HashMap<>();
    private final Map<String, Exam> examMap = new HashMap<>();
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

                String id = line.trim();


                Student s = studentMap.get(id);
                if (s == null) {
                    s = new Student(id);
                    studentMap.put(id, s);
                    students.add(s);
                }
            }
        }
    }

    //CLASSROOMS
    public void loadClassrooms(File csv) throws IOException {
        classrooms.clear();
        classroomMap.clear();
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

                String roomId = parts[0].trim();
                int capacity = Integer.parseInt(parts[1].trim());

                Classroom c = classroomMap.get(roomId);

                if (c == null) {
                    c = new Classroom(roomId, capacity);
                    classroomMap.put(roomId, c);
                    classrooms.add(c);
                } else {
                    // Update capacity if changed
                    if (c.getCapacity() != capacity) {
                        classroomMap.put(roomId,
                                new Classroom(roomId, capacity));
                        classrooms.remove(c);
                        classrooms.add(classroomMap.get(roomId));
                    }
                }
            }
        }
    }

    //COURSES
    public void loadCourses(File csv) throws IOException {
        courses.clear();
        courseMap.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                if (line.toUpperCase().contains("ALL OF THE COURSES IN THE SYSTEM")) continue;

                String courseCode = line.trim();

                Course course = courseMap.get(courseCode);
                if (course == null) {
                    course = new Course(courseCode);
                    courseMap.put(courseCode, course);
                    courses.add(course);
                }

                // Next line = student list
                String studentLine = br.readLine();
                if (studentLine == null) break;

                studentLine = studentLine
                        .replace("[", "")
                        .replace("]", "");

                String[] ids = studentLine.split(",");

                for (String id : ids) {
                    id = id.replace("'", "").trim();
                    Student s = studentMap.get(id);
                    if (s != null && !course.getStudents().contains(s)) {
                        course.addStudent(s);
                    }
                }
            }
        }
    }

    public void loadAttendance(File csv) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String upper = line.toUpperCase();
                if (upper.contains("ALL ATTENDANCE") || upper.contains("ATTENDANCE")) {
                    continue;
                }

                String courseCode = line.trim();

                Course course = courseMap.get(courseCode);
                if (course == null) {
                    course = new Course(courseCode);
                    courseMap.put(courseCode, course);
                    courses.add(course);
                }

                String studentLine = br.readLine();
                if (studentLine == null) break;

                String cleaned = studentLine.replace("[", "").replace("]", "").trim();
                if (cleaned.isEmpty()) continue;

                String[] ids = cleaned.split(",");
                for (String raw : ids) {
                    String id = raw.replace("'", "").replace("\"", "").trim();
                    if (id.isEmpty()) continue;

                    Student s = studentMap.get(id);
                    if (s == null) {
                        s = new Student(id);
                        studentMap.put(id, s);
                        students.add(s);
                    }

                    if (!course.getStudents().contains(s)) {
                        course.addStudent(s);
                    }
                }
            }
        }
    }

    public Map<String, Integer> convertClassroomsToCapacityMap() {
        Map<String, Integer> roomCapacities = new HashMap<>();

        for (Classroom classroom : classrooms) {
            roomCapacities.put(
                    classroom.getClassroomId(),
                    classroom.getCapacity()
            );
        }

        return roomCapacities;
    }
    public Map<String, Set<String>> convertCoursesToStudentMap() {
        Map<String, Set<String>> courseToStudents = new HashMap<>();

        for (Course course : courses) {
            Set<String> studentIds = new HashSet<>();

            for (Student student : course.getStudents()) {
                studentIds.add(student.getStudentId());
            }

            courseToStudents.put(
                    course.getCourseCode(),
                    studentIds
            );
        }

        return courseToStudents;
    }
    public Course getCourseFromId(String key){
        return courseMap.get(key);
    }
    public Student getStudentFromId(String key){
        return studentMap.get(key);
    }
    public Classroom getClassroomFromId(String key){
        return classroomMap.get(key);
    }
    public void setExamMap(List<Exam> exams){
        for (Exam exam : exams) {
            examMap.put(exam.getCourse().getCourseCode(), exam);
        }
    }
    public Exam getExamFromCourseID(String key){
        return examMap.get(key);
    }
}

