package controllers.employees;

import application.App;
import controllers.ModalController;
import db.models.Department;
import db.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.stream.Collectors;

public class EmployeesWindowController implements ModalController {
    private App app;
    private Stage stage;
    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    private ObservableList<Department> departmentEmployees = FXCollections.observableArrayList();
    private ObservableList<Department> selectorDepartments = FXCollections.observableArrayList();

    @FXML
    private Button deleteButton;

    @FXML
    private Button updateButton;

    @FXML
    private ListView<Department> departmentsList;

    @FXML
    private ComboBox<Department> departmentsSelector;

    @FXML
    private Button addDepartmentButton;

    @FXML
    private Button deleteDepartmentButton;

    @FXML
    private TableView<Employee> tableView;

    @FXML
    private TableColumn<Employee, String> lastNameColumn;

    @FXML
    private TableColumn<Employee, String> firstNameColumn;

    @FXML
    private TableColumn<Employee, String> fatherNameColumn;

    @FXML
    private TableColumn<Employee, String> positionColumn;

    @FXML
    private TableColumn<Employee, Double> salaryColumn;

    @FXML
    void initialize() {
        tableView.setItems(employees);
        tableView.setRowFactory(tv -> {
            TableRow<Employee> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleUpdateEmployee();
                }
            });
            return row;
        });


        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        fatherNameColumn.setCellValueFactory(cellData -> cellData.getValue().patherNameProperty());
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        salaryColumn.setCellValueFactory(cellData -> cellData.getValue().salaryProperty().asObject());

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        departmentsSelector.setDisable(true);

        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, employee, newValue) -> {
            if (newValue != null) {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                departmentsSelector.setDisable(false);

                departmentEmployees.setAll(app.getDepartments().getByEmployee(newValue.getId()));
                updateSelector();
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                departmentsSelector.setDisable(true);

                departmentEmployees.clear();
                selectorDepartments.setAll(app.getDepartments().getAll());
            }
        });

        departmentsList.setItems(departmentEmployees);
        departmentsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        deleteDepartmentButton.setDisable(true);

        departmentsList.getSelectionModel().selectedItemProperty().addListener((observableValue, department, newValue) -> deleteDepartmentButton.setDisable(newValue == null));

        departmentsSelector.setItems(selectorDepartments);
        departmentsSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Department item) {
                if (item != null) {
                    return item.getName();
                }
                return "";
            }

            @Override
            public Department fromString(String string) {
                return null;
            }
        });

        addDepartmentButton.setDisable(true);

        departmentsSelector.valueProperty().addListener((observableValue, department, newValue) -> addDepartmentButton.setDisable(newValue == null));
    }

    @FXML
    void handleAddEmployee() {
        UpdateEmployeeController controller = app.openModal("/employees/UpdateEmployeeWindow.fxml", "New Employee");
        controller.getStage().showAndWait();
        if (controller.isUpdated()) {
            employees.setAll(app.getEmployees().getAll());
            departmentsList.getSelectionModel().selectLast();
        }
    }

    @FXML
    void handleUpdateEmployee() {
        Employee employee = tableView.getSelectionModel().getSelectedItem();
        int index = tableView.getSelectionModel().getSelectedIndex();
        UpdateEmployeeController controller = app.openModal("/employees/UpdateEmployeeWindow.fxml", employee.getLastName() + " " + employee.getFirstName() + " " + employee.getPatherName());
        controller.setEmployee(employee);
        controller.getStage().showAndWait();
        if (controller.isUpdated()) {
            employees.setAll(app.getEmployees().getAll());
            tableView.getSelectionModel().selectIndices(index);
        }
    }

    @FXML
    void handleDeleteEmployee() {
        try {
            Employee employee = tableView.getSelectionModel().getSelectedItem();

            if (app.showConfirmation("Are you sure you want to delete employee " + employee.getLastName() + " " +
                    employee.getFirstName() + " " + employee.getPatherName() + "?")) {
                app.getEmployees().delete(employee.getId());
                employees.setAll(app.getEmployees().getAll());
            }
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }


    @FXML
    void handleAddDepartment() {
        Employee employee = tableView.getSelectionModel().getSelectedItem();
        Department department = departmentsSelector.getValue();
        try {
            app.getDepartments().addEmployee(department.getId(), employee.getId());
            departmentEmployees.setAll(app.getDepartments().getByEmployee(employee.getId()));
            updateSelector();
        } catch (IllegalArgumentException e) {
            app.showError(e.getMessage());
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }

    @FXML
    void handleDeleteDepartment() {
        Employee employee = tableView.getSelectionModel().getSelectedItem();
        Department department = departmentsList.getSelectionModel().getSelectedItem();
        try {
            app.getDepartments().deleteEmployee(department.getId(), employee.getId());
            departmentEmployees.setAll(app.getDepartments().getByEmployee(employee.getId()));
            updateSelector();
        } catch (IllegalArgumentException e) {
            app.showError(e.getMessage());
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }

    private void updateSelector() {
        selectorDepartments.setAll(app.getDepartments().getAll().stream().filter(dep -> departmentEmployees.stream().filter(depEmpl -> depEmpl.getId() == dep.getId()).findFirst().isEmpty()).collect(Collectors.toList()));
        departmentsSelector.setValue(null);
    }

    @Override
    public void setApp(App app) {
        this.app = app;
        employees.setAll(app.getEmployees().getAll());
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
