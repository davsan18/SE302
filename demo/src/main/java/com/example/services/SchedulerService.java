package com.example.services;

import com.example.objects.*;
import java.time.LocalDateTime;
import java.util.List;

public class SchedulerService {
    public void assignExamsAutomatically(List<Exam> exams, List<Classroom> rooms, List<LocalDateTime> timeSlots) {
        for (int i = 0; i < exams.size(); i++) {
            Exam currentExam = exams.get(i);

            if (currentExam.getStart() != null) continue;

            for (LocalDateTime slot : timeSlots) {
                boolean assigned = false;
                for (Classroom room : rooms) {
                    if (isRoomAvailable(room, slot, exams)) {
                        Exam newScheduledExam = new Exam(
                                currentExam.getCourse(),
                                room,
                                slot,
                                slot.plusHours(2));
                        exams.set(i, newScheduledExam);
                        assigned = true;
                        break;
                    }
                }
                if (assigned) break;
            }
        }
    }

    private boolean isRoomAvailable(Classroom room, LocalDateTime slot, List<Exam> allExams) {
        for (Exam e : allExams) {
            if (e.getStart() == null) continue;
            if (e.getClassroom().getClassroomId().equals(room.getClassroomId()) &&
                    e.getStart().equals(slot)) {
                return false;
            }
        }
        return true;
    }
}