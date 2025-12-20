package com.example.objects;

public class Classroom {
    private final String classroomId;
    private final int capacity;

    public Classroom(String classroomId, int capacity) {
        this.classroomId = classroomId;
        this.capacity = capacity;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return classroomId + " (Capacity=" + capacity + ")";
    }
}
