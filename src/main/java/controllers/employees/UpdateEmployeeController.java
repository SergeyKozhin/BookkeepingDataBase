package controllers.employees;

import application.App;
import controllers.FXUtils;
import controllers.ModalController;
import db.models.Department;
import db.models.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateEmployeeController implements ModalController {
    private Stage stage;
    private App app;
    private Employee employee;
    private boolean isUpdated = false;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField fatherNameInput;

    @FXML
    private TextField positionInput;

    @FXML
    private TextField salaryInput;


    @FXML
    void initialize() {
        FXUtils.addTextLimiter(firstNameInput, 20);
        FXUtils.addTextLimiter(lastNameInput, 20);
        FXUtils.addTextLimiter(fatherNameInput, 20);
        FXUtils.addTextLimiter(positionInput, 50);
    }

    @FXML
    void handleSave() {
        if (!isInputValid()) {
            return;
        }

        Employee newEmployee = new Employee();
        newEmployee.setFirstName(firstNameInput.getText());
        newEmployee.setLastName(lastNameInput.getText());
        newEmployee.setPatherName(fatherNameInput.getText());
        newEmployee.setPosition(positionInput.getText());
        newEmployee.setSalary(Double.parseDouble(salaryInput.getText()));

        try {
            if (employee != null) {
                newEmployee.setId(employee.getId());
                app.getEmployees().update(newEmployee);
            } else {
                app.getEmployees().add(newEmployee);
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

    public void setEmployee(Employee employee) {
        this.employee = employee;
        firstNameInput.setText(employee.getFirstName());
        lastNameInput.setText(employee.getLastName());
        fatherNameInput.setText(employee.getPatherName());
        positionInput.setText(employee.getPosition());
        salaryInput.setText(Double.toString(employee.getSalary()));
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
        if (firstNameInput.getText() == null || firstNameInput.getText().isEmpty()) {
            errorMessage.append("No first name provided\n");
        }

        if (lastNameInput.getText() == null || lastNameInput.getText().isEmpty()) {
            errorMessage.append("No last name provided\n");
        }

        if (positionInput.getText() == null || positionInput.getText().isEmpty()) {
            errorMessage.append("No position provided\n");
        }

        if (salaryInput.getText() == null || salaryInput.getText().isEmpty()) {
            errorMessage.append("No salary provided\n");
        } else {
            try {
                double salary = Double.parseDouble(salaryInput.getText());
                if (salary < 0) {
                    errorMessage.append("Salary can't be negative\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("No valid salary. must be float!\n");
            }
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
