package com.example;

import com.example.objects.Exam;
import com.example.services.SchedulerService;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final SchedulerService schedulerService = new SchedulerService();
    private List<Exam> scheduledExams = new ArrayList<>();



    @FXML private TabPane mainTabs;
    @FXML private ListView<Student> studentsList;
    @FXML private ListView<Course> coursesList;
    @FXML private ListView<Classroom> classroomsList;

    @FXML private TableView<Exam> scheduleTableAll;
    @FXML private TableView<Exam> scheduleTableStudent;

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

        setupScheduleTable();

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

        if (data.courses.isEmpty() || data.classrooms.isEmpty()) {
            info("Create Schedule", "Import Courses and Classrooms before creating schedule.");
            return;
        }

        try {
            scheduledExams = new ArrayList<>();
            for (Course c : data.courses) {
                scheduledExams.add(new Exam(c, null, null, null));
            }

            List<LocalDateTime> timeSlots = new ArrayList<>();
            LocalDateTime base = LocalDateTime.now()
                    .withHour(9).withMinute(0).withSecond(0).withNano(0);

            for (int day = 0; day < 5; day++) {
                for (int slot = 0; slot < 3; slot++) {
                    timeSlots.add(base.plusDays(day).plusHours(slot * 3));
                }
            }
            schedulerService.assignExamsAutomatically(scheduledExams, data.classrooms, timeSlots);
            boolean anyScheduled = false;
            for (Exam e : scheduledExams) {
                if (e.getStart() != null) { anyScheduled = true; break; }
            }
            if (!anyScheduled) {
                info("Create Schedule", "No exams could be scheduled.");
                scheduleCreated.set(false);
                return;
            }

            scheduleCreated.set(true);

            if (scheduleTableAll != null) {
                scheduleTableAll.getItems().setAll(scheduledExams);
            }
            if (scheduleTableStudent != null) {
                scheduleTableStudent.getItems().setAll(scheduledExams);
            }

            info("Create Schedule", "Schedule created successfully.");
            mainTabs.getSelectionModel().select(3);

        } catch (Exception ex) {
            scheduleCreated.set(false);
            showError("Schedule creation failed", ex);
        }
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

    private void setupScheduleTable() {
        if (scheduleTableAll != null && scheduleTableAll.getColumns().isEmpty()) {

            TableColumn<Exam, String> cCourse = new TableColumn<>("Course");
            cCourse.setCellValueFactory(e ->
                    new SimpleStringProperty(
                            e.getValue().getCourse() == null ? "-" : e.getValue().getCourse().getCourseCode()
                    ));

            TableColumn<Exam, String> cRoom = new TableColumn<>("Room");
            cRoom.setCellValueFactory(e ->
                    new SimpleStringProperty(
                            e.getValue().getClassroom() == null ? "-" : e.getValue().getClassroom().getClassroomId()
                    ));

            TableColumn<Exam, String> cStart = new TableColumn<>("Start");
            cStart.setCellValueFactory(e ->
                    new SimpleStringProperty(String.valueOf(e.getValue().getStart())));

            scheduleTableAll.getColumns().addAll(cCourse, cRoom, cStart);
        }

        if (scheduleTableStudent != null && scheduleTableStudent.getColumns().isEmpty()) {
            TableColumn<Exam, String> cCourse = new TableColumn<>("Course");
            cCourse.setCellValueFactory(e ->
                    new SimpleStringProperty(
                            e.getValue().getCourse() == null ? "-" : e.getValue().getCourse().getCourseCode()
                    ));

            TableColumn<Exam, String> cRoom = new TableColumn<>("Room");
            cRoom.setCellValueFactory(e ->
                    new SimpleStringProperty(
                            e.getValue().getClassroom() == null ? "-" : e.getValue().getClassroom().getClassroomId()
                    ));

            TableColumn<Exam, String> cStart = new TableColumn<>("Start");
            cStart.setCellValueFactory(e ->
                    new SimpleStringProperty(String.valueOf(e.getValue().getStart())));

            scheduleTableStudent.getColumns().addAll(cCourse, cRoom, cStart);
        }
    }










}
