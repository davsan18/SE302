package com.example.scheduler;

import java.util.Objects;

public final class TimeSlot implements Comparable<TimeSlot> {
    private final int dayIndex;
    private final int slotIndex;

    public TimeSlot(int dayIndex, int slotIndex) {
        this.dayIndex = dayIndex;
        this.slotIndex = slotIndex;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    @Override
    public int compareTo(TimeSlot other) {
        int d = Integer.compare(this.dayIndex, other.dayIndex);
        if (d != 0) return d;
        return Integer.compare(this.slotIndex, other.slotIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return dayIndex == timeSlot.dayIndex && slotIndex == timeSlot.slotIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayIndex, slotIndex);
    }

    @Override
    public String toString() {
        return "Day=" + dayIndex + ", Slot=" + slotIndex;
    }
}
