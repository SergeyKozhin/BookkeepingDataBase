package application;

import controllers.Controller;
import controllers.ModalController;
import controllers.projects.ProjectsWindowController;
import db.dao.DepartmentDAO;
import db.dao.EmployeeDAO;
import db.dao.ProjectDAO;
import db.dao.UserDAO;
import db.source.OracleSourceProvider;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;

public class App extends Application {
    private Stage primaryStage;
    private EmployeeDAO employees;
    private ProjectDAO projects;
    private DepartmentDAO departments;
    private UserDAO users;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DataSource ds = OracleSourceProvider.getDataSource();
        employees = new EmployeeDAO(ds);
        projects = new ProjectDAO(ds);
        departments = new DepartmentDAO(ds);
        users = new UserDAO(ds);

        this.primaryStage = stage;
        openWindow("/login/LoginWindow.fxml", "Login");
        stage.show();
    }

    public void openWindow(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views" + path));
            primaryStage.setScene(new Scene(loader.load()));

            primaryStage.setTitle(title);

            Controller controller = loader.getController();
            controller.setApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public <T extends ModalController> T openModal(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views" + path));

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(getPrimaryStage());
            dialogStage.setScene(new Scene(loader.load()));

            T controller = loader.getController();
            controller.setApp(this);
            controller.setStage(dialogStage);

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public EmployeeDAO getEmployees() {
        return employees;
    }

    public ProjectDAO getProjects() {
        return projects;
    }

    public DepartmentDAO getDepartments() {
        return departments;
    }

    public void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(getPrimaryStage());
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);

        alert.showAndWait();
    }

    public boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(getPrimaryStage());
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    public UserDAO getUsers() {
        return users;
    }
}
