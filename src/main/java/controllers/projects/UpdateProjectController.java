package controllers.projects;

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

public class UpdateProjectController implements ModalController {
    private Stage stage;
    private App app;
    private Project project;
    private boolean isUpdated = false;
    private ObservableList<Department> departments = FXCollections.observableArrayList();

    @FXML
    private TextField nameInput;

    @FXML
    private TextField costInput;

    @FXML
    private ComboBox<Department> departmentList;

    @FXML
    private DatePicker dateBegin;

    @FXML
    private DatePicker dateEnd;

    @FXML
    private DatePicker dateEndReal;

    @FXML
    void initialize() {
        departmentList.setItems(departments);
        departmentList.setConverter(new StringConverter<>() {
            @Override
            public String toString(Department department) {
                if (department != null) {
                    return department.getName();
                }
                return "";
            }

            @Override
            public Department fromString(String string) {
                return null;
            }
        });

        FXUtils.addTextLimiter(nameInput, 200);
    }

    @FXML
    void handleSave() {
        if (!isInputValid()) {
            return;
        }

        Project newProject = new Project();
        newProject.setName(nameInput.getText());
        newProject.setCost(Double.parseDouble(costInput.getText()));
        newProject.setDepartmentID(departmentList.getValue().getId());
        newProject.setDateBegin(Date.valueOf(dateBegin.getValue()));
        newProject.setDateEnd(Date.valueOf(dateEnd.getValue()));
        if (dateEndReal.getValue() != null) {
            newProject.setDateEndReal(Date.valueOf(dateEndReal.getValue()));
        } else {
            newProject.setDateEndReal(null);
        }

        try {
            if (project != null) {
                newProject.setId(project.getId());
                app.getProjects().update(newProject);
            } else {
                app.getProjects().add(newProject);
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
        departments.setAll(app.getDepartments().getAll());
    }

    public void setProject(Project project) {
        this.project = project;

        nameInput.setText(project.getName());
        costInput.setText(Double.toString(project.getCost()));
        departmentList.setValue(departments.stream().filter(dep -> dep.getId() == project.getDepartmentID()).findFirst().orElse(null));
        dateBegin.setValue(project.getDateBegin().toLocalDate());
        dateEnd.setValue(project.getDateEnd().toLocalDate());
        if (project.getDateEndReal() != null) {
            dateEndReal.setValue(project.getDateEndReal().toLocalDate());
        }
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

        if (costInput.getText() == null || costInput.getText().isEmpty()) {
            errorMessage.append("No cost provided\n");
        } else {
            try {
                double cost = Double.parseDouble(costInput.getText());
                if (cost < 0) {
                    errorMessage.append("Cost can't be negative\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("No valid cost. must be float!\n");
            }
        }

        if (departmentList.getValue() == null) {
            errorMessage.append("No department provided\n");
        }

        if (dateBegin.getValue() == null) {
            errorMessage.append("No beginning date provided\n");
        }
        if (dateEnd.getValue() == null) {
            errorMessage.append("No end date provided\n");
        }

        if ((dateBegin.getValue() != null && dateEnd.getValue() != null && dateEnd.getValue().isBefore(dateBegin.getValue())) ||
                (dateBegin.getValue() != null && dateEndReal.getValue() != null && dateEndReal.getValue().isBefore(dateBegin.getValue()))) {
            errorMessage.append("End date can't be earlier then begin date\n");
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
