package com.example.services;

import com.example.objects.ExamScheduler;

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
}
