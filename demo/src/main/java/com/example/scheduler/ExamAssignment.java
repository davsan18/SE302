package com.example.scheduler;

import java.util.Objects;

public final class ExamAssignment {
    private final String courseCode;
    private final String roomName;
    private final TimeSlot slot;

    public ExamAssignment(String courseCode, String roomName, TimeSlot slot) {
        this.courseCode = courseCode;
        this.roomName = roomName;
        this.slot = slot;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExamAssignment)) return false;
        ExamAssignment that = (ExamAssignment) o;
        return Objects.equals(courseCode, that.courseCode)
                && Objects.equals(roomName, that.roomName)
                && Objects.equals(slot, that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, roomName, slot);
    }

    @Override
    public String toString() {
        return courseCode + " -> " + roomName + " @ " + slot;
    }
}
