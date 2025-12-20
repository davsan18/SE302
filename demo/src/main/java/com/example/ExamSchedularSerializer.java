package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.example.objects.ExamScheduler;

public class ExamSchedularSerializer {

    public static ExamScheduler load(File file)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (ExamScheduler) ois.readObject();
        }
    }

    public static void save(File file, ExamScheduler save)
            throws IOException {

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(save);
        }
    }
}

