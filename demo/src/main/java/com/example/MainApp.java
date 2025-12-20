package com.example;

import java.io.File;
import java.io.IOException;

import com.example.objects.Classroom;
import com.example.objects.Course;
import com.example.objects.ExamScheduler;
import com.example.objects.Student;
import com.example.services.ExamSchedularSerializer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainApp extends Application {

    private ExamScheduler scheduler = new ExamScheduler();
    @Override
    public void start(Stage stage) {

        TabPane tabPane = new TabPane();

        ListView<Student> studentView = new ListView<>();
        ListView<Classroom> classroomView = new ListView<>();
        ListView<Course> courseView = new ListView<>();

        Tab studentsTab = new Tab("Students", studentView);
        Tab classroomsTab = new Tab("Classrooms", classroomView);
        Tab coursesTab = new Tab("Courses", courseView);

        studentsTab.setClosable(false);
        classroomsTab.setClosable(false);
        coursesTab.setClosable(false);

        tabPane.getTabs().addAll(studentsTab, classroomsTab, coursesTab);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        MenuItem importStudents = new MenuItem("Import Students");
        MenuItem importClassrooms = new MenuItem("Import Classrooms");
        MenuItem importCourses = new MenuItem("Import Courses");

        importStudents.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    scheduler.loadStudents(file);
                    studentView.getItems().setAll(scheduler.students);
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        importClassrooms.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    scheduler.loadClassrooms(file);
                    classroomView.getItems().setAll(scheduler.classrooms);
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        importCourses.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    scheduler.loadCourses(file);
                    courseView.getItems().setAll(scheduler.courses);
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        Menu Import = new Menu("Import");
        Import.getItems().addAll(importStudents, importClassrooms, importCourses);
        FileChooser saveLoadChooser = new FileChooser();
        saveLoadChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Exam Scheduler Files", "*.examsched")
        );
        MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> {
            File file = saveLoadChooser.showSaveDialog(stage);
            try {
                ExamSchedularSerializer.save(file,scheduler);
            } catch (IOException ex) {
                showError(ex);
            }
        });
        MenuItem load = new MenuItem("Load");
        load.setOnAction(e -> {
            File file = saveLoadChooser.showOpenDialog(stage);
            try {
                scheduler = ExamSchedularSerializer.load(file);
            } catch (IOException ex) {
                showError(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        Menu saveLoad = new Menu("Save/load");
        saveLoad.getItems().addAll(save,load);
        MenuBar menuBar = new MenuBar(Import,saveLoad);

        BorderPane root = new BorderPane();

        root.setTop(menuBar);
        root.setCenter(tabPane);

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Exam Scheduling System");
        stage.show();
    }
    private void showError(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("File Load Error");
        alert.setContentText(ex.getMessage());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    public static void main(String[] args) {
        launch();
    }
}