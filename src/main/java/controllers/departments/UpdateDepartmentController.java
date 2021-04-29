package controllers.departments;

import application.App;
import controllers.FXUtils;
import controllers.ModalController;
import db.models.Department;
import db.models.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Date;

public class UpdateDepartmentController implements ModalController {
    private Stage stage;
    private App app;
    private Department department;
    private boolean isUpdated = false;

    @FXML
    private TextField nameInput;


    @FXML
    void initialize() {
        FXUtils.addTextLimiter(nameInput, 20);
    }

    @FXML
    void handleSave() {
        if (!isInputValid()) {
            return;
        }

        Department newDepartment = new Department();
        newDepartment.setName(nameInput.getText());

        try {
            if (department != null) {
                newDepartment.setId(department.getId());
                app.getDepartments().update(newDepartment);
            } else {
                app.getDepartments().add(newDepartment);
            }

            isUpdated = true;
            stage.close();
        } catch (IllegalArgumentException e) {
            app.showError(e.getMessage());
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }

    @FXML
    void handleCancel() {
        stage.close();
    }

    @Override
    public void setApp(App app) {
        this.app = app;
    }

    public void setDepartment(Department department) {
        this.department = department;
        nameInput.setText(department.getName());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        if (nameInput.getText() == null || nameInput.getText().isEmpty()) {
            errorMessage.append("No name provided\n");
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            app.showError(errorMessage.toString());

            return false;
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }
}
