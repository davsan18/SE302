package com.example;

import com.example.objects.Exam;
import com.example.scheduler.SchedulerService;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    @FXML private ComboBox<Student> cbStudents;

    @FXML private MenuItem miEditExam;
    @FXML private MenuItem miViewSchedule;
    @FXML private Button btnCreateSchedule;
    @FXML private Button btnViewSchedule;

    @FXML
    public void initialize() {

        BooleanBinding scheduleMissing = scheduleCreated.not();
        if (cbStudents != null) cbStudents.disableProperty().bind(scheduleMissing);
        if (miEditExam != null) miEditExam.disableProperty().bind(scheduleMissing);
        if (miViewSchedule != null) miViewSchedule.disableProperty().bind(scheduleMissing);
        if (btnViewSchedule != null) btnViewSchedule.disableProperty().bind(scheduleMissing);
        if (btnCreateSchedule != null) btnCreateSchedule.setDisable(false);

        setupScheduleTable();

        if (cbStudents != null) {
            cbStudents.setOnAction(e -> applyStudentFilter());
        }


    }


    @FXML
    public void handleNew() {
        data.students.clear();
        data.classrooms.clear();
        data.courses.clear();
        refreshLists();
        if (cbStudents != null) {
            cbStudents.getSelectionModel().clearSelection();
            cbStudents.getItems().clear();
        }
        if (scheduleTableStudent != null) scheduleTableStudent.getItems().clear();

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
    /*@FXML
    public void handleCreateSchedule() {

        if (data.courses.isEmpty() || data.classrooms.isEmpty() || data.students.isEmpty()) {
            info("Create Schedule", "Import Students, Courses and Classrooms before creating schedule.");
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

            schedulerService.assignExamsAutomatically(
                    scheduledExams, data.classrooms, timeSlots
            );

            boolean anyScheduled = false;
            for (Exam e : scheduledExams) {
                if (e.getStart() != null) {
                    anyScheduled = true;
                    break;
                }
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

            applyStudentFilter();
            mainTabs.getSelectionModel().select(3);

            info("Create Schedule", "Schedule created successfully.");

        } catch (Exception ex) {
            scheduleCreated.set(false);
            showError("Schedule creation failed", ex);
        }
    }*/

    @FXML
   private void showScheduleInputWindow() {
        if (data.courses.isEmpty() || data.classrooms.isEmpty() || data.students.isEmpty()) {
            info("Create Schedule", "Import Students, Courses and Classrooms before creating schedule.");
            return;
        }
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Generate Schedule");

        Label dayLabel = new Label("Day count:");
        TextField dayField = new TextField();

        Label slotLabel = new Label("Slots per day:");
        TextField slotField = new TextField();

        Button generateBtn = new Button("Generate");
        Button cancelBtn = new Button("Cancel");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        generateBtn.setOnAction(e -> {
            try {
                int dayCount = Integer.parseInt(dayField.getText());
                int slotsPerDay = Integer.parseInt(slotField.getText());

                if (dayCount <= 0 || slotsPerDay <= 0) {
                    errorLabel.setText("Values must be positive");
                    return;
                }

                SchedulerService.ScheduleResult result = schedulerService.generateSchedule(
                        dayCount,
                        slotsPerDay,
                        data.convertClassroomsToCapacityMap(),
                        data.convertCoursesToStudentMap()
                );

                scheduledExams = convertScheduleResultToExams(result);

                scheduleTableAll.getItems().setAll(scheduledExams);

                scheduleCreated.set(true);
                applyStudentFilter();

                mainTabs.getSelectionModel().select(3); // go to Schedule tab
                dialog.close();

            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid integers");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, generateBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10,
                dayLabel, dayField,
                slotLabel, slotField,
                errorLabel,
                buttons
        );
        layout.setPadding(new Insets(15));

        dialog.setScene(new Scene(layout));
        dialog.showAndWait();
    }

    private List<Exam> convertScheduleResultToExams(
            SchedulerService.ScheduleResult result
    ) {
        List<Exam> exams = new ArrayList<>();

        if (!result.isFeasible()) {
            throw new IllegalStateException(result.getMessage());
        }

        for (var assignment : result.getAssignments()) {

            Course course = data.getCourseFromId(assignment.getCourseCode());
            Classroom room = data.getClassroomFromId(assignment.getRoomName());


            int day = assignment.getSlot().getDayIndex();
            int slot = assignment.getSlot().getSlotIndex();

            LocalDateTime start = LocalDateTime.now()
                    .withHour(9).withMinute(0).withSecond(0).withNano(0)
                    .plusDays(day)
                    .plusHours(slot * 3);

            Exam exam = new Exam(course, room, start, null);
            exams.add(exam);
        }

        return exams;
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
        if (cbStudents != null) cbStudents.getItems().setAll(data.students);

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
            cStart.setPrefWidth(180);

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

    private void applyStudentFilter() {
        if (scheduleTableStudent == null) return;

        if (!scheduleCreated.get()) {
            scheduleTableStudent.getItems().clear();
            return;
        }
        if (cbStudents == null || cbStudents.getValue() == null) {
            scheduleTableStudent.getItems().clear();
            return;
        }

        Student selected = cbStudents.getValue();
        List<String> selectedCourseCodes = new ArrayList<>();
        for (Course c : data.courses) {
            if (c.getStudents().contains(selected)) {
                selectedCourseCodes.add(c.getCourseCode());
            }
        }

        List<Exam> filtered = new ArrayList<>();
        for (Exam e : scheduledExams) {
            if (e.getCourse() == null) continue;
            String code = e.getCourse().getCourseCode();
            if (selectedCourseCodes.contains(code)) {
                filtered.add(e);
            }
        }

        scheduleTableStudent.getItems().setAll(filtered);
    }
}
