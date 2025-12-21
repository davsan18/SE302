package com.example;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.example.objects.Classroom;
import com.example.objects.Course;
import com.example.objects.Exam;
import com.example.objects.ExamScheduler;
import com.example.objects.Student;
import com.example.scheduler.SchedulerService;
import com.example.services.ExamSchedularSerializer;
import com.example.services.ScheduleEditController;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

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


public class MainController {
    private ExamScheduler data = new ExamScheduler();
    private final BooleanProperty scheduleCreated = new SimpleBooleanProperty(false);
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
    private final SchedulerService schedulerService = new SchedulerService();



    @FXML private TabPane mainTabs;
    @FXML private ListView<Student> studentsList;
    @FXML private ListView<Course> coursesList;

    @FXML private ListView<Classroom> classroomsList;
    @FXML private ListView<String> attendanceList;

    @FXML private TableView<Exam> scheduleTableAll;
    @FXML private TableView<Exam> scheduleTableStudent;
    @FXML private ComboBox<Student> cbStudents;

    @FXML private MenuItem miEditExam;
    @FXML private MenuItem miViewSchedule;
    @FXML private Button btnCreateSchedule;
    @FXML private Button btnViewSchedule;
    @FXML private Button btnExportSchedule;


    @FXML
    public void initialize() {

        BooleanBinding scheduleMissing = scheduleCreated.not();
        if (cbStudents != null) cbStudents.disableProperty().bind(scheduleMissing);
        if (miEditExam != null) miEditExam.disableProperty().bind(scheduleMissing);
        if (miViewSchedule != null) miViewSchedule.disableProperty().bind(scheduleMissing);
        if (btnViewSchedule != null) btnViewSchedule.disableProperty().bind(scheduleMissing);
        if (btnExportSchedule != null) btnExportSchedule.disableProperty().bind(scheduleMissing);
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
        data.exams.clear();
        if (cbStudents != null) {
            cbStudents.getSelectionModel().clearSelection();
            cbStudents.getItems().clear();
        }
        if (scheduleTableStudent != null) scheduleTableStudent.getItems().clear();

        scheduleCreated.set(false);
        refreshLists();
        info("New", "New session started (data cleared).");
    }

