package com.example.services;

import com.example.objects.*;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduleEditController {

    private List<Exam> allExams;

    public ScheduleEditController(List<Exam> allExams) {
        this.allExams = allExams;
    }

    public void manualMoveExam(Exam examToMove, Classroom newRoom, LocalDateTime newStart) {
        System.out.println("Moving " + examToMove.getCourse().getCourseCode() +
                " to Room: " + newRoom.getClassroomId() + " Time: " + newStart);

        checkConflicts(examToMove, newRoom, newStart);
        updateExam(examToMove, newRoom, newStart);
    }

    private void updateExam(Exam exam, Classroom room, LocalDateTime start) {
        exam.setClassroom(room);
        exam.setStart(start);
        exam.setEnd(start.plusHours(2));
        System.out.println("SUCCESS: Exam moved successfully.");
    }

    private void checkConflicts(Exam targetExam, Classroom targetRoom, LocalDateTime targetStart) {
        LocalDateTime targetEnd = targetStart.plusHours(2);

        int studentCount = targetExam.getCourse().getStudents().size();
        if (targetRoom.getCapacity() < studentCount) {
            System.err.println("WARNING: Capacity Exceeded! Room: " + targetRoom.getCapacity() +
                    ", Students: " + studentCount);
        }

        for (Exam other : allExams) {
            if (other == null || other == targetExam || other.getStart() == null || other.getEnd() == null) {
                continue;
            }
            boolean timeOverlap = isOverlapping(targetStart, targetEnd, other.getStart(), other.getEnd());

            if (timeOverlap) {
                if (other.getClassroom().getClassroomId().equals(targetRoom.getClassroomId())) {
                    System.err.println("WARNING: Room Conflict! Occupied by " +
                            other.getCourse().getCourseCode());
                }

                if (hasCommonStudents(targetExam.getCourse(), other.getCourse())) {
                    System.err.println("WARNING: Student Conflict! Common students with " +
                            other.getCourse().getCourseCode());
                }
            }
        }
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hasCommonStudents(Course c1, Course c2) {
        for (Student s1 : c1.getStudents()) {
            for (Student s2 : c2.getStudents()) {
                if (s1.getStudentId().equals(s2.getStudentId())) return true;
            }
        }
        return false;
    }
    public List<Exam> getExams(){
     return allExams;
    }
}
