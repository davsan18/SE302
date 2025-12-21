package com.example.objects;

import java.io.Serializable;

public class Classroom implements Serializable{
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
