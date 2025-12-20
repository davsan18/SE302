package com.example.services;

import com.example.objects.*;
import com.example.scheduler.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulerService {

    public void assignExamsAutomatically(List<Exam> exams, List<Classroom> rooms, List<LocalDateTime> timeSlots) {

        Map<String, Set<String>> courseToStudentsMap = new HashMap<>();
        for (Exam exam : exams) {
            String courseCode = exam.getCourse().getCourseCode();
            Set<String> studentIds = new HashSet<>();
            for (Student s : exam.getCourse().getStudents()) {
                studentIds.add(s.getStudentId());
            }
            courseToStudentsMap.put(courseCode, studentIds);
        }

        Map<String, Integer> roomCapacities = new HashMap<>();
        for (Classroom room : rooms) {
            roomCapacities.put(room.getClassroomId(), room.getCapacity());
        }

        ConflictGraphBuilder builder = new ConflictGraphBuilder();
        ConflictGraph graph = builder.build(courseToStudentsMap);

        ConstraintChecker checker = new ConstraintChecker();

        Map<TimeSlot, List<ExamAssignment>> scheduleBySlot = new HashMap<>();
        Map<String, List<TimeSlot>> studentToAssignedSlots = new HashMap<>();

        exams.sort((e1, e2) -> {
            String c1 = e1.getCourse().getCourseCode();
            String c2 = e2.getCourse().getCourseCode();

            int degree1 = graph.degreeOf(c1);
            int degree2 = graph.degreeOf(c2);

            int degreeCompare = Integer.compare(degree2, degree1);
            if (degreeCompare != 0) return degreeCompare;

            return Integer.compare(e2.getCourse().getStudents().size(), e1.getCourse().getStudents().size());
        });

        rooms.sort(Comparator.comparingInt(Classroom::getCapacity));

        List<java.time.LocalDate> uniqueDays = timeSlots.stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<java.time.LocalTime> uniqueTimes = timeSlots.stream()
                .map(LocalDateTime::toLocalTime)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < exams.size(); i++) {
            Exam currentExam = exams.get(i);

            if (currentExam.getStart() != null) continue;

            boolean isAssigned = false;
            int studentCount = currentExam.getCourse().getStudents().size();

            for (Classroom room : rooms) {
                if (isAssigned) break;

                if (room.getCapacity() < studentCount) {
                    continue;
                }

                List<LocalDateTime> shuffledSlots = new ArrayList<>(timeSlots);
                Collections.shuffle(shuffledSlots);

                for (LocalDateTime slot : shuffledSlots) {

                    int dayIndex = uniqueDays.indexOf(slot.toLocalDate());
                    int slotIndex = uniqueTimes.indexOf(slot.toLocalTime());

                    if (dayIndex == -1 || slotIndex == -1) continue;

                    TimeSlot targetTimeSlot = new TimeSlot(dayIndex, slotIndex);

                    boolean isSafe = checker.canPlaceCourse(
                            currentExam.getCourse().getCourseCode(),
                            targetTimeSlot,
                            room.getClassroomId(),
                            roomCapacities,
                            courseToStudentsMap,
                            scheduleBySlot,
                            studentToAssignedSlots,
                            graph);

                    if (isSafe) {
                        Exam newScheduledExam = new Exam(
                                currentExam.getCourse(),
                                room,
                                slot,
                                slot.plusHours(2));
                        exams.set(i, newScheduledExam);

                        ExamAssignment assignment = new ExamAssignment(
                                currentExam.getCourse().getCourseCode(),
                                room.getClassroomId(),
                                targetTimeSlot);

                        scheduleBySlot.computeIfAbsent(targetTimeSlot, k -> new ArrayList<>()).add(assignment);

                        for (String studentId : courseToStudentsMap.get(currentExam.getCourse().getCourseCode())) {
                            studentToAssignedSlots.computeIfAbsent(studentId, k -> new ArrayList<>()).add(targetTimeSlot);
                        }

                        isAssigned = true;
                        System.out.println("Assigned: " + currentExam.getCourse().getCourseCode() +
                                " -> " + room.getClassroomId());
                        break;
                    }
                }
            }

            if (!isAssigned) {
                System.err.println("WARNING: No Slot Found for " + currentExam.getCourse().getCourseCode());
            }
        }
    }
}