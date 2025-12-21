package com.example.services;

import com.example.objects.Exam;
import com.example.objects.ExamScheduler;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import java.io.*;

public class ExamSchedularSerializer {
    public static ExamScheduler load(File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return (ExamScheduler)ois.readObject();
    }
    public static void save(File file,ExamScheduler save) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(save);
    }

    // ======================
    // CSV EXPORT (Schedule)
    // ======================
    private static final DateTimeFormatter DT_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);

    public static void exportScheduleToCsv(List<Exam> exams, File file) throws IOException {
        if (exams == null) throw new IllegalArgumentException("exams is null");
        if (file == null) throw new IllegalArgumentException("file is null");

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            bw.write("Course,Room,Start");
            bw.newLine();

            for (Exam e : exams) {
                String course = (e.getCourse() == null) ? "" : e.getCourse().getCourseCode();
                String room = (e.getClassroom() == null) ? "" : e.getClassroom().getClassroomId();
                LocalDateTime dt = e.getStart();
                String start = (dt == null) ? "" : dt.format(DT_FORMAT);

                bw.write(csv(course) + "," + csv(room) + "," + csv(start));
                bw.newLine();
            }
        }
    }

    private static String csv(String s) {
        if (s == null) return "";
        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String t = s.replace("\"", "\"\"");
        return mustQuote ? "\"" + t + "\"" : t;
    }




}
