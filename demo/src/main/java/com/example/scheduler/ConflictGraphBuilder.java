package com.example.scheduler;

import java.util.*;

public final class ConflictGraphBuilder {

    public ConflictGraph build(Map<String, Set<String>> courseToStudentIds) {
        Objects.requireNonNull(courseToStudentIds, "courseToStudentIds");

        ConflictGraph graph = new ConflictGraph();
        for (String course : courseToStudentIds.keySet()) {
            graph.addNode(course);
        }

        Map<String, List<String>> studentToCourses = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : courseToStudentIds.entrySet()) {
            String course = e.getKey();
            Set<String> students = e.getValue();
            if (students == null) continue;
            for (String sid : students) {
                if (sid == null) continue;
                studentToCourses.computeIfAbsent(sid, k -> new ArrayList<>()).add(course);
            }
        }

        for (List<String> courses : studentToCourses.values()) {
            for (int i = 0; i < courses.size(); i++) {
                for (int j = i + 1; j < courses.size(); j++) {
                    graph.addUndirectedEdge(courses.get(i), courses.get(j));
                }
            }
        }

        return graph;
    }
}