    @FXML
    public void handleOpen() {
        File file = pickProj("Open Save File");
        if (file != null) {
            try {
                ExamScheduler newData = ExamSchedularSerializer.load(file);
                data = newData;
                refreshLists();
                if(data.exams != null){
                    scheduleCreated.set(true);
                }
            } catch (IOException ex) {
                showError(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @FXML
    public void handleSaveAs() {
        File file = pickProj("Select Save File");
        if (file != null) {
            try {
                ExamSchedularSerializer.save(file, data);
            } catch (IOException ex) {
                showError(ex);
            }
        }
    }

    @FXML
    public void handleExportSchedule() {
        if (!scheduleCreated.get() || data.exams == null || data.exams.isEmpty()) {
            info("Export Schedule", "No schedule to export. Create schedule first.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Export Schedule as CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("schedule.csv");

        Window w = (mainTabs != null && mainTabs.getScene() != null) ? mainTabs.getScene().getWindow() : null;
        File out = fc.showSaveDialog(w);
        if (out == null) return;

        try {
            // ✅ CSV export burada
            ExamSchedularSerializer.exportScheduleToCsv(data.exams, out);

            info("Export Schedule", "Schedule exported successfully.");
        } catch (IOException ex) {
            showError(ex);
        }
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
            showError(ex);
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
            showError(ex);
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
            showError(ex);
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
        if (data.exams.isEmpty()) {
            info("No Exams", "There are no exams to edit.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Exam");

        ComboBox<Exam> examCombo = new ComboBox<>();
        examCombo.getItems().addAll(data.exams);

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        ComboBox<Classroom> roomCombo = new ComboBox<>();
        roomCombo.getItems().addAll(data.classrooms);

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");

        saveBtn.setOnAction(e -> {
            Exam selected = examCombo.getValue();
            Classroom selectedClassroom = roomCombo.getValue();
            LocalTime time = LocalTime.parse(timeField.getText());
            LocalDate date = datePicker.getValue();
            LocalDateTime dateTime = LocalDateTime.of(date,time);
            if (selected == null) {
                info("Missing Data", "Please select an exam.");
                return;
            }
                try{
                    ScheduleEditController scheduleEditController = new ScheduleEditController(data.exams);
                    scheduleEditController.manualMoveExam(selected,selectedClassroom,dateTime);
                    dialog.close();
                }catch (Exception ex){
                    showError(ex);
                }




        });

        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(10,
                new Label("Exam:"), examCombo,
                new Label("New Date:"), datePicker,
                new Label("New Start Time (HH:mm):"), timeField,
                new Label("New Classroom:"), roomCombo,
                saveBtn, cancelBtn
        );

        root.setPadding(new Insets(10));
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
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
                if (!result.isFeasible()) {
                    showError(new IllegalStateException("No feasible solution was found."));
                    return;
                }
                data.exams = convertScheduleResultToExams(result);
                data.setExamMap(data.exams);
                scheduleTableAll.getItems().setAll(data.exams);

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
    public void handleViewAttendance() {
        mainTabs.getSelectionModel().select(2);
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
        if (scheduleTableAll != null) scheduleTableAll.getItems().setAll(data.exams);
        if (scheduleTableStudent != null) scheduleTableStudent.getItems().setAll(data.exams);
        if (attendanceList != null) {
            List<String> lines = new ArrayList<>();
            for (Course c : data.courses) {
                lines.add(c.getCourseCode() + " -> " + c.getStudents().size() + " students");
            }
            attendanceList.getItems().setAll(lines);
        }



    }

    private File pickCsv(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Window w = (mainTabs != null && mainTabs.getScene() != null) ? mainTabs.getScene().getWindow() : null;
        return fc.showOpenDialog(w);
    }
    private File pickProj(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Exam Files", "*.exams"));
        Window w = (mainTabs != null && mainTabs.getScene() != null) ? mainTabs.getScene().getWindow() : null;
        if (title.toLowerCase().contains("open")) {return fc.showOpenDialog(w);}
        else {return fc.showSaveDialog(w);}
    }

    private void info(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(text);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private void showError(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(ex.getClass().getSimpleName());
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
            cStart.setCellValueFactory(e -> {
                LocalDateTime dt = e.getValue().getStart();
                return new SimpleStringProperty(dt == null ? "-" : dt.format(DT_FORMAT));
            });

            cStart.setPrefWidth(180);

            scheduleTableAll.getColumns().addAll(cCourse, cRoom, cStart);
            scheduleTableAll.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
            cStart.setCellValueFactory(e -> {
                LocalDateTime dt = e.getValue().getStart();
                return new SimpleStringProperty(dt == null ? "-" : dt.format(DT_FORMAT));
            });

            scheduleTableStudent.getColumns().addAll(cCourse, cRoom, cStart);
            scheduleTableStudent.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        }
    }
    @FXML
    public void handleImportAttendance() {
        File f = pickCsv("Import Attendance");
        if (f == null) return;
        try {
            data.loadAttendance(f);
            refreshLists();
        } catch (Exception ex) {
            showError(ex);
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
        for (Exam e : data.exams) {
            if (e.getCourse() == null) continue;
            String code = e.getCourse().getCourseCode();
            if (selectedCourseCodes.contains(code)) {
                filtered.add(e);
            }
        }

        scheduleTableStudent.getItems().setAll(filtered);
    }
    @FXML
    public void handleUserManual() {

        Stage manualStage = new Stage();
        manualStage.setTitle("Program Manual");
        manualStage.initModality(Modality.APPLICATION_MODAL);

        TextArea manualText = new TextArea();
        manualText.setEditable(false);
        manualText.setWrapText(true);

        manualText.setText(
                "EXAM SCHEDULING PROGRAM – USER MANUAL\n\n" +

                        "--------------------------------------------------\n" +
                        "1. IMPORTING DATA (RECOMMENDED ORDER)\n" +
                        "--------------------------------------------------\n" +
                        "The user must import the following CSV files using the Import button\n" +
                        "on the menu bar, in the order listed below:\n\n" +
                        "1) Students.csv\n" +
                        "2) Courses.csv\n" +
                        "3) Classrooms.csv\n" +
                        "4) AttendanceList.csv\n\n" +

                        "--------------------------------------------------\n" +
                        "2. EXPECTED FILE STRUCTURES\n" +
                        "--------------------------------------------------\n\n" +

                        "2.1 Students.csv\n" +
                        "This file contains all student IDs in the system.\n\n" +
                        "Expected format:\n" +
                        "- First line: Header / description\n" +
                        "- Following lines: One student ID per line\n\n" +
                        "Example:\n" +
                        "ALL OF THE STUDENTS IN THE SYSTEM\n" +
                        "Std_ID_001\n" +
                        "Std_ID_002\n" +
                        "Std_ID_003\n\n" +

                        "--------------------------------------------------\n" +

                        "2.2 Courses.csv\n" +
                        "This file contains all course codes in the system.\n\n" +
                        "Expected format:\n" +
                        "- First line: Header / description\n" +
                        "- Following lines: One course code per line\n\n" +
                        "Example:\n" +
                        "ALL OF THE COURSES IN THE SYSTEM\n" +
                        "CSE101\n" +
                        "MATH202\n" +
                        "ENG103\n\n" +

                        "--------------------------------------------------\n" +

                        "2.3 Classrooms.csv\n" +
                        "This file defines classrooms and their capacities.\n\n" +
                        "Expected format:\n" +
                        "- First line: Header / description\n" +
                        "- Following lines: ClassroomID;Capacity\n\n" +
                        "Example:\n" +
                        "ALL OF THE CLASSROOMS; AND THEIR CAPACITIES IN THE SYSTEM\n" +
                        "Classroom_A;40\n" +
                        "Classroom_B;60\n\n" +

                        "--------------------------------------------------\n" +

                        "2.4 AttendanceList.csv\n" +
                        "This file defines which students attend which courses.\n\n" +
                        "Expected format:\n" +
                        "- A course code on one line\n" +
                        "- Followed by a list of student IDs attending that course\n" +
                        "- Repeated for each course\n\n" +
                        "Example:\n" +
                        "CSE101\n" +
                        "['Std_ID_001', 'Std_ID_002', 'Std_ID_005']\n\n" +
                        "MATH202\n" +
                        "['Std_ID_002', 'Std_ID_003']\n\n" +

                        "--------------------------------------------------\n" +
                        "3. CREATING THE SCHEDULE\n" +
                        "--------------------------------------------------\n" +
                        "After all files are successfully imported, press the\n" +
                        "'Create Schedule' button. Input the time slots and number of days.\n" +
                        "The system will generate an\n" +
                        "exam schedule while considering classroom capacities\n" +
                        "and student conflicts.\n\n" +

                        "--------------------------------------------------\n" +
                        "4. VIEWING THE SCHEDULE\n" +
                        "--------------------------------------------------\n" +
                        "Schedule (All Exams): Displays all scheduled exams.\n\n" +
                        "Schedule (By Student):\n" +
                        "- Select a Student ID\n" +
                        "- View all exams scheduled for that student\n\n" +

                        "--------------------------------------------------\n" +
                        "5. EDITING EXAMS\n" +
                        "--------------------------------------------------\n" +
                        "Using the Edit option in the menu bar, the user can:\n" +
                        "- Change the exam date\n" +
                        "- Change the exam start time\n" +
                        "- Change the classroom/location\n\n" +
                        "If conflicts occur, the system will notify the user\n" +
                        "and ask whether to continue.\n\n" +

                        "--------------------------------------------------\n" +
                        "6. EXPORTING THE SCHEDULE\n" +
                        "--------------------------------------------------\n" +
                        "Use 'Export Schedule' to export the full schedule\n" +
                        "as a CSV file.\n\n" +

                        "--------------------------------------------------\n" +
                        "7. SAVING AND RESTORING PROGRAM STATE\n" +
                        "--------------------------------------------------\n" +
                        "Save As:\n" +
                        "- Saves the current program state as a .exams file\n\n" +
                        "Open:\n" +
                        "- Loads a previously saved .exams file\n" +
                        "- Restores all data and schedules\n"
        );

        ScrollPane scrollPane = new ScrollPane(manualText);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 750, 600);
        manualStage.setScene(scene);
        manualStage.show();
    }
    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
