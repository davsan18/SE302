package com.example.scheduler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConstraintChecker {

    public boolean canPlaceCourse(
            String courseCode,
            TimeSlot targetSlot,
            String roomName,
            Map<String, Integer> roomCapacities,
            Map<String, Set<String>> courseToStudents,
            Map<TimeSlot, List<ExamAssignment>> scheduleBySlot,
            Map<String, List<TimeSlot>> studentToAssignedSlots,
            ConflictGraph conflictGraph
    ) {
        if (courseCode == null || targetSlot == null || roomName == null) return false;
        if (roomCapacities == null || courseToStudents == null
                || scheduleBySlot == null || studentToAssignedSlots == null
                || conflictGraph == null) return false;

        Integer capObj = roomCapacities.get(roomName);
        if (capObj == null) return false;
        int cap = capObj;

        Set<String> enrolledSet = courseToStudents.get(courseCode);
        int enrolled = enrolledSet == null ? 0 : enrolledSet.size();
        if (enrolled > cap) return false;

        List<ExamAssignment> alreadyInSlot = scheduleBySlot.get(targetSlot);
        if (alreadyInSlot != null) {
            for (ExamAssignment ea : alreadyInSlot) {
                if (roomName.equals(ea.getRoomName())) return false;
                if (conflictGraph.hasConflict(courseCode, ea.getCourseCode())) return false;
            }
        }

        if (enrolledSet == null) enrolledSet = Collections.emptySet();
        for (String sid : enrolledSet) {
            if (sid == null) continue;

            List<TimeSlot> assigned = studentToAssignedSlots.get(sid);
            if (assigned == null) assigned = Collections.emptyList();

            int countThisDay = 0;
            for (TimeSlot s : assigned) {
                if (s.getDayIndex() == targetSlot.getDayIndex()) countThisDay++;
            }
            if (countThisDay >= 2) return false;

            for (TimeSlot s : assigned) {
                if (s.getDayIndex() == targetSlot.getDayIndex()) {
                    if (Math.abs(s.getSlotIndex() - targetSlot.getSlotIndex()) == 1) return false;
                }
            }
        }

        return true;
    }
}