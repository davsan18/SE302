package com.example;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {


    private final BooleanProperty scheduleCreated = new SimpleBooleanProperty(false);


    @FXML private TabPane mainTabs;


    @FXML private MenuItem miEditExam;
    @FXML private MenuItem miViewSchedule;
    @FXML private Button btnCreateSchedule;
    @FXML private Button btnViewSchedule;

    @FXML
    public void initialize() {

        BooleanBinding noSchedule = scheduleCreated.not();

        miEditExam.disableProperty().bind(noSchedule);
        miViewSchedule.disableProperty().bind(noSchedule);
        btnViewSchedule.disableProperty().bind(noSchedule);

        btnCreateSchedule.setDisable(false);
    }


    @FXML
    public void handleViewStudents() {
        mainTabs.getSelectionModel().select(0);
    }

    @FXML
    public void handleViewCourses() {
        mainTabs.getSelectionModel().select(1);
    }

    @FXML
    public void handleViewClassrooms() {
        mainTabs.getSelectionModel().select(2);
    }

    @FXML
    public void handleViewSchedule() {
        if (!scheduleCreated.get()) return;
        mainTabs.getSelectionModel().select(3);
    }


    @FXML
    public void handleCreateSchedule() {
        // Placeholder: real scheduling will be added later
        scheduleCreated.set(true);
    }


    @FXML public void handleNew() {}
    @FXML public void handleOpen() {}
    @FXML public void handleSaveAs() {}
    @FXML public void handleExit() { System.exit(0); }

   
    @FXML public void handleUserManual() {}
    @FXML public void handleAbout() {}
}
