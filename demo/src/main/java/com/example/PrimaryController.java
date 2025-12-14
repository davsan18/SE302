package com.example;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

public class PrimaryController {

    private void info(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }

    @FXML
    public void handleNew() {
        info("New", "Create a new record (not implemented).");
    }

    @FXML
    public void handleOpen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File f = chooser.showOpenDialog(null);
        if (f != null) info("Open", "Opened: " + f.getAbsolutePath());
    }

    @FXML
    public void handleSave() {
        info("Save", "Save action triggered (not implemented).");
    }

    @FXML
    public void handleSaveAs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save As");
        File f = chooser.showSaveDialog(null);
        if (f != null) info("Save As", "Saved to: " + f.getAbsolutePath());
    }

    @FXML
    public void handleExit() {
        System.exit(0);
    }

    @FXML
    public void handleImport() {
        info("Import", "Choose an import option from the menu.");
    }

    @FXML
    public void handleImportStudents() { info("Import", "Import Students (not implemented)."); }
    @FXML
    public void handleImportCourses() { info("Import", "Import Courses (not implemented)."); }
    @FXML
    public void handleImportAttendances() { info("Import", "Import Attendances (not implemented)."); }
    @FXML
    public void handleImportClassrooms() { info("Import", "Import Classrooms (not implemented)."); }

    @FXML
    public void handleExportStudents() { info("Export", "Export Students (not implemented)."); }
    @FXML
    public void handleExportCourses() { info("Export", "Export Courses (not implemented)."); }
    @FXML
    public void handleExportAttendances() { info("Export", "Export Attendances (not implemented)."); }
    @FXML
    public void handleExportClassrooms() { info("Export", "Export Classrooms (not implemented)."); }

    @FXML
    public void handleAddStudent() { info("Add", "Add Student dialog (not implemented)."); }
    @FXML
    public void handleAddCourse() { info("Add", "Add Course dialog (not implemented)."); }
    @FXML
    public void handleAddAttendance() { info("Add", "Add Attendance dialog (not implemented)."); }
    @FXML
    public void handleAddClassroom() { info("Add", "Add Classroom dialog (not implemented)."); }

    @FXML
    public void handleEditStudent() { info("Edit", "Edit Student (not implemented)."); }
    @FXML
    public void handleEditCourse() { info("Edit", "Edit Course (not implemented)."); }
    @FXML
    public void handleEditAttendance() { info("Edit", "Edit Attendance (not implemented)."); }
    @FXML
    public void handleEditClassroom() { info("Edit", "Edit Classroom (not implemented)."); }
    @FXML
    public void handleEditExam() { info("Edit", "Edit Exam (not implemented)."); }

    @FXML
    public void handleDeleteStudent() { info("Delete", "Delete Student (not implemented)."); }
    @FXML
    public void handleDeleteCourse() { info("Delete", "Delete Course (not implemented)."); }
    @FXML
    public void handleDeleteAttendance() { info("Delete", "Delete Attendance (not implemented)."); }
    @FXML
    public void handleDeleteClassroom() { info("Delete", "Delete Classroom (not implemented)."); }

    @FXML
    public void handleViewStudents() { info("View", "View Students (not implemented)."); }
    @FXML
    public void handleViewCourses() { info("View", "View Courses (not implemented)."); }
    @FXML
    public void handleViewAttendances() { info("View", "View Attendances (not implemented)."); }
    @FXML
    public void handleViewClassrooms() { info("View", "View Classrooms (not implemented)."); }
    @FXML
    public void handleViewSchedule() { info("View", "View Schedule (not implemented)."); }

    @FXML
    public void handleSearch() { info("Search", "Search (not implemented)."); }
    @FXML
    public void handleCreateSchedule() { info("Schedule", "Create Schedule (not implemented)."); }

    @FXML
    public void handleUserManual() { info("Help", "Open user manual (not implemented)."); }
    @FXML
    public void handleAbout() { info("About", "Application version 0.1\nBuilt with JavaFX."); }
}
