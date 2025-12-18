package com.example.scheduler;

import java.util.*;

public final class ConflictGraph {
    private final Map<String, Set<String>> adjacency = new HashMap<>();

    public void addNode(String courseCode) {
        if (courseCode == null) return;
        adjacency.computeIfAbsent(courseCode, k -> new HashSet<>());
    }

    public void addUndirectedEdge(String courseA, String courseB) {
        if (courseA == null || courseB == null) return;
        if (courseA.equals(courseB)) return;
        addNode(courseA);
        addNode(courseB);
        adjacency.get(courseA).add(courseB);
        adjacency.get(courseB).add(courseA);
    }

    public Set<String> neighborsOf(String courseCode) {
        Set<String> n = adjacency.get(courseCode);
        if (n == null) return Collections.emptySet();
        return Collections.unmodifiableSet(n);
    }

    public boolean hasConflict(String courseA, String courseB) {
        Set<String> n = adjacency.get(courseA);
        return n != null && n.contains(courseB);
    }

    public Set<String> nodes() {
        return Collections.unmodifiableSet(adjacency.keySet());
    }

    public int degreeOf(String courseCode) {
        Set<String> n = adjacency.get(courseCode);
        return n == null ? 0 : n.size();
    }
}