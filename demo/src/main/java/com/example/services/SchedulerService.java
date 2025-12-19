package com.example.services;

import com.example.objects.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class SchedulerService {

    public void assignExamsAutomatically(List<Exam> exams, List<Classroom> rooms, List<LocalDateTime> timeSlots) {
        exams.sort((e1, e2) -> Integer.compare(
                e2.getCourse().getStudents().size(),
                e1.getCourse().getStudents().size()));

        rooms.sort(Comparator.comparingInt(Classroom::getCapacity));

        for (int i = 0; i < exams.size(); i++) {
            Exam currentExam = exams.get(i);

            if (currentExam.getStart() != null) continue;

            boolean isAssigned = false;

            for (LocalDateTime slot : timeSlots) {
                if (isAssigned) break;

                for (Classroom room : rooms) {
                    if (isSafeToAssign(currentExam, room, slot, exams)) {
                        Exam newScheduledExam = new Exam(
                                currentExam.getCourse(),
                                room,
                                slot,
                                slot.plusHours(2));
                        exams.set(i, newScheduledExam);
                        isAssigned = true;
                        System.out.println("Assigned: " + currentExam.getCourse().getCourseCode());
                        break;
                    }
                }
            }

            if (!isAssigned) {
                System.err.println("Warning: No suitable slot found for " + currentExam.getCourse().getCourseCode());
            }
        }
    }

    public boolean isRoomAvailable(Classroom room, LocalDateTime slot, List<Exam> allExams) {
        for (Exam e : allExams) {
            if (e.getStart() == null) continue;
            if (e.getClassroom().getClassroomId().equals(room.getClassroomId()) &&
                    e.getStart().equals(slot)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSafeToAssign(Exam examToPlace, Classroom room, LocalDateTime slot, List<Exam> allExams) {
        if (!isRoomAvailable(room, slot, allExams)) return false;

        int studentCount = examToPlace.getCourse().getStudents().size();
        if (room.getCapacity() < studentCount) return false;

        List<Student> studentsInThisExam = examToPlace.getCourse().getStudents();

        for (Student student : studentsInThisExam) {
            int examsOnThatDay = 0;

            for (Exam otherExam : allExams) {
                if (otherExam.getStart() == null || otherExam == examToPlace) continue;

                if (otherExam.getCourse().getStudents().contains(student)) {
                    LocalDateTime otherStart = otherExam.getStart();

                    if (otherStart.equals(slot)) return false;

                    long hoursDiff = Duration.between(otherStart, slot).abs().toHours();
                    if (hoursDiff < 3) {
                        return false;
                    }

                    if (otherStart.toLocalDate().equals(slot.toLocalDate())) {
                        examsOnThatDay++;
                    }
                }
            }

            if (examsOnThatDay >= 2) return false;
        }

        return true;
    }
}