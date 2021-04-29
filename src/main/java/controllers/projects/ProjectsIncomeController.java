package controllers.projects;

import application.App;
import controllers.ModalController;
import db.models.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ProjectsIncomeController implements ModalController {
    private App app;
    private Stage stage;
    private ObservableList<Project> projects = FXCollections.observableArrayList();

    @FXML
    private TableView<Project> tableView;

    @FXML
    private TableColumn<Project, String> nameColumn;

    @FXML
    private TableColumn<Project, Double> incomeColumn;

    @FXML
    void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        incomeColumn.setCellValueFactory(cellData -> cellData.getValue().incomeProperty().asObject());
        tableView.setItems(projects);
    }

    @Override
    public void setApp(App app) {
        this.app = app;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        projects.setAll(app.getProjects().getWithIncomes());
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
