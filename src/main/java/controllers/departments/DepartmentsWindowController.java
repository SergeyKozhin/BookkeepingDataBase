package controllers.departments;

import application.App;
import controllers.ModalController;
import controllers.projects.UpdateProjectController;
import db.models.Department;
import db.models.Employee;
import db.models.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class DepartmentsWindowController implements ModalController {
    private App app;
    private Stage stage;
    private ObservableList<Department> departments = FXCollections.observableArrayList();
    private ObservableList<Employee> departmentEmployees = FXCollections.observableArrayList();
    private ObservableList<Employee> selectorEmployees = FXCollections.observableArrayList();

    @FXML
    private Button deleteButton;

    @FXML
    private Button updateButton;

    @FXML
    private ListView<Department> departmentsList;

    @FXML
    private ListView<Employee> employeesList;

    @FXML
    private ComboBox<Employee> employeesSelector;

    @FXML
    private Button deleteEmployeeButton;

    @FXML
    private Button addEmployeeButton;

    @FXML
    void initialize() {
        departmentsList.setItems(departments);
        departmentsList.setCellFactory(param -> {
            ListCell<Department> cell = new ListCell<>() {
                @Override
                protected void updateItem(Department item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!cell.isEmpty())) {
                    handleUpdateDepartment();
                }
            });
            return cell;
        });

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        employeesSelector.setDisable(true);

        departmentsList.getSelectionModel().selectedItemProperty().addListener((observableValue, department, newValue) -> {
            if (newValue != null) {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                employeesSelector.setDisable(false);

                departmentEmployees.setAll(app.getEmployees().getByDepartment(newValue.getId()));
                updateSelector();
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                employeesSelector.setDisable(true);

                departmentEmployees.clear();
                selectorEmployees.setAll(app.getEmployees().getAll());
            }
        });

        employeesList.setItems(departmentEmployees);
        employeesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLastName() + " " + item.getFirstName() + " " + item.getPatherName());
                }
            }
        });

        deleteEmployeeButton.setDisable(true);

        employeesList.getSelectionModel().selectedItemProperty().addListener((observableValue, employee, newValue) -> deleteEmployeeButton.setDisable(newValue == null));

        employeesSelector.setItems(selectorEmployees);
        employeesSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Employee item) {
                if (item != null) {
                    return item.getLastName() + " " + item.getFirstName() + " " + item.getPatherName();
                }
                return "";
            }

            @Override
            public Employee fromString(String string) {
                return null;
            }
        });

        addEmployeeButton.setDisable(true);

        employeesSelector.valueProperty().addListener((observableValue, employee, newValue) -> addEmployeeButton.setDisable(newValue == null));
    }

    @FXML
    void handleAddDepartment() {
        UpdateDepartmentController controller = app.openModal("/departments/UpdateDepartmentWindow.fxml", "New Department");
        controller.getStage().showAndWait();
        if (controller.isUpdated()) {
            departments.setAll(app.getDepartments().getAll());
            departmentsList.getSelectionModel().selectLast();
        }
    }

    @FXML
    void handleUpdateDepartment() {
        Department department = departmentsList.getSelectionModel().getSelectedItem();
        int index = departmentsList.getSelectionModel().getSelectedIndex();
        UpdateDepartmentController controller = app.openModal("/departments/UpdateDepartmentWindow.fxml", department.getName());
        controller.setDepartment(department);
        controller.getStage().showAndWait();
        if (controller.isUpdated()) {
            departments.setAll(app.getDepartments().getAll());
            departmentsList.getSelectionModel().selectIndices(index);
        }
    }

    @FXML
    void handleDeleteDepartment() {
        try {
            Department department = departmentsList.getSelectionModel().getSelectedItem();
            List<Project> projects = app.getProjects().getByDepartment(department.getId());
            if (!projects.isEmpty()) {
                app.showError("Department has projects!");
                return;
            }

            if (app.showConfirmation("Are you sure you want to delete department " + department.getName() + "?")) {
                app.getDepartments().delete(department.getId());
                departments.setAll(app.getDepartments().getAll());
            }
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }


    @FXML
    void handleAddEmployee() {
        Department department = departmentsList.getSelectionModel().getSelectedItem();
        Employee employee = employeesSelector.getValue();
        try {
            app.getDepartments().addEmployee(department.getId(), employee.getId());
            departmentEmployees.setAll(app.getEmployees().getByDepartment(department.getId()));
            updateSelector();
        } catch (IllegalArgumentException e) {
            app.showError(e.getMessage());
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }

    @FXML
    void handleDeleteEmployee() {
        Department department = departmentsList.getSelectionModel().getSelectedItem();
        Employee employee = employeesList.getSelectionModel().getSelectedItem();
        try {
            app.getDepartments().deleteEmployee(department.getId(), employee.getId());
            departmentEmployees.setAll(app.getEmployees().getByDepartment(department.getId()));
            updateSelector();
        } catch (IllegalArgumentException e) {
            app.showError(e.getMessage());
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }

    private void updateSelector() {
        selectorEmployees.setAll(app.getEmployees().getAll().stream().filter(emp -> departmentEmployees.stream().filter(depEmpl -> depEmpl.getId() == emp.getId()).findFirst().isEmpty()).collect(Collectors.toList()));
        employeesSelector.setValue(null);
    }

    @Override
    public void setApp(App app) {
        this.app = app;
        departments.setAll(app.getDepartments().getAll());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
