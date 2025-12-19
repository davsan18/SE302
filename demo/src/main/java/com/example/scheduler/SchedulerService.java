package com.example.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SchedulerService {

    public static final class ScheduleResult {
        private final boolean feasible;
        private final List<ExamAssignment> assignments;
        private final String message;

        private ScheduleResult(boolean feasible, List<ExamAssignment> assignments, String message) {
            this.feasible = feasible;
            this.assignments = assignments;
            this.message = message;
        }

        public static ScheduleResult ok(List<ExamAssignment> assignments) {
            return new ScheduleResult(true, assignments, "Schedule created.");
        }

        public static ScheduleResult noSolution(String message) {
            return new ScheduleResult(false, Collections.<ExamAssignment>emptyList(), message);
        }

        public boolean isFeasible() {
            return feasible;
        }

        public List<ExamAssignment> getAssignments() {
            return assignments;
        }

        public String getMessage() {
            return message;
        }
    }

    public ConflictGraph buildConflictGraph(Map<String, Set<String>> courseToStudents) {
        return new ConflictGraphBuilder().build(courseToStudents);
    }

    public ScheduleResult generateSchedule(
            int dayCount,
            int slotsPerDay,
            Map<String, Integer> roomCapacities,
            Map<String, Set<String>> courseToStudents
    ) {
        if (dayCount <= 0 || slotsPerDay <= 0) {
            return ScheduleResult.noSolution("Invalid dayCount or slotsPerDay.");
        }
        if (roomCapacities == null || roomCapacities.isEmpty()) {
            return ScheduleResult.noSolution("No classrooms provided.");
        }
        if (courseToStudents == null || courseToStudents.isEmpty()) {
            return ScheduleResult.noSolution("No courses provided.");
        }

        ConflictGraph graph = buildConflictGraph(courseToStudents);

        List<TimeSlot> allSlots = new ArrayList<TimeSlot>();
        for (int d = 0; d < dayCount; d++) {
            for (int s = 0; s < slotsPerDay; s++) {
                allSlots.add(new TimeSlot(d, s));
            }
        }

        List<String> courses = new ArrayList<String>(courseToStudents.keySet());
        Collections.sort(courses, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                int deg = Integer.compare(graph.degreeOf(b), graph.degreeOf(a));
                if (deg != 0) return deg;
                int ea = courseToStudents.get(a) == null ? 0 : courseToStudents.get(a).size();
                int eb = courseToStudents.get(b) == null ? 0 : courseToStudents.get(b).size();
                return Integer.compare(eb, ea);
            }
        });

        Map<TimeSlot, List<ExamAssignment>> scheduleBySlot = new HashMap<TimeSlot, List<ExamAssignment>>();
        Map<String, List<TimeSlot>> studentToAssignedSlots = new HashMap<String, List<TimeSlot>>();
        List<ExamAssignment> result = new ArrayList<ExamAssignment>();
        ConstraintChecker checker = new ConstraintChecker();

        List<String> rooms = new ArrayList<String>(roomCapacities.keySet());
        Collections.sort(rooms, new Comparator<String>() {
            @Override
            public int compare(String r1, String r2) {
                return Integer.compare(roomCapacities.get(r1), roomCapacities.get(r2));
            }
        });

        Set<String> scheduledCourses = new HashSet<String>();

        for (String course : courses) {
            if (scheduledCourses.contains(course)) {
                continue;
            }

            boolean placed = false;

            for (TimeSlot slot : allSlots) {
                for (String room : rooms) {
                    if (!checker.canPlaceCourse(
                            course, slot, room,
                            roomCapacities, courseToStudents,
                            scheduleBySlot, studentToAssignedSlots,
                            graph
                    )) {
                        continue;
                    }

                    ExamAssignment ea = new ExamAssignment(course, room, slot);

                    List<ExamAssignment> list = scheduleBySlot.get(slot);
                    if (list == null) {
                        list = new ArrayList<ExamAssignment>();
                        scheduleBySlot.put(slot, list);
                    }
                    list.add(ea);

                    result.add(ea);

                    Set<String> students = courseToStudents.get(course);
                    if (students != null) {
                        for (String sid : students) {
                            if (sid == null) continue;
                            List<TimeSlot> slots = studentToAssignedSlots.get(sid);
                            if (slots == null) {
                                slots = new ArrayList<TimeSlot>();
                                studentToAssignedSlots.put(sid, slots);
                            }
                            slots.add(slot);
                        }
                    }

                    scheduledCourses.add(course);
                    placed = true;
                    break;
                }
                if (placed) break;
            }

            if (!placed) {
                return ScheduleResult.noSolution(
                        "No feasible schedule found under hard constraints. Increase dayCount/slotsPerDay or review data."
                );
            }
        }

        return ScheduleResult.ok(Collections.unmodifiableList(result));
    }
}