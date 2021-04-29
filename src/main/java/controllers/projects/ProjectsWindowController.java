package controllers.projects;

import application.App;
import controllers.Controller;
import controllers.departments.DepartmentsWindowController;
import controllers.employees.EmployeesWindowController;
import db.models.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.sql.Date;

public class ProjectsWindowController implements Controller {
    private App app;
    private ObservableList<Project> projects = FXCollections.observableArrayList();

    @FXML
    private TableView<Project> tableView;

    @FXML
    private TableColumn<Project, String> nameColumn;

    @FXML
    private TableColumn<Project, Double> costColumn;

    @FXML
    private TableColumn<Project, String> deptColumn;

    @FXML
    private TableColumn<Project, Date> dateBegColumn;

    @FXML
    private TableColumn<Project, Date> dateEndColumn;

    @FXML
    private TableColumn<Project, Date> dateEndRealColumn;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    void handleAddProject(ActionEvent event) {
        UpdateProjectController controller = app.openModal("/projects/UpdateProjectWindow.fxml", "New project");
        controller.getStage().showAndWait();
        if (controller.isUpdated()) {
            projects.setAll(app.getProjects().getAll());
            tableView.getSelectionModel().selectLast();
        }
    }

    @FXML
    void handleDeleteProject(ActionEvent event) {
        try {
            Project project = tableView.getSelectionModel().getSelectedItem();
            if (project.getDateEndReal() == null) {
                app.showError("Can't delete unfinished project");
                return;
            }

            if (app.showConfirmation("Are you sure you want to delete project " + project.getName() + "?")) {
                app.getProjects().delete(project.getId());
                projects.setAll(app.getProjects().getAll());
            }
        } catch (RuntimeException e) {
            app.showError(e.getCause().getMessage());
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        app.getPrimaryStage().close();
    }

    @FXML
    void handleUpdateProject(ActionEvent event) {
        Project project = tableView.getSelectionModel().getSelectedItem();
        int index = tableView.getSelectionModel().getSelectedIndex();
        UpdateProjectController controller = app.openModal("/projects/UpdateProjectWindow.fxml", project.getName());
        controller.setProject(project);
        controller.getStage().showAndWait();
        if (controller.isUpdated()) {
            projects.setAll(app.getProjects().getAll());
            tableView.getSelectionModel().selectIndices(index);
        }
    }

    @FXML
    void handleIncome() {
        ProjectsIncomeController controller = app.openModal("/projects/ProjectsIncomeWindow.fxml", "Income");
        controller.getStage().showAndWait();
    }

    @FXML
    void handleMangeDepartments() {
        DepartmentsWindowController controller = app.openModal("/departments/DepartmentsWindow.fxml", "Departments");
        controller.getStage().showAndWait();
    }

    @FXML
    void handleMangeEmployees() {
        EmployeesWindowController controller = app.openModal("/employees/EmployeesWindow.fxml", "Departments");
        controller.getStage().showAndWait();
    }

    @FXML
    void initialize() {
        tableView.setRowFactory(tv -> {
            TableRow<Project> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    handleUpdateProject(null);
                }
            });
            return row;
        });

        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        costColumn.setCellValueFactory(cellData -> cellData.getValue().costProperty().asObject());
        deptColumn.setCellValueFactory(cellData -> app.getDepartments().get(cellData.getValue().getDepartmentID()).nameProperty());
        dateBegColumn.setCellValueFactory(cellData -> cellData.getValue().dateBeginProperty());
        dateEndColumn.setCellValueFactory(cellData -> cellData.getValue().dateEndProperty());
        dateEndRealColumn.setCellValueFactory(cellData -> cellData.getValue().dateEndRealProperty());

        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        tableView.setItems(projects);
        tableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, project, newValue) -> {
            if (newValue != null) {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        }));
    }

    @Override
    public void setApp(App app) {
        this.app = app;
        projects.setAll(app.getProjects().getAll());
    }
}
