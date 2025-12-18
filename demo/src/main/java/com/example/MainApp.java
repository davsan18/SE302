package com.example;

import com.example.objects.Classroom;
import com.example.objects.Course;
import com.example.objects.ExamScheduler;
import com.example.objects.Student;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainApp extends Application {

    private final ExamScheduler scheduler = new ExamScheduler();
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

        Menu menu = new Menu("Import");
        menu.getItems().addAll(importStudents, importClassrooms, importCourses);
        MenuBar menuBar = new MenuBar(menu);

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