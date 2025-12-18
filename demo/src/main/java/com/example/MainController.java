package com.example;
import com.example.objects.Classroom;
import com.example.objects.Course;
import com.example.objects.ExamScheduler;
import com.example.objects.Student;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class MainController {
    private final ExamScheduler data = new ExamScheduler();
    private final BooleanProperty scheduleCreated = new SimpleBooleanProperty(false);


    @FXML private TabPane mainTabs;
    @FXML private ListView<Student> studentsList;
    @FXML private ListView<Course> coursesList;
    @FXML private ListView<Classroom> classroomsList;

    @FXML private TableView<?> scheduleTableAll;
    @FXML private TableView<?> scheduleTableStudent;

    @FXML private MenuItem miEditExam;
    @FXML private MenuItem miViewSchedule;
    @FXML private Button btnCreateSchedule;
    @FXML private Button btnViewSchedule;

    @FXML
    public void initialize() {

        BooleanBinding scheduleMissing = scheduleCreated.not();

        if (miEditExam != null) miEditExam.disableProperty().bind(scheduleMissing);
        if (miViewSchedule != null) miViewSchedule.disableProperty().bind(scheduleMissing);
        if (btnViewSchedule != null) btnViewSchedule.disableProperty().bind(scheduleMissing);

        if (btnCreateSchedule != null) btnCreateSchedule.setDisable(false);
    }


    @FXML
    public void handleNew() {
        data.students.clear();
        data.classrooms.clear();
        data.courses.clear();
        refreshLists();
        scheduleCreated.set(false);
        info("New", "New session started (data cleared).");
    }

    @FXML
    public void handleOpen() {
        info("Open", "Open (placeholder).");
    }

    @FXML
    public void handleSaveAs() {
        info("Save As", "Save As (placeholder).");
    }

    @FXML
    public void handleExit() {
        System.exit(0);
    }

    @FXML
    public void handleImportStudents() {
        File f = pickCsv("Import Students");
        if (f == null) return;
        try {
            data.loadStudents(f);
            refreshLists();
        } catch (Exception ex) {
            showError("Students import failed", ex);
        }
    }

    @FXML
    public void handleImportCourses() {
        File f = pickCsv("Import Courses");
        if (f == null) return;
        try {
            data.loadCourses(f);
            refreshLists();
        } catch (Exception ex) {
            showError("Courses import failed", ex);
        }
    }

    @FXML
    public void handleImportClassrooms() {
        File f = pickCsv("Import Classrooms");
        if (f == null) return;
        try {
            data.loadClassrooms(f);
            refreshLists();
        } catch (Exception ex) {
            showError("Classrooms import failed", ex);
        }
    }

    @FXML public void handleViewStudents()   { mainTabs.getSelectionModel().select(0); }
    @FXML public void handleViewCourses()    { mainTabs.getSelectionModel().select(1); }
    @FXML public void handleViewClassrooms() { mainTabs.getSelectionModel().select(2); }

    @FXML
    public void handleViewSchedule() {
        if (!scheduleCreated.get()) {
            info("Schedule", "No schedule exists yet. Create schedule first.");
            return;
        }
        mainTabs.getSelectionModel().select(3); // Schedule (All Exams)
    }

    @FXML
    public void handleEditExam() {
        info("Edit Exam", "Edit Exam UI (placeholder).");
    }

    @FXML
    public void handleCreateSchedule() {
        //The scheduling algorithm connected here

        if (data.courses.isEmpty() || data.classrooms.isEmpty()) {
            info("Create Schedule", "Import Courses and Classrooms before creating schedule.");
            return;
        }

        scheduleCreated.set(true);
        info("Create Schedule", "Schedule created (placeholder). Exam/Schedule edit is now enabled.");
    }

    @FXML
    public void handleUserManual() {
        info("Help", "User Manual (placeholder).");
    }

    @FXML
    public void handleAbout() {
        info("About", "Application version 0.1\nBuilt with JavaFX.");
    }

    private void refreshLists() {
        if (studentsList != null) studentsList.getItems().setAll(data.students);
        if (coursesList != null) coursesList.getItems().setAll(data.courses);
        if (classroomsList != null) classroomsList.getItems().setAll(data.classrooms);
    }

    private File pickCsv(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Window w = (mainTabs != null && mainTabs.getScene() != null) ? mainTabs.getScene().getWindow() : null;
        return fc.showOpenDialog(w);
    }

    private void info(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(text);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private void showError(String header, Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.setContentText(ex.getMessage());
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }
}
